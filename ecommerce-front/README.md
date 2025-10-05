# Alten E-Commerce - Frontend

Application e-commerce

## Fonctionnalités

### Authentification
- Connexion avec JWT
- Guard pour protéger les routes
- Intercepteur HTTP pour injection automatique du token
- Déconnexion automatique si token expiré

### Gestion des produits
- Liste des produits avec pagination
- Filtrage par catégorie
- Recherche par nom/description
- Badge de statut (en stock, stock faible, rupture)
- Notation avec étoiles

### Panier
- Ajout de produits au panier
- Modification de quantité
- Suppression d'articles
- Calcul du total
- Badge indiquant le nombre d'articles
- Vérification du stock disponible

### Contact
- Formulaire avec validation
- Message de succès

## Installation

### 1. Cloner le repository
```bash
git clone <repository-url>
cd repository
```

### 2. Installer les dépendances
```bash
npm install
```

### 3. Vérifier l'installation
```bash
ng version
```

## Démarrage

```bash
ng serve
```

L'application sera accessible sur **http://localhost:4200**

## Structure du projet

```
src/
├── app/
│   ├── component/               # Composants de l'application
│   │   ├── shop/                # Liste des produits
│   │   ├── panier/              # Gestion du panier
│   │   ├── admin/               # Gestion des produits (admin)
│   │   ├── contact/             # Formulaire de contact
│   │   └── connexion/           # Page de connexion
│   │
│   ├── services/                # Services métier
│   │   ├── auth.service.ts      
│   │   ├── panier.service.ts    
│   │   └── produit.service.ts 
│   │
│   ├── guards/                  # Protection des routes
│   │   └── auth.guard.ts        # Vérifie l'authentification
│   │
│   ├── interceptors/            # Intercepteurs HTTP
│   │   └── auth.interceptor.ts  # Injection du token JWT
│   │
│   ├── models/                  # Interfaces TypeScript
│   │
│   ├── app.component.ts         # Composant racine
│   ├── app.component.html       # Template avec sidebar
│   ├── app.config.ts            # Configuration de l'app
│   └── app.routes.ts            # Configuration des routes
│
├── styles.css                   # Styles globaux
├── index.html                   # Point d'entrée HTML
└── main.ts                      # Point d'entrée TypeScript
```

## Reste à faire

### Fonctionnalités
- ** Gestion de la liste d'envie **
- ** Formulaire de creation de compte **
- ** Page de profil utilisateur **
- ** Page de détails du produit **
- ** Tests unitaires et e2e **
