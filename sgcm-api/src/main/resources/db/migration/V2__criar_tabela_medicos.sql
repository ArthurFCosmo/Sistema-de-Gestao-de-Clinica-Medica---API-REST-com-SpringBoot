CREATE TABLE tb_medicos (
    id BIGINT AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    crm VARCHAR(9) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    id_especialidade BIGINT NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_medico_especialidade FOREIGN KEY (id_especialidade) REFERENCES tb_especialidades(id)
);