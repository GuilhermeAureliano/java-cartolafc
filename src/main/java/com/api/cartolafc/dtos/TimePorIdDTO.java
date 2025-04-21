package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TimePorIdDTO(
        TimeDTO time,
        @JsonProperty("pontos_campeonato") Double pontosCampeonato,
        @JsonProperty("capitao_id") String capitaoId,
        @JsonProperty("reserva_luxo_id") String reservaLuxoId,
        @JsonProperty("pontos") Double pontos,
        @JsonProperty("variacao_patrimonio") Double variacaoPatrimonio,
        @JsonProperty("esquema_id") Integer esquemaId,
        @JsonProperty("rodada_atual") Integer rodadaAtual,
        @JsonProperty("patrimonio") Double patrimonio,
        @JsonProperty("valor_time") Double valorTime
) {}
