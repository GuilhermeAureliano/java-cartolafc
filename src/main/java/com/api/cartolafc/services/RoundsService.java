package com.api.cartolafc.services;

import com.api.cartolafc.dtos.RoundDTO;
import com.api.cartolafc.utils.Utils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.api.cartolafc.utils.Utils.BASE_URL;

@Service
public class RoundsService {

    private final RestTemplate restTemplate;

    public RoundsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<RoundDTO> findRounds() {
        String url = BASE_URL + "/rodadas";

        ResponseEntity<List<RoundDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                Utils.createHttpEntityWithUserAgent(),
                new ParameterizedTypeReference<List<RoundDTO>>() {
                }
        );

        List<RoundDTO> rounds = response.getBody();
        if (rounds == null || rounds.isEmpty()) {
            throw new RuntimeException("No rounds found");
        }

        return rounds;
    }
}
