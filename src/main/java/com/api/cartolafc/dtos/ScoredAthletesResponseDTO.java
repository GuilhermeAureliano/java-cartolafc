package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ScoredAthletesResponseDTO(
        @JsonProperty("atletas") Map<String, AthleteApiDTO> atletas,
        @JsonProperty("rodada") Integer rodada,
        @JsonProperty("total_atletas") Integer totalAtletas
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AthleteApiDTO(
            @JsonProperty("pontuacao") Double pontuacao
    ) {}
}
