package com.alten.ecommerce_api.repository;

import com.alten.ecommerce_api.model.dao.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    Optional<Produit> findByCode(String code);

    @Query("SELECT p FROM Produit p WHERE " +
            "LOWER(p.nom) LIKE LOWER(CONCAT('%', :critereDerecherche, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :critereDerecherche, '%')) OR " +
            "LOWER(p.categorie) LIKE LOWER(CONCAT('%', :critereDerecherche, '%'))")
    Page<Produit> rechercherProduits(@Param("critereDerecherche") String critereDerecherche, Pageable pageable);

    @Query("SELECT p FROM Produit p WHERE " +
            "(:categorie IS NULL OR p.categorie = :categorie) AND " +
            "(:critereDerecherche IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :critereDerecherche, '%')))")
    Page<Produit> filtrerProduits(
            @Param("categorie") String categorie,
            @Param("critereDerecherche") String critereDerecherche,
            Pageable pageable
    );
}