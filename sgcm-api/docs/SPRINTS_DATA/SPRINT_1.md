# Dados e relatórios dos sprints

Neste arquivos são armazenadas informações a respeito do desenvolvimento do projeto.

---

## Sprint 1: Endpoints para consultas com filtros personalizados para a entidade MEDICO (Concluído)

### Contexto:

Após as implementações dos endpoints básicos do CRUD, serão implementados mais métodos de consulta aos dados da API.

| Done | Método | Endpoint | Descrição |
| ---  |   ---  |    ---   |    ---    |
|  []  | `GET` | `/api/medico` | Listar médicos por especialidade |
|  []  | `GET` | `/api/consulta` | Listar consultas de um médico |
|  []  | `GET` | `/api/consulta` | Listar consultas de um paciente |
|  []  | `GET` | `/api/consulta` | Listar consultas por status |
|  []  | `GET` | `/api/consulta` | Listar consultas em uma data específica (parâmetro `?data=yyyy-MM-dd`) |
|  []  | `GET` | `/api/prescricao` | Buscar prescrição de uma consulta |

Nesse sprint faremos APENAS o "Listar médicos por especialidade"

### Todo:

Implementar o endpoint personalizado.

### Sprint retrospective:

Inicialmente o endpoint foi implementado com @PathVariable de maneira simples, retornando uma lista com o resultado completo da consluta, sem paginação.

Então, decidi modificar o endpoint já existente com o @GetMapping, criando uma classe de Specifications, e colocando um filtro no MedicoService apenas para filtrar por especialidadeId.

Assim, o endpoint @GetMapping retornanda: Uma lista vazia |ou| Uma lista com todos os Medicos (Mas com paginação) |ou| Uma lista filtrada por especialidadeId |ou| 404 quando não existir especialidade com o id especificado.

O endpoint @GetMapping("/{id}") continua existindo, trazendo o recurso específico por Id.

### Commit: 

commit 43a51ec34987f56dcd92d90ac406926c9bab51e9 (HEAD -> staging, origin/staging)
Author: Arthur Cosmo <arthurcosmoservices@gmail.com>
Date:   Wed Nov 11 23:40:23 2026 -0300

    Reestruturação no endpoint get da entidade Medico, agora com paginação e filtros personalizados implementados

### Próximo passo:

Devem ser ajustadas as classes MedicoServiceTest e MedicoControllerTest. Também deve ser criada a classe MedicoSpecsTest com @DataJpaTest.
