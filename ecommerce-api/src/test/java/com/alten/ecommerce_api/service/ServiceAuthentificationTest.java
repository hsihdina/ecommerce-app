package com.alten.ecommerce_api.service;

import com.alten.ecommerce_api.exception.CompteExistantException;
import com.alten.ecommerce_api.exception.CredentialsInvalidesException;
import com.alten.ecommerce_api.model.dao.Utilisateur;
import com.alten.ecommerce_api.model.dto.Authent.ConnexionDto;
import com.alten.ecommerce_api.model.dto.Authent.CreerCompteDto;
import com.alten.ecommerce_api.model.dto.Authent.TokenDto;
import com.alten.ecommerce_api.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service Authentification")
class ServiceAuthentificationTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ServiceJWT serviceJWT;

    @InjectMocks
    private ServiceAuthentification serviceAuthentification;

    private CreerCompteDto creerCompteDTO;
    private ConnexionDto connexionDTO;
    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        creerCompteDTO = CreerCompteDto.builder()
                .nomUtilisateur("testuser")
                .prenom("Test")
                .email("test@test.com")
                .motDePasse("password123")
                .build();

        connexionDTO = ConnexionDto.builder()
                .email("test@test.com")
                .motDePasse("password123")
                .build();

        utilisateur = Utilisateur.builder()
                .id(1L)
                .email("test@test.com")
                .motDePasse("encodedPassword")
                .build();
    }

    @Test
    @DisplayName("Créer compte - avec données valides - devrait créer le compte")
    void creerCompte_avecDonneesValides_devraitCreerCompte() {
        // Given
        when(utilisateurRepository.existsByEmail(creerCompteDTO.getEmail())).thenReturn(false);
        when(utilisateurRepository.existsByNomUtilisateur(creerCompteDTO.getNomUtilisateur())).thenReturn(false);
        when(passwordEncoder.encode(creerCompteDTO.getMotDePasse())).thenReturn("encodedPassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        // When
        serviceAuthentification.creerCompte(creerCompteDTO);

        // Then
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    @DisplayName("Créer compte - avec email existant - devrait lever une exception")
    void creerCompte_avecEmailExistant_devraitLeverException() {
        // Given
        when(utilisateurRepository.existsByEmail(creerCompteDTO.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> serviceAuthentification.creerCompte(creerCompteDTO))
                .isInstanceOf(CompteExistantException.class)
                .hasMessageContaining("Un compte existe déjà avec cet email");

        verify(utilisateurRepository, never()).save(any(Utilisateur.class));
    }

    @Test
    @DisplayName("Créer compte - avec nom d'utilisateur existant - devrait lever une exception")
    void creerCompte_avecNomUtilisateurExistant_devraitLeverException() {
        // Given
        when(utilisateurRepository.existsByEmail(creerCompteDTO.getEmail())).thenReturn(false);
        when(utilisateurRepository.existsByNomUtilisateur(creerCompteDTO.getNomUtilisateur())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> serviceAuthentification.creerCompte(creerCompteDTO))
                .isInstanceOf(CompteExistantException.class)
                .hasMessageContaining("Un compte existe déjà avec ce nom d'utilisateur");

        verify(utilisateurRepository, never()).save(any(Utilisateur.class));
    }

    @Test
    @DisplayName("Connexion - avec credentials valides - devrait retourner un token")
    void connexion_avecCredentialsValides_devraitRetournerToken() {
        // Given
        when(utilisateurRepository.findByEmail(connexionDTO.getEmail())).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches(connexionDTO.getMotDePasse(), utilisateur.getMotDePasse())).thenReturn(true);
        when(serviceJWT.genererToken(utilisateur.getEmail(), false)).thenReturn("jwt-token");

        // When
        TokenDto resultat = serviceAuthentification.connexion(connexionDTO);

        // Then
        assertThat(resultat).isNotNull();
        assertThat(resultat.getToken()).isEqualTo("jwt-token");
        assertThat(resultat.getEmail()).isEqualTo("test@test.com");
        verify(serviceJWT, times(1)).genererToken(utilisateur.getEmail(), false);
    }

    @Test
    @DisplayName("Connexion - avec email inexistant - devrait lever une exception")
    void connexion_avecEmailInexistant_devraitLeverException() {
        // Given
        when(utilisateurRepository.findByEmail(connexionDTO.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> serviceAuthentification.connexion(connexionDTO))
                .isInstanceOf(CredentialsInvalidesException.class)
                .hasMessageContaining("Email ou mot de passe incorrect");

        verify(serviceJWT, never()).genererToken(any(), anyBoolean());
    }

    @Test
    @DisplayName("Connexion - avec mot de passe incorrect - devrait lever une exception")
    void connexion_avecMotDePasseIncorrect_devraitLeverException() {
        // Given
        when(utilisateurRepository.findByEmail(connexionDTO.getEmail())).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches(connexionDTO.getMotDePasse(), utilisateur.getMotDePasse())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> serviceAuthentification.connexion(connexionDTO))
                .isInstanceOf(CredentialsInvalidesException.class)
                .hasMessageContaining("Email ou mot de passe incorrect");

        verify(serviceJWT, never()).genererToken(any(), anyBoolean());
    }

    @Test
    @DisplayName("Connexion admin - devrait retourner token avec flag admin")
    void connexionAdmin_devraitRetournerTokenAvecFlagAdmin() {
        // Given
        utilisateur.setEmail("admin@admin.com");
        connexionDTO.setEmail("admin@admin.com");

        when(utilisateurRepository.findByEmail(connexionDTO.getEmail())).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches(connexionDTO.getMotDePasse(), utilisateur.getMotDePasse())).thenReturn(true);
        when(serviceJWT.genererToken(utilisateur.getEmail(), true)).thenReturn("admin-jwt-token");

        // When
        TokenDto resultat = serviceAuthentification.connexion(connexionDTO);

        // Then
        assertThat(resultat).isNotNull();
        assertThat(resultat.isEstAdmin()).isTrue();
        verify(serviceJWT, times(1)).genererToken("admin@admin.com", true);
    }
}