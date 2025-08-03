-- =====================================================
-- ORACLE TRIGGERS FOR CRAVE KITCHEN PORTAL
-- =====================================================

-- =====================================================
-- MENU CATEGORIES TRIGGERS
-- =====================================================

-- Trigger for automatic ID generation
CREATE OR REPLACE TRIGGER trg_ck_menu_categories_id
    BEFORE
INSERT ON
ck_menu_categories
FOR
EACH
ROW
BEGIN
    IF :NEW.id IS NULL THEN
    SELECT seq_ck_menu_categories_id.NEXTVAL
    INTO
:NEW.id FROM DUAL;
END
IF;
END;
/

-- Trigger for automatic timestamp management
CREATE OR REPLACE TRIGGER trg_ck_menu_categories_timestamps
    BEFORE
INSERT OR
UPDATE ON ck_menu_categories
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.created_at := SYSTIMESTAMP;
:NEW.updated_at := SYSTIMESTAMP;
    ELSIF UPDATING THEN
        :NEW.updated_at := SYSTIMESTAMP;
END
IF;
END;
/

-- =====================================================
-- MENU ITEMS TRIGGERS
-- =====================================================

-- Trigger for automatic ID generation
CREATE OR REPLACE TRIGGER trg_ck_menu_items_id
    BEFORE
INSERT ON
ck_menu_items
FOR
EACH
ROW
BEGIN
    IF :NEW.id IS NULL THEN
    SELECT seq_ck_menu_items_id.NEXTVAL
    INTO
:NEW.id FROM DUAL;
END
IF;
END;
/

-- Trigger for automatic timestamp management
CREATE OR REPLACE TRIGGER trg_ck_menu_items_timestamps
    BEFORE
INSERT OR
UPDATE ON ck_menu_items
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.created_at := SYSTIMESTAMP;
:NEW.updated_at := SYSTIMESTAMP;
    ELSIF UPDATING THEN
        :NEW.updated_at := SYSTIMESTAMP;
END
IF;
END;
/

-- =====================================================
-- MENU ITEM IMAGES TRIGGERS
-- =====================================================

-- Trigger for automatic ID generation
CREATE OR REPLACE TRIGGER trg_ck_menu_item_images_id
    BEFORE
INSERT ON
ck_menu_item_images
FOR
EACH
ROW
BEGIN
    IF :NEW.id IS NULL THEN
    SELECT seq_ck_menu_item_images_id.NEXTVAL
    INTO
:NEW.id FROM DUAL;
END
IF;
END;
/

-- Trigger for automatic timestamp management
CREATE OR REPLACE TRIGGER trg_ck_menu_item_images_timestamps
    BEFORE
INSERT OR
UPDATE ON ck_menu_item_images
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.created_at := SYSTIMESTAMP;
:NEW.updated_at := SYSTIMESTAMP;
    ELSIF UPDATING THEN
        :NEW.updated_at := SYSTIMESTAMP;
END
IF;
END;
/

-- =====================================================
-- MENU ITEM AVAILABILITY TRIGGERS
-- =====================================================

-- Trigger for automatic ID generation
CREATE OR REPLACE TRIGGER trg_ck_menu_item_availability_id
    BEFORE
INSERT ON
ck_menu_item_availability
FOR
EACH
ROW
BEGIN
    IF :NEW.id IS NULL THEN
    SELECT seq_ck_menu_item_availability_id.NEXTVAL
    INTO
:NEW.id FROM DUAL;
END
IF;
END;
/

-- Trigger for automatic timestamp management
CREATE OR REPLACE TRIGGER trg_ck_menu_item_availability_timestamps
    BEFORE
INSERT OR
UPDATE ON ck_menu_item_availability
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.created_at := SYSTIMESTAMP;
:NEW.updated_at := SYSTIMESTAMP;
    ELSIF UPDATING THEN
        :NEW.updated_at := SYSTIMESTAMP;
END
IF;
END;
/

-- =====================================================
-- VENDORS TRIGGERS (if table exists)
-- =====================================================

-- Trigger for automatic ID generation
CREATE OR REPLACE TRIGGER trg_ck_vendors_id
    BEFORE
INSERT ON
ck_vendors
FOR
EACH
ROW
BEGIN
    IF :NEW.id IS NULL THEN
    SELECT seq_ck_vendors_id.NEXTVAL
    INTO
:NEW.id FROM DUAL;
END
IF;
END;
/

-- Trigger for automatic timestamp management
CREATE OR REPLACE TRIGGER trg_ck_vendors_timestamps
    BEFORE
INSERT OR
UPDATE ON ck_vendors
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.created_at := SYSTIMESTAMP;
:NEW.updated_at := SYSTIMESTAMP;
    ELSIF UPDATING THEN
        :NEW.updated_at := SYSTIMESTAMP;
END
IF;
END;
/

-- =====================================================
-- VENDOR PROFILES TRIGGERS (if table exists)
-- =====================================================

-- Trigger for automatic ID generation
CREATE OR REPLACE TRIGGER trg_ck_vendor_profiles_id
    BEFORE
INSERT ON
ck_vendor_profiles
FOR
EACH
ROW
BEGIN
    IF :NEW.id IS NULL THEN
    SELECT seq_ck_vendor_profiles_id.NEXTVAL
    INTO
:NEW.id FROM DUAL;
END
IF;
END;
/

-- Trigger for automatic timestamp management
CREATE OR REPLACE TRIGGER trg_ck_vendor_profiles_timestamps
    BEFORE
INSERT OR
UPDATE ON ck_vendor_profiles
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.created_at := SYSTIMESTAMP;
:NEW.updated_at := SYSTIMESTAMP;
    ELSIF UPDATING THEN
        :NEW.updated_at := SYSTIMESTAMP;
END
IF;
END;
/

-- =====================================================
-- REFRESH TOKENS TRIGGERS (if table exists)
-- =====================================================

-- Trigger for automatic ID generation
CREATE OR REPLACE TRIGGER trg_ck_refresh_tokens_id
    BEFORE
INSERT ON
ck_refresh_tokens
FOR
EACH
ROW
BEGIN
    IF :NEW.id IS NULL THEN
    SELECT seq_ck_refresh_tokens_id.NEXTVAL
    INTO
:NEW.id FROM DUAL;
END
IF;
END;
/

-- =====================================================
-- AUDIT LOGS TRIGGERS (if table exists)
-- =====================================================

-- Trigger for automatic ID generation
CREATE OR REPLACE TRIGGER trg_ck_audit_logs_id
    BEFORE
INSERT ON
ck_audit_logs
FOR
EACH
ROW
BEGIN
    IF :NEW.id IS NULL THEN
    SELECT seq_ck_audit_logs_id.NEXTVAL
    INTO
:NEW.id FROM DUAL;
END
IF;
END;
/

-- Trigger for automatic timestamp management
CREATE OR REPLACE TRIGGER trg_ck_audit_logs_timestamps
    BEFORE
INSERT ON
ck_audit_logs
FOR
EACH
ROW
BEGIN
    :NEW.created_at := SYSTIMESTAMP;
END;
/

-- =====================================================
-- BUSINESS HOURS TRIGGERS (if table exists)
-- =====================================================

-- Trigger for automatic ID generation
CREATE OR REPLACE TRIGGER trg_ck_business_hours_id
    BEFORE
INSERT ON
ck_business_hours
FOR
EACH
ROW
BEGIN
    IF :NEW.id IS NULL THEN
    SELECT seq_ck_business_hours_id.NEXTVAL
    INTO
:NEW.id FROM DUAL;
END
IF;
END;
/

-- Trigger for automatic timestamp management
CREATE OR REPLACE TRIGGER trg_ck_business_hours_timestamps
    BEFORE
INSERT OR
UPDATE ON ck_business_hours
    FOR EACH ROW
BEGIN
    IF INSERTING THEN
        :NEW.created_at := SYSTIMESTAMP;
:NEW.updated_at := SYSTIMESTAMP;
    ELSIF UPDATING THEN
        :NEW.updated_at := SYSTIMESTAMP;
END
IF;
END;
/

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Verify triggers were created
SELECT trigger_name, table_name, trigger_type, triggering_event
FROM user_triggers
WHERE table_name LIKE 'CK_%'
ORDER BY table_name, trigger_name;

-- Test trigger functionality (optional)
-- INSERT INTO ck_menu_categories (name, description, vendor_id, is_active, is_featured)
-- VALUES ('Test Category', 'Test Description', 1, 1, 0); 