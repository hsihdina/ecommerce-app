package com.alten.ecommerce_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GestionnaireExceptionsGlobal {

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<ReponseErreur> ressourceNotFoundException(RessourceIntrouvableException ex) {
        ReponseErreur erreur = new ReponseErreur(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(erreur, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ProduitExistantException.class, CompteExistantException.class, RessourceExistanteException.class})
    public ResponseEntity<ReponseErreur> ressourceConflitcsException(RuntimeException ex) {
        ReponseErreur erreur = new ReponseErreur(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(erreur, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CredentialsInvalidesException.class)
    public ResponseEntity<ReponseErreur> unauthorizedCredentialsException(CredentialsInvalidesException ex) {
        ReponseErreur erreur = new ReponseErreur(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(erreur, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccesDeniException.class)
    public ResponseEntity<ReponseErreur> accesDeniedException(AccesDeniException ex) {
        ReponseErreur erreur = new ReponseErreur(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(erreur, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(StockInsuffisantException.class)
    public ResponseEntity<ReponseErreur> stockInsuffisantException(StockInsuffisantException ex) {
        ReponseErreur erreur = new ReponseErreur(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(erreur, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> gererValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> erreurs = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String nomChamp = ((FieldError) error).getField();
            String messageErreur = error.getDefaultMessage();
            erreurs.put(nomChamp, messageErreur);
        });
        return new ResponseEntity<>(erreurs, HttpStatus.BAD_REQUEST);
    }

    record ReponseErreur(int statut, String message, LocalDateTime horodatage) {
    }
}