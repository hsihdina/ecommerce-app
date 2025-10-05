export interface Produit {
  id: number;
  code: string;
  nom: string;
  description: string;
  image: string;
  categorie: string;
  prix: number;
  quantite: number;
  referenceInterne: string;
  shellId: number;
  statutInventaire: 'INSTOCK' | 'LOWSTOCK' | 'OUTOFSTOCK';
  notation: number;
  creeLe: number;
  modifieLe: number;
}
