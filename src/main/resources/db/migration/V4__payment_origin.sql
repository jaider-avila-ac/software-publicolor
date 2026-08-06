-- Distingue pagos en efectivo/reales de aplicaciones automáticas de saldo a favor,
-- para que el crédito ya recibido no se cuente dos veces en los totales de caja.
ALTER TABLE payments ADD COLUMN origin VARCHAR(20) NOT NULL DEFAULT 'CASH'
    CHECK (origin IN ('CASH', 'CREDIT_APPLIED'));
