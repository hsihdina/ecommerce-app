package com.alten.ecommerce_api.model.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paniers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticlePanier> articles = new ArrayList<>();

    public void ajouterArticle(ArticlePanier article) {
        articles.add(article);
        article.setPanier(this);
    }

    public void retirerArticle(ArticlePanier article) {
        articles.remove(article);
        article.setPanier(null);
    }
}