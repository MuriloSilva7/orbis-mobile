# Frontend: comentarios em alertas

Este documento descreve a integracao do frontend com o recurso de comentarios/notas operacionais na timeline de alertas.

## Resumo

Alertas agora aceitam comentarios criados por usuarios `ADMIN` e `TECNICO`. O comentario e salvo como um evento da timeline do alerta com `tipo: "COMENTARIO"`.

O recurso nao altera status do alerta, SLA, tecnico responsavel, manutencao ou `encerradoEm`.

Usuarios `VISITANTE` podem visualizar comentarios nas consultas de eventos, mas nao podem criar comentarios.

## Criar comentario

Endpoint:

```http
POST /alertas/:id/comentarios
```

Payload:

```json
{
  "mensagem": "Verifiquei a maquina e a vibracao vem do eixo principal."
}
```

Resposta esperada:

```json
{
  "id": 31,
  "alertaId": 12,
  "usuarioId": 5,
  "tipo": "COMENTARIO",
  "mensagem": "Verifiquei a maquina e a vibracao vem do eixo principal.",
  "descricao": "Comentario adicionado",
  "criadoEm": "2026-06-10T14:30:00.000Z",
  "usuario": {
    "id": 5,
    "nome": "Carlos",
    "email": "carlos@orbis.com",
    "role": "TECNICO"
  },
  "manutencao": null
}
```

## Validacoes

- `mensagem` e obrigatoria.
- O backend aplica `trim` na mensagem.
- Mensagem vazia retorna `400`.
- Mensagem com mais de 1000 caracteres retorna `400`.
- Alerta inexistente retorna `404`.
- Usuario sem permissao retorna `403`.

## Permissoes

| Perfil | Pode ver | Pode comentar |
| --- | --- | --- |
| `ADMIN` | Sim | Sim |
| `TECNICO` | Sim | Sim |
| `VISITANTE` | Sim | Nao |

## Onde os comentarios aparecem

Comentarios aparecem como eventos `COMENTARIO` nas consultas atuais:

- `GET /alertas/:id`
- `GET /alertas/:id/eventos`
- `GET /alertas/eventos`

Exemplo de evento na timeline:

```json
{
  "id": 31,
  "alertaId": 12,
  "tipo": "COMENTARIO",
  "mensagem": "Verifiquei a maquina e a vibracao vem do eixo principal.",
  "descricao": "Comentario adicionado",
  "criadoEm": "2026-06-10T14:30:00.000Z",
  "usuario": {
    "id": 5,
    "nome": "Carlos",
    "email": "carlos@orbis.com",
    "role": "TECNICO"
  },
  "manutencao": null
}
```

## Sugestoes de UI

- No detalhe do alerta, exibir eventos `COMENTARIO` dentro da timeline.
- Usar um card/linha visual diferente de eventos de status, por exemplo "Nota de Carlos".
- Mostrar autor, role e data/hora.
- Exibir campo de comentario apenas para `ADMIN` e `TECNICO`.
- Para `VISITANTE`, esconder o formulario de comentario.
- Apos criar comentario, o front pode:
  - adicionar o evento retornado no topo da timeline; ou
  - recarregar `GET /alertas/:id/eventos`.

## Observacoes

- Comentarios nao sao editaveis nem deletaveis nesta primeira versao.
- A timeline continua sendo a fonte principal para entender o historico do alerta.
- A IA consegue ler comentarios porque eles aparecem nos eventos do alerta.
