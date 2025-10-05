package com.alten.ecommerce_api.model.dto;

import com.alten.ecommerce_api.model.enumeration.StatutInventaire;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduitDto {

    private Long id;

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    private String image;

    @NotBlank(message = "La catégorie est obligatoire")
    private String categorie;

    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être positif")
    private Double prix;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private Integer quantite;

    private String referenceInterne;

    private Long shellId;

    @NotNull(message = "Le statut d'inventaire est obligatoire")
    private StatutInventaire statutInventaire;

    @Min(value = 0, message = "La notation ne peut pas être négative")
    @Max(value = 5, message = "La notation ne peut pas dépasser 5")
    private Double notation;

    private Long creeLe;

    private Long modifieLe;
}