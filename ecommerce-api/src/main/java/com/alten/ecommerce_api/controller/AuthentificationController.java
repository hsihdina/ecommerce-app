package com.alten.ecommerce_api.controller;

import com.alten.ecommerce_api.model.dto.Authent.ConnexionDto;
import com.alten.ecommerce_api.model.dto.Authent.CreerCompteDto;
import com.alten.ecommerce_api.model.dto.Authent.TokenDto;
import com.alten.ecommerce_api.service.ServiceAuthentification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "API de gestion des comptes et authentification")
public class AuthentificationController {

    private final ServiceAuthentification serviceAuthentification;

    @PostMapping("/account")
    @Operation(summary = "Créer un nouveau compte utilisateur")
    public ResponseEntity<String> creerCompte(@Valid @RequestBody CreerCompteDto compteDto) {
        serviceAuthentification.creerCompte(compteDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Compte créé avec succès");
    }

    @PostMapping("/token")
    @Operation(summary = "Se connecter et obtenir un token JWT")
    public ResponseEntity<TokenDto> seConnecter(@Valid @RequestBody ConnexionDto connexionDto) {
        TokenDto token = serviceAuthentification.connexion(connexionDto);
        return ResponseEntity.ok(token);
    }
}
