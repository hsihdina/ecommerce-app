package com.alten.ecommerce_api.controller;

import com.alten.ecommerce_api.model.dao.Utilisateur;
import com.alten.ecommerce_api.model.dto.Authent.UtilisateurPrincipalDto;
import com.alten.ecommerce_api.model.dto.panier.AjouterArticlePanierDto;
import com.alten.ecommerce_api.model.dto.panier.ListeEnvieDto;
import com.alten.ecommerce_api.model.dto.panier.ModifierQuantiteDto;
import com.alten.ecommerce_api.model.dto.panier.PanierDto;
import com.alten.ecommerce_api.repository.UtilisateurRepository;
import com.alten.ecommerce_api.service.ServiceListeEnvie;
import com.alten.ecommerce_api.service.ServicePanier;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PanierController {

    private final ServicePanier servicePanier;
    private final ServiceListeEnvie serviceListeEnvie;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping("/panier")
    @Tag(name = "Panier")
    @Operation(summary = "Obtenir le panier de l'utilisateur connecté")
    public ResponseEntity<PanierDto> obtenirPanier(@AuthenticationPrincipal UtilisateurPrincipalDto utilisateurPrincipalDto) {
        Utilisateur utilisateur = obtenirUtilisateur(utilisateurPrincipalDto);
        PanierDto panier = servicePanier.obtenirPanier(utilisateur);
        return ResponseEntity.ok(panier);
    }

    @PostMapping("/panier/produits")
    @Tag(name = "Panier")
    @Operation(summary = "Ajouter un produit au panier")
    public ResponseEntity<PanierDto> ajouterArticle(
            @Valid @RequestBody AjouterArticlePanierDto articlePanierDto,
            @AuthenticationPrincipal UtilisateurPrincipalDto utilisateurPrincipalDto) {
        Utilisateur utilisateur = obtenirUtilisateur(utilisateurPrincipalDto);
        PanierDto panier = servicePanier.ajouterArticle(utilisateur, articlePanierDto);
        return ResponseEntity.ok(panier);
    }

    @PutMapping("/panier/produits/{articleId}")
    @Tag(name = "Panier")
    @Operation(summary = "Modifier la quantité d'un article dans le panier")
    public ResponseEntity<PanierDto> modifierQuantite(
            @PathVariable Long articleId,
            @Valid @RequestBody ModifierQuantiteDto quantiteDto,
            @AuthenticationPrincipal UtilisateurPrincipalDto utilisateurPrincipalDto) {
        Utilisateur utilisateur = obtenirUtilisateur(utilisateurPrincipalDto);
        PanierDto panier = servicePanier.modifierQuantite(utilisateur, articleId, quantiteDto.getQuantite());
        return ResponseEntity.ok(panier);
    }

    @DeleteMapping("/panier/produits/{articleId}")
    @Tag(name = "Panier")
    @Operation(summary = "Retirer un article du panier")
    public ResponseEntity<PanierDto> retirerArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UtilisateurPrincipalDto utilisateurPrincipalDto) {
        Utilisateur utilisateur = obtenirUtilisateur(utilisateurPrincipalDto);
        PanierDto panier = servicePanier.retirerArticle(utilisateur, articleId);
        return ResponseEntity.ok(panier);
    }

    @DeleteMapping("/panier")
    @Tag(name = "Panier")
    @Operation(summary = "Vider le panier")
    public ResponseEntity<Void> viderPanier(@AuthenticationPrincipal UtilisateurPrincipalDto utilisateurPrincipalDto) {
        Utilisateur utilisateur = obtenirUtilisateur(utilisateurPrincipalDto);
        servicePanier.viderPanier(utilisateur);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/liste-envie")
    @Tag(name = "Liste d'envie")
    @Operation(summary = "Obtenir la liste d'envie de l'utilisateur")
    public ResponseEntity<ListeEnvieDto> obtenirListeEnvie(@AuthenticationPrincipal UtilisateurPrincipalDto utilisateurPrincipalDto) {
        Utilisateur utilisateur = obtenirUtilisateur(utilisateurPrincipalDto);
        ListeEnvieDto listeEnvie = serviceListeEnvie.obtenirListeEnvie(utilisateur);
        return ResponseEntity.ok(listeEnvie);
    }

    @PostMapping("/liste-envie/produits/{produitId}")
    @Tag(name = "Liste d'envie")
    @Operation(summary = "Ajouter un produit à la liste d'envie")
    public ResponseEntity<ListeEnvieDto> ajouterProduitListeEnvie(
            @PathVariable Long produitId,
            @AuthenticationPrincipal UtilisateurPrincipalDto utilisateurPrincipalDto) {
        Utilisateur utilisateur = obtenirUtilisateur(utilisateurPrincipalDto);
        ListeEnvieDto listeEnvie = serviceListeEnvie.ajouterProduit(utilisateur, produitId);
        return ResponseEntity.ok(listeEnvie);
    }

    @DeleteMapping("/liste-envie/produits/{produitId}")
    @Tag(name = "Liste d'envie")
    @Operation(summary = "Retirer un produit de la liste d'envie")
    public ResponseEntity<ListeEnvieDto> retirerProduitListeEnvie(
            @PathVariable Long produitId,
            @AuthenticationPrincipal UtilisateurPrincipalDto utilisateurPrincipalDto) {
        Utilisateur utilisateur = obtenirUtilisateur(utilisateurPrincipalDto);
        ListeEnvieDto listeEnvie = serviceListeEnvie.retirerProduit(utilisateur, produitId);
        return ResponseEntity.ok(listeEnvie);
    }

    private Utilisateur obtenirUtilisateur(UtilisateurPrincipalDto utilisateurPrincipalDto) {
        return utilisateurRepository.findByEmail(utilisateurPrincipalDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}
