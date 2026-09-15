package br.com.cosmodev.sgcmapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "tb_medicos")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 9)
    private String crm;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false)
    private Boolean ativo;

    @ManyToOne
    @JoinColumn(name = "id_especialidade", nullable = false)
    private Especialidade especialidade;

}
