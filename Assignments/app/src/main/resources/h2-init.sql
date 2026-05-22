-- H2-compatible DDL (no PostgreSQL ENUM types; use VARCHAR instead)


CREATE TABLE IF NOT EXISTS INTERACAO (
    IDInteracao INT PRIMARY KEY,
    DataInteracao DATE,
    Texto VARCHAR(255),
    CedulaProfissionalM VARCHAR(50),
    IDUtilizador INT,
    EAbusiva BOOLEAN
);

CREATE TABLE IF NOT EXISTS ESTATISTICA_ABUSO (
    EAbusiva BOOLEAN,
    Total INT
);

CREATE TABLE IF NOT EXISTS CASOS_DE_CYBERBULLYING (
    IDCaso INT,
    DataAbertura DATE,
    DataFecho DATE,
    Descricao VARCHAR(255),
    AreaAtuacao VARCHAR(255),
    Anotacoes VARCHAR(255),
    GrauGravidade INT,
    DataAvaliacao DATE,
    TextoAD VARCHAR(255),
    CedulaProfissionalP VARCHAR(255)
);
