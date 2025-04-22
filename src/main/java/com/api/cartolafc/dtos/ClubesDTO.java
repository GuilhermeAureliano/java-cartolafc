package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClubesDTO(
        @JsonProperty("nome") String nome,
        @JsonProperty("apelido") String apelido,
        @JsonProperty("nome_fantasia") String nomeFantasia,
        @JsonProperty("id") String timeId
) {
}
