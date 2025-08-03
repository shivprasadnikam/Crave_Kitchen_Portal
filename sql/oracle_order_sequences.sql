-- Oracle SQL Scripts for Crave Kitchen Portal Order Management Sequences
-- All sequences follow the seq_ck_ naming convention

-- =====================================================
-- SEQUENCES FOR ORDER MANAGEMENT SYSTEM
-- =====================================================

-- Orders sequence
CREATE SEQUENCE seq_ck_orders_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Order items sequence
CREATE SEQUENCE seq_ck_order_items_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Customers sequence
CREATE SEQUENCE seq_ck_customers_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Order status history sequence
CREATE SEQUENCE seq_ck_order_status_history_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Payment transactions sequence
CREATE SEQUENCE seq_ck_payment_transactions_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Order reviews sequence
CREATE SEQUENCE seq_ck_order_reviews_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Promotions sequence
CREATE SEQUENCE seq_ck_promotions_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Order promotions sequence
CREATE SEQUENCE seq_ck_order_promotions_id
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- =====================================================
-- COMMENTS FOR DOCUMENTATION
-- =====================================================
COMMENT ON SEQUENCE seq_ck_orders_id IS 'Sequence for generating unique order IDs';
COMMENT ON SEQUENCE seq_ck_order_items_id IS 'Sequence for generating unique order item IDs';
COMMENT ON SEQUENCE seq_ck_customers_id IS 'Sequence for generating unique customer IDs';
COMMENT ON SEQUENCE seq_ck_order_status_history_id IS 'Sequence for generating unique order status history IDs';
COMMENT ON SEQUENCE seq_ck_payment_transactions_id IS 'Sequence for generating unique payment transaction IDs';
COMMENT ON SEQUENCE seq_ck_order_reviews_id IS 'Sequence for generating unique order review IDs';
COMMENT ON SEQUENCE seq_ck_promotions_id IS 'Sequence for generating unique promotion IDs';
COMMENT ON SEQUENCE seq_ck_order_promotions_id IS 'Sequence for generating unique order promotion IDs'; 