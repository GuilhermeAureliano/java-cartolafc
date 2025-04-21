package com.api.cartolafc.controllers;

import com.api.cartolafc.dtos.TimeDTO;
import com.api.cartolafc.dtos.TimePorIdDTO;
import com.api.cartolafc.services.TimesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TimesController {

    private final TimesService timesService;

    public TimesController(TimesService timesService) {
        this.timesService = timesService;
    }

    @Operation(summary = "Buscar time por nome", description = "Busca informações de um time pelo nome.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Time encontrado"),
            @ApiResponse(responseCode = "404", description = "Time não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/times")
    public ResponseEntity<?> buscarTimePorNome(@Parameter(description = "Nome do time (ex: Sportv)", example = "Sportv") @RequestParam("q") String nome) {
        try {
            TimeDTO time = timesService.buscarTimePorNome(nome);
            if (time == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Time não encontrado para o nome: " + nome);
            }
            return ResponseEntity.ok(time);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao consultar API do Cartola: " + e.getMessage());
        }
    }

    @Operation(summary = "Buscar time por ID", description = "Busca informações de um time específico pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Time encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Time não encontrado para o ID fornecido"),
            @ApiResponse(responseCode = "500", description = "Erro ao consultar a API do Cartola")
    })
    @GetMapping("/time/id/{id}")
    public ResponseEntity<?> buscarTimePorId(
            @Parameter(description = "ID do time no Cartola", example = "398396")
            @PathVariable("id") String id) {
        try {
            TimePorIdDTO time = timesService.buscarTimePorId(id);
            if (time == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Time não encontrado para o ID: " + id);
            }
            return ResponseEntity.ok(time);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao consultar API do Cartola: " + e.getMessage());
        }
    }

    @Operation(summary = "Buscar time por ID e rodada", description = "Busca informações de um time específico em uma rodada específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Time encontrado com sucesso para a rodada"),
            @ApiResponse(responseCode = "404", description = "Time não encontrado para o ID e rodada informados"),
            @ApiResponse(responseCode = "500", description = "Erro ao consultar a API do Cartola")
    })
    @GetMapping("/time/id/{id}/{rodada}")
    public ResponseEntity<?> buscarTimePorIdNaRodada(
            @Parameter(description = "ID do time no Cartola", example = "398396")
            @PathVariable("id") String id,
            @Parameter(description = "Número da rodada", example = "2")
            @PathVariable("rodada") int rodada) {
        try {
            TimePorIdDTO time = timesService.buscarTimePorIdNaRodada(id, rodada);
            if (time == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Time não encontrado para o ID: " + id + " e rodada: " + rodada);
            }
            return ResponseEntity.ok(time);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao consultar API do Cartola: " + e.getMessage());
        }
    }

}

