package com.api.cartolafc.dtos;

import com.api.cartolafc.enums.MarketStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record MarketDTO(
        @JsonProperty("rodada_atual") Integer currentRound,
        @JsonProperty("status_mercado") MarketStatus marketStatus,
        @JsonProperty("temporada") Integer season,
        @JsonProperty("fechamento") MarketClosingDTO closing,
        @JsonProperty("times_escalados") Integer teamsScaled
) {
}
