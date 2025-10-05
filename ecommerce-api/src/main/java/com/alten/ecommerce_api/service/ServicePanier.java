package com.alten.ecommerce_api.service;

import com.alten.ecommerce_api.exception.RessourceIntrouvableException;
import com.alten.ecommerce_api.exception.StockInsuffisantException;
import com.alten.ecommerce_api.mapper.PanierMapper;
import com.alten.ecommerce_api.model.dao.ArticlePanier;
import com.alten.ecommerce_api.model.dao.Panier;
import com.alten.ecommerce_api.model.dao.Produit;
import com.alten.ecommerce_api.model.dao.Utilisateur;
import com.alten.ecommerce_api.model.dto.panier.AjouterArticlePanierDto;
import com.alten.ecommerce_api.model.dto.panier.PanierDto;
import com.alten.ecommerce_api.repository.ArticlePanierRepository;
import com.alten.ecommerce_api.repository.PanierRepository;
import com.alten.ecommerce_api.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ServicePanier {

    private final PanierRepository panierRepository;
    private final ArticlePanierRepository articlePanierRepository;
    private final ProduitRepository produitRepository;
    private final PanierMapper panierMapper;

    @Transactional(readOnly = true)
    public PanierDto obtenirPanier(Utilisateur utilisateur) {
        Panier panier = obtenirOuCreerPanier(utilisateur);
        return panierMapper.versPanierDTO(panier);
    }

    @Transactional
    public PanierDto ajouterArticle(Utilisateur utilisateur, AjouterArticlePanierDto articlePanierDto) {
        Panier panier = obtenirOuCreerPanier(utilisateur);
        Produit produit = produitRepository.findById(articlePanierDto.getProduitId())
                .orElseThrow(() -> new RessourceIntrouvableException("Produit non trouvé"));

        // Vérifier le stock
        if (produit.getQuantite() < articlePanierDto.getQuantite()) {
            throw new StockInsuffisantException("Stock insuffisant pour le produit : " + produit.getNom());
        }

        // Vérifier si l'article existe déjà dans le panier
        ArticlePanier articleExistant = articlePanierRepository
                .findByPanierAndProduitId(panier, articlePanierDto.getProduitId())
                .orElse(null);

        if (Objects.nonNull(articleExistant)) {
            int nouvelleQuantite = articleExistant.getQuantite() + articlePanierDto.getQuantite();
            if (produit.getQuantite() < nouvelleQuantite) {
                throw new StockInsuffisantException("Stock insuffisant pour le produit : " + produit.getNom());
            }
            articleExistant.setQuantite(nouvelleQuantite);
            articlePanierRepository.save(articleExistant);
        } else {
            ArticlePanier nouvelArticle = ArticlePanier.builder()
                    .panier(panier)
                    .produit(produit)
                    .quantite(articlePanierDto.getQuantite())
                    .build();

            panier.ajouterArticle(nouvelArticle);
            articlePanierRepository.save(nouvelArticle);
        }

        return panierMapper.versPanierDTO(panierRepository.save(panier));
    }

    @Transactional
    public PanierDto modifierQuantite(Utilisateur utilisateur, Long articleId, Integer quantite) {
        Panier panier = obtenirOuCreerPanier(utilisateur);
        ArticlePanier article = articlePanierRepository.findById(articleId)
                .orElseThrow(() -> new RessourceIntrouvableException("Article non trouvé dans le panier"));

        if (!article.getPanier().getId().equals(panier.getId())) {
            throw new RessourceIntrouvableException("Article non trouvé dans votre panier");
        }

        if (article.getProduit().getQuantite() < quantite) {
            throw new StockInsuffisantException("Stock insuffisant");
        }

        article.setQuantite(quantite);
        articlePanierRepository.save(article);

        return panierMapper.versPanierDTO(panier);
    }

    @Transactional
    public PanierDto retirerArticle(Utilisateur utilisateur, Long articleId) {
        Panier panier = obtenirOuCreerPanier(utilisateur);
        ArticlePanier article = articlePanierRepository.findById(articleId)
                .orElseThrow(() -> new RessourceIntrouvableException("Article non trouvé dans le panier"));

        if (!article.getPanier().getId().equals(panier.getId())) {
            throw new RessourceIntrouvableException("Article non trouvé dans votre panier");
        }

        panier.retirerArticle(article);
        articlePanierRepository.delete(article);

        return panierMapper.versPanierDTO(panier);
    }

    @Transactional
    public void viderPanier(Utilisateur utilisateur) {
        Panier panier = obtenirOuCreerPanier(utilisateur);
        panier.getArticles().clear();
        panierRepository.save(panier);
    }

    private Panier obtenirOuCreerPanier(Utilisateur utilisateur) {
        return panierRepository.findByUtilisateur(utilisateur)
                .orElseGet(() -> {
                    Panier nouveauPanier = new Panier();
                    nouveauPanier.setUtilisateur(utilisateur);
                    return panierRepository.save(nouveauPanier);
                });
    }
}