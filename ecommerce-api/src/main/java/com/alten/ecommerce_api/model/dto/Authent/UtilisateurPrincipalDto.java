package com.alten.ecommerce_api.model.dto.Authent;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UtilisateurPrincipalDto {
    private String email;
    private Boolean estAdmin;
}
