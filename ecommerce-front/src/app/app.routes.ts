// src/app/app.routes.ts
import {Routes} from '@angular/router';
import {adminGuard, authGuard} from './guard/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/shop',
    pathMatch: 'full'
  },
  {
    path: 'connexion',
    loadComponent: () => import('./component/connexion/connexion.component').then(m => m.ConnexionComponent)
  },
  {
    path: 'shop',
    loadComponent: () => import('./component/shop/shop.component').then(m => m.ShopComponent),
    canActivate: [authGuard]
  },
  {
    path: 'panier',
    loadComponent: () => import('./component/panier/panier.component').then(m => m.PanierComponent),
    canActivate: [authGuard]
  },
  {
    path: 'contact',
    loadComponent: () => import('./component/contact/contact.component').then(m => m.ContactComponent),
    canActivate: [authGuard]
  },
  {
    path: 'admin/produits',
    loadComponent: () => import('./component/admin/admin-produit/admin-produit.component').then(m => m.AdminProduitComponent),
    canActivate: [authGuard, adminGuard]
  },
  {
    path: '**',
    redirectTo: '/shop'
  }
];
