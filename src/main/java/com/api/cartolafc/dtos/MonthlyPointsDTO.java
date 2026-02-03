package com.api.cartolafc.dtos;

import com.api.cartolafc.utils.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;

public record MonthlyPointsDTO(
        @JsonProperty("pontos_mensais") Double monthlyPoints
) {
    public MonthlyPointsDTO {
        monthlyPoints = Utils.round(monthlyPoints, 2);
    }
}
