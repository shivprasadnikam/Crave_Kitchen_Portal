-- Migration script to add spice_level column to ck_menu_items table
-- Run this script if the table already exists and you need to add the spice_level column

-- Add spice_level column to ck_menu_items table
ALTER TABLE ck_menu_items ADD
(
    spice_level VARCHAR2
(50) DEFAULT 'none'
);

-- Update existing records to have a default spice level
UPDATE ck_menu_items SET spice_level = 'none' WHERE spice_level IS NULL;

-- Add comment to the column
COMMENT ON COLUMN ck_menu_items.spice_level IS 'Spice level description (e.g., none, mild, medium, hot, extra hot)'; 