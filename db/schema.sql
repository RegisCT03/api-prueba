-- 1. Tablas de Catálogos
CREATE TABLE rol (
    id SERIAL PRIMARY KEY,
    rol VARCHAR(100) NOT NULL
);

CREATE TABLE mood (
    id SERIAL PRIMARY KEY,
    mood VARCHAR(100) NOT NULL
);

CREATE TABLE status (
    id SERIAL PRIMARY KEY,
    color VARCHAR(100) NOT NULL,
    description VARCHAR(100)
);

CREATE TABLE juego (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- 2. Tabla de Usuarios
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
   date_of_birth DATE,
    gender VARCHAR(100),
    id_rol INTEGER REFERENCES rol(id),
    ideal_sleep_hours FLOAT DEFAULT 8.0
);

-- 3. Registro Diario (Semáforo y Sueño)
CREATE TABLE daily_checkin (
    id SERIAL PRIMARY KEY,
    id_user INTEGER REFERENCES users(id) ON DELETE CASCADE,
    sleep_start TIME,
    sleep_end TIME,
    hours_sleep FLOAT,
    id_mood INTEGER REFERENCES mood(id),
    id_status INTEGER REFERENCES status(id),
    date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sleep_debt FLOAT,
    battery INTEGER CHECK (battery BETWEEN 0 AND 100)
);

-- 4. Sesiones de Juego
CREATE TABLE game_sessions (
    id SERIAL PRIMARY KEY,
    id_daily_checkin INTEGER REFERENCES daily_checkin(id) ON DELETE CASCADE,
    id_juego INTEGER REFERENCES juego(id),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    score_value FLOAT,
    battery INTEGER,
    metadata JSONB
);

-- 5. Mensajes y Sugerencias
CREATE TABLE message (
    id SERIAL PRIMARY KEY,
    id_daily_checkin INTEGER REFERENCES daily_checkin(id),
    id_game_session INTEGER REFERENCES game_sessions(id),
    message VARCHAR(255) NOT NULL
);