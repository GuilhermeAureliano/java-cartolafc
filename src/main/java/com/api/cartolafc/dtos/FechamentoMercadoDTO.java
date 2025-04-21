package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FechamentoMercadoDTO(
        @JsonProperty("dia") Integer dia,
        @JsonProperty("mes") Integer mes,
        @JsonProperty("ano") Integer ano,
        @JsonProperty("hora") Integer hora,
        @JsonProperty("minuto") Integer minuto,
        @JsonProperty("timestamp") Long timestamp
) {
}

