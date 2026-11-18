# Dados e relatórios dos sprints

Neste arquivos são armazenadas informações a respeito do desenvolvimento do projeto.

---

## Sprint 6: Implementação do filtro de consultas por status na entidade CONSULTA (Concluído)

### Contexto:

Dando continuidade à implementação de novas specifications no nosso endpoint de filtrar busca de consultas, implementaremos agora a specification de filtrar por status.

### Todo:

Implementar a specification:

|  []  | `GET` | `/api/consulta` | Listar consultas por status |


Também devem ser implementados os testes para as classes criadas/modificadas.

### Sprint retrospective:

O novo filtro foi criado ConsultaSpecs.comStatus(), o service e o controller receberam essa implementação.

Os testes das 3 classes foram adaptados, e passaram com sucesso.

### Commit:

commit 4568f19a6054b76f0ef5096533499bae484622e3 (HEAD -> staging, origin/staging, origin/main, origin/HEAD, main)
Author: Arthur Cosmo <arthurcosmoservices@gmail.com>
Date:   Tue Nov 17 19:41:33 2026 -0300

    Implementacao do filtro de consultas por status no endpoint @Get + testes

### Próximo passo:

Após essa specification na entidade consulta, seguiremos ainda na mesma entidade, para o filtro por data.

| Done | Método | Endpoint | Descrição |
| ---  |   ---  |    ---   |    ---    |
|  [x]  | `GET` | `/api/medico` | Listar médicos por especialidade |
|  [x]  | `GET` | `/api/consulta` | Listar consultas de um médico |
|  [x]  | `GET` | `/api/consulta` | Listar consultas de um paciente |
|  [x]  | `GET` | `/api/consulta` | Listar consultas por status |
|  []  | `GET` | `/api/consulta` | Listar consultas em uma data específica (parâmetro `?data=yyyy-MM-dd`) |
|  []  | `GET` | `/api/prescricao` | Buscar prescrição de uma consulta |