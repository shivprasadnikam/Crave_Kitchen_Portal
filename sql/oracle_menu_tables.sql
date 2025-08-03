-- Oracle SQL Scripts for Crave Kitchen Portal Menu Tables
-- All tables follow the CK_ naming convention

-- =====================================================
-- SEQUENCES
-- =====================================================
CREATE SEQUENCE seq_ck_menu_categories_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE SEQUENCE seq_ck_menu_items_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE SEQUENCE seq_ck_menu_item_images_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE SEQUENCE seq_ck_menu_item_availability_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- =====================================================
-- MENU CATEGORIES TABLE
-- =====================================================
CREATE TABLE ck_menu_categories (
    id NUMBER(19) PRIMARY KEY,
    vendor_id NUMBER(19) NOT NULL,
    name VARCHAR2(100) NOT NULL,
    description CLOB,
    display_order NUMBER(10) DEFAULT 0,
    is_active NUMBER(1) DEFAULT 1,
    is_featured NUMBER(1) DEFAULT 0,
    image_url VARCHAR2(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(100),
    updated_by VARCHAR2(100),
    CONSTRAINT fk_menu_categories_vendor FOREIGN KEY (vendor_id) REFERENCES ck_vendor_profiles(id),
    CONSTRAINT chk_menu_categories_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_menu_categories_featured CHECK (is_featured IN (0, 1))
);

-- Index for better performance
CREATE INDEX idx_menu_categories_vendor ON ck_menu_categories(vendor_id);
CREATE INDEX idx_menu_categories_active ON ck_menu_categories(is_active);
CREATE INDEX idx_menu_categories_featured ON ck_menu_categories(is_featured);
CREATE INDEX idx_menu_categories_display_order ON ck_menu_categories(display_order);

-- =====================================================
-- MENU ITEMS TABLE
-- =====================================================
CREATE TABLE ck_menu_items (
    id NUMBER(19) PRIMARY KEY,
    vendor_id NUMBER(19) NOT NULL,
    category_id NUMBER(19) NOT NULL,
    name VARCHAR2(200) NOT NULL,
    description CLOB,
    price NUMBER(10,2) NOT NULL,
    original_price NUMBER(10,2),
    is_available NUMBER(1) DEFAULT 1,
    is_featured NUMBER(1) DEFAULT 0,
    is_vegetarian NUMBER(1) DEFAULT 0,
    is_vegan NUMBER(1) DEFAULT 0,
    is_gluten_free NUMBER(1) DEFAULT 0,
    is_spicy NUMBER(1) DEFAULT 0,
    spice_level VARCHAR2(50) DEFAULT 'none',
    calories NUMBER(10),
    protein_grams NUMBER(8,2),
    carbs_grams NUMBER(8,2),
    fat_grams NUMBER(8,2),
    allergens CLOB,
    ingredients CLOB,
    cooking_instructions CLOB,
    preparation_time_minutes NUMBER(10),
    display_order NUMBER(10) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(100),
    updated_by VARCHAR2(100),
    CONSTRAINT fk_menu_items_vendor FOREIGN KEY (vendor_id) REFERENCES ck_vendor_profiles(id),
    CONSTRAINT fk_menu_items_category FOREIGN KEY (category_id) REFERENCES ck_menu_categories(id),
    CONSTRAINT chk_menu_items_available CHECK (is_available IN (0, 1)),
    CONSTRAINT chk_menu_items_featured CHECK (is_featured IN (0, 1)),
    CONSTRAINT chk_menu_items_vegetarian CHECK (is_vegetarian IN (0, 1)),
    CONSTRAINT chk_menu_items_vegan CHECK (is_vegan IN (0, 1)),
    CONSTRAINT chk_menu_items_gluten_free CHECK (is_gluten_free IN (0, 1)),
    CONSTRAINT chk_menu_items_spicy CHECK (is_spicy IN (0, 1)),
    CONSTRAINT chk_menu_items_price CHECK (price >= 0),
    CONSTRAINT chk_menu_items_original_price CHECK (original_price >= 0)
);

-- Indexes for better performance
CREATE INDEX idx_menu_items_vendor ON ck_menu_items(vendor_id);
CREATE INDEX idx_menu_items_category ON ck_menu_items(category_id);
CREATE INDEX idx_menu_items_available ON ck_menu_items(is_available);
CREATE INDEX idx_menu_items_featured ON ck_menu_items(is_featured);
CREATE INDEX idx_menu_items_vegetarian ON ck_menu_items(is_vegetarian);
CREATE INDEX idx_menu_items_vegan ON ck_menu_items(is_vegan);
CREATE INDEX idx_menu_items_gluten_free ON ck_menu_items(is_gluten_free);
CREATE INDEX idx_menu_items_spicy ON ck_menu_items(is_spicy);
CREATE INDEX idx_menu_items_display_order ON ck_menu_items(display_order);

-- =====================================================
-- MENU ITEM IMAGES TABLE
-- =====================================================
CREATE TABLE ck_menu_item_images (
    id NUMBER(19) PRIMARY KEY,
    menu_item_id NUMBER(19) NOT NULL,
    image_url VARCHAR2(500) NOT NULL,
    image_type VARCHAR2(50),
    file_name VARCHAR2(255),
    file_size_bytes NUMBER(19),
    width_pixels NUMBER(10),
    height_pixels NUMBER(10),
    is_primary NUMBER(1) DEFAULT 0,
    display_order NUMBER(10) DEFAULT 0,
    alt_text VARCHAR2(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(100),
    updated_by VARCHAR2(100),
    CONSTRAINT fk_menu_item_images_item FOREIGN KEY (menu_item_id) REFERENCES ck_menu_items(id) ON DELETE CASCADE,
    CONSTRAINT chk_menu_item_images_primary CHECK (is_primary IN (0, 1)),
    CONSTRAINT chk_menu_item_images_file_size CHECK (file_size_bytes >= 0),
    CONSTRAINT chk_menu_item_images_dimensions CHECK (width_pixels >= 0 AND height_pixels >= 0)
);

-- Indexes for better performance
CREATE INDEX idx_menu_item_images_item ON ck_menu_item_images(menu_item_id);
CREATE INDEX idx_menu_item_images_primary ON ck_menu_item_images(is_primary);
CREATE INDEX idx_menu_item_images_display_order ON ck_menu_item_images(display_order);

-- =====================================================
-- MENU ITEM AVAILABILITY TABLE
-- =====================================================
CREATE TABLE ck_menu_item_availability (
    id NUMBER(19) PRIMARY KEY,
    menu_item_id NUMBER(19) NOT NULL,
    day_of_week NUMBER(1) NOT NULL, -- 1=Monday, 2=Tuesday, ..., 7=Sunday
    is_available NUMBER(1) DEFAULT 1,
    available_from TIME,
    available_until TIME,
    max_quantity_per_day NUMBER(10),
    current_quantity_available NUMBER(10),
    is_special_offer NUMBER(1) DEFAULT 0,
    special_offer_price NUMBER(10,2),
    special_offer_description VARCHAR2(500),
    special_offer_valid_from TIMESTAMP,
    special_offer_valid_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(100),
    updated_by VARCHAR2(100),
    CONSTRAINT fk_menu_item_availability_item FOREIGN KEY (menu_item_id) REFERENCES ck_menu_items(id) ON DELETE CASCADE,
    CONSTRAINT chk_menu_item_availability_day CHECK (day_of_week BETWEEN 1 AND 7),
    CONSTRAINT chk_menu_item_availability_available CHECK (is_available IN (0, 1)),
    CONSTRAINT chk_menu_item_availability_special_offer CHECK (is_special_offer IN (0, 1)),
    CONSTRAINT chk_menu_item_availability_quantity CHECK (max_quantity_per_day >= 0 AND current_quantity_available >= 0),
    CONSTRAINT chk_menu_item_availability_special_price CHECK (special_offer_price >= 0),
    CONSTRAINT uk_menu_item_availability UNIQUE (menu_item_id, day_of_week)
);

-- Indexes for better performance
CREATE INDEX idx_menu_item_availability_item ON ck_menu_item_availability(menu_item_id);
CREATE INDEX idx_menu_item_availability_day ON ck_menu_item_availability(day_of_week);
CREATE INDEX idx_menu_item_availability_available ON ck_menu_item_availability(is_available);
CREATE INDEX idx_menu_item_availability_special_offer ON ck_menu_item_availability(is_special_offer);

-- =====================================================
-- TRIGGERS FOR SEQUENCES
-- =====================================================
CREATE OR REPLACE TRIGGER trg_ck_menu_categories_id
    BEFORE INSERT ON ck_menu_categories
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT seq_ck_menu_categories_id.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_menu_items_id
    BEFORE INSERT ON ck_menu_items
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT seq_ck_menu_items_id.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_menu_item_images_id
    BEFORE INSERT ON ck_menu_item_images
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT seq_ck_menu_item_images_id.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_menu_item_availability_id
    BEFORE INSERT ON ck_menu_item_availability
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT seq_ck_menu_item_availability_id.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

-- =====================================================
-- TRIGGERS FOR UPDATED_AT TIMESTAMP
-- =====================================================
CREATE OR REPLACE TRIGGER trg_ck_menu_categories_updated
    BEFORE UPDATE ON ck_menu_categories
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_menu_items_updated
    BEFORE UPDATE ON ck_menu_items
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_menu_item_images_updated
    BEFORE UPDATE ON ck_menu_item_images
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_menu_item_availability_updated
    BEFORE UPDATE ON ck_menu_item_availability
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- =====================================================
-- SAMPLE DATA INSERTION (Optional)
-- =====================================================
-- Uncomment and modify as needed for testing

/*
-- Insert sample vendor (assuming vendor ID 1 exists)
INSERT INTO ck_menu_categories (vendor_id, name, description, display_order, is_active, is_featured) 
VALUES (1, 'Appetizers', 'Delicious starters to begin your meal', 1, 1, 1);

INSERT INTO ck_menu_categories (vendor_id, name, description, display_order, is_active, is_featured) 
VALUES (1, 'Main Course', 'Hearty main dishes', 2, 1, 0);

INSERT INTO ck_menu_categories (vendor_id, name, description, display_order, is_active, is_featured) 
VALUES (1, 'Desserts', 'Sweet treats to end your meal', 3, 1, 1);

-- Insert sample menu items
INSERT INTO ck_menu_items (vendor_id, category_id, name, description, price, is_available, is_featured) 
VALUES (1, 1, 'Spring Rolls', 'Fresh vegetables wrapped in rice paper', 8.99, 1, 1);

INSERT INTO ck_menu_items (vendor_id, category_id, name, description, price, is_available, is_featured) 
VALUES (1, 2, 'Grilled Chicken', 'Tender grilled chicken with herbs', 18.99, 1, 0);
*/

-- =====================================================
-- COMMENTS FOR DOCUMENTATION
-- =====================================================
COMMENT ON TABLE ck_menu_categories IS 'Stores menu categories for vendors';
COMMENT ON COLUMN ck_menu_categories.id IS 'Primary key for menu categories';
COMMENT ON COLUMN ck_menu_categories.vendor_id IS 'Foreign key to vendor profiles';
COMMENT ON COLUMN ck_menu_categories.name IS 'Category name';
COMMENT ON COLUMN ck_menu_categories.description IS 'Category description';
COMMENT ON COLUMN ck_menu_categories.display_order IS 'Order for display purposes';
COMMENT ON COLUMN ck_menu_categories.is_active IS 'Whether category is active (1=active, 0=inactive)';
COMMENT ON COLUMN ck_menu_categories.is_featured IS 'Whether category is featured (1=featured, 0=not featured)';

COMMENT ON TABLE ck_menu_items IS 'Stores individual menu items';
COMMENT ON COLUMN ck_menu_items.id IS 'Primary key for menu items';
COMMENT ON COLUMN ck_menu_items.vendor_id IS 'Foreign key to vendor profiles';
COMMENT ON COLUMN ck_menu_items.category_id IS 'Foreign key to menu categories';
COMMENT ON COLUMN ck_menu_items.name IS 'Menu item name';
COMMENT ON COLUMN ck_menu_items.description IS 'Menu item description';
COMMENT ON COLUMN ck_menu_items.price IS 'Current price of the item';
COMMENT ON COLUMN ck_menu_items.original_price IS 'Original price before discounts';
COMMENT ON COLUMN ck_menu_items.is_available IS 'Whether item is available (1=available, 0=unavailable)';
COMMENT ON COLUMN ck_menu_items.is_featured IS 'Whether item is featured (1=featured, 0=not featured)';
COMMENT ON COLUMN ck_menu_items.is_vegetarian IS 'Whether item is vegetarian (1=yes, 0=no)';
COMMENT ON COLUMN ck_menu_items.is_vegan IS 'Whether item is vegan (1=yes, 0=no)';
COMMENT ON COLUMN ck_menu_items.is_gluten_free IS 'Whether item is gluten-free (1=yes, 0=no)';
COMMENT ON COLUMN ck_menu_items.is_spicy IS 'Whether item is spicy (1=yes, 0=no)';

COMMENT ON TABLE ck_menu_item_images IS 'Stores images for menu items';
COMMENT ON COLUMN ck_menu_item_images.id IS 'Primary key for menu item images';
COMMENT ON COLUMN ck_menu_item_images.menu_item_id IS 'Foreign key to menu items';
COMMENT ON COLUMN ck_menu_item_images.image_url IS 'URL of the image';
COMMENT ON COLUMN ck_menu_item_images.is_primary IS 'Whether this is the primary image (1=primary, 0=secondary)';

COMMENT ON TABLE ck_menu_item_availability IS 'Stores availability schedule for menu items';
COMMENT ON COLUMN ck_menu_item_availability.id IS 'Primary key for menu item availability';
COMMENT ON COLUMN ck_menu_item_availability.menu_item_id IS 'Foreign key to menu items';
COMMENT ON COLUMN ck_menu_item_availability.day_of_week IS 'Day of week (1=Monday, 2=Tuesday, ..., 7=Sunday)';
COMMENT ON COLUMN ck_menu_item_availability.is_available IS 'Whether item is available on this day (1=available, 0=unavailable)';
COMMENT ON COLUMN ck_menu_item_availability.available_from IS 'Time from which item is available';
COMMENT ON COLUMN ck_menu_item_availability.available_until IS 'Time until which item is available';
COMMENT ON COLUMN ck_menu_item_availability.is_special_offer IS 'Whether there is a special offer (1=yes, 0=no)'; 