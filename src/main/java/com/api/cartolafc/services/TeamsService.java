package com.api.cartolafc.services;

import com.api.cartolafc.dtos.TeamDTO;
import com.api.cartolafc.dtos.TeamByIdDTO;
import com.api.cartolafc.utils.Utils;
import static com.api.cartolafc.utils.Utils.BASE_URL;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TeamsService {

    private final RestTemplate restTemplate;

    public TeamsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TeamDTO findTeamByName(String name) {
        String url = BASE_URL + "/times?q=" + name;

        ResponseEntity<List<TeamDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                Utils.createHttpEntityWithUserAgent(),
                new ParameterizedTypeReference<>() {
                }
        );

        List<TeamDTO> teams = response.getBody();
        if (teams == null || teams.isEmpty()) {
            return null;
        }

        String normalizedTerm = Utils.normalizeSlug(name);
        TeamDTO foundTeam = teams.stream()
                .filter(team -> team.slug().equalsIgnoreCase(normalizedTerm)
                        || team.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
        
        if (foundTeam == null) {
            return null;
        }
        
        // Limpa a URL do escudo removendo o prefixo do CDN
        String cleanedShieldUrl = Utils.cleanShieldUrl(foundTeam.shieldUrlPng());
        return new TeamDTO(
                foundTeam.initialSeason(),
                foundTeam.cartolaName(),
                foundTeam.name(),
                cleanedShieldUrl,
                foundTeam.slug(),
                foundTeam.teamId()
        );
    }

    public TeamByIdDTO findTeamById(String id) {
        return findTeamByUrl(BASE_URL + "/time/id/" + id);
    }

    public TeamByIdDTO findTeamByIdInRound(String id, int round) {
        return findTeamByUrl(BASE_URL + "/time/id/" + id + "/" + round);
    }

    private TeamByIdDTO findTeamByUrl(String url) {
        ResponseEntity<TeamByIdDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                Utils.createHttpEntityWithUserAgent(),
                TeamByIdDTO.class
        );

        TeamByIdDTO body = response.getBody();
        if (body == null) {
            throw new RuntimeException("Resposta da API está vazia.");
        }

        if (body.team() != null && body.team().shieldUrlPng() != null) {
            String cleanedShieldUrl = Utils.cleanShieldUrl(body.team().shieldUrlPng());
            TeamDTO cleanedTeam = new TeamDTO(
                    body.team().initialSeason(),
                    body.team().cartolaName(),
                    body.team().name(),
                    cleanedShieldUrl,
                    body.team().slug(),
                    body.team().teamId()
            );
            return new TeamByIdDTO(
                    cleanedTeam,
                    body.championshipPoints(),
                    body.captainId(),
                    body.luxuryReserveId(),
                    body.points(),
                    body.assetsVariation(),
                    body.formationId(),
                    body.currentRound(),
                    body.assets(),
                    body.teamValue()
            );
        }

        return body;
    }
}
