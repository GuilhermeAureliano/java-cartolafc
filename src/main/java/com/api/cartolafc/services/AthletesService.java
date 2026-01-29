package com.api.cartolafc.services;

import com.api.cartolafc.dtos.ScoredAthletesOutputDTO;
import com.api.cartolafc.dtos.ScoredAthletesResponseDTO;
import com.api.cartolafc.utils.Utils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.api.cartolafc.utils.Utils.BASE_URL;

@Service
public class AthletesService {

    private final RestTemplate restTemplate;

    public AthletesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ScoredAthletesOutputDTO findScoredAthletes() {
        String url = BASE_URL + "/atletas/pontuados";

        ResponseEntity<ScoredAthletesResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                Utils.createHttpEntityWithUserAgent(),
                ScoredAthletesResponseDTO.class
        );

        ScoredAthletesResponseDTO body = response.getBody();
        if (body == null) {
            throw new RuntimeException("Erro ao consultar API do Cartola: Atletas pontuados não encontrados");
        }

        Map<String, ScoredAthletesResponseDTO.AthleteApiDTO> athletesMap         = Optional.ofNullable(body.atletas())
                .orElse(Map.of());

        List<ScoredAthletesOutputDTO.AthleteScore> athletes = athletesMap.entrySet().stream()
                .map(entry -> new ScoredAthletesOutputDTO.AthleteScore(
                        Integer.valueOf(entry.getKey()),
                        Optional.ofNullable(entry.getValue().pontuacao()).orElse(0.0)
                ))
                .toList();

        return new ScoredAthletesOutputDTO(
                body.rodada(),
                body.totalAtletas(),
                athletes
        );
    }
}
