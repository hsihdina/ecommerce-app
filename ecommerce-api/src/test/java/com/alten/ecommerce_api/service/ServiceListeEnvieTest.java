package com.alten.ecommerce_api.service;

import com.alten.ecommerce_api.model.dto.panier.ListeEnvieDto;
import com.alten.ecommerce_api.model.dto.ProduitDto;
import com.alten.ecommerce_api.model.dao.ListeEnvie;
import com.alten.ecommerce_api.model.dao.Produit;
import com.alten.ecommerce_api.model.dao.Utilisateur;
import com.alten.ecommerce_api.exception.RessourceExistanteException;
import com.alten.ecommerce_api.exception.RessourceIntrouvableException;
import com.alten.ecommerce_api.mapper.PanierMapper;
import com.alten.ecommerce_api.model.enumeration.StatutInventaire;
import com.alten.ecommerce_api.repository.ListeEnvieRepository;
import com.alten.ecommerce_api.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service Liste d'Envie")
class ServiceListeEnvieTest {

    @Mock
    private ListeEnvieRepository listeEnvieRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private PanierMapper panierMapper;

    @InjectMocks
    private ServiceListeEnvie serviceListeEnvie;

    private Utilisateur utilisateur;
    private Produit produit;
    private ListeEnvie listeEnvie;
    private ListeEnvieDto listeEnvieDTO;
    private ProduitDto produitDTO;

    @BeforeEach
    void setUp() {
        utilisateur = Utilisateur.builder()
                .id(1L)
                .nomUtilisateur("testuser")
                .email("user@test.com").build();

        produit = Produit.builder()
                .id(1L)
                .code("PROD001")
                .nom("Produit Test")
                .prix(99.99)
                .quantite(10)
                .categorie("Test")
                .statutInventaire(StatutInventaire.INSTOCK)
                .build();

        listeEnvie = ListeEnvie.builder()
                .id(1L)
                .utilisateur(utilisateur)
                .produit(produit)
                .build();

        produitDTO = ProduitDto.builder()
                .id(1L)
                .code("PROD001")
                .nom("Produit Test")
                .build();

        listeEnvieDTO = new ListeEnvieDto();
        listeEnvieDTO.setId(1L);
        listeEnvieDTO.setProduits(Arrays.asList(produitDTO));
    }

    @Test
    @DisplayName("Obtenir liste d'envie avec produits")
    void obtenirListeEnvie_avecProduits_devraitRetournerListe() {
        // Given
        List<ListeEnvie> listeEnvies = Arrays.asList(listeEnvie);
        when(listeEnvieRepository.findByUtilisateur(utilisateur)).thenReturn(listeEnvies);
        when(panierMapper.versListeEnvieDTO(listeEnvies)).thenReturn(listeEnvieDTO);

        // When
        ListeEnvieDto resultat = serviceListeEnvie.obtenirListeEnvie(utilisateur);

        // Then
        assertThat(resultat).isNotNull();
        assertThat(resultat.getProduits()).hasSize(1);
        verify(listeEnvieRepository, times(1)).findByUtilisateur(utilisateur);
        verify(panierMapper, times(1)).versListeEnvieDTO(listeEnvies);
    }

    @Test
    @DisplayName("Obtenir liste d'envie sans produits")
    void obtenirListeEnvie_sansProduits_devraitRetournerListeVide() {
        // Given
        List<ListeEnvie> listeEnviesVide = new ArrayList<>();
        ListeEnvieDto listeVide = new ListeEnvieDto(null, new ArrayList<>());
        when(listeEnvieRepository.findByUtilisateur(utilisateur)).thenReturn(listeEnviesVide);
        when(panierMapper.versListeEnvieDTO(listeEnviesVide)).thenReturn(listeVide);

        // When
        ListeEnvieDto resultat = serviceListeEnvie.obtenirListeEnvie(utilisateur);

        // Then
        assertThat(resultat).isNotNull();
        assertThat(resultat.getProduits()).isEmpty();
        verify(listeEnvieRepository, times(1)).findByUtilisateur(utilisateur);
    }

    @Test
    @DisplayName("Ajouter produit valide non présent")
    void ajouterProduit_produitValideNonPresent_devraitAjouterProduit() {
        // Given
        Long produitId = 1L;
        when(listeEnvieRepository.existsByUtilisateurAndProduitId(utilisateur, produitId)).thenReturn(false);
        when(produitRepository.findById(produitId)).thenReturn(Optional.of(produit));
        when(listeEnvieRepository.save(any(ListeEnvie.class))).thenReturn(listeEnvie);
        when(listeEnvieRepository.findByUtilisateur(utilisateur)).thenReturn(Arrays.asList(listeEnvie));
        when(panierMapper.versListeEnvieDTO(any())).thenReturn(listeEnvieDTO);

        // When
        ListeEnvieDto resultat = serviceListeEnvie.ajouterProduit(utilisateur, produitId);

        // Then
        assertThat(resultat).isNotNull();
        verify(listeEnvieRepository, times(1)).existsByUtilisateurAndProduitId(utilisateur, produitId);
        verify(produitRepository, times(1)).findById(produitId);
        verify(listeEnvieRepository, times(1)).save(any(ListeEnvie.class));
    }

    @Test
    @DisplayName("Ajouter produit déjà présent")
    void ajouterProduit_produitDejaPresent_devraitLeverException() {
        // Given
        Long produitId = 1L;
        when(listeEnvieRepository.existsByUtilisateurAndProduitId(utilisateur, produitId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> serviceListeEnvie.ajouterProduit(utilisateur, produitId))
                .isInstanceOf(RessourceExistanteException.class)
                .hasMessageContaining("Le produit est déjà dans votre liste d'envie");

        verify(listeEnvieRepository, times(1)).existsByUtilisateurAndProduitId(utilisateur, produitId);
        verify(produitRepository, never()).findById(any());
        verify(listeEnvieRepository, never()).save(any(ListeEnvie.class));
    }

    @Test
    @DisplayName("Ajouter produit inexistant")
    void ajouterProduit_produitInexistant_devraitLeverException() {
        // Given
        Long produitId = 999L;
        when(listeEnvieRepository.existsByUtilisateurAndProduitId(utilisateur, produitId)).thenReturn(false);
        when(produitRepository.findById(produitId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> serviceListeEnvie.ajouterProduit(utilisateur, produitId))
                .isInstanceOf(RessourceIntrouvableException.class)
                .hasMessageContaining("Produit non trouvé");

        verify(listeEnvieRepository, times(1)).existsByUtilisateurAndProduitId(utilisateur, produitId);
        verify(produitRepository, times(1)).findById(produitId);
        verify(listeEnvieRepository, never()).save(any(ListeEnvie.class));
    }

    @Test
    @DisplayName("Retirer produit présent")
    void retirerProduit_produitPresent_devraitRetirerProduit() {
        // Given
        Long produitId = 1L;
        when(listeEnvieRepository.findByUtilisateurAndProduitId(utilisateur, produitId))
                .thenReturn(Optional.of(listeEnvie));
        when(listeEnvieRepository.findByUtilisateur(utilisateur)).thenReturn(new ArrayList<>());
        when(panierMapper.versListeEnvieDTO(any())).thenReturn(new ListeEnvieDto(1L, new ArrayList<>()));

        // When
        ListeEnvieDto resultat = serviceListeEnvie.retirerProduit(utilisateur, produitId);

        // Then
        assertThat(resultat).isNotNull();
        verify(listeEnvieRepository, times(1)).findByUtilisateurAndProduitId(utilisateur, produitId);
        verify(listeEnvieRepository, times(1)).delete(listeEnvie);
        verify(listeEnvieRepository, times(1)).findByUtilisateur(utilisateur);
    }

    @Test
    @DisplayName("Retirer produit non présent")
    void retirerProduit_produitNonPresent_devraitLeverException() {
        // Given
        Long produitId = 999L;
        when(listeEnvieRepository.findByUtilisateurAndProduitId(utilisateur, produitId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> serviceListeEnvie.retirerProduit(utilisateur, produitId))
                .isInstanceOf(RessourceIntrouvableException.class)
                .hasMessageContaining("Produit non trouvé dans votre liste d'envie");

        verify(listeEnvieRepository, times(1)).findByUtilisateurAndProduitId(utilisateur, produitId);
        verify(listeEnvieRepository, never()).delete(any(ListeEnvie.class));
    }

    @Test
    @DisplayName("Ajouter plusieurs produits")
    void ajouterPlusieursProduits_devraitTousLesAjouter() {
        // Given
        Produit produit2 = new Produit();
        produit2.setId(2L);
        produit2.setCode("PROD002");

        when(listeEnvieRepository.existsByUtilisateurAndProduitId(eq(utilisateur), anyLong()))
                .thenReturn(false);
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(produitRepository.findById(2L)).thenReturn(Optional.of(produit2));
        when(listeEnvieRepository.save(any(ListeEnvie.class))).thenReturn(listeEnvie);
        when(listeEnvieRepository.findByUtilisateur(utilisateur)).thenReturn(Arrays.asList(listeEnvie));
        when(panierMapper.versListeEnvieDTO(any())).thenReturn(listeEnvieDTO);

        // When
        serviceListeEnvie.ajouterProduit(utilisateur, 1L);
        serviceListeEnvie.ajouterProduit(utilisateur, 2L);

        // Then
        verify(listeEnvieRepository, times(2)).save(any(ListeEnvie.class));
        verify(produitRepository, times(1)).findById(1L);
        verify(produitRepository, times(1)).findById(2L);
    }
}