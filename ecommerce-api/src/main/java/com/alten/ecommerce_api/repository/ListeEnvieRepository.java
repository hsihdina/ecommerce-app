package com.alten.ecommerce_api.repository;

import com.alten.ecommerce_api.model.dao.ListeEnvie;
import com.alten.ecommerce_api.model.dao.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListeEnvieRepository extends JpaRepository<ListeEnvie, Long> {

    List<ListeEnvie> findByUtilisateur(Utilisateur utilisateur);

    Optional<ListeEnvie> findByUtilisateurAndProduitId(Utilisateur utilisateur, Long produitId);

    boolean existsByUtilisateurAndProduitId(Utilisateur utilisateur, Long produitId);
}