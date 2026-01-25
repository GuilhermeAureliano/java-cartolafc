package com.api.cartolafc.services;

import com.api.cartolafc.dtos.ClubsDTO;
import com.api.cartolafc.utils.Utils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.api.cartolafc.utils.Utils.BASE_URL;

@Service
public class ClubsService {

    private final RestTemplate restTemplate;

    public ClubsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public Map<Integer, ClubsDTO> findClubs() {
        String url = BASE_URL + "/clubes";

        ResponseEntity<Map<Integer, ClubsDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                Utils.createHttpEntityWithUserAgent(),
                new ParameterizedTypeReference<Map<Integer, ClubsDTO>>() {
                }
        );

        Map<Integer, ClubsDTO> clubs = response.getBody();
        if (clubs == null || clubs.isEmpty()) {
            throw new RuntimeException("Nenhum clube encontrado");
        }

        return clubs;
    }
}
