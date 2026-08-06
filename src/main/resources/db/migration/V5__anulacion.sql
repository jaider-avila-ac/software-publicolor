-- Permite anular pagos, egresos e ingresos manuales sin borrarlos (se conserva el
-- historial, pero dejan de contar en todos los totales/reportes).
ALTER TABLE payments ADD COLUMN annulled BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE payments ADD COLUMN annulled_at TIMESTAMP NULL;
ALTER TABLE payments ADD COLUMN annulled_reason TEXT NULL;

ALTER TABLE expenses ADD COLUMN annulled BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE expenses ADD COLUMN annulled_at TIMESTAMP NULL;
ALTER TABLE expenses ADD COLUMN annulled_reason TEXT NULL;

ALTER TABLE manual_incomes ADD COLUMN annulled BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE manual_incomes ADD COLUMN annulled_at TIMESTAMP NULL;
ALTER TABLE manual_incomes ADD COLUMN annulled_reason TEXT NULL;
