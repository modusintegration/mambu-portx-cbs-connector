
-- DROP TABLE public.idempotentsession;

CREATE TABLE public.idempotent_session (
	id int8 NOT NULL,
	client_request_id uuid NOT NULL,
	isvalid bool NOT NULL,
	operation_name text NOT NULL,
	parameters jsonb NOT NULL,
	response_code int4 NULL,
	response_entity jsonb NULL,
	response_message text NULL,
	request_time timestamp NULL,
	client_request_id_status varchar(255) NOT NULL,
	CONSTRAINT client_request_id_un_1 UNIQUE (client_request_id),
	CONSTRAINT idempotent_session_pkey PRIMARY KEY (id)
);


CREATE SEQUENCE public.sequence_generator
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


/*
    
    CREATE TABLE ${flyway:defaultSchema}.wwc_idempotentsession (
    id bigint NOT NULL,
    client_request_id uuid NOT NULL,
    isvalid boolean NOT NULL,
    operation_name text NOT NULL,
    parameters jsonb NOT NULL,
    response_code integer,
    response_entity jsonb,
    response_message text,
    request_time timestamp without time zone
);


ALTER TABLE ${flyway:defaultSchema}.wwc_idempotentsession OWNER TO ${flyway:user};

ALTER TABLE ${flyway:defaultSchema}.wwc_idempotentsession
    ADD CONSTRAINT client_request_id_un UNIQUE (client_request_id);


ALTER TABLE ONLY ${flyway:defaultSchema}.wwc_idempotentsession
    ADD CONSTRAINT wwc_idempotentsession_pkey PRIMARY KEY (id);


ALTER TABLE ${flyway:defaultSchema}.wwc_idempotentsession ADD client_request_id_status varchar(255) DEFAULT 'SAVED';

ALTER TABLE ${flyway:defaultSchema}.wwc_idempotentsession ALTER COLUMN client_request_id_status SET NOT NULL;

ALTER TABLE ${flyway:defaultSchema}.wwc_idempotentsession ALTER COLUMN client_request_id_status DROP DEFAULT;

CREATE SEQUENCE ${flyway:defaultSchema}.sequenceGenerator
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    */