--liquibase formatted sql

--changeset EricM:1
-- create a table to hold sales data
CREATE TABLE SALES (ID NUMBER NOT NULL, ITEM VARCHAR2(50 BYTE) NOT NULL, QUANTITY NUMBER(*, 0) NOT NULL, AMOUNT FLOAT(22) NOT NULL, CONSTRAINT SALES_PK PRIMARY KEY (ID));
--rollback DROP TABLE SALES;

--changeset SonyaV:2
-- create a sequence to generate primary keys for the sales table
CREATE SEQUENCE SALE_SEQUENCE START WITH 21 MAXVALUE 100000;
--rollback DROP SEQUENCE SALE_SEQUENCE; 

--changeset TsviZ:3 runOnChange:true endDelimiter:/
-- create a trigger to generate primary keys for the sales table
CREATE OR REPLACE TRIGGER "SALE_PRIMARY_KEY_TRG"
   before insert on "SALES"
   for each row
begin
   if inserting then
      if :NEW."ID" is null then
         select SALE_SEQUENCE.nextval into :NEW."ID" from dual;
      end if;
   end if;
end;
/
--rollback DROP TRIGGER "SALE_PRIMARY_KEY_TRG"; 

-- changeset EricM:4 endDelimiter:$$
-- create a procedure to collect sales data
CREATE OR REPLACE PROCEDURE GET_SALES (p_item IN VARCHAR2, p_quantity OUT NUMBER, p_amount OUT FLOAT) AS
BEGIN
   SELECT SUM(QUANTITY) INTO p_quantity FROM SALES WHERE ITEM = p_item;
   SELECT SUM(AMOUNT) INTO p_amount FROM SALES WHERE ITEM = p_item;
END;
/
$$
--rollback DROP PROCEDURE GET_SALES;

--changeset SonyaV:5 endDelimiter:$$
-- create a function to collect sales data
CREATE OR REPLACE FUNCTION GET_SALES_FUNC (p_item IN VARCHAR2) RETURN FLOAT AS
   v_amount FLOAT;
BEGIN
   SELECT SUM(AMOUNT) INTO v_amount FROM SALES WHERE ITEM = p_item;
   RETURN v_amount;
END;
$$
--rollback DROP FUNCTION GET_SALES_FUNC;

--changeset TsviZ:6
-- create a view to show sales data
CREATE OR REPLACE VIEW SALES_VIEW AS
SELECT ITEM, SUM(QUANTITY) AS QUANTITY, SUM(AMOUNT) AS AMOUNT
FROM SALES
GROUP BY ITEM;
--rollback DROP VIEW SALES_VIEW;

--changeset EricM:7
-- create a synonym to the sales table
CREATE SYNONYM SALES_SYNONYM FOR SALES;
--rollback DROP SYNONYM SALES_SYNONYM;

--changeset SonyaV:8
-- create a materialized view to show sales data
CREATE MATERIALIZED VIEW SALES_MVIEW AS
SELECT ITEM, SUM(QUANTITY) AS QUANTITY, SUM(AMOUNT) AS AMOUNT
FROM SALES
GROUP BY ITEM;
--rollback DROP MATERIALIZED VIEW SALES_MVIEW;

--changeset TsviZ:9
-- insert some data into the sales table
INSERT INTO SALES (ITEM, QUANTITY, AMOUNT) VALUES ('Shelving', 6, 300);
INSERT INTO SALES (ITEM, QUANTITY, AMOUNT) VALUES ('Rug', 2, 100);
INSERT INTO SALES (ITEM, QUANTITY, AMOUNT) VALUES ('Desk', 1, 200);
INSERT INTO SALES (ITEM, QUANTITY, AMOUNT) VALUES ('Arm Chair', 3, 150);
-- rollback DELETE FROM SALES WHERE ITEM = 'Shelving';
-- rollback DELETE FROM SALES WHERE ITEM = 'Rug';
-- rollback DELETE FROM SALES WHERE ITEM = 'Desk';
-- rollback DELETE FROM SALES WHERE ITEM = 'Arm Chair';

