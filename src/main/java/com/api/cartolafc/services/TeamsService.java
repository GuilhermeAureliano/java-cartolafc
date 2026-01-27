package com.api.cartolafc.services;

import com.api.cartolafc.dtos.TeamDTO;
import com.api.cartolafc.dtos.TeamByIdDTO;
import com.api.cartolafc.dtos.RoundDTO;
import com.api.cartolafc.utils.Utils;
import static com.api.cartolafc.utils.Utils.BASE_URL;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TeamsService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;
    private final RoundsService roundsService;

    public TeamsService(RestTemplate restTemplate, RoundsService roundsService) {
        this.restTemplate = restTemplate;
        this.roundsService = roundsService;
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

    public Double calculateMonthlyPoints(String id) {
        try {
            List<RoundDTO> allRounds = roundsService.findRounds();
            LocalDate now = LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();
            
            double totalPoints = 0.0;
            
            for (RoundDTO round : allRounds) {
                try {
                    LocalDateTime startDate = LocalDateTime.parse(round.start(), DATE_TIME_FORMATTER);
                    LocalDate startLocalDate = startDate.toLocalDate();
                    
                    int roundMonth = startLocalDate.getMonthValue();
                    int roundYear = startLocalDate.getYear();
                    
                    // Rodada de mês anterior ao atual - pula para a próxima
                    if (roundYear < currentYear || 
                        (roundYear == currentYear && roundMonth < currentMonth)) {
                        continue;
                    }
                    
                    // Rodada de mês posterior ao atual - interrompe o loop
                    if (roundYear > currentYear || 
                        (roundYear == currentYear && roundMonth > currentMonth)) {
                        break;
                    }
                    
                    // Rodada do mês atual - processa
                    TeamByIdDTO teamInRound = findTeamByIdInRoundSafely(id, round.roundId());
                    if (teamInRound.points() != null) {
                        totalPoints += teamInRound.points();
                    }
                } catch (RoundNotAvailableException e) {
                    // Rodada não disponível, interrompe o loop pois as próximas também não estarão
                    break;
                } catch (Exception e) {
                    continue;
                }
            }
            
            return totalPoints;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private TeamByIdDTO findTeamByIdInRoundSafely(String id, int round) throws RoundNotAvailableException {
        try {
            return findTeamByIdInRound(id, round);
        } catch (HttpClientErrorException e) {                          
            if (e.getStatusCode().is4xxClientError()) {
                throw new RoundNotAvailableException();
            }
            throw new RoundNotAvailableException();
        } catch (Exception e) {
            throw new RoundNotAvailableException();
        }
    }

    private static class RoundNotAvailableException extends Exception {
    }
}
