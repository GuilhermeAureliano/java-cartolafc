package com.api.cartolafc.services;

import com.api.cartolafc.dtos.AthleteDTO;
import com.api.cartolafc.dtos.TeamByIdDTO;
import com.api.cartolafc.dtos.TeamDTO;
import com.api.cartolafc.dtos.RoundDTO;
import com.api.cartolafc.dtos.ScoredAthletesOutputDTO;
import com.api.cartolafc.utils.Utils;
import static com.api.cartolafc.utils.Utils.BASE_URL;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TeamsService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Multiplicador de pontuação do capitão - regra do Cartola em que a pontuação do capitão vale 1.5x. */
    private static final double CAPTAIN_SCORE_MULTIPLIER = 1.5;

    private final RestTemplate restTemplate;
    private final RoundsService roundsService;
    private final AthletesService athletesService;

    public TeamsService(RestTemplate restTemplate, RoundsService roundsService, AthletesService athletesService) {
        this.restTemplate = restTemplate;
        this.roundsService = roundsService;
        this.athletesService = athletesService;
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
                    body.teamValue(),
                    body.athletes()
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
            
            List<RoundDTO> currentMonthRounds = allRounds.stream()
                    .filter(round -> {
                        try {
                            LocalDateTime startDate = LocalDateTime.parse(round.start(), DATE_TIME_FORMATTER);
                            LocalDate startLocalDate = startDate.toLocalDate();
                            return startLocalDate.getMonthValue() == currentMonth 
                                    && startLocalDate.getYear() == currentYear;
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
            
            if (currentMonthRounds.isEmpty()) {
                return 0.0;
            }

            ExecutorService executor = Executors.newFixedThreadPool(
                    Math.min(currentMonthRounds.size(), 4)
            );
            
            try {
                List<CompletableFuture<Double>> futures = currentMonthRounds.stream()
                        .map(round -> CompletableFuture.supplyAsync(() -> {
                            try {
                                TeamByIdDTO teamInRound = findTeamByIdInRound(id, round.roundId());
                                return teamInRound.points() != null ? teamInRound.points() : 0.0;
                            } catch (HttpClientErrorException e) {
                                return 0.0;
                            } catch (Exception e) {
                                return 0.0;
                            }
                        }, executor))
                        .collect(Collectors.toList());
                
                return futures.stream()
                        .map(CompletableFuture::join)
                        .mapToDouble(Double::doubleValue)
                        .sum();
            } finally {
                executor.shutdown();
            }
        } catch (Exception e) {
            return 0.0;
        }
    }

    public Optional<Double> calculatePartialScore(String id, int round) {
        TeamByIdDTO team;
        try {
            team = findTeamByIdInRound(id, round);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() != null && e.getStatusCode().value() == 404) {
                return Optional.empty();
            }
            throw e;
        }

        List<Integer> athleteIds = Optional.ofNullable(team.athletes())
                .orElse(List.of())
                .stream()
                .map(AthleteDTO::athleteId)
                .filter(Objects::nonNull)
                .toList();

        Integer captainId = parseCaptainId(team.captainId());

        ScoredAthletesOutputDTO scoredAthletes = athletesService.findScoredAthletes();
        Map<Integer, Double> scoreByAthleteId = scoredAthletes.atletas().stream()
                .collect(Collectors.toMap(ScoredAthletesOutputDTO.AthleteScore::athleteId, ScoredAthletesOutputDTO.AthleteScore::pontuacao, (a, b) -> a));

        double parcial = athleteIds.stream()
                .mapToDouble(athleteId -> {
                    double score = scoreByAthleteId.getOrDefault(athleteId, 0.0);
                    return athleteId.equals(captainId) ? score * CAPTAIN_SCORE_MULTIPLIER : score;
                })
                .sum();

        return Optional.of(parcial);
    }

    private Integer parseCaptainId(String captainId) {
        if (captainId == null || captainId.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(captainId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
