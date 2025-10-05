# Alten E-Commerce - Application Full Backend

## Description

Application e-commerce.

### Fonctionnalités principales

#### Backend
- API REST complète avec Spring Boot 3.5 + Java 17
- Base de données H2 en mémoire
- Authentification JWT
- Gestion des produits
- Gestion du panier d'achat
- Gestion de liste d'envie
- Droits administrateur (admin@admin.com)
- Pagination et filtrage
- MapStruct pour le mapping DTO/DAO
- Tests unitaires (JUnit 5 + Mockito)
- Tests d'intégration (Cucumber)
- Documentation Swagger
- Couverture de code avec JaCoCo


## Installation et Démarrage
### Backend

```bash
# Installer les dépendances
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

L'API sera disponible sur `http://localhost:8080`

#### Accès aux outils
- **H2 Console**: http://localhost:8080/h2-console
    - JDBC URL: `jdbc:h2:mem:ecommercedb`
    - Username: `sa`
    - Password: *(vide)*

- **Swagger UI**: http://localhost:8080/swagger-ui.html

## Tests

### Tests Backend

```bash
# Tests unitaires uniquement
mvn test

# Tests unitaires + intégration
mvn verify

# Rapport de couverture
mvn clean verify jacoco:report
# Ouvrir target/site/jacoco/index.html
```

##  Comptes de Test

### Compte Administrateur
- **Email**: admin@admin.com
- **Mot de passe**: admin123 *(à créer lors de la première connexion)*

### Compte Utilisateur Normal
- Créer un compte via `/api/account`

## API Endpoints

### Authentification
```
POST /api/account          - Créer un compte
POST /api/token            - Se connecter (obtenir JWT)
```

### Produits
```
GET    /api/products              - Liste avec pagination/filtres
GET    /api/products/{id}         - Détails d'un produit
POST   /api/products              - Créer (admin uniquement)
PUT    /api/products/{id}         - Modifier (admin uniquement)
PATCH  /api/products/{id}         - Modification partielle (admin)
DELETE /api/products/{id}         - Supprimer (admin uniquement)
```

### Panier
```
GET    /api/panier                     - Obtenir le panier
POST   /api/panier/produits            - Ajouter un article
PUT    /api/panier/produits/{id}       - Modifier la quantité
DELETE /api/panier/produits/{id}       - Retirer un article
DELETE /api/panier                     - Vider le panier
```

### Liste d'envie
```
GET    /api/liste-envie                    - Obtenir la liste
POST   /api/liste-envie/produits/{id}      - Ajouter un produit
DELETE /api/liste-envie/produits/{id}      - Retirer un produit
```

## Structure du Projet

### Backend
```
src/main/java/com/alten/ecommerce_api/
├── config/              # Configuration Spring Security et Swagger
├── conroller/           # Contrôleurs REST
├── model/               
├     ├── dao/           # Entités JPA
├     ├── dto/           # Data Transfer Objects
├     └── enumeration/   # Énumérations
├── exception/           # Gestion des exceptions
├── mapper/              # Mappers MapStruct
├── repository/          # Repositories Spring Data
├── security/            # Filtres et configuration JWT
└── service/             # Couche métier

src/test/java/
├── service/             # Tests unitaires
└── integration/         # Tests Cucumber
    ├── steps/           # Step definitions
```
## Exemples d'utilisation avec curl

### Créer un compte
```bash
curl -X POST http://localhost:8080/api/account \
  -H "Content-Type: application/json" \
  -d '{
    "nomUtilisateur": "admin",
    "prenom": "admin",
    "email": "admin@admin.com",
    "motDePasse": "admin123"
  }'
```

### Se connecter
```bash
curl -X POST http://localhost:8080/api/token \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@admin.com",
    "motDePasse": "admin123"
  }'
```

### Obtenir la liste des produits (avec token)
```bash
curl -X GET 'http://localhost:8080/api/products?page=0&taille=10' \
  -H "Authorization: Bearer TOKEN_JWT"
```

### Ajouter un produit au panier
```bash
curl -X POST http://localhost:8080/api/panier/produits \
  -H "Authorization: Bearer TOKEN_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "produitId": 1,
    "quantite": 2
  }'
```