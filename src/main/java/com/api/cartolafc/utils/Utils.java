package com.api.cartolafc.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.text.Normalizer;

public class Utils {

    public static final String BASE_URL = "https://api.cartola.globo.com";

    public static String normalizarSlug(String texto) {
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]+", "");
        return semAcento.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    public static HttpEntity<String> createHttpEntityWithUserAgent() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");
        return new HttpEntity<>(headers);
    }

    public static String limparUrlEscudo(String urlEscudo) {
        if (urlEscudo == null || urlEscudo.isEmpty()) {
            return urlEscudo;
        }
        
        int indiceS3 = urlEscudo.indexOf("https://s3.glbimg.com/");
        if (indiceS3 != -1) {
            return urlEscudo.substring(indiceS3);
        }
        
        return urlEscudo;
    }
}
