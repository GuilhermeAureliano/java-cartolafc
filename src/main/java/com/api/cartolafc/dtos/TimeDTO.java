package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TimeDTO(
        @JsonProperty("temporada_inicial") Integer temporadaInicial,
        @JsonProperty("nome_cartola") String nomeCartola,
        @JsonProperty("nome") String nome,
        @JsonProperty("url_escudo_png") String urlEscudoPng,
        @JsonProperty("slug") String slug,
        @JsonProperty("time_id") String timeId
        ) {
}
