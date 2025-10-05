package com.alten.ecommerce_api.model.dto.panier;

import com.alten.ecommerce_api.model.dto.ProduitDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListeEnvieDto {
    private Long id;
    private List<ProduitDto> produits;
}