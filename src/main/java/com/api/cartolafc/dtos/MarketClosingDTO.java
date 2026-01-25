package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MarketClosingDTO(
        @JsonProperty("dia") Integer day,
        @JsonProperty("mes") Integer month,
        @JsonProperty("ano") Integer year,
        @JsonProperty("hora") Integer hour,
        @JsonProperty("minuto") Integer minute,
        @JsonProperty("timestamp") Long timestamp
) {
}
