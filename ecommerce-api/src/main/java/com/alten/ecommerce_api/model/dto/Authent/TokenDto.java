package com.alten.ecommerce_api.model.dto.Authent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String token;
    private String type = "Bearer";
    private String email;
    private boolean estAdmin;

    public TokenDto(String token, String email, boolean estAdmin) {
        this.token = token;
        this.email = email;
        this.estAdmin = estAdmin;
    }
}