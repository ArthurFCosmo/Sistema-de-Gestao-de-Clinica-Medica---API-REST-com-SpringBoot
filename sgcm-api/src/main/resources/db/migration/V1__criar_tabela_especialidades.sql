CREATE TABLE tb_especialidades (
    id BIGINT AUTO_INCREMENT,
    nome VARCHAR(100) UNIQUE NOT NULL,
    descricao VARCHAR(255),

    PRIMARY KEY (id)
);