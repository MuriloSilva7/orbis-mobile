# Frontend - Manutencao Preventiva Sem Alerta

Este documento descreve as mudancas de API para o front adaptar o fluxo de manutencoes preventivas. O arquivo e informativo e nao precisa ser versionado.

## Resumo

Agora uma manutencao pode ser:

- `CORRETIVA`: criada a partir de um alerta, como ja existia.
- `PREVENTIVA`: criada diretamente para uma maquina, sem alerta.

Tecnicos podem criar manutencoes preventivas. Admins e visitantes visualizam, mas nao criam preventiva.

## Criar Manutencao

Endpoint:

```http
POST /manutencoes
```

### Corretiva

Payload atual continua valido:

```json
{
  "alertaId": 12,
  "observacao": "Iniciando atendimento do alerta."
}
```

### Preventiva

Novo payload:

```json
{
  "tipo": "PREVENTIVA",
  "maquinaId": 8,
  "observacao": "Inspecao preventiva mensal."
}
```

Regras:

- `tipo` deve ser `PREVENTIVA`.
- `maquinaId` e obrigatorio.
- `alertaId` nao deve ser enviado para preventiva.
- `observacao` e obrigatoria.

## Listar Manutencoes

Endpoint:

```http
GET /manutencoes
```

Comportamento de listagem por perfil:

- `ADMIN`: lista corretivas e preventivas.
- `VISITANTE`: lista corretivas e preventivas, somente leitura.
- `TECNICO`: lista preventivas de todos os tecnicos.

Isso permite ao tecnico consultar historico preventivo de uma maquina, mesmo quando a preventiva foi criada por outro tecnico.

## Campos Novos

As respostas de manutencao passam a trazer:

```json
{
  "id": 31,
  "tipo": "PREVENTIVA",
  "alertaId": null,
  "maquinaId": 8,
  "usuarioId": 5,
  "observacao": "Inspecao preventiva mensal.",
  "status": "EM_ANDAMENTO",
  "criadoEm": "2026-06-10T15:00:00.000Z",
  "alerta": null,
  "maquina": {
    "id": 8,
    "nome": "Prensa Hidraulica",
    "setor": "Producao",
    "tipo": "Prensa",
    "criticidade": "ALTA",
    "ativo": true,
    "integridade": 87,
    "scoreEstabilidade": 92
  },
  "usuario": {
    "id": 5,
    "nome": "Carlos",
    "email": "carlos@orbis.com",
    "role": "TECNICO",
    "telefone": "11999999999",
    "especialidade": "Mecanica"
  }
}
```

Para manutencao corretiva, `alerta` vem preenchido. Para preventiva, `alerta` vem `null`.

## Atualizar Manutencao

Endpoint:

```http
PUT /manutencoes/:id
```

Payload continua igual:

```json
{
  "status": "RESOLVIDO",
  "observacao": "Preventiva finalizada."
}
```

Quando uma preventiva e marcada como `RESOLVIDO`, o backend normaliza a maquina:

- integridade volta para `100`;
- score de estabilidade volta para `100`;
- previsoes de manutencao/falha sao limpas;
- sensores voltam aos valores ideais.

## Ajustes Sugeridos No Front

- Adicionar seletor de tipo no formulario: `Corretiva` ou `Preventiva`.
- Se `Preventiva`, mostrar seletor de maquina e esconder seletor/campo de alerta.
- Nas listas e detalhes, mostrar badge `Preventiva` ou `Corretiva`.
- Tratar `alerta: null` sem tentar renderizar link/detalhe de alerta.
- Mostrar nome da maquina e tecnico responsavel nas preventivas.
- Para visitante, manter botoes de criar/editar/encerrar ocultos ou desabilitados.

## Erros Esperados

- `400`: tipo invalido, maquina invalida, observacao invalida.
- `404`: maquina nao encontrada.
- `403`: admin ou visitante tentando criar preventiva; visitante tentando criar ou atualizar.
- `409`: tentativa de atualizar manutencao ja encerrada.
