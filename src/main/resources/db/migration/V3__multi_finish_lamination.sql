-- Un concepto puede tener varios acabados y varios laminados a la vez
-- (ej. un vinilo puede ser transparente + mate + laminado simultáneamente).

ALTER TABLE job_items DROP COLUMN IF EXISTS finish_id;
ALTER TABLE job_items DROP COLUMN IF EXISTS lamination_id;

CREATE TABLE job_item_finishes (
    job_item_id BIGINT NOT NULL REFERENCES job_items (id) ON DELETE CASCADE,
    finish_id   BIGINT NOT NULL REFERENCES finishes (id),
    PRIMARY KEY (job_item_id, finish_id)
);

CREATE TABLE job_item_laminations (
    job_item_id    BIGINT NOT NULL REFERENCES job_items (id) ON DELETE CASCADE,
    lamination_id  BIGINT NOT NULL REFERENCES laminations (id),
    PRIMARY KEY (job_item_id, lamination_id)
);
