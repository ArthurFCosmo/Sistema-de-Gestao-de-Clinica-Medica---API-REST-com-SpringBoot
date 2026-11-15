# Dados e relatórios dos sprints

Neste arquivos são armazenadas informações a respeito do desenvolvimento do projeto.

---

## Sprint 2: Testes para consultas com filtros personalizados para a entidade MEDICO

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

### Próximo passo:

Após esse método de consulta em médico, seguiremos o roteiro de implementação. Listar consultas pelo Id do Médico.

| Done | Método | Endpoint | Descrição |
| ---  |   ---  |    ---   |    ---    |
|  [x]  | `GET` | `/api/medicos/especialidade/{especialidadeId}` | Listar médicos por especialidade |
|  []  | `GET` | `/api/consultas/medico/{medicoId}` | Listar consultas de um médico |
|  []  | `GET` | `/api/consultas/paciente/{pacienteId}` | Listar consultas de um paciente |
|  []  | `GET` | `/api/consultas/status/{status}` | Listar consultas por status |
|  []  | `GET` | `/api/consultas/medico/{medicoId}/status/{status}` | Listar consultas de um médico por status |
|  []  | `GET` | `/api/consultas/data` | Listar consultas em uma data específica (parâmetro `?data=yyyy-MM-dd`) |
|  []  | `GET` | `/api/prescricoes/consulta/{consultaId}` | Buscar prescrição de uma consulta |
