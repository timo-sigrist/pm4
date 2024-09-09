--
-- PostgreSQL database dump
--

-- Dumped from database version 16.2 (Debian 16.2-1.pgdg120+2)
-- Dumped by pg_dump version 16.2 (Debian 16.2-1.pgdg120+2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: day_sheet; Type: TABLE; Schema: public; Owner: dbadmin
--

CREATE TABLE public.day_sheet (
    confirmed boolean,
    date date,
    id bigint NOT NULL,
    day_report character varying(255),
    user_id character varying(255)
);


ALTER TABLE public.day_sheet OWNER TO dbadmin;

--
-- Name: day_sheet_id_seq; Type: SEQUENCE; Schema: public; Owner: dbadmin
--

CREATE SEQUENCE public.day_sheet_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.day_sheet_id_seq OWNER TO dbadmin;

--
-- Name: day_sheet_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dbadmin
--

ALTER SEQUENCE public.day_sheet_id_seq OWNED BY public.day_sheet.id;


--
-- Name: timestamp; Type: TABLE; Schema: public; Owner: dbadmin
--

CREATE TABLE public."timestamp" (
    end_time time(6) without time zone,
    start_time time(6) without time zone,
    day_sheet_id bigint,
    id bigint NOT NULL,
    user_id character varying(255)
);


ALTER TABLE public."timestamp" OWNER TO dbadmin;

--
-- Name: timestamp_id_seq; Type: SEQUENCE; Schema: public; Owner: dbadmin
--

CREATE SEQUENCE public.timestamp_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.timestamp_id_seq OWNER TO dbadmin;

--
-- Name: timestamp_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dbadmin
--

ALTER SEQUENCE public.timestamp_id_seq OWNED BY public."timestamp".id;


--
-- Name: day_sheet id; Type: DEFAULT; Schema: public; Owner: dbadmin
--

ALTER TABLE ONLY public.day_sheet ALTER COLUMN id SET DEFAULT nextval('public.day_sheet_id_seq'::regclass);


--
-- Name: timestamp id; Type: DEFAULT; Schema: public; Owner: dbadmin
--

ALTER TABLE ONLY public."timestamp" ALTER COLUMN id SET DEFAULT nextval('public.timestamp_id_seq'::regclass);


--
-- Data for Name: day_sheet; Type: TABLE DATA; Schema: public; Owner: dbadmin
--

COPY public.day_sheet (confirmed, date, id, day_report, user_id) FROM stdin;
\.


--
-- Data for Name: timestamp; Type: TABLE DATA; Schema: public; Owner: dbadmin
--

COPY public."timestamp" (end_time, start_time, day_sheet_id, id, user_id) FROM stdin;
\.


--
-- Name: day_sheet_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dbadmin
--

SELECT pg_catalog.setval('public.day_sheet_id_seq', 1, false);


--
-- Name: timestamp_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dbadmin
--

SELECT pg_catalog.setval('public.timestamp_id_seq', 1, false);


--
-- Name: day_sheet day_sheet_pkey; Type: CONSTRAINT; Schema: public; Owner: dbadmin
--

ALTER TABLE ONLY public.day_sheet
    ADD CONSTRAINT day_sheet_pkey PRIMARY KEY (id);


--
-- Name: timestamp timestamp_pkey; Type: CONSTRAINT; Schema: public; Owner: dbadmin
--

ALTER TABLE ONLY public."timestamp"
    ADD CONSTRAINT timestamp_pkey PRIMARY KEY (id);


--
-- Name: timestamp fktjv22i5knvoeqmbkul16fnx5i; Type: FK CONSTRAINT; Schema: public; Owner: dbadmin
--

ALTER TABLE ONLY public."timestamp"
    ADD CONSTRAINT fktjv22i5knvoeqmbkul16fnx5i FOREIGN KEY (day_sheet_id) REFERENCES public.day_sheet(id);


--
-- PostgreSQL database dump complete
--

