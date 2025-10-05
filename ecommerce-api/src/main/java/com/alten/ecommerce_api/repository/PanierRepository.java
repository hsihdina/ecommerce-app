package com.alten.ecommerce_api.repository;

import com.alten.ecommerce_api.model.dao.Panier;
import com.alten.ecommerce_api.model.dao.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PanierRepository extends JpaRepository<Panier, Long> {

    Optional<Panier> findByUtilisateur(Utilisateur utilisateur);
}