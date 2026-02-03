package com.api.cartolafc.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;

public class Utils {

    public static final String BASE_URL = "https://api.cartola.globo.com";

    public static double round(Double value, int decimals) {
        if (value == null) return 0.0;
        return BigDecimal.valueOf(value)
                .setScale(decimals, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static String normalizeSlug(String text) {
        String withoutAccents = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]+", "");
        return withoutAccents.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    public static HttpEntity<String> createHttpEntityWithUserAgent() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");
        return new HttpEntity<>(headers);
    }

    public static String cleanShieldUrl(String shieldUrl) {
        if (shieldUrl == null || shieldUrl.isEmpty()) {
            return shieldUrl;
        }
        
        int s3Index = shieldUrl.indexOf("https://s3.glbimg.com/");
        if (s3Index != -1) {
            return shieldUrl.substring(s3Index);
        }
        
        return shieldUrl;
    }
}
