# Explicação: UI (frontend estático)

## Onde fica

A UI está em:

- `src/main/resources/static/index.html`
- `src/main/resources/static/js/*`
- `src/main/resources/static/css/*`
- `src/main/resources/static/data/times.csv`

Quando a aplicação sobe, a UI fica disponível em `GET /`.

## Como funciona

Em alto nível:

- A UI lê o arquivo `data/times.csv` (uma linha por time).
- Para cada time, chama `GET /times?q={nome}` para descobrir o `time_id`.
- Com o `time_id`, chama:
  - `GET /time/id/{id}` para detalhes e pontos,
  - `GET /time/id/{id}/pontos-mensais` para o agregado do mês.
- Com esses dados, monta a tabela, permite busca e ordenação.

## Customização

### Editar os times exibidos

Edite `src/main/resources/static/data/times.csv` e coloque um time por linha.

### Base URL da API

No arquivo `src/main/resources/static/js/api.js` existe `API_BASE = ''`.

- Vazio (`''`) significa “mesma origem” (ideal quando UI e API rodam juntas).
- Se você quiser apontar para outro host/porta, preencha com algo como `http://localhost:8081`.

