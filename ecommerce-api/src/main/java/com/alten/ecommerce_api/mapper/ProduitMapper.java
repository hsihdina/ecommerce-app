package com.alten.ecommerce_api.mapper;

import com.alten.ecommerce_api.model.dao.Produit;
import com.alten.ecommerce_api.model.dto.ProduitDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProduitMapper {

    @Mapping(target = "creeLe", source = "creeLe")
    @Mapping(target = "modifieLe", source = "modifieLe")
    ProduitDto versProduitDTO(Produit produit);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "modifieLe", ignore = true)
    Produit versProduit(ProduitDto creerProduitDTO);
}