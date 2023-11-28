--------------------------------------------------------
--  Table REPORT
--------------------------------------------------------

CREATE SEQUENCE report_seq MINVALUE 0 MAXVALUE 999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 1 NO
    CYCLE;

CREATE TABLE "report"
(
    "id"              BIGINT       NOT NULL,
    "status"          VARCHAR(50)  NOT NULL,
    "type"            VARCHAR(250) NOT NULL,
    "request_date"    TIMESTAMP    NOT NULL,
    "generation_date" TIMESTAMP,
    "requesting_user" VARCHAR(250) NOT NULL,
    "file_location"   VARCHAR(600),
    "file_size"       BIGINT
);

ALTER TABLE "report"
    ADD CONSTRAINT "pk_report" PRIMARY KEY ("id");

COMMENT ON TABLE "report" IS 'Represents a report';

COMMENT ON COLUMN "report"."id" IS 'The report id';
COMMENT ON COLUMN "report"."status" IS 'The status of the report, e.g. DONE, PENDING etc.';
COMMENT ON COLUMN "report"."type" IS 'The type of the report, e.g. R0001. R0002 etc.';
COMMENT ON COLUMN "report"."request_date" IS 'The date the report was requested';
COMMENT ON COLUMN "report"."generation_date" is 'The date the report was generated';
COMMENT ON COLUMN "report"."requesting_user" IS 'The URID of the user that requested the report, e.g. UK1234556666';
COMMENT ON COLUMN "report"."file_location" IS 'The location og the generated report, relative to the S3 bucket';
