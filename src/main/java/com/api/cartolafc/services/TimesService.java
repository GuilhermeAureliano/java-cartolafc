package com.api.cartolafc.services;

import com.api.cartolafc.dtos.TimeDTO;
import com.api.cartolafc.dtos.TimePorIdDTO;
import com.api.cartolafc.utils.Utils;
import static com.api.cartolafc.utils.Utils.BASE_URL;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TimesService {

    private final RestTemplate restTemplate;

    public TimesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TimeDTO buscarTimePorNome(String nome) {
        String url = BASE_URL + "/times?q=" + nome;

        ResponseEntity<List<TimeDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                Utils.createHttpEntityWithUserAgent(),
                new ParameterizedTypeReference<>() {
                }
        );

        List<TimeDTO> times = response.getBody();
        if (times == null || times.isEmpty()) {
            return null;
        }

        String termoNormalizado = Utils.normalizarSlug(nome);
        TimeDTO timeEncontrado = times.stream()
                .filter(time -> time.slug().equalsIgnoreCase(termoNormalizado)
                        || time.nome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);
        
        if (timeEncontrado == null) {
            return null;
        }
        
        // Limpa a URL do escudo removendo o prefixo do CDN
        String urlEscudoLimpa = Utils.limparUrlEscudo(timeEncontrado.urlEscudoPng());
        return new TimeDTO(
                timeEncontrado.temporadaInicial(),
                timeEncontrado.nomeCartola(),
                timeEncontrado.nome(),
                urlEscudoLimpa,
                timeEncontrado.slug(),
                timeEncontrado.timeId()
        );
    }

    public TimePorIdDTO buscarTimePorId(String id) {
        return buscarTimePorUrl(BASE_URL + "/time/id/" + id);
    }

    public TimePorIdDTO buscarTimePorIdNaRodada(String id, int rodada) {
        return buscarTimePorUrl(BASE_URL + "/time/id/" + id + "/" + rodada);
    }

    private TimePorIdDTO buscarTimePorUrl(String url) {
        ResponseEntity<TimePorIdDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                Utils.createHttpEntityWithUserAgent(),
                TimePorIdDTO.class
        );

        TimePorIdDTO body = response.getBody();
        if (body == null) {
            throw new RuntimeException("Resposta da API está vazia.");
        }

        if (body.time() != null && body.time().urlEscudoPng() != null) {
            String urlEscudoLimpa = Utils.limparUrlEscudo(body.time().urlEscudoPng());
            TimeDTO timeLimpo = new TimeDTO(
                    body.time().temporadaInicial(),
                    body.time().nomeCartola(),
                    body.time().nome(),
                    urlEscudoLimpa,
                    body.time().slug(),
                    body.time().timeId()
            );
            return new TimePorIdDTO(
                    timeLimpo,
                    body.pontosCampeonato(),
                    body.capitaoId(),
                    body.reservaLuxoId(),
                    body.pontos(),
                    body.variacaoPatrimonio(),
                    body.esquemaId(),
                    body.rodadaAtual(),
                    body.patrimonio(),
                    body.valorTime()
            );
        }

        return body;
    }
}