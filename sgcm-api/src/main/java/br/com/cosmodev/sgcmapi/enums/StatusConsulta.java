package br.com.cosmodev.sgcmapi.enums;

import java.util.List;

public enum StatusConsulta {
    AGENDADA,
    CONFIRMADA,
    REALIZADA,
    CANCELADA;

    // Constantes ------------------------------------------------------------------------------------------------------
    private static final List<StatusConsulta> listaDeStatusAtivos = List.of(CONFIRMADA, AGENDADA);

    public static List<StatusConsulta> ativos() {
        return listaDeStatusAtivos;
    }

}
