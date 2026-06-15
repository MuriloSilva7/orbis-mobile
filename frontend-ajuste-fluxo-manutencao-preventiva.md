# Ajuste de Fluxo - Manutencao Preventiva

Este mini README corrige um ponto do fluxo de manutencao preventiva.

## Regra Corrigida

Manutencao preventiva deve ser criada apenas por usuarios `TECNICO`.

O `ADMIN` pode visualizar manutencoes preventivas, mas nao deve conseguir criar uma preventiva diretamente.

## O Que Muda No Front

### Para `TECNICO`

Manter o fluxo de criacao preventiva:

```json
{
  "tipo": "PREVENTIVA",
  "maquinaId": 8,
  "observacao": "Inspecao preventiva mensal."
}
```

Endpoint:

```http
POST /manutencoes
```

### Para `ADMIN`

O front deve ocultar ou desabilitar a opcao de criar manutencao preventiva.

O admin ainda pode:

- visualizar manutencoes preventivas;
- visualizar manutencoes corretivas;
- criar manutencao corretiva quando o fluxo estiver ligado a um alerta, se essa tela existir para admin.

O admin nao deve:

- ver o botao "Criar preventiva";
- enviar `POST /manutencoes` com `tipo: "PREVENTIVA"`.

## Comportamento Da API

Se um admin tentar criar preventiva, a API retorna:

```http
403 Forbidden
```

Mensagem:

```json
{
  "mensagem": "Apenas tecnicos podem criar manutencao preventiva!"
}
```

## Fluxo Recomendado

Na tela de manutencoes:

- Se `role === "TECNICO"`: mostrar botao de criar preventiva.
- Se `role === "ADMIN"`: mostrar apenas listagem e detalhes.
- Se `role === "VISITANTE"`: mostrar apenas leitura, sem botoes de acao.

Na tela de detalhe de maquina:

- Se `role === "TECNICO"`: permitir criar preventiva para aquela maquina.
- Se `role === "ADMIN"` ou `VISITANTE`: ocultar a acao de criar preventiva.

## Motivo

A manutencao preventiva representa uma acao operacional executada por tecnico. O admin acompanha e gerencia o sistema, mas nao registra esse tipo de execucao diretamente.
