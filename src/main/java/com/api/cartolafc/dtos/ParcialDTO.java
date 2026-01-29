package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ParcialDTO(
        @JsonProperty("parcial") Double parcial
) {
    public ParcialDTO {
        parcial = parcial != null ? parcial : 0.0;
    }
}
