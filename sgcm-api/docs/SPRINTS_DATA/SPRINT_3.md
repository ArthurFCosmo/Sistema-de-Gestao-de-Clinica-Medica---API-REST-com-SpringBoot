# Dados e relatórios dos sprints

Neste arquivos são armazenadas informações a respeito do desenvolvimento do projeto.

---

## Sprint 3: Implementação do endpoint de filtrar consultas por Médico na rota /consulta usando paginação e reutilizando o antigo endpoint @GET.

### Contexto:

Após a implementação dos métodos de consultas personalizados para a entidade "Medico", faremos operação semelhante no endpoint /consluta. Um filtro de consultas atreladas a um médico.

### Todo:

Implementar o filtro com paginação tanto em ConsultaService quanto em ConsultaController, depois implementar os testes unitários e passá-los com sucesso.

|  []  | `GET` | `/api/consulta/medico/{medicoId}` | Listar consultas de um médico |

### Sprint retrospective:

O ConsultaService foi modificado, a classe ConsultaSpecs foi gerada sem problemas, e o ConsultaController teve seu endpoint @Get atualizado.

### Commit:

commit 14f9a77e619027cdc0d7f508fff45d950b3c4969 (HEAD -> staging, origin/staging, origin/main, origin/HEAD, main)
Author: Arthur Cosmo <arthurcosmoservices@gmail.com>
Date:   Sun Nov 15 23:49:42 2026 -0300

    Implementação do endpoint de consulta personalizada @Get para a entidade Consulta (Listar consultas por medico)


### Próximo passo:

Implementar os testes:

ConsultaSpecsTest (Criar)
ConsultaServiceTest (Atualizar)
ConsultaControllerTest (Atualizar)

 e passá-los com sucesso.
