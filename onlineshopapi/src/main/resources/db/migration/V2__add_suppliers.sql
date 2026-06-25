-- Migration V2: Add supplier support to products
--
-- This migration:
-- 1. Creates suppliers table with contact information
-- 2. Adds supplier_id foreign key to products table
-- 3. Seeds a default "General Supplier" for existing products
-- 4. Enforces NOT NULL constraint on supplier_id
--
-- Rollback: See rollback strategy in CLAUDE.md

-- Create suppliers table
CREATE TABLE suppliers (
    id          UUID         PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    address     VARCHAR(500)
);

-- Create default supplier for existing products
INSERT INTO suppliers (id, name, description)
VALUES ('00000000-0000-0000-0000-000000000000', 'General Supplier', 'Default supplier for existing products');

-- Add supplier_id column to products (nullable first)
ALTER TABLE products
ADD COLUMN supplier_id UUID;

-- Update all existing products to use default supplier
UPDATE products
SET supplier_id = '00000000-0000-0000-0000-000000000000'
WHERE supplier_id IS NULL;

-- Make supplier_id NOT NULL and add foreign key constraint
ALTER TABLE products
ALTER COLUMN supplier_id SET NOT NULL,
ADD CONSTRAINT fk_products_supplier
    FOREIGN KEY (supplier_id) REFERENCES suppliers (id);
