package com.api.cartolafc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusMercado {
    MERCADO_ABERTO(1),
    MERCADO_FECHADO(2);

    private final int codigo;

    StatusMercado(int codigo) {
        this.codigo = codigo;
    }

    @JsonValue
    public String getNome() {
        return this.name();
    }

    public int getCodigo() {
        return codigo;
    }

    @JsonCreator
    public static StatusMercado fromCodigo(int codigo) {
        for (StatusMercado status : StatusMercado.values()) {
            if (status.getCodigo() == codigo) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código de status_mercado inválido: " + codigo);
    }
}