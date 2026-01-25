package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TeamByIdDTO(
        @JsonProperty("time") TeamDTO team,
        @JsonProperty("pontos_campeonato") Double championshipPoints,
        @JsonProperty("capitao_id") String captainId,
        @JsonProperty("reserva_luxo_id") String luxuryReserveId,
        @JsonProperty("pontos") Double points,
        @JsonProperty("variacao_patrimonio") Double assetsVariation,
        @JsonProperty("esquema_id") Integer formationId,
        @JsonProperty("rodada_atual") Integer currentRound,
        @JsonProperty("patrimonio") Double assets,
        @JsonProperty("valor_time") Double teamValue
) {
    private static final Double INITIAL_ASSETS = 100.0;

    public TeamByIdDTO {
        championshipPoints = championshipPoints != null ? championshipPoints : 0.0;
        points = points != null ? points : 0.0;
        assets = assets != null ? assets : INITIAL_ASSETS;
    }
}
