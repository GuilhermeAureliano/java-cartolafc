package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.api.cartolafc.utils.Utils;

import java.util.Collections;
import java.util.List;

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
        @JsonProperty("valor_time") Double teamValue,
        @JsonProperty("atletas") List<AthleteDTO> athletes
) {
    private static final Double INITIAL_ASSETS = 100.0;

    public TeamByIdDTO {
        championshipPoints = Utils.round(championshipPoints, 2);
        points = Utils.round(points, 2);
        assets = assets != null ? assets : INITIAL_ASSETS;
        athletes = athletes != null ? athletes : Collections.emptyList();
    }
}
