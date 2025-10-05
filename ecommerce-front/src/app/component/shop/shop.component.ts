import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ProduitService} from '../../service/produit.service';
import {PanierService} from '../../service/panier.service';
import {Produit} from '../../model/Produit';
import {PageProduits} from '../../model/Panier/PageProduits';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-shop',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './shop.component.html',
  styleUrls: ['./shop.component.css']
})
export class ShopComponent implements OnInit {
  produits: Produit[] = [];
  pageActuelle = 0;
  taillePage = 10;
  totalPages = 0;
  totalElements = 0;

  categorieSelectionnee = '';
  recherche = '';
  categories: string[] = [];

  chargement = false;
  nombreArticlesPanier = 0;

  constructor(
    private produitService: ProduitService,
    private panierService: PanierService
  ) {}

  ngOnInit(): void {
    this.chargerProduits();
    this.chargerPanier();

    this.panierService.panier$.subscribe(panier => {
      if (panier) {
        this.nombreArticlesPanier = panier.articles.reduce(
          (total, article) => total + article.quantite,
          0
        );
      }
    });
  }

  chargerProduits(): void {
    this.chargement = true;
    this.produitService.obtenirProduits(this.pageActuelle, this.taillePage, this.categorieSelectionnee || undefined, this.recherche || undefined
    ).subscribe({
      next: (page: PageProduits) => {
        this.produits = page.content;
        this.totalPages = page.totalPages;
        this.totalElements = page.totalElements;
        this.extraireCategories();
        this.chargement = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des produits:', error);
        this.chargement = false;
      }
    });
  }

  chargerPanier(): void {
    this.panierService.obtenirPanier().subscribe();
  }

  extraireCategories(): void {
    const categoriesUniques = new Set(this.produits.map(p => p.categorie));
    this.categories = Array.from(categoriesUniques).sort();
  }

  ajouterAuPanier(produit: Produit, quantite: number = 1): void {
    if (produit.quantite < quantite) {
      alert('Stock insuffisant');
      return;
    }

    this.panierService.ajouterArticle({
      produitId: produit.id,
      quantite: quantite
    }).subscribe({
      next: () => {
        alert(`${produit.nom} ajouté au panier`);
      },
      error: (error) => {
        console.error('Erreur lors de lajout au panier:', error);
        alert('Erreur lors de lajout au panier');
      }
    });
  }

  filtrerParCategorie(): void {
    this.pageActuelle = 0;
    this.chargerProduits();
  }

  rechercher(): void {
    this.pageActuelle = 0;
    this.chargerProduits();
  }

  changerPage(page: number): void {
    this.pageActuelle = page;
    this.chargerProduits();
  }

  obtenirStatutClasse(statut: string): string {
    switch (statut) {
      case 'INSTOCK': return 'en-stock';
      case 'LOWSTOCK': return 'stock-faible';
      case 'OUTOFSTOCK': return 'rupture-stock';
      default: return '';
    }
  }

  obtenirStatutTexte(statut: string): string {
    switch (statut) {
      case 'INSTOCK': return 'En stock';
      case 'LOWSTOCK': return 'Stock faible';
      case 'OUTOFSTOCK': return 'Rupture';
      default: return statut;
    }
  }

  obtenirEtoiles(notation: number): string[] {
    const etoiles = [];
    for (let i = 1; i <= 5; i++) {
      if (i <= notation) {
        etoiles.push('★');
      } else if (i - 0.5 <= notation) {
        etoiles.push('½');
      } else {
        etoiles.push('☆');
      }
    }
    return etoiles;
  }
}
