import {Produit} from '../Produit';

export interface ArticlePanier {
  id: number;
  produit: Produit;
  quantite: number;
  sousTotal: number;
}
