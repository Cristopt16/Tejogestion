
CREATE DATABASE TejoGestion;

USE TejoGestion;


CREATE TABLE Cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(120)
);

CREATE TABLE Cancha (
    id_cancha INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Disponible'
);

INSERT INTO Cancha (nombre) VALUES
('Cancha 1'),
('Cancha 2');

CREATE TABLE Reserva (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Activa',
    id_cliente INT NOT NULL,
    id_cancha INT NOT NULL,

    CONSTRAINT fk_reserva_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES Cliente(id_cliente),

    CONSTRAINT fk_reserva_cancha
        FOREIGN KEY (id_cancha)
        REFERENCES Cancha(id_cancha),

    CONSTRAINT chk_horario
        CHECK (hora_fin > hora_inicio)
);

USE TejoGestion;

SHOW TABLES;

SELECT * FROM Cliente;
SELECT * FROM Cancha;
SELECT * FROM Reserva;
