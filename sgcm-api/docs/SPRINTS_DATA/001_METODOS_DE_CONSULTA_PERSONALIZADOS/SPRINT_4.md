# Dados e relatórios dos sprints

Neste arquivos são armazenadas informações a respeito do desenvolvimento do projeto.

---

## Sprint 4: Testes para consultas com filtros personalizados para a entidade CONSULTA (Concluído)

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
|  [x]  | `GET` | `/api/medico` | Listar médicos por especialidade |
|  [x]  | `GET` | `/api/consulta` | Listar consultas de um médico |
|  []  | `GET` | `/api/consulta` | Listar consultas de um paciente |
|  []  | `GET` | `/api/consulta` | Listar consultas por status |
|  []  | `GET` | `/api/consulta` | Listar consultas em uma data específica (parâmetro `?data=yyyy-MM-dd`) |
|  []  | `GET` | `/api/prescricao` | Buscar prescrição de uma consulta |
