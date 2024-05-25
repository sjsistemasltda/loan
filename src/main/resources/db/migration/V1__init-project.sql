CREATE SCHEMA IF NOT EXISTS bank;

CREATE SEQUENCE IF NOT EXISTS bank.loan_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS bank.person_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS bank.loan
(
    id               BIGINT         NOT NULL,
    amount           DECIMAL(18, 2) NOT NULL,
    invoice_quantity INTEGER        NOT NULL,
    payment_status   VARCHAR(50)    NOT NULL,
    created_at       date           NOT NULL,
    person_id        BIGINT         NOT NULL,
    CONSTRAINT pk_loan PRIMARY KEY (id)
);

CREATE TABLE  IF NOT EXISTS bank.person
(
    id                 BIGINT         NOT NULL,
    name               VARCHAR(50)    NOT NULL,
    identifier         VARCHAR(14)    NOT NULL,
    identifier_type    VARCHAR(2)     NOT NULL,
    birth_date         date           NOT NULL,
    min_amount_monthly DECIMAL(18, 2) NOT NULL,
    max_amount_loan    DECIMAL(18, 2) NOT NULL,
    created_at         date           NOT NULL,
    CONSTRAINT pk_person PRIMARY KEY (id)
);

ALTER TABLE bank.loan
    ADD CONSTRAINT FK_LOAN_ON_PERSON FOREIGN KEY (person_id) REFERENCES bank.person (id);

ALTER TABLE bank.person
    ADD CONSTRAINT unique_identifier_identifier_type UNIQUE (identifier, identifier_type);