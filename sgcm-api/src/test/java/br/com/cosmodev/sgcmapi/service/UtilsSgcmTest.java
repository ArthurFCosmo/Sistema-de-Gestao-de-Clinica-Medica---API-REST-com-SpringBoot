package br.com.cosmodev.sgcmapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UtilsSgcmTest {

    @Test
    void deveMascararCpfComSucesso() {

        String resultado = UtilsSgcm.mascararCpf("123.456.789-00");

        assertEquals("123.***.***-**", resultado);

    }

    @Test
    void deveRemoverOqueNaoForNumeroComSucesso() {

        String resultado = UtilsSgcm.formatarTelefone("00123451234");

        assertEquals("(00) 12345-1234", resultado);

    }

    @Test
    void deveTratarCrm() {

        String resultado = UtilsSgcm.tratarCrm("123456-PE");

        assertEquals("123456PE", resultado);

    }

    @Test
    void deveFormatarCrm() {

        String resultado = UtilsSgcm.formatarCrm("123456PE");

        assertEquals("123456-PE", resultado);
    }

    @Test
    void deveFormatarTelefoneFixoComSuccesso() {

        String resultado = UtilsSgcm.formatarTelefone("0012341234");

        assertEquals("(00) 1234-1234", resultado);

    }

    @Test
    void deveCalcularIdadeComSucesso() {

        int resultado = UtilsSgcm.calcularIdade(LocalDate.parse("2002-10-10"));

        assertEquals(23, resultado);

    }
}
