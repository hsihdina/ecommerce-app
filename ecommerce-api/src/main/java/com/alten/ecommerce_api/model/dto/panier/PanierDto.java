package com.alten.ecommerce_api.model.dto.panier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanierDto {
    private Long id;
    private List<ArticlePanierDto> articles;
    private Double montantTotal;
}