package com.api.cartolafc.services;

import com.api.cartolafc.dtos.MercadoDTO;
import com.api.cartolafc.utils.Utils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.api.cartolafc.utils.Utils.BASE_URL;

@Service
public class MercadoService {

    private final RestTemplate restTemplate;

    public MercadoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public MercadoDTO buscarInformacoesMercado() {
        String url = BASE_URL + "/mercado/status";

        ResponseEntity<MercadoDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                Utils.createHttpEntityWithUserAgent(),
                MercadoDTO.class
        );

        MercadoDTO mercado = response.getBody();
        if (mercado == null) {
            throw new RuntimeException("Erro ao consultar API do Cartola: Mercado não encontrado");
        }
        return mercado;
    }
}
