-- Código único (letras + números) para pagos/abonos, igual que trabajos/egresos/ingresos.
CREATE SEQUENCE payment_consecutive_seq START WITH 1 INCREMENT BY 1;

ALTER TABLE payments ADD COLUMN code VARCHAR(20);

UPDATE payments SET code = 'AB-' || LPAD(nextval('payment_consecutive_seq')::text, 4, '0') WHERE code IS NULL;

ALTER TABLE payments ALTER COLUMN code SET NOT NULL;
ALTER TABLE payments ADD CONSTRAINT uq_payments_code UNIQUE (code);
