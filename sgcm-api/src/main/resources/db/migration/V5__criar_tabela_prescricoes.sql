CREATE TABLE tb_prescricoes (
    id BIGINT AUTO_INCREMENT,
    descricao VARCHAR(255) NOT NULL,
    medicamentos VARCHAR(255),
    data_emissao DATE NOT NULL,

    id_consulta BIGINT NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_prescricao_consulta FOREIGN KEY (id_consulta) REFERENCES tb_consultas(id)
);