-- Create sequences for Oracle ID generation
-- Run this script to create the required sequences for the application

-- Sequence for menu items
CREATE SEQUENCE menu_item_seq
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Sequence for menu categories
CREATE SEQUENCE menu_category_seq
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Sequence for menu item images
CREATE SEQUENCE menu_item_image_seq
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Sequence for menu item availability
CREATE SEQUENCE menu_item_availability_seq
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Sequence for vendors
CREATE SEQUENCE vendor_seq
    START WITH 1
    INCREMENT BY 1
NOCACHE
    NOCYCLE;

-- Add comments to sequences
COMMENT ON SEQUENCE menu_item_seq IS 'Sequence for generating unique IDs for menu items';
COMMENT ON SEQUENCE menu_category_seq IS 'Sequence for generating unique IDs for menu categories';
COMMENT ON SEQUENCE menu_item_image_seq IS 'Sequence for generating unique IDs for menu item images';
COMMENT ON SEQUENCE menu_item_availability_seq IS 'Sequence for generating unique IDs for menu item availability';
COMMENT ON SEQUENCE vendor_seq IS 'Sequence for generating unique IDs for vendors';

-- Grant permissions (adjust as needed for your Oracle setup)
-- GRANT SELECT ON menu_item_seq TO my_user;
-- GRANT SELECT ON menu_category_seq TO my_user;
-- GRANT SELECT ON menu_item_image_seq TO my_user;
-- GRANT SELECT ON menu_item_availability_seq TO my_user;
-- GRANT SELECT ON vendor_seq TO my_user; 