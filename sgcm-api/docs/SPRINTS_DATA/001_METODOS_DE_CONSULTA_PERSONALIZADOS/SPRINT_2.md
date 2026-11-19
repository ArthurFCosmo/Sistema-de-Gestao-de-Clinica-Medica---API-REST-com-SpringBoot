# Dados e relatórios dos sprints

Neste arquivos são armazenadas informações a respeito do desenvolvimento do projeto.

---

## Sprint 2: Testes para consultas com filtros personalizados para a entidade MEDICO (Concluído)

### Contexto:

Após a implementação dos métodos de consultas personalizados para a entidade "Medico", ficaram pendedntes os testes.

### Todo:

Implementar os testes:

MedicoSpecsTest (Criar)
MedicoServiceTest (Atualizar)
MedicoControllerTest (Atualizar)

 e passá-los com sucesso.

### Sprint retrospective:

Todos os testes pendentes foram ajustados implementados e rodados com sucesso, levando em conta a nova arquitetura com paginação.

### Commit:

commit 309155d3c1159072cdb19d6ecc97e9fae0dde906 (HEAD -> staging, origin/staging, origin/main, origin/HEAD, main)
Author: Arthur Cosmo <arthurcosmoservices@gmail.com>
Date:   Sun Nov 15 18:01:35 2026 -0300

    Implementação/atualização dos testes para o endpoint de consulta personalizada da entidade Medico

### Próximo passo:

Após esse método de consulta em médico, seguiremos o roteiro de implementação. Listar consultas pelo Id do Médico.

| Done | Método | Endpoint | Descrição |
| ---  |   ---  |    ---   |    ---    |
|  [x]  | `GET` | `/api/medico` | Listar médicos por especialidade |
|  []  | `GET` | `/api/consulta` | Listar consultas de um médico |
|  []  | `GET` | `/api/consulta` | Listar consultas de um paciente |
|  []  | `GET` | `/api/consulta` | Listar consultas por status |
|  []  | `GET` | `/api/consulta` | Listar consultas em uma data específica (parâmetro `?data=yyyy-MM-dd`) |
|  []  | `GET` | `/api/prescricao` | Buscar prescrição de uma consulta |
