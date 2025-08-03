-- =====================================================
-- COMPLETE ORACLE DATABASE SETUP FOR CRAVE KITCHEN PORTAL
-- =====================================================
-- Run this script as SYSDBA to set up the complete database

-- =====================================================
-- STEP 1: CREATE TABLESPACE
-- =====================================================

CREATE TABLESPACE crave_kitchen_data
DATAFILE 'crave_kitchen_data.dbf'
SIZE 100M
AUTOEXTEND ON NEXT 50M MAXSIZE 1G;

-- =====================================================
-- STEP 2: CREATE USER
-- =====================================================

CREATE USER crave_kitchen
IDENTIFIED BY crave_kitchen123
DEFAULT TABLESPACE crave_kitchen_data
QUOTA UNLIMITED ON crave_kitchen_data;

-- =====================================================
-- STEP 3: GRANT PRIVILEGES
-- =====================================================

-- Basic privileges
GRANT CONNECT, RESOURCE TO crave_kitchen;
GRANT CREATE SESSION TO crave_kitchen;
GRANT CREATE TABLE TO crave_kitchen;
GRANT CREATE SEQUENCE TO crave_kitchen;
GRANT CREATE VIEW TO crave_kitchen;
GRANT CREATE PROCEDURE TO crave_kitchen;
GRANT CREATE TRIGGER TO crave_kitchen;
GRANT UNLIMITED TABLESPACE TO crave_kitchen;

-- Additional privileges for JPA/Hibernate
GRANT SELECT ANY DICTIONARY TO crave_kitchen;
GRANT SELECT ANY TABLE TO crave_kitchen;
GRANT INSERT ANY TABLE TO crave_kitchen;
GRANT UPDATE ANY TABLE TO crave_kitchen;
GRANT DELETE ANY TABLE TO crave_kitchen;

-- =====================================================
-- STEP 4: CREATE SEQUENCES
-- =====================================================

-- Menu categories sequence
CREATE SEQUENCE seq_ck_menu_categories_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Menu items sequence
CREATE SEQUENCE seq_ck_menu_items_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Menu item images sequence
CREATE SEQUENCE seq_ck_menu_item_images_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Menu item availability sequence
CREATE SEQUENCE seq_ck_menu_item_availability_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Vendors sequence
CREATE SEQUENCE seq_ck_vendors_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Vendor profiles sequence
CREATE SEQUENCE seq_ck_vendor_profiles_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Refresh tokens sequence
CREATE SEQUENCE seq_ck_refresh_tokens_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Audit logs sequence
CREATE SEQUENCE seq_ck_audit_logs_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Business hours sequence
CREATE SEQUENCE seq_ck_business_hours_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- =====================================================
-- STEP 5: GRANT SEQUENCE ACCESS
-- =====================================================

GRANT SELECT ON seq_ck_menu_categories_id TO crave_kitchen;
GRANT SELECT ON seq_ck_menu_items_id TO crave_kitchen;
GRANT SELECT ON seq_ck_menu_item_images_id TO crave_kitchen;
GRANT SELECT ON seq_ck_menu_item_availability_id TO crave_kitchen;
GRANT SELECT ON seq_ck_vendors_id TO crave_kitchen;
GRANT SELECT ON seq_ck_vendor_profiles_id TO crave_kitchen;
GRANT SELECT ON seq_ck_refresh_tokens_id TO crave_kitchen;
GRANT SELECT ON seq_ck_audit_logs_id TO crave_kitchen;
GRANT SELECT ON seq_ck_business_hours_id TO crave_kitchen;

-- =====================================================
-- STEP 6: CREATE SYNONYMS
-- =====================================================

CREATE SYNONYM crave_kitchen.seq_menu_categories_id FOR seq_ck_menu_categories_id;
CREATE SYNONYM crave_kitchen.seq_menu_items_id FOR seq_ck_menu_items_id;
CREATE SYNONYM crave_kitchen.seq_menu_item_images_id FOR seq_ck_menu_item_images_id;
CREATE SYNONYM crave_kitchen.seq_menu_item_availability_id FOR seq_ck_menu_item_availability_id;
CREATE SYNONYM crave_kitchen.seq_vendors_id FOR seq_ck_vendors_id;
CREATE SYNONYM crave_kitchen.seq_vendor_profiles_id FOR seq_ck_vendor_profiles_id;
CREATE SYNONYM crave_kitchen.seq_refresh_tokens_id FOR seq_ck_refresh_tokens_id;
CREATE SYNONYM crave_kitchen.seq_audit_logs_id FOR seq_ck_audit_logs_id;
CREATE SYNONYM crave_kitchen.seq_business_hours_id FOR seq_ck_business_hours_id;

-- =====================================================
-- STEP 7: VERIFICATION
-- =====================================================

-- Verify user creation
SELECT username, default_tablespace, account_status
FROM dba_users
WHERE username = 'CRAVE_KITCHEN';

-- Verify tablespace creation
SELECT tablespace_name, status
FROM dba_tablespaces
WHERE tablespace_name = 'CRAVE_KITCHEN_DATA';

-- Verify sequences creation
SELECT sequence_name, min_value, max_value, increment_by, last_number
FROM dba_sequences
WHERE sequence_owner = 'SYS' AND sequence_name LIKE 'SEQ_CK_%'
ORDER BY sequence_name;

-- Verify privileges
SELECT privilege, admin_option
FROM dba_sys_privs
WHERE grantee = 'CRAVE_KITCHEN'
ORDER BY privilege;

-- =====================================================
-- STEP 8: TEST CONNECTION
-- =====================================================

-- Test connection (run as crave_kitchen user)
-- CONNECT crave_kitchen/crave_kitchen123

-- Test sequence generation
-- SELECT seq_ck_menu_categories_id.NEXTVAL FROM DUAL;
-- SELECT seq_ck_menu_items_id.NEXTVAL FROM DUAL;

-- =====================================================
-- NEXT STEPS
-- =====================================================

-- After running this script:
-- 1. Run oracle_menu_tables.sql as crave_kitchen user
-- 2. Run oracle_triggers.sql as crave_kitchen user
-- 3. Update application.properties with Oracle connection details
-- 4. Start the Spring Boot application

COMMIT; 