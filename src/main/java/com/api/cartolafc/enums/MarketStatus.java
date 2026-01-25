package com.api.cartolafc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MarketStatus {
    MARKET_OPEN(1),
    MARKET_CLOSED(2);

    private final int code;

    MarketStatus(int code) {
        this.code = code;
    }

    @JsonValue
    public String getName() {
        return this.name();
    }

    public int getCode() {
        return code;
    }

    @JsonCreator
    public static MarketStatus fromCode(int code) {
        for (MarketStatus status : MarketStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código de status_mercado inválido: " + code);
    }
}
