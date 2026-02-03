# Java - Cartola FC (API + UI)

Aplicação em **Java 21** com **Spring Boot** que **consome a API oficial do Cartola FC** e expõe endpoints REST (com **Swagger/OpenAPI**), além de uma **UI estática** em `src/main/resources/static/` para acompanhar ranking/estatísticas de uma liga via `data/times.csv`.

## O que você encontra aqui

- **API REST**: endpoints para clubes, rodadas, mercado, times e atletas pontuados.
- **Swagger/OpenAPI**:
  - Swagger UI: `http://localhost:8080/swagger-ui/index.html`
  - OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- **Healthcheck para containers**: `http://localhost:8080/actuator/health`
- **UI (página inicial)**: `http://localhost:8080/`

## Requisitos

- **Java 21**
- (Opcional) **Docker** / **Docker Compose**

> O projeto inclui Maven Wrapper (`./mvnw`), então você não precisa ter Maven instalado.

## Rodar localmente

```bash
./mvnw spring-boot:run
```

Se a porta `8080` já estiver em uso, rode em outra porta:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## Rodar com Docker

### Build e execução (Docker)

```bash
docker build -t cartolafc .
docker run --rm -p 8080:8080 cartolafc
```

### Build e execução (Docker Compose)

```bash
docker compose up --build
```

## Referência rápida da API (endpoints)

> A API faz chamadas para `https://api.cartola.globo.com` (inclui `User-Agent` padrão nas requisições).

- **Clubes**: `GET /clubes`
- **Rodadas**: `GET /rodadas`
- **Mercado (status)**: `GET /mercado/status`
- **Atletas pontuados**: `GET /atletas/pontuados`
- **Buscar time por nome**: `GET /times?q={nome}`
- **Time por ID**: `GET /time/id/{id}`
- **Time por ID e rodada**: `GET /time/id/{id}/{rodada}`
- **Parcial por rodada (capitão 1.5x)**: `GET /time/id/{id}/{rodada}/parcial`
- **Pontos mensais (mês atual)**: `GET /time/id/{id}/pontos-mensais`

### Exemplos com curl

```bash
curl -s "http://localhost:8080/mercado/status"
curl -s "http://localhost:8080/clubes"
curl -s "http://localhost:8080/rodadas"
curl -s "http://localhost:8080/atletas/pontuados"
curl -s "http://localhost:8080/times?q=Sportv"
curl -s "http://localhost:8080/time/id/398396"
curl -s "http://localhost:8080/time/id/398396/2"
curl -s "http://localhost:8080/time/id/398396/2/parcial"
curl -s "http://localhost:8080/time/id/398396/pontos-mensais"
```

## Documentação

- **Explicação (arquitetura)**: [`docs/explanation/architecture.md`](docs/explanation/architecture.md)
- **Explicação (UI/CSV)**: [`docs/explanation/frontend.md`](docs/explanation/frontend.md)

## Autor

[@guilhermeaureliano](https://github.com/guilhermeaureliano)
