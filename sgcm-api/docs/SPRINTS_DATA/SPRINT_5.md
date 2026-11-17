# Dados e relatórios dos sprints

Neste arquivos são armazenadas informações a respeito do desenvolvimento do projeto.

---

## Sprint 5: Implementação do filtro de consultas por paciente na entidade CONSULTA usando paginação

### Contexto:

Após a implementação dos métodos de consultas personalizados para a entidade "Consulta", faremos a implementação de outra specification, dessa vez para filtrar por paciente. Também desenvolveremos os testes da implementação.

### Todo:

Implementar o endpoint:

|  []  | `GET` | `/api/consulta/paciente/{pacienteId}` | Listar consultas de um paciente |

Esse endpoint deve modificar o @Get existente, assim como nos sprints passados.

Também devem ser implementados os testes para as classes criadas/modificadas.

### Sprint retrospective:

Foi adicionada a specification comPaciente() com sucesso, e foram adaptadas as classes de teste. Todos os testes passaram.

#### Ponto interessante na implementação

No consultaservice, começamos a usar specificantions encadeadas para gerar um predicado WHERE mais complexo nos filtros:

```
// Gerando um predicado vazio para o WHERE, que pede um AND (Por si só é uma specification que não filtra nada, deixa passar)
        Specification<Consulta> spec = (root, query, cb) -> cb.conjunction();
        if (medicoId != null) {
            spec = spec.and(ConsultaSpecs.comMedico(medicoId));
        }
        if (pacienteId != null) {
            spec = spec.and(ConsultaSpecs.comPaciente(pacienteId));
        }
```

### Commit:

### Próximo passo:

Após essa specification na entidade consulta, seguiremos ainda na mesma entidade, para o filtro por status.

| Done | Método | Endpoint | Descrição |
| ---  |   ---  |    ---   |    ---    |
|  [x]  | `GET` | `/api/medico/especialidade/{especialidadeId}` | Listar médicos por especialidade |
|  [x]  | `GET` | `/api/consulta/medico/{medicoId}` | Listar consultas de um médico |
|  [x]  | `GET` | `/api/consulta/paciente/{pacienteId}` | Listar consultas de um paciente |
|  []  | `GET` | `/api/consulta/status/{status}` | Listar consultas por status |
|  []  | `GET` | `/api/consulta/medico/{medicoId}/status/{status}` | Listar consultas de um médico por status |
|  []  | `GET` | `/api/consulta/data` | Listar consultas em uma data específica (parâmetro `?data=yyyy-MM-dd`) |
|  []  | `GET` | `/api/prescricao/consulta/{consultaId}` | Buscar prescrição de uma consulta |