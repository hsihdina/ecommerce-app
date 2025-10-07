// src/app/app.component.ts
import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {AuthService} from './service/auth.service';
import {PanierService} from './service/panier.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Alten E-Commerce';
  estConnecte = false;
  estAdmin = false;
  emailUtilisateur: string | null = null;
  nombreArticlesPanier = 0;

  constructor(
    public authService: AuthService,
    private panierService: PanierService
  ) {}

  ngOnInit(): void {
    this.authService.token$.subscribe(token => {
      this.estConnecte = token !== null;
      this.estAdmin = token?.estAdmin || false;
      this.emailUtilisateur = token?.email || null;
    });

    this.panierService.panier$.subscribe(panier => {
      this.nombreArticlesPanier = panier
        ? panier.articles.reduce((total, article) => total + article.quantite, 0)
        : 0;
    });
  }

  seDeconnecter(): void {
    this.authService.seDeconnecter();
  }
}
