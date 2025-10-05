package com.alten.ecommerce_api.service;

import com.alten.ecommerce_api.exception.ProduitExistantException;
import com.alten.ecommerce_api.exception.RessourceIntrouvableException;
import com.alten.ecommerce_api.model.dao.Produit;
import com.alten.ecommerce_api.model.dto.ProduitDto;
import com.alten.ecommerce_api.model.enumeration.StatutInventaire;
import com.alten.ecommerce_api.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service Produit")
class ServiceProduitTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ServiceProduit serviceProduit;

    private Produit produit;
    private ProduitDto produitDTO;

    @BeforeEach
    void setUp() {

        produit = Produit.builder()
                .code("TEST001")
                .nom("Produit Test")
                .prix(99.99)
                .quantite(10)
                .categorie("Test")
                .statutInventaire(StatutInventaire.INSTOCK)
                .build();

        produitDTO = ProduitDto.builder()
                .id(1L)
                .code("TEST001")
                .nom("Produit Test")
                .build();
    }

    @Test
    @DisplayName("Obtenir tous les produits - devrait retourner une page de produits")
    void obtenirTousLesProduits_devraitRetournerPageDeProduits() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Produit> pageProduits = new PageImpl<>(Arrays.asList(produit));
        when(produitRepository.findAll(pageable)).thenReturn(pageProduits);

        // When
        Page<ProduitDto> resultat = serviceProduit.obtenirTousLesProduits(pageable);

        // Then
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).hasSize(1);
        verify(produitRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Obtenir produit par ID - avec ID existant - devrait retourner le produit")
    void obtenirProduitParId_avecIdExistant_devraitRetournerProduit() {
        // Given
        produit.setId(1L);
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));

        // When
        ProduitDto resultat = serviceProduit.obtenirProduitParId(1L);

        // Then
        assertThat(resultat).isNotNull();
        assertThat(resultat.getId()).isEqualTo(1L);
        verify(produitRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Obtenir produit par ID - avec ID inexistant - devrait lever une exception")
    void obtenirProduitParId_avecIdInexistant_devraitLeverException() {
        // Given
        when(produitRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> serviceProduit.obtenirProduitParId(999L))
                .isInstanceOf(RessourceIntrouvableException.class)
                .hasMessageContaining("Produit non trouvé");

        verify(produitRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Créer produit - avec données valides - devrait créer le produit")
    void creerProduit_avecDonneesValides_devraitCreerProduit() {
        // Given
        ProduitDto nouveauProduitDto = ProduitDto.builder()
                .code("TEST001")
                .nom("Produit Test")
                .prix(99.99)
                .quantite(10)
                .categorie("Test")
                .statutInventaire(StatutInventaire.INSTOCK)
                .build();
        when(produitRepository.findByCode(nouveauProduitDto.getCode())).thenReturn(Optional.empty());
        when(produitRepository.save(produit)).thenReturn(produit);

        // When
        ProduitDto resultat = serviceProduit.creerProduit(nouveauProduitDto);

        // Then
        assertThat(resultat).isNotNull();
        assertThat(resultat.getCode()).isEqualTo("TEST001");
        verify(produitRepository, times(1)).save(produit);
    }

    @Test
    @DisplayName("Créer produit - avec code existant - devrait lever une exception")
    void creerProduit_avecCodeExistant_devraitLeverException() {
        // Given
        when(produitRepository.findByCode(produitDTO.getCode())).thenReturn(Optional.of(produit));

        // When & Then
        assertThatThrownBy(() -> serviceProduit.creerProduit(produitDTO))
                .isInstanceOf(ProduitExistantException.class)
                .hasMessageContaining("Un produit existe déjà avec le code");

        verify(produitRepository, never()).save(any(Produit.class));
    }

    @Test
    @DisplayName("Mettre à jour produit - avec ID valide - devrait mettre à jour le produit")
    void mettreAJourProduit_avecIdValide_devraitMettreAJourProduit() {
        // Given
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(produitRepository.save(produit)).thenReturn(produit);

        // When
        ProduitDto resultat = serviceProduit.mettreAJourProduit(1L, produitDTO);

        // Then
        assertThat(resultat).isNotNull();
        verify(produitRepository, times(1)).save(produit);
    }

    @Test
    @DisplayName("Supprimer produit - avec ID existant - devrait supprimer le produit")
    void supprimerProduit_avecIdExistant_devraitSupprimerProduit() {
        // Given
        when(produitRepository.existsById(1L)).thenReturn(true);

        // When
        serviceProduit.supprimerProduit(1L);

        // Then
        verify(produitRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Supprimer produit - avec ID inexistant - devrait lever une exception")
    void supprimerProduit_avecIdInexistant_devraitLeverException() {
        // Given
        when(produitRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> serviceProduit.supprimerProduit(999L))
                .isInstanceOf(RessourceIntrouvableException.class);

        verify(produitRepository, never()).deleteById(any(Long.class));
    }
}