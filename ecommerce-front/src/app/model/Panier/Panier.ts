import {ArticlePanier} from './ArticlePanier';

export interface Panier {
  id: number;
  articles: ArticlePanier[];
  montantTotal: number;
}
