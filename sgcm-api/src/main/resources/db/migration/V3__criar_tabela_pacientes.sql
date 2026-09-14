CREATE TABLE tb_pacientes (
    id BIGINT AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    cpf varchar(11) NOT NULL UNIQUE,
    email varchar(255) NOT NULL UNIQUE,
    telefone varchar(20) NOT NULL,
    data_nascimento DATE NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    PRIMARY KEY (id)
);