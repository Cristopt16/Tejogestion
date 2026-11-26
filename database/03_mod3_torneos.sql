-- ==========================================================
-- SPRINT 3 - MÓDULO 03: GESTIÓN DE TORNEOS Y POSICIONES (TejoGestion)
-- Prerrequisitos: Las tablas USUARIO, ROL, CANCHA y CLIENTE
--                 ya deben existir del Sprint 1.
-- ==========================================================
USE tejogestion;

-- 1. TORNEO
CREATE TABLE IF NOT EXISTS TORNEO (
    id_torneo INT(11) NOT NULL AUTO_INCREMENT,
    ref_usuario INT(11) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NULL,
    modalidad VARCHAR(40) NOT NULL,
    reglamento TEXT NULL,
    cupo_maximo INT(11) NOT NULL,
    cupos_disponibles INT(11) NOT NULL,
    estado VARCHAR(25) NOT NULL DEFAULT 'InscripcionesAbiertas',
    CONSTRAINT pk_torneo PRIMARY KEY (id_torneo),
    CONSTRAINT fk_torneo_usuario FOREIGN KEY (ref_usuario)
        REFERENCES USUARIO(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_torneo_cupo CHECK (cupo_maximo >= 2),
    CONSTRAINT chk_torneo_cupos_disp CHECK (cupos_disponibles >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. TORNEO_CANCHA
CREATE TABLE IF NOT EXISTS TORNEO_CANCHA (
    id_torneo_cancha INT(11) NOT NULL AUTO_INCREMENT,
    ref_torneo INT(11) NOT NULL,
    ref_cancha INT(11) NOT NULL,
    CONSTRAINT pk_torneo_cancha PRIMARY KEY (id_torneo_cancha),
    CONSTRAINT uk_torneo_cancha UNIQUE (ref_torneo, ref_cancha),
    CONSTRAINT fk_tc_torneo FOREIGN KEY (ref_torneo)
        REFERENCES TORNEO(id_torneo) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_tc_cancha FOREIGN KEY (ref_cancha)
        REFERENCES CANCHA(id_cancha) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. EQUIPO
CREATE TABLE IF NOT EXISTS EQUIPO (
    id_equipo INT(11) NOT NULL AUTO_INCREMENT,
    ref_torneo INT(11) NOT NULL,
    nombre_equipo VARCHAR(80) NOT NULL,
    fecha_inscripcion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'Inscrito',
    CONSTRAINT pk_equipo PRIMARY KEY (id_equipo),
    CONSTRAINT uk_equipo_torneo UNIQUE (ref_torneo, nombre_equipo),
    CONSTRAINT fk_equipo_torneo FOREIGN KEY (ref_torneo)
        REFERENCES TORNEO(id_torneo) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. PARTICIPANTE
CREATE TABLE IF NOT EXISTS PARTICIPANTE (
    id_participante INT(11) NOT NULL AUTO_INCREMENT,
    ref_equipo INT(11) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    documento_telefono VARCHAR(30) NOT NULL,
    CONSTRAINT pk_participante PRIMARY KEY (id_participante),
    CONSTRAINT fk_participante_equipo FOREIGN KEY (ref_equipo)
        REFERENCES EQUIPO(id_equipo) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. FIXTURE
CREATE TABLE IF NOT EXISTS FIXTURE (
    id_fixture INT(11) NOT NULL AUTO_INCREMENT,
    ref_torneo INT(11) NOT NULL,
    modalidad_aplicada VARCHAR(40) NOT NULL,
    total_rondas INT(11) NOT NULL,
    fecha_generacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'Generado',
    CONSTRAINT pk_fixture PRIMARY KEY (id_fixture),
    CONSTRAINT uk_fixture_torneo UNIQUE (ref_torneo),
    CONSTRAINT fk_fixture_torneo FOREIGN KEY (ref_torneo)
        REFERENCES TORNEO(id_torneo) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_fixture_rondas CHECK (total_rondas >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. PARTIDA
CREATE TABLE IF NOT EXISTS PARTIDA (
    id_partida INT(11) NOT NULL AUTO_INCREMENT,
    ref_fixture INT(11) NOT NULL,
    ref_equipo_local INT(11) NOT NULL,
    ref_equipo_visitante INT(11) NOT NULL,
    ref_cancha INT(11) NULL,
    numero_ronda INT(11) NOT NULL,
    fecha_partida DATE NULL,
    hora_inicio TIME NULL,
    marcador_local INT(11) NULL,
    marcador_visitante INT(11) NULL,
    observaciones TEXT NULL,
    estado VARCHAR(25) NOT NULL DEFAULT 'Pendiente',
    CONSTRAINT pk_partida PRIMARY KEY (id_partida),
    CONSTRAINT fk_partida_fixture FOREIGN KEY (ref_fixture)
        REFERENCES FIXTURE(id_fixture) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_partida_local FOREIGN KEY (ref_equipo_local)
        REFERENCES EQUIPO(id_equipo) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_partida_visitante FOREIGN KEY (ref_equipo_visitante)
        REFERENCES EQUIPO(id_equipo) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_partida_cancha FOREIGN KEY (ref_cancha)
        REFERENCES CANCHA(id_cancha) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT chk_partida_ronda CHECK (numero_ronda >= 1),
    CONSTRAINT chk_partida_ml CHECK (marcador_local IS NULL OR marcador_local >= 0),
    CONSTRAINT chk_partida_mv CHECK (marcador_visitante IS NULL OR marcador_visitante >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. TABLA_POSICIONES
CREATE TABLE IF NOT EXISTS TABLA_POSICIONES (
    id_posicion INT(11) NOT NULL AUTO_INCREMENT,
    ref_torneo INT(11) NOT NULL,
    ref_equipo INT(11) NOT NULL,
    partidas_jugadas INT(11) NOT NULL DEFAULT 0,
    partidas_ganadas INT(11) NOT NULL DEFAULT 0,
    partidas_empatadas INT(11) NOT NULL DEFAULT 0,
    partidas_perdidas INT(11) NOT NULL DEFAULT 0,
    puntos_favor INT(11) NOT NULL DEFAULT 0,
    puntos_contra INT(11) NOT NULL DEFAULT 0,
    diferencia_puntos INT(11) NOT NULL DEFAULT 0,
    puntaje_total INT(11) NOT NULL DEFAULT 0,
    CONSTRAINT pk_posicion PRIMARY KEY (id_posicion),
    CONSTRAINT uk_posicion_equipo UNIQUE (ref_equipo),
    CONSTRAINT fk_posicion_torneo FOREIGN KEY (ref_torneo)
        REFERENCES TORNEO(id_torneo) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_posicion_equipo FOREIGN KEY (ref_equipo)
        REFERENCES EQUIPO(id_equipo) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
