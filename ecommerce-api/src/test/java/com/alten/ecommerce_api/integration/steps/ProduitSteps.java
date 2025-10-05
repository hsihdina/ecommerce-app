package com.alten.ecommerce_api.integration.steps;

import com.alten.ecommerce_api.exception.CompteExistantException;
import com.alten.ecommerce_api.model.dao.Produit;
import com.alten.ecommerce_api.model.dto.Authent.ConnexionDto;
import com.alten.ecommerce_api.model.dto.Authent.CreerCompteDto;
import com.alten.ecommerce_api.model.dto.Authent.TokenDto;
import com.alten.ecommerce_api.model.dto.ProduitDto;
import com.alten.ecommerce_api.model.enumeration.StatutInventaire;
import com.alten.ecommerce_api.repository.ProduitRepository;
import com.alten.ecommerce_api.service.ServiceAuthentification;
import io.cucumber.java.Before;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Etantdonnéque;
import io.cucumber.java.fr.Etqu;
import io.cucumber.java.fr.Quand;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;

@RequiredArgsConstructor
public class ProduitSteps {

    private final TestRestTemplate restTemplate;
    private final ServiceAuthentification authService;
    private final ProduitRepository produitRepository;

    private String tokenAdmin;
    private String tokenUser;
    private ResponseEntity<?> derniereReponse;
    private ProduitDto produitACreer;
    private Integer produitIdCourant;
    private Double nouveauPrix;


    private void creerUtilisateurSiBesoin(String email, String username, String prenom, String rawPassword) {
        try {
            authService.creerCompte(new CreerCompteDto(username, prenom, email, rawPassword));
        } catch (CompteExistantException ignored) {
        }
    }

    @Before
    public void init() {
        creerUtilisateurSiBesoin("admin@admin.com", "admin", "Admin", "admin123");
        creerUtilisateurSiBesoin("user@user.com", "user", "User", "user123");

        if (produitRepository.count() == 0) {
            Produit p1 = Produit.builder()
                    .code("PROD001")
                    .nom("Ordinateur Portable Dell")
                    .description("Ordinateur portable haute performance")
                    .categorie("Informatique")
                    .prix(899.99)
                    .quantite(50)
                    .statutInventaire(StatutInventaire.INSTOCK)
                    .notation(4.5)
                    .build();

            Produit p2 = Produit.builder()
                    .code("PROD002")
                    .nom("Souris Logitech")
                    .description("Souris sans fil")
                    .categorie("Accessoires")
                    .prix(99.99)
                    .quantite(100)
                    .statutInventaire(StatutInventaire.INSTOCK)
                    .notation(4.8)
                    .build();

            Produit p3 = Produit.builder()
                    .code("PROD003")
                    .nom("Clavier Mécanique Corsair")
                    .description("Clavier mécanique RGB")
                    .categorie("Accessoires")
                    .prix(129.99)
                    .quantite(75)
                    .statutInventaire(StatutInventaire.INSTOCK)
                    .notation(4.7)
                    .build();
            produitRepository.saveAll(List.of(p1, p2, p3));
        }
    }

    @Etantdonnéque("je suis connecté en tant qu'admin")
    public void jeSuisConnecteEnTantQuAdmin() {
        TokenDto token = authService.connexion(new ConnexionDto("admin@admin.com", "admin123"));
        tokenAdmin = token.getToken();
    }

    @Etantdonnéque("je suis connecté en tant qu'utilisateur normal")
    public void jeSuisConnecteEnTantQuUtilisateurNormal() {
        TokenDto token = authService.connexion(new ConnexionDto("user@user.com", "user123"));
        tokenUser = token.getToken();
    }

    @Quand("je crée un produit avec les informations valides")
    public void jeCreeUnProduitAvecLesInformationsValides() {
        produitACreer = new ProduitDto();
        produitACreer.setCode("TEST-CUCUMBER-001");
        produitACreer.setNom("Produit Test Cucumber");
        produitACreer.setDescription("Description du produit test");
        produitACreer.setCategorie("Test");
        produitACreer.setPrix(99.99);
        produitACreer.setQuantite(50);
        produitACreer.setStatutInventaire(StatutInventaire.INSTOCK);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenAdmin);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProduitDto> requete = new HttpEntity<>(produitACreer, headers);
        derniereReponse = restTemplate.postForEntity("/api/products", requete, ProduitDto.class);

        if (derniereReponse.getBody() instanceof ProduitDto dto && dto.getId() != null) {
            produitIdCourant = dto.getId().intValue();
        }
    }

    @Alors("le produit est créé avec succès")
    public void leProduitEstCreeAvecSucces() {
        Assertions.assertThat(derniereReponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(derniereReponse.getBody()).isNotNull();
    }

    @Alors("il est visible dans la liste des produits")
    public void ilEstVisibleDansLaListeDesProduits() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenAdmin);
        HttpEntity<Void> requete = new HttpEntity<>(headers);

        ResponseEntity<String> reponse = restTemplate.exchange(
                "/api/products?recherche=" + produitACreer.getCode(),
                HttpMethod.GET, requete, String.class);

        Assertions.assertThat(reponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(reponse.getBody()).contains(produitACreer.getCode());
    }

    @Quand("je tente de créer un produit")
    public void jeTenteDeCreerUnProduit() {
        ProduitDto produitDto = ProduitDto.builder()
                .code("TEST-USER-001")
                .nom("Produit Test User")
                .categorie("Test")
                .prix(49.99)
                .quantite(10)
                .statutInventaire(StatutInventaire.INSTOCK)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenUser);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProduitDto> requete = new HttpEntity<>(produitDto, headers);
        derniereReponse = restTemplate.postForEntity("/api/products", requete, String.class);
    }

    @Alors("je reçois une erreur {int} Forbidden")
    public void jeRecoisUneErreurForbidden(int codeStatut) {
        Assertions.assertThat(derniereReponse.getStatusCode().value()).isEqualTo(codeStatut);
    }

    @Quand("je consulte la liste des produits")
    public void jeConsulteLaListeDesProduits() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenUser);
        HttpEntity<Void> req = new HttpEntity<>(headers);
        derniereReponse = restTemplate.exchange("/api/products", HttpMethod.GET, req, String.class);
    }

    @Alors("je reçois la liste complète avec pagination")
    public void jeRecoisListeCompleteAvecPagination() {
        Assertions.assertThat(derniereReponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat((String) derniereReponse.getBody()).contains("content");
    }

    @Quand("je filtre les produits par catégorie {string}")
    public void jeFiltreLesProduitsParCategorie(String categorie) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenUser);
        HttpEntity<Void> req = new HttpEntity<>(headers);
        derniereReponse = restTemplate.exchange(
                "/api/products?categorie=" + categorie, HttpMethod.GET, req, String.class);
    }

    @Alors("je reçois uniquement les produits de cette catégorie")
    public void jeRecoisUniquementLesProduitsDeCetteCategorie() {
        Assertions.assertThat(derniereReponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat((String) derniereReponse.getBody()).contains("categorie");
    }

    @Quand("je recherche {string}")
    public void jeRecherche(String motCle) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenUser);
        HttpEntity<Void> req = new HttpEntity<>(headers);
        derniereReponse = restTemplate.exchange(
                "/api/products?recherche=" + motCle, HttpMethod.GET, req, String.class);
    }

    @Alors("je reçois les produits contenant ce mot-clé")
    public void jeRecoisLesProduitsContenantCeMotCle() {
        Assertions.assertThat(derniereReponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Quand("je modifie le prix du produit à {string}")
    public void jeModifieLePrixDuProduitA(String prix) {
        nouveauPrix = Double.parseDouble(prix);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenAdmin);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProduitDto maj = new ProduitDto();
        maj.setPrix(nouveauPrix);

        HttpEntity<ProduitDto> req = new HttpEntity<>(maj, headers);
        derniereReponse = restTemplate.exchange(
                "/api/products/" + produitIdCourant, HttpMethod.PUT, req, String.class);
    }

    @Alors("le produit est mis à jour avec succès")
    public void leProduitEstMisAJourAvecSucces() {
        Assertions.assertThat(derniereReponse.getStatusCode())
                .isIn(HttpStatus.OK, HttpStatus.NO_CONTENT);
    }

    @Alors("le nouveau prix est visible")
    public void leNouveauPrixEstVisible() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenAdmin);
        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<String> reponse = restTemplate.exchange(
                "/api/products/" + produitIdCourant, HttpMethod.GET, req, String.class);

        Assertions.assertThat(reponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(reponse.getBody()).contains(String.valueOf(nouveauPrix));
    }

    @Quand("je supprime le produit")
    public void jeSupprimeLeProduit() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenAdmin);
        HttpEntity<Void> req = new HttpEntity<>(headers);
        derniereReponse = restTemplate.exchange(
                "/api/products/" + produitIdCourant, HttpMethod.DELETE, req, String.class);
    }

    @Alors("le produit est supprimé avec succès")
    public void leProduitEstSupprimeAvecSucces() {
        Assertions.assertThat(derniereReponse.getStatusCode())
                .isIn(HttpStatus.OK, HttpStatus.NO_CONTENT);
    }

    @Alors("il n'est plus visible dans la liste")
    public void ilNEstPlusVisibleDansLaListe() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenAdmin);
        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<String> reponse = restTemplate.exchange(
                "/api/products/" + produitIdCourant, HttpMethod.GET, req, String.class);

        Assertions.assertThat(reponse.getStatusCode()).isIn(HttpStatus.NOT_FOUND, HttpStatus.NO_CONTENT);
    }

    @Quand("je tente de modifier le produit")
    public void jeTenteDeModifierLeProduit() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenUser);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProduitDto produitDto = new ProduitDto();
        produitDto.setPrix(123.45);

        HttpEntity<ProduitDto> req = new HttpEntity<>(produitDto, headers);
        derniereReponse = restTemplate.exchange(
                "/api/products/" + produitIdCourant, HttpMethod.PUT, req, String.class);
    }

    @Etqu("un produit existe avec l'ID {int}")
    public void unProduitExisteAvecLID(int id) {
        produitIdCourant = id;
    }
}
