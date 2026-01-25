package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TeamDTO(
        @JsonProperty("temporada_inicial") Integer initialSeason,
        @JsonProperty("nome_cartola") String cartolaName,
        @JsonProperty("nome") String name,
        @JsonProperty("url_escudo_png") String shieldUrlPng,
        @JsonProperty("slug") String slug,
        @JsonProperty("time_id") String teamId
) {
}
