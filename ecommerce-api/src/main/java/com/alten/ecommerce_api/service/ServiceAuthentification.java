package com.alten.ecommerce_api.service;

import com.alten.ecommerce_api.exception.CompteExistantException;
import com.alten.ecommerce_api.exception.CredentialsInvalidesException;
import com.alten.ecommerce_api.model.dao.Panier;
import com.alten.ecommerce_api.model.dao.Utilisateur;
import com.alten.ecommerce_api.model.dto.Authent.ConnexionDto;
import com.alten.ecommerce_api.model.dto.Authent.CreerCompteDto;
import com.alten.ecommerce_api.model.dto.Authent.TokenDto;
import com.alten.ecommerce_api.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServiceAuthentification {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceJWT serviceJWT;

    @Transactional
    public void creerCompte(CreerCompteDto compteDto) {
        if (utilisateurRepository.existsByEmail(compteDto.getEmail())) {
            throw new CompteExistantException("Un compte existe déjà avec cet email");
        }

        if (utilisateurRepository.existsByNomUtilisateur(compteDto.getNomUtilisateur())) {
            throw new CompteExistantException("Un compte existe déjà avec ce nom d'utilisateur");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nomUtilisateur(compteDto.getNomUtilisateur())
                .prenom(compteDto.getPrenom())
                .email(compteDto.getEmail())
                .motDePasse(passwordEncoder.encode(compteDto.getMotDePasse()))
                .build();

        // Créer un panier pour l'utilisateur
        Panier panier = new Panier();
        panier.setUtilisateur(utilisateur);
        utilisateur.setPanier(panier);

        utilisateurRepository.save(utilisateur);
    }

    public TokenDto connexion(ConnexionDto connexionDto) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(connexionDto.getEmail())
                .orElseThrow(() -> new CredentialsInvalidesException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(connexionDto.getMotDePasse(), utilisateur.getMotDePasse())) {
            throw new CredentialsInvalidesException("Email ou mot de passe incorrect");
        }

        String token = serviceJWT.genererToken(utilisateur.getEmail(), utilisateur.estAdmin());

        return new TokenDto(token, utilisateur.getEmail(), utilisateur.estAdmin());
    }
}
