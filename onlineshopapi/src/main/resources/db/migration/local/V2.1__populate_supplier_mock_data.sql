-- Migration V2.1: Populate suppliers with mock data for local development
--
-- This migration:
-- 1. Inserts 4 real suppliers with contact information
-- 2. Updates existing products to link to appropriate suppliers based on category
--
-- Note: This migration only runs in the 'local' profile

-- Insert real suppliers (replacing/augmenting default)
INSERT INTO suppliers (id, name, description, contact_email, contact_phone, address)
VALUES
    ('50771e01-0000-0000-0000-000000000001', 'TechVision Electronics', 'Premium electronics supplier specializing in consumer devices', 'contact@techvision.com', '+1-555-0101', '123 Tech Avenue, Silicon Valley, CA'),
    ('50771e02-0000-0000-0000-000000000002', 'GlobalThreads Textiles', 'International clothing and apparel manufacturer', 'sales@globalthreads.com', '+1-555-0102', '456 Fashion Street, New York, NY'),
    ('50771e03-0000-0000-0000-000000000003', 'HomeStyle Distributors', 'Home and garden products wholesale', 'info@homestyle.com', '+1-555-0103', '789 Garden Lane, Portland, OR'),
    ('50771e04-0000-0000-0000-000000000004', 'ActiveGear Sports', 'Sports equipment and fitness accessories', 'orders@activegear.com', '+1-555-0104', '321 Athletic Drive, Denver, CO');

-- Update existing products to use specific suppliers based on category
-- Note: Using category names to match since UUIDs may vary between environments

-- Electronics products -> TechVision Electronics
UPDATE products
SET supplier_id = '50771e01-0000-0000-0000-000000000001'
WHERE category_id IN (SELECT id FROM product_categories WHERE name = 'Electronics');

-- Clothing products -> GlobalThreads Textiles
UPDATE products
SET supplier_id = '50771e02-0000-0000-0000-000000000002'
WHERE category_id IN (SELECT id FROM product_categories WHERE name = 'Clothing');

-- Home & Garden products -> HomeStyle Distributors
UPDATE products
SET supplier_id = '50771e03-0000-0000-0000-000000000003'
WHERE category_id IN (SELECT id FROM product_categories WHERE name = 'Home & Garden');

-- Sports products -> ActiveGear Sports
UPDATE products
SET supplier_id = '50771e04-0000-0000-0000-000000000004'
WHERE category_id IN (SELECT id FROM product_categories WHERE name = 'Sports');
