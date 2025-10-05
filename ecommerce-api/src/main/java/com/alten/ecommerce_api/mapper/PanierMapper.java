package com.alten.ecommerce_api.mapper;

import com.alten.ecommerce_api.model.dao.ArticlePanier;
import com.alten.ecommerce_api.model.dao.ListeEnvie;
import com.alten.ecommerce_api.model.dao.Panier;
import com.alten.ecommerce_api.model.dto.panier.ArticlePanierDto;
import com.alten.ecommerce_api.model.dto.panier.ListeEnvieDto;
import com.alten.ecommerce_api.model.dto.panier.PanierDto;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PanierMapper {

    private final ProduitMapper produitMapper = Mappers.getMapper(ProduitMapper.class);

    public PanierDto versPanierDTO(Panier panier) {
        if (Objects.isNull(panier)) {
            return null;
        }

        List<ArticlePanierDto> articlesDTO = panier.getArticles().stream()
                .map(this::versArticlePanierDTO)
                .collect(Collectors.toList());

        double montantTotal = articlesDTO.stream()
                .mapToDouble(ArticlePanierDto::getSousTotal)
                .sum();

        return new PanierDto(panier.getId(), articlesDTO, montantTotal);
    }

    public ArticlePanierDto versArticlePanierDTO(ArticlePanier article) {
        if (Objects.isNull(article)) {
            return null;
        }

        return new ArticlePanierDto(
                article.getId(),
                produitMapper.versProduitDTO(article.getProduit()),
                article.getQuantite(),
                article.getProduit().getPrix() * article.getQuantite()
        );
    }

    public ListeEnvieDto versListeEnvieDTO(List<ListeEnvie> listeEnvies) {
        if (Objects.isNull(listeEnvies) || listeEnvies.isEmpty()) {
            return new ListeEnvieDto(null, List.of());
        }

        return new ListeEnvieDto(
                listeEnvies.get(0).getUtilisateur().getId(),
                listeEnvies.stream()
                        .map(le -> produitMapper.versProduitDTO(le.getProduit()))
                        .collect(Collectors.toList())
        );
    }
}