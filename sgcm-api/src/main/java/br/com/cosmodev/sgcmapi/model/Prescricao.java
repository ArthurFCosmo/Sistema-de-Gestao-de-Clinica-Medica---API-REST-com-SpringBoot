package br.com.cosmodev.sgcmapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "tb_prescricoes")
public class Prescricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column
    private String medicamentos;

    @Column(nullable = false)
    private LocalDate data_emissao;

    @OneToOne
    @JoinColumn(name = "id_consulta", nullable = false)
    private Consulta consulta;

}
