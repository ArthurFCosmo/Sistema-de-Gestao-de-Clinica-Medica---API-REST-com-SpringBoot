# Dados e relatórios dos sprints

Neste arquivos são armazenadas informações a respeito do desenvolvimento do projeto.

---

## Sprint 4: Testes para consultas com filtros personalizados para a entidade CONSULTA

### Contexto:

Após a implementação dos métodos de consultas personalizados para a entidade "Consulta", ficaram pendedntes os testes.

### Todo:

Implementar os testes:

ConsultaSpecsTest (Criar)
ConsultaServiceTest (Atualizar)
ConsultaControllerTest (Atualizar)

 e passá-los com sucesso.

### Sprint retrospective:

Todos os testes pendentes foram ajustados implementados e rodados com sucesso, levando em conta a nova arquitetura com paginação.

### Commit:

commit 7d5e9145b999f2173e62b2152404cc938808978b (HEAD -> staging, origin/staging, origin/main, origin/HEAD, main)
Author: Arthur Cosmo <arthurcosmoservices@gmail.com>
Date:   Mon Nov 16 20:18:12 2026 -0300

    Implementação/atualização dos testes para novo endpoint @Get com paginação para a entidade Consulta


### Próximo passo:

Após esse método de consulta em médico, seguiremos o roteiro de implementação. Listar consultas pelo Id do Paciente.

| Done | Método | Endpoint | Descrição |
| ---  |   ---  |    ---   |    ---    |
|  [x]  | `GET` | `/api/medico/especialidade/{especialidadeId}` | Listar médicos por especialidade |
|  [x]  | `GET` | `/api/consulta/medico/{medicoId}` | Listar consultas de um médico |
|  []  | `GET` | `/api/consulta/paciente/{pacienteId}` | Listar consultas de um paciente |
|  []  | `GET` | `/api/consulta/status/{status}` | Listar consultas por status |
|  []  | `GET` | `/api/consulta/medico/{medicoId}/status/{status}` | Listar consultas de um médico por status |
|  []  | `GET` | `/api/consulta/data` | Listar consultas em uma data específica (parâmetro `?data=yyyy-MM-dd`) |
|  []  | `GET` | `/api/prescricao/consulta/{consultaId}` | Buscar prescrição de uma consulta |
