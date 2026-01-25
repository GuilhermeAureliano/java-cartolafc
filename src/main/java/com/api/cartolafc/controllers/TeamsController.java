package com.api.cartolafc.controllers;

import com.api.cartolafc.dtos.TeamDTO;
import com.api.cartolafc.dtos.TeamByIdDTO;
import com.api.cartolafc.services.TeamsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TeamsController {

    private final TeamsService teamsService;

    public TeamsController(TeamsService teamsService) {
        this.teamsService = teamsService;
    }

    @Operation(summary = "Buscar time por nome", description = "Busca informações de um time pelo nome.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Time encontrado"),
            @ApiResponse(responseCode = "404", description = "Time não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping("/times")
    public ResponseEntity<?> findTeamByName(@Parameter(description = "Nome do time (ex: Sportv)", example = "Sportv") @RequestParam("q") String name) {
        try {
            TeamDTO team = teamsService.findTeamByName(name);
            if (team == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Time não encontrado para o nome: " + name);
            }
            return ResponseEntity.ok(team);
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
    public ResponseEntity<?> findTeamById(
            @Parameter(description = "ID do time no Cartola", example = "398396")
            @PathVariable("id") String id) {
        try {
            TeamByIdDTO team = teamsService.findTeamById(id);
            if (team == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Time não encontrado para o ID: " + id);
            }
            return ResponseEntity.ok(team);
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
    public ResponseEntity<?> findTeamByIdInRound(
            @Parameter(description = "ID do time no Cartola", example = "398396")
            @PathVariable("id") String id,
            @Parameter(description = "Número da rodada", example = "2")
            @PathVariable("rodada") int round) {
        try {
            TeamByIdDTO team = teamsService.findTeamByIdInRound(id, round);
            if (team == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Time não encontrado para o ID: " + id + " e rodada: " + round);
            }
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao consultar API do Cartola: " + e.getMessage());
        }
    }

}
