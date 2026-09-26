package br.com.cosmodev.sgcmapi.service;

import java.time.LocalDate;
import java.time.Period;

public final class UtilsSgcm {

    // Construtor privado para que a classe não seja instanciada
    private UtilsSgcm() {}

    public static String mascararCpf(String cpf) {
        return cpf.substring(0, 3) + ".***.***-**";
    }

    public static String tratarTelefone(String telefone) {
        return telefone.replaceAll("[^0-9]", ""); // remove tudo que não for dígito de 0 a 9
    }

    public static String formatarTelefone(String telefone) {

        String ddd = telefone.substring(0,2);

        // celular: DDD + 9 dígitos
        if (telefone.length() == 11) {
            String primeiraParte = telefone.substring(2, 7);
            String segundaParte = telefone.substring(7);
            return "(" + ddd + ") " + primeiraParte + "-" + segundaParte;
        }

        // fixo: DDD + 8 dígitos
        if (telefone.length() == 10) { // fixo: DDD + 8 dígitos
            String primeiraParte = telefone.substring(2, 6);
            String segundaParte = telefone.substring(6);
            return "(" + ddd + ") " + primeiraParte + "-" + segundaParte;
        }

        return telefone; // caso as formatações falhem
    }

    public static int calcularIdade(LocalDate dataNascimento) {
        LocalDate dataAtual = LocalDate.now();
        Period intervalo = Period.between(dataNascimento, dataAtual);

        return intervalo.getYears();
    }
}
