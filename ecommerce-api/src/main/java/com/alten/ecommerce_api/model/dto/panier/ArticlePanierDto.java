package com.alten.ecommerce_api.model.dto.panier;

import com.alten.ecommerce_api.model.dto.ProduitDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticlePanierDto {
    private Long id;
    private ProduitDto produit;
    private Integer quantite;
    private Double sousTotal;
}