package com.api.cartolafc.controllers;

import com.api.cartolafc.services.MarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mercado")
public class MarketController {

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @Operation(summary = "Buscar informações do mercado", description = "Retorna o Status atual do mercado (aberto/fechado, rodada atual etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações do mercado encontradas"),
            @ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/status")
    public ResponseEntity<?> findMarketInfo() {
        try {
            return ResponseEntity.ok(marketService.findMarketInfo());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao consultar API do Cartola: " + e.getMessage());
        }
    }
}
