package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ScoredAthletesOutputDTO(
        @JsonProperty("rodada") Integer rodada,
        @JsonProperty("total_atletas") Integer totalAtletas,
        @JsonProperty("atletas") List<AthleteScore> atletas
) {
    public record AthleteScore(
            @JsonProperty("atleta_id") Integer athleteId,
            @JsonProperty("pontuacao") Double pontuacao
    ) {}
}
