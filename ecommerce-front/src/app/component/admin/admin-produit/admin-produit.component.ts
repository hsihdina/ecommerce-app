import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Produit} from '../../../model/Produit';
import {ProduitService} from '../../../service/produit.service';

@Component({
  selector: 'app-admin-produit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-produit.component.html',
  styleUrls: ['./admin-produit.component.css']
})
export class AdminProduitComponent implements OnInit {
  produits: Produit[] = [];
  chargement = false;
  modeEdition = false;
  modeCreation = false;

  produitEnCours: Partial<Produit> = {};

  categories = ['Informatique', 'Accessoires', 'Audio', 'Moniteurs', 'Stockage', 'Périphériques', 'Tablettes'];
  statutsInventaire = ['INSTOCK', 'LOWSTOCK', 'OUTOFSTOCK'];

  constructor(private produitService: ProduitService) {}

  ngOnInit(): void {
    this.chargerProduits();
  }

  chargerProduits(): void {
    this.chargement = true;
    this.produitService.obtenirProduits(0, 100).subscribe({
      next: (page) => {
        this.produits = page.content;
        this.chargement = false;
      },
      error: (error) => {
        console.error('Erreur:', error);
        alert('Erreur lors du chargement des produits');
        this.chargement = false;
      }
    });
  }

  nouveauProduit(): void {
    this.modeCreation = true;
    this.modeEdition = false;
    this.produitEnCours = {
      code: '',
      nom: '',
      description: '',
      image: 'https://picsum.photos/200/300',
      categorie: 'Informatique',
      prix: 0,
      quantite: 0,
      statutInventaire: 'INSTOCK',
      notation: 0
    };
  }

  editerProduit(produit: Produit): void {
    this.modeEdition = true;
    this.modeCreation = false;
    this.produitEnCours = { ...produit };
  }

  annuler(): void {
    this.modeEdition = false;
    this.modeCreation = false;
    this.produitEnCours = {};
  }

  sauvegarder(): void {
    if (this.modeCreation) {
      this.creer();
    } else if (this.modeEdition) {
      this.mettreAJour();
    }
  }

  creer(): void {
    this.produitService.creerProduit(this.produitEnCours).subscribe({
      next: () => {
        alert('Produit créé avec succès');
        this.annuler();
        this.chargerProduits();
      },
      error: (error) => {
        console.error('Erreur:', error);
        alert('Erreur lors de la création: ' + (error.error?.message || 'Erreur inconnue'));
      }
    });
  }

  mettreAJour(): void {
    const id = (this.produitEnCours as Produit).id;
    this.produitService.mettreAJourProduit(id, this.produitEnCours as Partial<Produit>).subscribe({
      next: () => {
        alert('Produit mis à jour avec succès');
        this.annuler();
        this.chargerProduits();
      },
      error: (error) => {
        console.error('Erreur:', error);
        alert('Erreur lors de la mise à jour');
      }
    });
  }

  supprimer(produit: Produit): void {
    if (confirm(`Voulez-vous vraiment supprimer ${produit.nom} ?`)) {
      this.produitService.supprimerProduit(produit.id).subscribe({
        next: () => {
          alert('Produit supprimé avec succès');
          this.chargerProduits();
        },
        error: (error) => {
          console.error('Erreur:', error);
          alert('Erreur lors de la suppression');
        }
      });
    }
  }

  obtenirStatutClasse(statut: string): string {
    switch (statut) {
      case 'INSTOCK': return 'en-stock';
      case 'LOWSTOCK': return 'stock-faible';
      case 'OUTOFSTOCK': return 'rupture-stock';
      default: return '';
    }
  }
}
