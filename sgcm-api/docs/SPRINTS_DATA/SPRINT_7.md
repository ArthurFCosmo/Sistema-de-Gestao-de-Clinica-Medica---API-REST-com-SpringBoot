# Dados e relatórios dos sprints

Neste arquivos são armazenadas informações a respeito do desenvolvimento do projeto.

---

## Sprint 6: Implementação do filtro de consultas por data na entidade CONSULTA

### Contexto:

Dando continuidade à implementação de novas specifications no nosso endpoint de filtrar busca de consultas, implementaremos agora a specification de filtrar por data.

### Todo:

Implementar a specification:

|  []  | `GET` | `/api/consulta` | Listar consultas em uma data específica (parâmetro `?data=yyyy-MM-dd`) |

Também devem ser implementados os testes para as classes criadas/modificadas.

### Sprint retrospective:

O novo filtro foi implementado, e os testes foram atualizados. Todos os testes passaram.

#### Ponto interessante do desenvolvimento: Lógica da construção do predicado do filtro

```
public static Specification<Consulta> comData(LocalDate data) {
        return (root, query, cb) -> {
            if (data == null) { return null; }

            LocalDateTime dataInicio = data.atStartOfDay();
            LocalDateTime dataFim = data.plusDays(1).atStartOfDay();

            return cb.and(
                cb.greaterThanOrEqualTo(root.get("dataHora"), dataInicio),
                cb.lessThan(root.get("dataHora"), dataFim)
            );
        };
    }
```

`cb.and` junta dois predicados com AND, eles são declarados como parâmetros.

### Commit:

### Próximo passo:

Após essa specification na entidade consulta, seguiremos para a entidade prescricao. Nela criaremos o endpoint com paginação, e adicionaremos o filtro de prescrição de uma consulta.

| Done | Método | Endpoint | Descrição |
| ---  |   ---  |    ---   |    ---    |
|  [x]  | `GET` | `/api/medico` | Listar médicos por especialidade |
|  [x]  | `GET` | `/api/consulta` | Listar consultas de um médico |
|  [x]  | `GET` | `/api/consulta` | Listar consultas de um paciente |
|  [x]  | `GET` | `/api/consulta` | Listar consultas por status |
|  [x]  | `GET` | `/api/consulta` | Listar consultas em uma data específica (parâmetro `?data=yyyy-MM-dd`) |
|  []  | `GET` | `/api/prescricao` | Buscar prescrição de uma consulta |