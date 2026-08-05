-- Publicolor — esquema inicial (3FN, dinero en NUMERIC, sin JSON para datos operativos)

CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(150) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(30)  NOT NULL DEFAULT 'ADMIN',
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE clients (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_clients_name ON clients (lower(name));

-- Catálogos (sin UI de administración en v1, solo lectura para selects)
CREATE TABLE product_types (
    id     BIGSERIAL PRIMARY KEY,
    name   VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE finishes (
    id     BIGSERIAL PRIMARY KEY,
    name   VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE laminations (
    id     BIGSERIAL PRIMARY KEY,
    name   VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE payment_methods (
    id     BIGSERIAL PRIMARY KEY,
    name   VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE income_categories (
    id     BIGSERIAL PRIMARY KEY,
    name   VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE expense_categories (
    id     BIGSERIAL PRIMARY KEY,
    name   VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Consecutivos atómicos, independientes de la secuencia interna de las tablas
CREATE SEQUENCE job_consecutive_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE receipt_consecutive_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE jobs (
    id                  BIGSERIAL PRIMARY KEY,
    client_id           BIGINT NOT NULL REFERENCES clients (id),
    consecutive_number  BIGINT NOT NULL UNIQUE,
    title               VARCHAR(200) NOT NULL,
    total_amount        NUMERIC(14, 2) NOT NULL CHECK (total_amount >= 0),
    status              VARCHAR(30) NOT NULL DEFAULT 'ABIERTA'
                        CHECK (status IN ('ABIERTA', 'PARCIALMENTE_PAGADA', 'PAGADA', 'CANCELADA')),
    notes               TEXT,
    job_date            DATE NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_jobs_client_id ON jobs (client_id);
CREATE INDEX idx_jobs_status ON jobs (status);
CREATE INDEX idx_jobs_job_date ON jobs (job_date);
CREATE INDEX idx_jobs_created_at ON jobs (created_at);

CREATE TABLE job_items (
    id               BIGSERIAL PRIMARY KEY,
    job_id           BIGINT NOT NULL REFERENCES jobs (id) ON DELETE CASCADE,
    product_type_id  BIGINT NOT NULL REFERENCES product_types (id),
    description      VARCHAR(255),
    quantity         NUMERIC(10, 2) CHECK (quantity IS NULL OR quantity >= 0),
    width            NUMERIC(10, 2) CHECK (width IS NULL OR width >= 0),
    height           NUMERIC(10, 2) CHECK (height IS NULL OR height >= 0),
    finish_id        BIGINT REFERENCES finishes (id),
    lamination_id    BIGINT REFERENCES laminations (id),
    unit_price       NUMERIC(14, 2) CHECK (unit_price IS NULL OR unit_price >= 0),
    total_amount     NUMERIC(14, 2) NOT NULL CHECK (total_amount >= 0),
    notes            TEXT
);
CREATE INDEX idx_job_items_job_id ON job_items (job_id);

CREATE TABLE payments (
    id                 BIGSERIAL PRIMARY KEY,
    job_id             BIGINT NOT NULL REFERENCES jobs (id),
    payment_method_id  BIGINT REFERENCES payment_methods (id),
    amount             NUMERIC(14, 2) NOT NULL CHECK (amount > 0),
    payment_date       DATE NOT NULL,
    notes              TEXT,
    created_at         TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_payments_job_id ON payments (job_id);
CREATE INDEX idx_payments_payment_date ON payments (payment_date);

CREATE TABLE manual_incomes (
    id                   BIGSERIAL PRIMARY KEY,
    income_category_id  BIGINT REFERENCES income_categories (id),
    concept              VARCHAR(255) NOT NULL,
    amount                NUMERIC(14, 2) NOT NULL CHECK (amount > 0),
    income_date          DATE NOT NULL,
    notes                 TEXT,
    support_reference    VARCHAR(255),
    created_at            TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_manual_incomes_income_date ON manual_incomes (income_date);

CREATE TABLE expenses (
    id                   BIGSERIAL PRIMARY KEY,
    expense_category_id BIGINT REFERENCES expense_categories (id),
    concept              VARCHAR(255) NOT NULL,
    amount               NUMERIC(14, 2) NOT NULL CHECK (amount > 0),
    expense_date         DATE NOT NULL,
    notes                TEXT,
    support_reference    VARCHAR(255),
    created_at           TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_expenses_expense_date ON expenses (expense_date);

CREATE TABLE collection_receipts (
    id                  BIGSERIAL PRIMARY KEY,
    job_id              BIGINT NOT NULL REFERENCES jobs (id),
    consecutive_number  BIGINT NOT NULL UNIQUE,
    generated_at        TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_collection_receipts_job_id ON collection_receipts (job_id);
