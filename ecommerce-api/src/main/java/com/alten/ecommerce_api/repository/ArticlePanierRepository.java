package com.alten.ecommerce_api.repository;

import com.alten.ecommerce_api.model.dao.ArticlePanier;
import com.alten.ecommerce_api.model.dao.Panier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticlePanierRepository extends JpaRepository<ArticlePanier, Long> {

    Optional<ArticlePanier> findByPanierAndProduitId(Panier panier, Long produitId);
}
