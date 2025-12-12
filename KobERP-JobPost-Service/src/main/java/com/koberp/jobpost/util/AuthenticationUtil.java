package com.koberp.jobpost.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtil {

    /**
     * JWT token'dan user ID'yi alır
     */
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            
            // Auth0'dan gelen token'daki user ID'yi al
            // Auth0'da genellikle "sub" claim'inde user ID bulunur
            String subject = jwt.getSubject();
            
            // "auth0|xxxxx" formatındaysa sadece ID kısmını al
            if (subject != null && subject.contains("|")) {
                return subject.split("\\|")[1];
            }
            
            return subject;
        }
        
        throw new RuntimeException("Kullanıcı kimliği doğrulanamadı");
    }

    /**
     * JWT token'ı alır
     */
    public Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            return (Jwt) authentication.getPrincipal();
        }
        
        throw new RuntimeException("JWT token bulunamadı");
    }

    /**
     * Kullanıcının email adresini alır
     */
    public String getCurrentUserEmail() {
        Jwt jwt = getCurrentJwt();
        return jwt.getClaim("email");
    }

    /**
     * Kullanıcının adını alır
     */
    public String getCurrentUserName() {
        Jwt jwt = getCurrentJwt();
        return jwt.getClaim("name");
    }

    /**
     * Token'dan belirli bir claim'i alır
     */
    public <T> T getClaim(String claimName, Class<T> clazz) {
        Jwt jwt = getCurrentJwt();
        return jwt.getClaim(claimName);
    }
}
