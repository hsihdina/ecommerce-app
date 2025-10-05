package com.alten.ecommerce_api.controller;

import com.alten.ecommerce_api.exception.AccesDeniException;
import com.alten.ecommerce_api.model.dto.Authent.UtilisateurPrincipalDto;
import com.alten.ecommerce_api.model.dto.ProduitDto;
import com.alten.ecommerce_api.service.ServiceProduit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Produits", description = "API de gestion des produits")
public class ProduitController {

    private final ServiceProduit serviceProduit;

    @GetMapping
    @Operation(summary = "Obtenir la liste des produits avec pagination et filtres")
    public ResponseEntity<Page<ProduitDto>> obtenirProduits(
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) String critereDerecherche,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int taille,
            @RequestParam(defaultValue = "id") String tri,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction directionTri = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, taille, Sort.by(directionTri, tri));

        Page<ProduitDto> produits;
        if (Objects.nonNull(categorie) || Objects.nonNull(critereDerecherche)) {
            produits = serviceProduit.filtrerProduits(categorie, critereDerecherche, pageable);
        } else {
            produits = serviceProduit.obtenirTousLesProduits(pageable);
        }

        return ResponseEntity.ok(produits);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un produit par son ID")
    public ResponseEntity<ProduitDto> obtenirProduit(@PathVariable Long id) {
        ProduitDto produit = serviceProduit.obtenirProduitParId(id);
        return ResponseEntity.ok(produit);
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau produit (admin uniquement)")
    public ResponseEntity<ProduitDto> creerProduit(
            @Valid @RequestBody ProduitDto dto,
            @AuthenticationPrincipal UtilisateurPrincipalDto utilisateurPrincipalDto) {
        verifierAdmin(utilisateurPrincipalDto);
        ProduitDto produit = serviceProduit.creerProduit(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produit);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un produit (admin uniquement)")
    public ResponseEntity<ProduitDto> mettreAJourProduit(
            @PathVariable Long id,
            @RequestBody ProduitDto dto,
            @AuthenticationPrincipal UtilisateurPrincipalDto utilisateurPrincipalDto) {
        verifierAdmin(utilisateurPrincipalDto);
        ProduitDto produit = serviceProduit.mettreAJourProduit(id, dto);
        return ResponseEntity.ok(produit);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre à jour partiellement un produit (admin uniquement)")
    public ResponseEntity<ProduitDto> mettreAJourPartiellement(
            @PathVariable Long id,
            @RequestBody ProduitDto dto,
            @AuthenticationPrincipal UtilisateurPrincipalDto utilisateurPrincipalDto) {
        verifierAdmin(utilisateurPrincipalDto);
        ProduitDto produit = serviceProduit.mettreAJourProduit(id, dto);
        return ResponseEntity.ok(produit);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit (admin uniquement)")
    public ResponseEntity<Void> supprimerProduit(
            @PathVariable Long id,
            @AuthenticationPrincipal UtilisateurPrincipalDto utilisateurPrincipalDto) {
        verifierAdmin(utilisateurPrincipalDto);
        serviceProduit.supprimerProduit(id);
        return ResponseEntity.noContent().build();
    }

    private void verifierAdmin(UtilisateurPrincipalDto utilisateurPrincipalDto) {
        if (Objects.isNull(utilisateurPrincipalDto) || !Boolean.TRUE.equals(utilisateurPrincipalDto.getEstAdmin())) {
            throw new AccesDeniException("Seul l'administrateur peut effectuer cette action");
        }
    }
}
