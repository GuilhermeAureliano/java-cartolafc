package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoundDTO(
        @JsonProperty("inicio") String start,
        @JsonProperty("fim") String end,
        @JsonProperty("nome_rodada") String roundName,
        @JsonProperty("rodada_id") Integer roundId
) {
}
