-- Catálogos base — editables luego vía BD directa; sin UI de administración en v1

INSERT INTO product_types (name) VALUES
    ('Banner'),
    ('Vinilo'),
    ('Aviso'),
    ('Mug'),
    ('Tijera publicitaria'),
    ('Otro');

INSERT INTO finishes (name) VALUES
    ('Mate'),
    ('Brillante'),
    ('Transparente mate'),
    ('Transparente brillante'),
    ('No aplica'),
    ('Otro');

INSERT INTO laminations (name) VALUES
    ('Sin laminado'),
    ('Laminado mate'),
    ('Laminado brillante'),
    ('Otro');

INSERT INTO payment_methods (name) VALUES
    ('Efectivo'),
    ('Transferencia'),
    ('Nequi'),
    ('Bancolombia'),
    ('Otro');

INSERT INTO expense_categories (name) VALUES
    ('Materiales'),
    ('Transporte'),
    ('Servicios'),
    ('Mantenimiento'),
    ('Nómina'),
    ('Arriendo'),
    ('Otro');

INSERT INTO income_categories (name) VALUES
    ('Venta directa'),
    ('Abono anticipado'),
    ('Otro');
