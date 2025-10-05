package com.alten.ecommerce_api.model.dao;

import com.alten.ecommerce_api.model.enumeration.StatutInventaire;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "PRODUITS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String nom;

    @Column(length = 1000)
    private String description;

    private String image;

    @Column(nullable = false)
    private String categorie;

    @Column(nullable = false)
    private Double prix;

    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "reference_interne")
    private String referenceInterne;

    @Column(name = "shell_id")
    private Long shellId;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_inventaire", nullable = false)
    private StatutInventaire statutInventaire;

    private Double notation;

    @Column(name = "cree_le", nullable = false, updatable = false)
    private Long creeLe;

    @Column(name = "modifie_le")
    private Long modifieLe;

    @PrePersist
    protected void onCreate() {
        creeLe = Instant.now().toEpochMilli();
        modifieLe = creeLe;
    }

    @PreUpdate
    protected void onUpdate() {
        modifieLe = Instant.now().toEpochMilli();
    }
}