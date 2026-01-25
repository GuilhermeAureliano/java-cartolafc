package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClubsDTO(
        @JsonProperty("nome") String name,
        @JsonProperty("apelido") String nickname,
        @JsonProperty("nome_fantasia") String fantasyName,
        @JsonProperty("id") String teamId
) {
}
