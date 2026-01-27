package com.api.cartolafc.controllers;

import com.api.cartolafc.dtos.RoundDTO;
import com.api.cartolafc.services.RoundsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rodadas")
public class RoundsController {

    private final RoundsService roundsService;

    public RoundsController(RoundsService roundsService) {
        this.roundsService = roundsService;
    }

    @Operation(summary = "Buscar informações das rodadas", description = "Retorna todas as rodadas do Campeonato Brasileiro presentes no Cartola FC.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rodadas encontradas"),
            @ApiResponse(responseCode = "404", description = "Rodadas não encontradas"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping
    public ResponseEntity<?> findRounds() {
        try {
            List<RoundDTO> rounds = roundsService.findRounds();
            return ResponseEntity.ok(rounds);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao consultar API do Cartola: " + e.getMessage());
        }
    }
}
