-- =====================================================
-- ORACLE SEQUENCES FOR CRAVE KITCHEN PORTAL
-- =====================================================

-- Sequence for menu categories
CREATE SEQUENCE seq_ck_menu_categories_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Sequence for menu items
CREATE SEQUENCE seq_ck_menu_items_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Sequence for menu item images
CREATE SEQUENCE seq_ck_menu_item_images_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Sequence for menu item availability
CREATE SEQUENCE seq_ck_menu_item_availability_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Sequence for vendors (if not exists)
CREATE SEQUENCE seq_ck_vendors_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Sequence for vendor profiles (if not exists)
CREATE SEQUENCE seq_ck_vendor_profiles_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Sequence for refresh tokens (if not exists)
CREATE SEQUENCE seq_ck_refresh_tokens_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Sequence for audit logs (if not exists)
CREATE SEQUENCE seq_ck_audit_logs_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Sequence for business hours (if not exists)
CREATE SEQUENCE seq_ck_business_hours_id
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- =====================================================
-- SEQUENCE GRANTS
-- =====================================================

-- Grant sequence usage to the application user
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
-- SEQUENCE SYNONYMS (Optional - for easier access)
-- =====================================================

-- Create synonyms for easier access
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
-- VERIFICATION QUERIES
-- =====================================================

-- Verify sequences were created
SELECT sequence_name, min_value, max_value, increment_by, last_number
FROM user_sequences
WHERE sequence_name LIKE 'SEQ_CK_%'
ORDER BY sequence_name;

-- Test sequence generation
SELECT seq_ck_menu_categories_id.NEXTVAL
FROM DUAL;
SELECT seq_ck_menu_items_id.NEXTVAL
FROM DUAL;
SELECT seq_ck_menu_item_images_id.NEXTVAL
FROM DUAL;
SELECT seq_ck_menu_item_availability_id.NEXTVAL
FROM DUAL; 