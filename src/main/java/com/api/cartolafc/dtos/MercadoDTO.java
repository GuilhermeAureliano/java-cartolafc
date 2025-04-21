package com.api.cartolafc.dtos;

import com.api.cartolafc.enums.StatusMercado;
import com.fasterxml.jackson.annotation.JsonProperty;

public record MercadoDTO(
        @JsonProperty("rodada_atual") Integer rodadaAtual,
        @JsonProperty("status_mercado") StatusMercado statusMercado,
        @JsonProperty("temporada") Integer temporada,
        FechamentoMercadoDTO fechamento,
        @JsonProperty("times_escalados") Integer timesEscalados
) {
}
