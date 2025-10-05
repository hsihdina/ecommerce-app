import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable, tap} from 'rxjs';
import {Router} from '@angular/router';
import {Connexion} from '../model/auth/Connexion';
import {Token} from '../model/auth/Token';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';
  private tokenSubject = new BehaviorSubject<Token | null>(this.getTokenFromStorage());
  public token$ = this.tokenSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  seConnecter(credentials: Connexion): Observable<Token> {
    return this.http.post<Token>(`${this.apiUrl}/token`, credentials).pipe(
      tap(token => {
        this.sauvegarderToken(token);
        this.tokenSubject.next(token);
      })
    );
  }

  seDeconnecter(): void {
    localStorage.removeItem('token');
    this.tokenSubject.next(null);
    this.router.navigate(['/connexion']);
  }

  obtenirToken(): string | null {
    const tokenData = this.getTokenFromStorage();
    return tokenData ? tokenData.token : null;
  }

  estConnecte(): boolean {
    return this.obtenirToken() !== null;
  }

  estAdmin(): boolean {
    const tokenData = this.getTokenFromStorage();
    return tokenData ? tokenData.estAdmin : false;
  }

  private sauvegarderToken(token: Token): void {
    localStorage.setItem('token', JSON.stringify(token));
  }

  private getTokenFromStorage(): Token | null {
    const tokenStr = localStorage.getItem('token');
    return tokenStr ? JSON.parse(tokenStr) : null;
  }
}
