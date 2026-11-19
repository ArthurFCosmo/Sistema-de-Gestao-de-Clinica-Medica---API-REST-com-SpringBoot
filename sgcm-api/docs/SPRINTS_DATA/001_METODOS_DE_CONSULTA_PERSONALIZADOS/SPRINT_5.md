# Dados e relatórios dos sprints

Neste arquivos são armazenadas informações a respeito do desenvolvimento do projeto.

---

## Sprint 5: Implementação do filtro de consultas por paciente na entidade CONSULTA usando paginação (Concluído)

### Contexto:

Após a implementação dos métodos de consultas personalizados para a entidade "Consulta", faremos a implementação de outra specification, dessa vez para filtrar por paciente. Também desenvolveremos os testes da implementação.

### Todo:

Implementar a specification:

|  []  | `GET` | `/api/consulta/` | Listar consultas de um paciente |

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

commit 3b27607f0433d587096b7a5184d5d10af5fe236f (HEAD -> staging, origin/staging, origin/main, origin/HEAD, main)
Author: Arthur Cosmo <arthurcosmoservices@gmail.com>
Date:   Mon Nov 16 21:42:05 2026 -0300

    Adicao de nova specification no filtro de consulta, filtrando por paciente. Foram implementados testes para as modificacoes e novos cenarios de uso.

### Próximo passo:

Após essa specification na entidade consulta, seguiremos ainda na mesma entidade, para o filtro por status.

| Done | Método | Endpoint | Descrição |
| ---  |   ---  |    ---   |    ---    |
|  [x]  | `GET` | `/api/medico` | Listar médicos por especialidade |
|  [x]  | `GET` | `/api/consulta` | Listar consultas de um médico |
|  [x]  | `GET` | `/api/consulta` | Listar consultas de um paciente |
|  []  | `GET` | `/api/consulta` | Listar consultas por status |
|  []  | `GET` | `/api/consulta` | Listar consultas em uma data específica (parâmetro `?data=yyyy-MM-dd`) |
|  []  | `GET` | `/api/prescricao` | Buscar prescrição de uma consulta |