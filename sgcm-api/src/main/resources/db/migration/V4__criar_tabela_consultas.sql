CREATE TABLE tb_consultas (
    id BIGINT AUTO_INCREMENT,
    data_hora DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    observacoes VARCHAR(255),
    id_medico BIGINT NOT NULL,
    id_paciente BIGINT NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_consulta_medico FOREIGN KEY (id_medico) REFERENCES tb_medicos(id),
    CONSTRAINT fk_consulta_paciente FOREIGN KEY (id_paciente) REFERENCES tb_pacientes(id)
);