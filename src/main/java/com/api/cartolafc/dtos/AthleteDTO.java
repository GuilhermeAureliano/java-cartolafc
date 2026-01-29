package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AthleteDTO(
        @JsonProperty("jogos_num") Integer gamesPlayed,
        @JsonProperty("atleta_id") Integer athleteId,
        @JsonProperty("rodada_id") Integer roundId,
        @JsonProperty("clube_id") Integer clubId,
        @JsonProperty("posicao_id") Integer positionId,
        @JsonProperty("pontos_num") Double points,
        @JsonProperty("media_num") Double average,
        @JsonProperty("variacao_num") Double variation,
        @JsonProperty("preco_num") Double price,
        @JsonProperty("entrou_em_campo") Boolean played,
        @JsonProperty("apelido") String nickname,
        @JsonProperty("nome") String name
) {
}
