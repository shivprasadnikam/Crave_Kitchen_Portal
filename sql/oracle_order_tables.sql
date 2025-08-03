-- Oracle SQL Scripts for Crave Kitchen Portal Order Management Tables
-- All tables follow the CK_ naming convention

-- =====================================================
-- CUSTOMERS TABLE
-- =====================================================
CREATE TABLE ck_customers (
    id NUMBER(19) PRIMARY KEY,
    user_id VARCHAR2(100) UNIQUE NOT NULL,
    first_name VARCHAR2(100) NOT NULL,
    last_name VARCHAR2(100) NOT NULL,
    email VARCHAR2(255) UNIQUE NOT NULL,
    phone_number VARCHAR2(20),
    date_of_birth DATE,
    gender VARCHAR2(10),
    is_active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(100),
    updated_by VARCHAR2(100),
    CONSTRAINT chk_customers_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_customers_gender CHECK (gender IN ('male', 'female', 'other', 'prefer_not_to_say'))
);

-- Indexes for customers table
CREATE INDEX idx_customers_user_id ON ck_customers(user_id);
CREATE INDEX idx_customers_email ON ck_customers(email);
CREATE INDEX idx_customers_active ON ck_customers(is_active);

-- =====================================================
-- ORDERS TABLE
-- =====================================================
CREATE TABLE ck_orders (
    id NUMBER(19) PRIMARY KEY,
    order_number VARCHAR2(50) UNIQUE NOT NULL,
    customer_id NUMBER(19) NOT NULL,
    vendor_id NUMBER(19) NOT NULL,
    order_status VARCHAR2(50) NOT NULL,
    order_type VARCHAR2(50) NOT NULL,
    total_amount NUMBER(10,2) NOT NULL,
    subtotal_amount NUMBER(10,2) NOT NULL,
    tax_amount NUMBER(10,2) DEFAULT 0,
    tip_amount NUMBER(10,2) DEFAULT 0,
    discount_amount NUMBER(10,2) DEFAULT 0,
    payment_status VARCHAR2(50) NOT NULL,
    payment_method VARCHAR2(50),
    estimated_preparation_time NUMBER(10),
    actual_preparation_time NUMBER(10),
    pickup_time TIMESTAMP,
    table_number VARCHAR2(20),
    order_notes CLOB,
    customer_notes CLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(100),
    updated_by VARCHAR2(100),
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES ck_customers(id),
    CONSTRAINT fk_orders_vendor FOREIGN KEY (vendor_id) REFERENCES ck_vendor_profiles(id),
    CONSTRAINT chk_orders_status CHECK (order_status IN ('pending', 'confirmed', 'preparing', 'ready', 'completed', 'cancelled')),
    CONSTRAINT chk_orders_type CHECK (order_type IN ('pickup', 'dine_in')),
    CONSTRAINT chk_orders_payment_status CHECK (payment_status IN ('pending', 'paid', 'failed', 'refunded')),
    CONSTRAINT chk_orders_amounts CHECK (total_amount >= 0 AND subtotal_amount >= 0 AND tax_amount >= 0 AND tip_amount >= 0 AND discount_amount >= 0)
);

-- Indexes for orders table
CREATE INDEX idx_orders_customer ON ck_orders(customer_id);
CREATE INDEX idx_orders_vendor ON ck_orders(vendor_id);
CREATE INDEX idx_orders_status ON ck_orders(order_status);
CREATE INDEX idx_orders_payment_status ON ck_orders(payment_status);
CREATE INDEX idx_orders_created_at ON ck_orders(created_at);
CREATE INDEX idx_orders_order_number ON ck_orders(order_number);

-- =====================================================
-- ORDER ITEMS TABLE
-- =====================================================
CREATE TABLE ck_order_items (
    id NUMBER(19) PRIMARY KEY,
    order_id NUMBER(19) NOT NULL,
    menu_item_id NUMBER(19) NOT NULL,
    quantity NUMBER(10) NOT NULL,
    unit_price NUMBER(10,2) NOT NULL,
    total_price NUMBER(10,2) NOT NULL,
    special_instructions CLOB,
    customization_options CLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(100),
    updated_by VARCHAR2(100),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES ck_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_menu_item FOREIGN KEY (menu_item_id) REFERENCES ck_menu_items(id),
    CONSTRAINT chk_order_items_quantity CHECK (quantity > 0),
    CONSTRAINT chk_order_items_prices CHECK (unit_price >= 0 AND total_price >= 0)
);

-- Indexes for order items table
CREATE INDEX idx_order_items_order ON ck_order_items(order_id);
CREATE INDEX idx_order_items_menu_item ON ck_order_items(menu_item_id);

-- =====================================================
-- ORDER STATUS HISTORY TABLE
-- =====================================================
CREATE TABLE ck_order_status_history (
    id NUMBER(19) PRIMARY KEY,
    order_id NUMBER(19) NOT NULL,
    status VARCHAR2(50) NOT NULL,
    status_description VARCHAR2(500),
    changed_by VARCHAR2(100),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_status_history_order FOREIGN KEY (order_id) REFERENCES ck_orders(id) ON DELETE CASCADE,
    CONSTRAINT chk_order_status_history_status CHECK (status IN ('pending', 'confirmed', 'preparing', 'ready', 'completed', 'cancelled'))
);

-- Indexes for order status history table
CREATE INDEX idx_order_status_history_order ON ck_order_status_history(order_id);
CREATE INDEX idx_order_status_history_changed_at ON ck_order_status_history(changed_at);

-- =====================================================
-- PAYMENT TRANSACTIONS TABLE
-- =====================================================
CREATE TABLE ck_payment_transactions (
    id NUMBER(19) PRIMARY KEY,
    order_id NUMBER(19) NOT NULL,
    transaction_id VARCHAR2(100) UNIQUE,
    payment_method VARCHAR2(50) NOT NULL,
    amount NUMBER(10,2) NOT NULL,
    currency VARCHAR2(3) DEFAULT 'USD',
    status VARCHAR2(50) NOT NULL,
    gateway_response CLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(100),
    updated_by VARCHAR2(100),
    CONSTRAINT fk_payment_transactions_order FOREIGN KEY (order_id) REFERENCES ck_orders(id) ON DELETE CASCADE,
    CONSTRAINT chk_payment_transactions_status CHECK (status IN ('pending', 'completed', 'failed', 'refunded')),
    CONSTRAINT chk_payment_transactions_amount CHECK (amount >= 0)
);

-- Indexes for payment transactions table
CREATE INDEX idx_payment_transactions_order ON ck_payment_transactions(order_id);
CREATE INDEX idx_payment_transactions_status ON ck_payment_transactions(status);
CREATE INDEX idx_payment_transactions_transaction_id ON ck_payment_transactions(transaction_id);

-- =====================================================
-- ORDER REVIEWS TABLE
-- =====================================================
CREATE TABLE ck_order_reviews (
    id NUMBER(19) PRIMARY KEY,
    order_id NUMBER(19) NOT NULL,
    customer_id NUMBER(19) NOT NULL,
    vendor_id NUMBER(19) NOT NULL,
    rating NUMBER(1) NOT NULL,
    review_text CLOB,
    is_anonymous NUMBER(1) DEFAULT 0,
    is_verified_purchase NUMBER(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(100),
    updated_by VARCHAR2(100),
    CONSTRAINT fk_order_reviews_order FOREIGN KEY (order_id) REFERENCES ck_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_reviews_customer FOREIGN KEY (customer_id) REFERENCES ck_customers(id),
    CONSTRAINT fk_order_reviews_vendor FOREIGN KEY (vendor_id) REFERENCES ck_vendor_profiles(id),
    CONSTRAINT chk_order_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT chk_order_reviews_anonymous CHECK (is_anonymous IN (0, 1)),
    CONSTRAINT chk_order_reviews_verified CHECK (is_verified_purchase IN (0, 1)),
    CONSTRAINT uk_order_reviews_order UNIQUE (order_id)
);

-- Indexes for order reviews table
CREATE INDEX idx_order_reviews_order ON ck_order_reviews(order_id);
CREATE INDEX idx_order_reviews_customer ON ck_order_reviews(customer_id);
CREATE INDEX idx_order_reviews_vendor ON ck_order_reviews(vendor_id);
CREATE INDEX idx_order_reviews_rating ON ck_order_reviews(rating);

-- =====================================================
-- PROMOTIONS TABLE
-- =====================================================
CREATE TABLE ck_promotions (
    id NUMBER(19) PRIMARY KEY,
    vendor_id NUMBER(19),
    promotion_code VARCHAR2(50) UNIQUE,
    promotion_name VARCHAR2(200) NOT NULL,
    description CLOB,
    discount_type VARCHAR2(50) NOT NULL,
    discount_value NUMBER(10,2) NOT NULL,
    minimum_order_amount NUMBER(10,2),
    maximum_discount_amount NUMBER(10,2),
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    usage_limit NUMBER(10),
    usage_count NUMBER(10) DEFAULT 0,
    is_active NUMBER(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(100),
    updated_by VARCHAR2(100),
    CONSTRAINT fk_promotions_vendor FOREIGN KEY (vendor_id) REFERENCES ck_vendor_profiles(id),
    CONSTRAINT chk_promotions_discount_type CHECK (discount_type IN ('percentage', 'fixed_amount')),
    CONSTRAINT chk_promotions_discount_value CHECK (discount_value >= 0),
    CONSTRAINT chk_promotions_active CHECK (is_active IN (0, 1)),
    CONSTRAINT chk_promotions_usage_count CHECK (usage_count >= 0)
);

-- Indexes for promotions table
CREATE INDEX idx_promotions_vendor ON ck_promotions(vendor_id);
CREATE INDEX idx_promotions_code ON ck_promotions(promotion_code);
CREATE INDEX idx_promotions_active ON ck_promotions(is_active);
CREATE INDEX idx_promotions_valid_dates ON ck_promotions(valid_from, valid_until);

-- =====================================================
-- ORDER PROMOTIONS TABLE
-- =====================================================
CREATE TABLE ck_order_promotions (
    id NUMBER(19) PRIMARY KEY,
    order_id NUMBER(19) NOT NULL,
    promotion_id NUMBER(19) NOT NULL,
    discount_amount NUMBER(10,2) NOT NULL,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(100),
    CONSTRAINT fk_order_promotions_order FOREIGN KEY (order_id) REFERENCES ck_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_promotions_promotion FOREIGN KEY (promotion_id) REFERENCES ck_promotions(id),
    CONSTRAINT chk_order_promotions_discount CHECK (discount_amount >= 0)
);

-- Indexes for order promotions table
CREATE INDEX idx_order_promotions_order ON ck_order_promotions(order_id);
CREATE INDEX idx_order_promotions_promotion ON ck_order_promotions(promotion_id);

-- =====================================================
-- TRIGGERS FOR SEQUENCES
-- =====================================================
CREATE OR REPLACE TRIGGER trg_ck_customers_id
    BEFORE INSERT ON ck_customers
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT seq_ck_customers_id.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_orders_id
    BEFORE INSERT ON ck_orders
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT seq_ck_orders_id.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_order_items_id
    BEFORE INSERT ON ck_order_items
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT seq_ck_order_items_id.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_order_status_history_id
    BEFORE INSERT ON ck_order_status_history
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT seq_ck_order_status_history_id.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_payment_transactions_id
    BEFORE INSERT ON ck_payment_transactions
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT seq_ck_payment_transactions_id.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_order_reviews_id
    BEFORE INSERT ON ck_order_reviews
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT seq_ck_order_reviews_id.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_promotions_id
    BEFORE INSERT ON ck_promotions
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT seq_ck_promotions_id.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_order_promotions_id
    BEFORE INSERT ON ck_order_promotions
    FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT seq_ck_order_promotions_id.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

-- =====================================================
-- TRIGGERS FOR UPDATED_AT TIMESTAMP
-- =====================================================
CREATE OR REPLACE TRIGGER trg_ck_customers_updated
    BEFORE UPDATE ON ck_customers
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_orders_updated
    BEFORE UPDATE ON ck_orders
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_order_items_updated
    BEFORE UPDATE ON ck_order_items
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_payment_transactions_updated
    BEFORE UPDATE ON ck_payment_transactions
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_order_reviews_updated
    BEFORE UPDATE ON ck_order_reviews
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_ck_promotions_updated
    BEFORE UPDATE ON ck_promotions
    FOR EACH ROW
BEGIN
    :NEW.updated_at := CURRENT_TIMESTAMP;
END;
/

-- =====================================================
-- COMMENTS FOR DOCUMENTATION
-- =====================================================
COMMENT ON TABLE ck_customers IS 'Stores customer information';
COMMENT ON TABLE ck_orders IS 'Stores order information';
COMMENT ON TABLE ck_order_items IS 'Stores individual items within orders';
COMMENT ON TABLE ck_order_status_history IS 'Stores order status change history';
COMMENT ON TABLE ck_payment_transactions IS 'Stores payment transaction details';
COMMENT ON TABLE ck_order_reviews IS 'Stores customer reviews for orders';
COMMENT ON TABLE ck_promotions IS 'Stores promotional offers and discounts';
COMMENT ON TABLE ck_order_promotions IS 'Stores promotions applied to specific orders'; 