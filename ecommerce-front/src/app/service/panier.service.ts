// src/app/services/panier.service.ts
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import {Panier} from '../model/Panier/Panier';
import {AjouterArticlePanier} from '../model/Panier/AjouterArticlePanier';

@Injectable({
  providedIn: 'root'
})
export class PanierService {
  private apiUrl = 'http://localhost:8080/api/panier';
  private panierSubject = new BehaviorSubject<Panier | null>(null);
  public panier$ = this.panierSubject.asObservable();

  constructor(private http: HttpClient) {}

  obtenirPanier(): Observable<Panier> {
    return this.http.get<Panier>(this.apiUrl).pipe(
      tap(panier => this.panierSubject.next(panier))
    );
  }

  ajouterArticle(article: AjouterArticlePanier): Observable<Panier> {
    return this.http.post<Panier>(`${this.apiUrl}/produits`, article).pipe(
      tap(panier => this.panierSubject.next(panier))
    );
  }

  modifierQuantite(articleId: number, quantite: number): Observable<Panier> {
    return this.http.put<Panier>(
      `${this.apiUrl}/produits/${articleId}`,
      { quantite }
    ).pipe(
      tap(panier => this.panierSubject.next(panier))
    );
  }

  retirerArticle(articleId: number): Observable<Panier> {
    return this.http.delete<Panier>(`${this.apiUrl}/produits/${articleId}`).pipe(
      tap(panier => this.panierSubject.next(panier))
    );
  }

  viderPanier(): Observable<void> {
    return this.http.delete<void>(this.apiUrl).pipe(
      tap(() => this.panierSubject.next(null))
    );
  }
}
