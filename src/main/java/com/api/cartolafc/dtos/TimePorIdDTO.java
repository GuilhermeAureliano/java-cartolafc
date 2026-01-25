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
) {
    private static final Double PATRIMONIO_INICIAL = 100.0;

    public TimePorIdDTO {
        pontosCampeonato = pontosCampeonato != null ? pontosCampeonato : 0.0;
        pontos = pontos != null ? pontos : 0.0;
        patrimonio = patrimonio != null ? patrimonio : PATRIMONIO_INICIAL;
    }
}
