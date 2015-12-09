--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: cuartel_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE cuartel_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE cuartel_id_seq OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: cuartel; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE cuartel (
    id integer DEFAULT nextval('cuartel_id_seq'::regclass) NOT NULL,
    nombre_cuartel character varying(50) NOT NULL,
    latitud double precision DEFAULT 0::double precision,
    longitud double precision DEFAULT 0::double precision,
    actividad smallint DEFAULT 1::smallint,
    localidad character varying(50) DEFAULT NULL::character varying,
    uso integer DEFAULT 0 NOT NULL
);


ALTER TABLE cuartel OWNER TO postgres;

--
-- Name: soldado_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE soldado_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE soldado_id_seq OWNER TO postgres;

--
-- Name: soldado; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE soldado (
    id integer DEFAULT nextval('soldado_id_seq'::regclass) NOT NULL,
    nombre character varying(50) NOT NULL,
    apellidos character varying(100) NOT NULL,
    fecha_nacimiento date,
    rango character varying DEFAULT 'Soldado'::character varying,
    lugar_nacimiento character varying(50) DEFAULT NULL::character varying,
    id_unidad integer,
    uso integer DEFAULT 0 NOT NULL,
    CONSTRAINT soldado_id_unidad_check CHECK ((id_unidad >= 0)),
    CONSTRAINT soldado_rango_check CHECK (((rango)::text = ANY ((ARRAY['Soldado'::character varying, 'Soldado de Primera'::character varying, 'Cabo'::character varying, 'Cabo Primero'::character varying, 'Cabo Mayor'::character varying, 'Sargento'::character varying, 'Sargento Primero'::character varying, 'Brigada'::character varying, 'Subteniente'::character varying, 'Suboficial Mayor'::character varying, 'Alférez'::character varying, 'Teniente'::character varying, 'Capitán'::character varying, 'Comandante'::character varying, 'Teniente Coronel'::character varying, 'Coronel'::character varying, 'General de Brigada'::character varying, 'General de Division'::character varying, 'Teniente General'::character varying, 'General de Ejército'::character varying])::text[])))
);


ALTER TABLE soldado OWNER TO postgres;

--
-- Name: unidad_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE unidad_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE unidad_id_seq OWNER TO postgres;

--
-- Name: unidad; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE unidad (
    id integer DEFAULT nextval('unidad_id_seq'::regclass) NOT NULL,
    nombre_unidad character varying(50) NOT NULL,
    tipo character varying NOT NULL,
    no_tropas integer DEFAULT 0,
    fecha_creacion date,
    id_cuartel integer,
    uso integer DEFAULT 0 NOT NULL,
    CONSTRAINT unidad_id_cuartel_check CHECK ((id_cuartel >= 0)),
    CONSTRAINT unidad_no_tropas_check CHECK ((no_tropas >= 0)),
    CONSTRAINT unidad_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['Compañia'::character varying, 'Batallon'::character varying, 'Regimiento'::character varying, 'Brigada'::character varying, 'Division'::character varying])::text[])))
);


ALTER TABLE unidad OWNER TO postgres;

--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE usuarios (
    usuario character varying(50) NOT NULL,
    password character varying(255) NOT NULL,
    rol character varying NOT NULL,
    CONSTRAINT usuarios_rol_check CHECK (((rol)::text = ANY ((ARRAY['administrador'::character varying, 'tecnico'::character varying, 'usuario'::character varying])::text[])))
);


ALTER TABLE usuarios OWNER TO postgres;

--
-- Data for Name: cuartel; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: cuartel_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('cuartel_id_seq', 19, true);


--
-- Data for Name: soldado; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: soldado_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('soldado_id_seq', 2, true);


--
-- Data for Name: unidad; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: unidad_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('unidad_id_seq', 14, true);


--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO usuarios VALUES ('Guillermo', '1234', 'usuario');
INSERT INTO usuarios VALUES ('Dani', '1234', 'tecnico');
INSERT INTO usuarios VALUES ('Raider', 'pamaloyo18', 'administrador');


--
-- Name: cuartel_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY cuartel
    ADD CONSTRAINT cuartel_pkey PRIMARY KEY (id);


--
-- Name: soldado_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY soldado
    ADD CONSTRAINT soldado_pkey PRIMARY KEY (id);


--
-- Name: unidad_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY unidad
    ADD CONSTRAINT unidad_pkey PRIMARY KEY (id);


--
-- Name: usuarios_usuario_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_usuario_key UNIQUE (usuario);


--
-- Name: soldado_id_unidad_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX soldado_id_unidad_idx ON soldado USING btree (id_unidad);


--
-- Name: unidad_id_cuartel_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX unidad_id_cuartel_idx ON unidad USING btree (id_cuartel);


--
-- Name: unidad_id_cuartel_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY unidad
    ADD CONSTRAINT unidad_id_cuartel_fkey FOREIGN KEY (id_cuartel) REFERENCES cuartel(id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY soldado
    ADD CONSTRAINT soldado_id_unidad_fkey FOREIGN KEY (id_unidad) REFERENCES unidad(id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

