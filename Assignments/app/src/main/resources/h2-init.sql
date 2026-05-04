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
