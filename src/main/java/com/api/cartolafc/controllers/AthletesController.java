package com.api.cartolafc.controllers;

import com.api.cartolafc.dtos.ScoredAthletesOutputDTO;
import com.api.cartolafc.services.AthletesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/atletas")
public class AthletesController {

    private final AthletesService athletesService;

    public AthletesController(AthletesService athletesService) {
        this.athletesService = athletesService;
    }

    @Operation(summary = "Buscar atletas pontuados", description = "Retorna os atletas pontuados na rodada atual (API /atletas/pontuados do Cartola). Cada atleta inclui apenas atleta_id e pontuacao.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atletas pontuados encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/pontuados")
    public ResponseEntity<?> findScoredAthletes() {
        try {
            ScoredAthletesOutputDTO result = athletesService.findScoredAthletes();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao consultar API do Cartola: " + e.getMessage());
        }
    }
}
