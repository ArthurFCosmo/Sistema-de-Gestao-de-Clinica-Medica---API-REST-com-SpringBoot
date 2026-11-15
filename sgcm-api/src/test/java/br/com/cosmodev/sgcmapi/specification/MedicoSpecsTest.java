package br.com.cosmodev.sgcmapi.specification;

import br.com.cosmodev.sgcmapi.model.Especialidade;
import br.com.cosmodev.sgcmapi.model.Medico;
import br.com.cosmodev.sgcmapi.repository.MedicoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class MedicoSpecsTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MedicoRepository medicoRepository;

    // TESTANDO A SPECIFICATION (comEspecialidade) ---------------------------------------------------------------------

    @Test
    void deveRetornarApenasMedicosDaEspecialidadeInformada() {

        // PREPARAÇÃO: 2 cardiologistas e 1 ortopedista salvos no banco
        Especialidade cardiologia = salvarEspecialidade("Cardiologia");
        Especialidade ortopedia = salvarEspecialidade("Ortopedia");
        salvarMedico("Ana Souza", "111111-PE", "ana@email.com", cardiologia);
        salvarMedico("Bruno Lima", "222222-PE", "bruno@email.com", cardiologia);
        salvarMedico("Carlos Dias", "333333-PE", "carlos@email.com", ortopedia);

        // EXECUÇÃO
        List<Medico> resultado = medicoRepository.findAll(MedicoSpecs.comEspecialidade(cardiologia.getId()));

        // CONFIRMAÇÃO: só os 2 cardiologistas voltaram
        Set<String> nomes = resultado.stream().map(Medico::getNome).collect(Collectors.toSet());

        assertAll(
                () -> assertEquals(2, resultado.size()),
                () -> assertEquals(Set.of("Ana Souza", "Bruno Lima"), nomes),
                () -> assertTrue(resultado.stream()
                        .allMatch(m -> m.getEspecialidade().getId().equals(cardiologia.getId())))
        );

    }

    @Test
    void deveRetornarListaVaziaQuandoEspecialidadeNaoTemMedicos() {

        // PREPARAÇÃO: especialidade existe, mas nenhum médico está vinculado a ela
        Especialidade cardiologia = salvarEspecialidade("Cardiologia");
        Especialidade dermatologia = salvarEspecialidade("Dermatologia");
        salvarMedico("Ana Souza", "111111-PE", "ana@email.com", cardiologia);

        // EXECUÇÃO
        List<Medico> resultado = medicoRepository.findAll(MedicoSpecs.comEspecialidade(dermatologia.getId()));

        // CONFIRMAÇÃO
        assertTrue(resultado.isEmpty());

    }

    @Test
    void deveRetornarTodosOsMedicosQuandoEspecialidadeForNula() {

        // PREPARAÇÃO: médicos de especialidades diferentes
        Especialidade cardiologia = salvarEspecialidade("Cardiologia");
        Especialidade ortopedia = salvarEspecialidade("Ortopedia");
        salvarMedico("Ana Souza", "111111-PE", "ana@email.com", cardiologia);
        salvarMedico("Carlos Dias", "333333-PE", "carlos@email.com", ortopedia);

        // EXECUÇÃO: a spec retorna null (sem filtro) quando o id é nulo
        List<Medico> resultado = medicoRepository.findAll(MedicoSpecs.comEspecialidade(null));

        // CONFIRMAÇÃO: ninguém foi filtrado
        assertEquals(2, resultado.size());

    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExisteEspecialidadeComOIdInformado() {

        // PREPARAÇÃO
        Especialidade cardiologia = salvarEspecialidade("Cardiologia");
        salvarMedico("Ana Souza", "111111-PE", "ana@email.com", cardiologia);

        // EXECUÇÃO: id inexistente (a validação de existência é do service, não da spec)
        List<Medico> resultado = medicoRepository.findAll(MedicoSpecs.comEspecialidade(999L));

        // CONFIRMAÇÃO
        assertTrue(resultado.isEmpty());

    }

    // TESTANDO A SPECIFICATION JUNTO COM PAGINAÇÃO --------------------------------------------------------------------

    @Test
    void deveAplicarFiltroDeEspecialidadeJuntoComPaginacaoEOrdenacao() {

        // PREPARAÇÃO: 3 cardiologistas e 1 ortopedista; página de tamanho 2 ordenada por nome
        Especialidade cardiologia = salvarEspecialidade("Cardiologia");
        Especialidade ortopedia = salvarEspecialidade("Ortopedia");
        salvarMedico("Carla Nunes", "333333-PE", "carla@email.com", cardiologia);
        salvarMedico("Ana Souza", "111111-PE", "ana@email.com", cardiologia);
        salvarMedico("Bruno Lima", "222222-PE", "bruno@email.com", cardiologia);
        salvarMedico("Zeca Pires", "444444-PE", "zeca@email.com", ortopedia);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("nome"));

        // EXECUÇÃO
        Page<Medico> pagina = medicoRepository.findAll(MedicoSpecs.comEspecialidade(cardiologia.getId()), pageable);

        // CONFIRMAÇÃO: o total considera só o filtro (3), e a página traz os 2 primeiros por nome
        assertAll(
                () -> assertEquals(3, pagina.getTotalElements()),
                () -> assertEquals(2, pagina.getTotalPages()),
                () -> assertEquals(2, pagina.getContent().size()),
                () -> assertEquals("Ana Souza", pagina.getContent().get(0).getNome()),
                () -> assertEquals("Bruno Lima", pagina.getContent().get(1).getNome())
        );

    }

    // ENTIDADES PRE MONTADAS E HELPERS PARA ELIMINAR BOILERPLATE ------------------------------------------------------

    private Especialidade salvarEspecialidade(String nome) {
        Especialidade especialidade = new Especialidade();
        especialidade.setNome(nome);
        return entityManager.persistAndFlush(especialidade);
    }

    private Medico salvarMedico(String nome, String crm, String email, Especialidade especialidade) {
        return entityManager.persistAndFlush(
                new Medico(null, nome, crm, email, "81999999999", true, especialidade)
        );
    }

}