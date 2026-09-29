-- =============================================================================
-- Personal Finance Dashboard - Database Initialization Script
-- =============================================================================
-- This script runs on the first PostgreSQL startup via docker-entrypoint-initdb.d.
-- It creates the categories table and seeds it with default system categories.
-- JPA (ddl-auto=update) will validate/extend the schema on backend startup.
-- =============================================================================

-- Create the categories table
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    icon VARCHAR(50),
    type VARCHAR(20) NOT NULL,
    user_id BIGINT
);

-- Create an index on type for faster filtering
CREATE INDEX IF NOT EXISTS idx_categories_type ON categories (type);

-- Create an index on user_id for filtering user-specific vs system categories
CREATE INDEX IF NOT EXISTS idx_categories_user_id ON categories (user_id);

-- =============================================================================
-- Default EXPENSE categories (user_id IS NULL = system/default categories)
-- =============================================================================
INSERT INTO categories (name, icon, type, user_id) VALUES
    ('Food & Dining',      'utensils',     'EXPENSE', NULL),
    ('Transportation',     'car',          'EXPENSE', NULL),
    ('Entertainment',      'film',         'EXPENSE', NULL),
    ('Bills & Utilities',  'receipt',      'EXPENSE', NULL),
    ('Shopping',           'cart',         'EXPENSE', NULL),
    ('Healthcare',         'heart',        'EXPENSE', NULL),
    ('Education',          'book',         'EXPENSE', NULL),
    ('Housing',            'home',         'EXPENSE', NULL),
    ('Personal Care',      'user',         'EXPENSE', NULL),
    ('Travel',             'plane',        'EXPENSE', NULL),
    ('Gifts & Donations',  'gift',         'EXPENSE', NULL),
    ('Other Expense',      'dots',         'EXPENSE', NULL)
ON CONFLICT (name) DO NOTHING;

-- =============================================================================
-- Default INCOME categories (user_id IS NULL = system/default categories)
-- =============================================================================
INSERT INTO categories (name, icon, type, user_id) VALUES
    ('Salary',             'briefcase',    'INCOME', NULL),
    ('Freelance',          'laptop',       'INCOME', NULL),
    ('Investments',        'trending-up',  'INCOME', NULL),
    ('Rental Income',      'home',         'INCOME', NULL),
    ('Gifts Received',     'gift',         'INCOME', NULL),
    ('Other Income',       'dots',         'INCOME', NULL)
ON CONFLICT (name) DO NOTHING;
