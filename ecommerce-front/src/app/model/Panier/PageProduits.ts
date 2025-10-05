import {Produit} from '../Produit';

export interface PageProduits {
  content: Produit[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
