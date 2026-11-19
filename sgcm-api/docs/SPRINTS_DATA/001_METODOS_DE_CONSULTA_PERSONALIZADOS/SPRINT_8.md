# Dados e relatórios dos sprints

Neste arquivos são armazenadas informações a respeito do desenvolvimento do projeto.

---

## Sprint 8: Implementação do filtro de prescricao por id da consulta na entidade PRESCRICAO (Concluído)

### Contexto:

Criaremos um novo endpoint. Diferente dos sprints recentes, em que adaptamos o endpoint @Get existente para usar paginação, dessa vez não usaremos paginação, pois é consulta a um item específico e único, não uma lista de itens, então não faz sentido paginar.

### Todo:

Implementar o novo endpoint em PrescricaoController.

|  []  | `GET` | `/api/prescricao/consulta/{consultaId}` | Buscar prescrição de uma consulta |

Também devem ser desenvolvidos os testes das respectivas classes.

### Sprint retrospective:

O novo endpoint foi criado, foi adicionado um novo método no PrescricaoRepository para realizar a busca por id da consulta. Controller e Service também foram atualizados.
Os testes dessas classes foram desenvolvidos/atualizados, e todos passaram.

### Commit:

commit 3f6f031c0195f9c4338b07561007afdb65995084 (HEAD -> staging, origin/staging, origin/main, origin/HEAD, main)
Author: Arthur Cosmo <arthurcosmoservices@gmail.com>
Date:   Wed Nov 18 20:38:30 2026 -0300

    Novo endpoint get: consultar a prescricao pelo id da consulta


### Próximo passo:

Por enquanto, todos os métodos de consulta personalizados exigidos foram implementados com sucesso.

| Done | Método | Endpoint | Descrição |
| ---  |   ---  |    ---   |    ---    |
|  [x]  | `GET` | `/api/medico` | Listar médicos por especialidade |
|  [x]  | `GET` | `/api/consulta` | Listar consultas de um médico |
|  [x]  | `GET` | `/api/consulta` | Listar consultas de um paciente |
|  [x]  | `GET` | `/api/consulta` | Listar consultas por status |
|  [x]  | `GET` | `/api/consulta` | Listar consultas em uma data específica (parâmetro `?data=yyyy-MM-dd`) |
|  [x]  | `GET` | `/api/prescricao` | Buscar prescrição de uma consulta |

No próximo Sprint deveremos implementar nas demais entidades o ajuste no método @Get, para todos funcionarem por com paginação, mesmo sem filtros.