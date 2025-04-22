package com.api.cartolafc.controllers;

import com.api.cartolafc.dtos.ClubesDTO;
import com.api.cartolafc.services.ClubesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clubes")
public class ClubesController {

    private final ClubesService clubesService;

    public ClubesController(ClubesService clubesService) {
        this.clubesService = clubesService;
    }

    @Operation(summary = "Buscar informações dos clubes", description = "Retorna todos os clubes presentes no Cartola FC.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clubes encontrados"),
            @ApiResponse(responseCode = "404", description = "Clubes não encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping
    public ResponseEntity<?> buscarClubes() {
        try {
            Map<Integer, ClubesDTO> clubes = clubesService.buscarClubes();
            return ResponseEntity.ok(clubes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao consultar API do Cartola: " + e.getMessage());
        }
    }
}
