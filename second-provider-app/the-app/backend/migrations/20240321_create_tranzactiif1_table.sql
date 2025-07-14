-- Table: public.tranzactiif1

-- DROP TABLE IF EXISTS public.tranzactiif1;

CREATE TABLE IF NOT EXISTS public.tranzactiif1
(
    id bigint NOT NULL DEFAULT nextval('users_id_seq'::regclass),
    id_factura bigint NOT NULL,
    username character varying(255) COLLATE pg_catalog."default" NOT NULL,
    tip character varying(255) COLLATE pg_catalog."default" NOT NULL,
    sum double precision NOT NULL,
    date date NOT NULL,
    CONSTRAINT tranzactiif1_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tranzactiif1
    OWNER to postgres; 