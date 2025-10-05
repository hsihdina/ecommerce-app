# language: fr
Fonctionnalité: Gestion des produits
  En tant qu'utilisateur de l'API
  Je veux gérer les produits
  Afin de maintenir le catalogue à jour


  Scénario: Création d'un produit par l'administrateur
    Étant donné que je suis connecté en tant qu'admin
    Quand je crée un produit avec les informations valides
    Alors le produit est créé avec succès
    Et il est visible dans la liste des produits

  Scénario: Tentative de création par un utilisateur normal
    Étant donné que je suis connecté en tant qu'utilisateur normal
    Quand je tente de créer un produit
    Alors je reçois une erreur 403 Forbidden

  Scénario: Consultation de la liste des produits
    Étant donné que je suis connecté en tant qu'utilisateur normal
    Quand je consulte la liste des produits
    Alors je reçois la liste complète avec pagination

  Scénario: Filtrage des produits par catégorie
    Étant donné que je suis connecté en tant qu'utilisateur normal
    Quand je filtre les produits par catégorie "Informatique"
    Alors je reçois uniquement les produits de cette catégorie

  Scénario: Recherche de produits
    Étant donné que je suis connecté en tant qu'utilisateur normal
    Quand je recherche "Ordinateur"
    Alors je reçois les produits contenant ce mot-clé

  Scénario: Mise à jour d'un produit par l'administrateur
    Étant donné que je suis connecté en tant qu'admin
    Et qu'un produit existe avec l'ID 1
    Quand je modifie le prix du produit à "149.99"
    Alors le produit est mis à jour avec succès
    Et le nouveau prix est visible

  Scénario: Suppression d'un produit par l'administrateur
    Étant donné que je suis connecté en tant qu'admin
    Et qu'un produit existe avec l'ID 2
    Quand je supprime le produit
    Alors le produit est supprimé avec succès
    Et il n'est plus visible dans la liste

  Scénario: Tentative de modification par un utilisateur normal
    Étant donné que je suis connecté en tant qu'utilisateur normal
    Et qu'un produit existe avec l'ID 1
    Quand je tente de modifier le produit
    Alors je reçois une erreur 403 Forbidden
