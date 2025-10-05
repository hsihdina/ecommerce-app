import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PageProduits} from '../model/Panier/PageProduits';
import {Produit} from '../model/Produit';

@Injectable({
  providedIn: 'root'
})
export class ProduitService {
  private apiUrl = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  obtenirProduits(page: number = 0, taille: number = 10, categorie?: string, recherche?:
  string, tri: string = 'id', direction: string = 'ASC'): Observable<PageProduits> {

    let params = new HttpParams()
      .set('page', page.toString())
      .set('taille', taille.toString())
      .set('tri', tri)
      .set('direction', direction);

    if (categorie) {
      params = params.set('categorie', categorie);
    }
    if (recherche) {
      params = params.set('critereDerecherche', recherche);
    }

    return this.http.get<PageProduits>(this.apiUrl, { params });
  }

  obtenirProduit(id: number): Observable<Produit> {
    return this.http.get<Produit>(`${this.apiUrl}/${id}`);
  }

  creerProduit(produit: Partial<Produit>): Observable<Produit> {
    return this.http.post<Produit>(this.apiUrl, produit);
  }

  mettreAJourProduit(id: number, produit: Partial<Produit>): Observable<Produit> {
    return this.http.put<Produit>(`${this.apiUrl}/${id}`, produit);
  }

  supprimerProduit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
