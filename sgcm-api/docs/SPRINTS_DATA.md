# Dados e relatórios dos sprints

Neste arquivos são armazenadas informações a respeito do desenvolvimento do projeto.


---

## Sprint 1: Endpoints para consultas com filtros personalizados para a entidade MEDICO

### Contexto:

Após as implementações dos endpoints básicos do CRUD, serão implementados mais métodos de consulta aos dados da API.

| Done | Método | Endpoint | Descrição |
| ---  |   ---  |    ---   |    ---    |
|  []  | `GET` | `/api/medicos/especialidade/{especialidadeId}` | Listar médicos por especialidade |
|  []  | `GET` | `/api/consultas/medico/{medicoId}` | Listar consultas de um médico |
|  []  | `GET` | `/api/consultas/paciente/{pacienteId}` | Listar consultas de um paciente |
|  []  | `GET` | `/api/consultas/status/{status}` | Listar consultas por status |
|  []  | `GET` | `/api/consultas/medico/{medicoId}/status/{status}` | Listar consultas de um médico por status |
|  []  | `GET` | `/api/consultas/data` | Listar consultas em uma data específica (parâmetro `?data=yyyy-MM-dd`) |
|  []  | `GET` | `/api/prescricoes/consulta/{consultaId}` | Buscar prescrição de uma consulta |


Nesse sprint faremos APENAS o "Listar médicos por especialidade"

### Todo:

Implementar o endpoint personalizado.

### Sprint retrospective:

Inicialmente o endpoint foi implementado com @PathVariable de maneira simples, retornando uma lista com o resultado completo da consluta, sem paginação.

Então, decidi modificar o endpoint já existente com o @GetMapping, criando uma classe de Specifications, e colocando um filtro no MedicoService apenas para filtrar por especialidadeId.

Assim, o endpoint @GetMapping retornanda: Uma lista vazia |ou| Uma lista com todos os Medicos (Mas com paginação) |ou| Uma lista filtrada por especialidadeId |ou| 404 quando não existir especialidade com o id especificado.

O endpoint @GetMapping("/{id}") continua existindo, trazendo o recurso específico por Id.

### Commit: 

Author: Arthur Cosmo <arthurcosmoservices@gmail.com>
Date:   Wed Nov 11 23:40:23 2026 -0300

    Reestruturação no endpoint get da entidade Medico, agora com paginação e filtros personalizados implementados

### Próximo passo:

Devem ser ajustadas as classes MedicoServiceTest e MedicoControllerTest. Também deve ser criada a classe MedicoSpecsTest com @DataJpaTest.

---

## Sprint 3: Documentação da aplicação com Swagger

### Contexto:

Após as implementações das camadas service, controller e o tratamento de excecções, resta apenas a implementação da segurança com SpringSecurity.
Antes dessa implementação, documentarei toda o sistema com o Swagger.

### Todo:

Implementar e revisar documentação com Swagger.

### Sprint retrospective:

---