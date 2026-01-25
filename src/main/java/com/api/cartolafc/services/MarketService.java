package com.api.cartolafc.services;

import com.api.cartolafc.dtos.MarketDTO;
import com.api.cartolafc.utils.Utils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.api.cartolafc.utils.Utils.BASE_URL;

@Service
public class MarketService {

    private final RestTemplate restTemplate;

    public MarketService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MarketDTO findMarketInfo() {
        String url = BASE_URL + "/mercado/status";

        ResponseEntity<MarketDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                Utils.createHttpEntityWithUserAgent(),
                MarketDTO.class
        );

        MarketDTO market = response.getBody();
        if (market == null) {
            throw new RuntimeException("Erro ao consultar API do Cartola: Mercado não encontrado");
        }
        return market;
    }
}
