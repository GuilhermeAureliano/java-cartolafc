# Contribuindo

Obrigado por considerar contribuir com este projeto.

## Pré-requisitos

- **Java 21**
- Git
- (Opcional) Docker / Docker Compose

## Como rodar o projeto

Veja o `README.md` para as opções local/Docker.

## Padrões do projeto

- **Mensagens e textos do backend**: em **português** (inclusive mensagens de erro).
- **Nomes de classes/métodos**: em **inglês** (padrão do código já existente).
- **Endpoints**: em português quando refletirem o domínio (ex.: `/rodadas`, `/mercado`).
- **Swagger/OpenAPI**: novos endpoints devem ter `@Operation`, `@ApiResponses` e `@Parameter` quando aplicável.

## Sugestão de fluxo de PR

- Crie uma branch a partir de `main`
- Faça mudanças pequenas e objetivas
- Atualize/adicione documentação quando necessário
- Garanta que os testes passam:

```bash
./mvnw test
```

## Reportar bugs / pedir melhorias

Ao abrir um issue, inclua:

- Passos para reproduzir
- Resultado esperado vs. obtido
- Logs relevantes (se possível)
- Versão do Java e do Docker (se aplicável)

