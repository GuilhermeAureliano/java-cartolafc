package com.api.cartolafc.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MonthlyPointsDTO(
        @JsonProperty("pontos_mensais") Double monthlyPoints
) {
    public MonthlyPointsDTO {
        monthlyPoints = monthlyPoints != null ? monthlyPoints : 0.0;
    }
}
