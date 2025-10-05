package com.alten.ecommerce_api.service;

import com.alten.ecommerce_api.exception.RessourceExistanteException;
import com.alten.ecommerce_api.exception.RessourceIntrouvableException;
import com.alten.ecommerce_api.mapper.PanierMapper;
import com.alten.ecommerce_api.model.dao.ListeEnvie;
import com.alten.ecommerce_api.model.dao.Produit;
import com.alten.ecommerce_api.model.dao.Utilisateur;
import com.alten.ecommerce_api.model.dto.panier.ListeEnvieDto;
import com.alten.ecommerce_api.repository.ListeEnvieRepository;
import com.alten.ecommerce_api.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceListeEnvie {

    private final ListeEnvieRepository listeEnvieRepository;
    private final ProduitRepository produitRepository;
    private final PanierMapper panierMapper;

    @Transactional(readOnly = true)
    public ListeEnvieDto obtenirListeEnvie(Utilisateur utilisateur) {
        List<ListeEnvie> listeEnvies = listeEnvieRepository.findByUtilisateur(utilisateur);
        return panierMapper.versListeEnvieDTO(listeEnvies);
    }

    @Transactional
    public ListeEnvieDto ajouterProduit(Utilisateur utilisateur, Long produitId) {
        if (listeEnvieRepository.existsByUtilisateurAndProduitId(utilisateur, produitId)) {
            throw new RessourceExistanteException("Le produit est déjà dans votre liste d'envie");
        }

        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RessourceIntrouvableException("Produit non trouvé"));

        ListeEnvie listeEnvie = new ListeEnvie();
        listeEnvie.setUtilisateur(utilisateur);
        listeEnvie.setProduit(produit);
        listeEnvieRepository.save(listeEnvie);

        List<ListeEnvie> listeEnvies = listeEnvieRepository.findByUtilisateur(utilisateur);
        return panierMapper.versListeEnvieDTO(listeEnvies);
    }

    @Transactional
    public ListeEnvieDto retirerProduit(Utilisateur utilisateur, Long produitId) {
        ListeEnvie listeEnvie = listeEnvieRepository
                .findByUtilisateurAndProduitId(utilisateur, produitId)
                .orElseThrow(() -> new RessourceIntrouvableException("Produit non trouvé dans votre liste d'envie"));

        listeEnvieRepository.delete(listeEnvie);

        List<ListeEnvie> listeEnvies = listeEnvieRepository.findByUtilisateur(utilisateur);
        return panierMapper.versListeEnvieDTO(listeEnvies);
    }
}