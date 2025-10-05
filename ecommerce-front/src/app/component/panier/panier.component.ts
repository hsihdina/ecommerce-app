import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {PanierService} from '../../service/panier.service';
import {Panier} from '../../model/Panier/Panier';
import {ArticlePanier} from '../../model/Panier/ArticlePanier';

@Component({
  selector: 'app-panier',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './panier.component.html',
  styleUrls: ['./panier.component.css']
})
export class PanierComponent implements OnInit {
  panier: Panier | null = null;
  chargement = false;

  constructor(private panierService: PanierService) {}

  ngOnInit(): void {
    this.chargerPanier();
  }

  chargerPanier(): void {
    this.chargement = true;
    this.panierService.obtenirPanier().subscribe({
      next: (panier) => {
        this.panier = panier;
        this.chargement = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement du panier:', error);
        this.chargement = false;
      }
    });
  }

  modifierQuantite(article: ArticlePanier, nouvelleQuantite: number): void {
    if (nouvelleQuantite < 1) {
      return;
    }

    if (nouvelleQuantite > article.produit.quantite) {
      alert('Stock insuffisant');
      return;
    }

    this.panierService.modifierQuantite(article.id, nouvelleQuantite).subscribe({
      next: (panier) => {
        this.panier = panier;
      },
      error: (error) => {
        console.error('Erreur lors de la modification:', error);
        alert('Erreur lors de la modification de la quantité');
      }
    });
  }

  retirerArticle(article: ArticlePanier): void {
    if (confirm(`Voulez-vous retirer ${article.produit.nom} du panier ?`)) {
      this.panierService.retirerArticle(article.id).subscribe({
        next: (panier) => {
          this.panier = panier;
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          alert('Erreur lors de la suppression de l\'article');
        }
      });
    }
  }

  viderPanier(): void {
    if (confirm('Voulez-vous vraiment vider le panier ?')) {
      this.panierService.viderPanier().subscribe({
        next: () => {
          this.panier = null;
        },
        error: (error) => {
          console.error('Erreur lors du vidage du panier:', error);
          alert('Erreur lors du vidage du panier');
        }
      });
    }
  }

  obtenirNombreArticles(): number {
    return this.panier ? this.panier.articles.reduce((total, article) => total + article.quantite, 0) : 0;
  }
}
