CREATE TABLE "event"
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(255),
    date     VARCHAR(255),
    location VARCHAR(255)
);

CREATE TABLE "assistant"
(
    id SERIAL PRIMARY KEY
);

CREATE TABLE "event_assistant"
(
    id       SERIAL PRIMARY KEY,
    event_id INTEGER REFERENCES "event" (id),
    assistant_id  INTEGER REFERENCES "assistant" (id),
    UNIQUE (event_id, assistant_id) -- Para evitar duplicados
);


--Ejecute para tener usuarios registrados
INSERT INTO "assistant" DEFAULT VALUES; -- Asistente 1
INSERT INTO "assistant" DEFAULT VALUES; -- Asistente 2
INSERT INTO "assistant" DEFAULT VALUES; -- Asistente 3