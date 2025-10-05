package com.alten.ecommerce_api.service;

import com.alten.ecommerce_api.exception.ProduitExistantException;
import com.alten.ecommerce_api.exception.RessourceIntrouvableException;
import com.alten.ecommerce_api.mapper.ProduitMapper;
import com.alten.ecommerce_api.model.dao.Produit;
import com.alten.ecommerce_api.model.dto.ProduitDto;
import com.alten.ecommerce_api.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ServiceProduit {

    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper = Mappers.getMapper(ProduitMapper.class);

    @Transactional(readOnly = true)
    public Page<ProduitDto> obtenirTousLesProduits(Pageable pageable) {
        return produitRepository.findAll(pageable)
                .map(produitMapper::versProduitDTO);
    }

    @Transactional(readOnly = true)
    public ProduitDto obtenirProduitParId(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Produit non trouvé avec l'ID : " + id));
        return produitMapper.versProduitDTO(produit);
    }

    @Transactional(readOnly = true)
    public Page<ProduitDto> filtrerProduits(String categorie, String critereDerecherche, Pageable pageable) {
        return produitRepository.filtrerProduits(categorie, critereDerecherche, pageable)
                .map(produitMapper::versProduitDTO);
    }

    @Transactional
    public ProduitDto creerProduit(ProduitDto produitDto) {
        if (produitRepository.findByCode(produitDto.getCode()).isPresent()) {
            throw new ProduitExistantException("Un produit existe déjà avec le code : " + produitDto.getCode());
        }

        Produit produit = produitMapper.versProduit(produitDto);
        Produit produitSauvegarde = produitRepository.save(produit);
        return produitMapper.versProduitDTO(produitSauvegarde);
    }

    @Transactional
    public ProduitDto mettreAJourProduit(Long id, ProduitDto produitDto) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Produit non trouvé avec l'ID : " + id));

        if (nonNull(produitDto.getCode())) produit.setCode(produitDto.getCode());
        if (nonNull(produitDto.getNom())) produit.setNom(produitDto.getNom());
        if (nonNull(produitDto.getDescription())) produit.setDescription(produitDto.getDescription());
        if (nonNull(produitDto.getImage())) produit.setImage(produitDto.getImage());
        if (nonNull(produitDto.getCategorie())) produit.setCategorie(produitDto.getCategorie());
        if (nonNull(produitDto.getPrix())) produit.setPrix(produitDto.getPrix());
        if (nonNull(produitDto.getQuantite())) produit.setQuantite(produitDto.getQuantite());
        if (nonNull(produitDto.getReferenceInterne())) produit.setReferenceInterne(produitDto.getReferenceInterne());
        if (nonNull(produitDto.getShellId())) produit.setShellId(produitDto.getShellId());
        if (nonNull(produitDto.getStatutInventaire())) produit.setStatutInventaire(produitDto.getStatutInventaire());
        if (nonNull(produitDto.getNotation())) produit.setNotation(produitDto.getNotation());

        Produit produitMisAJour = produitRepository.save(produit);
        return produitMapper.versProduitDTO(produitMisAJour);
    }

    @Transactional
    public void supprimerProduit(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new RessourceIntrouvableException("Produit non trouvé avec l'ID : " + id);
        }
        produitRepository.deleteById(id);
    }
}
