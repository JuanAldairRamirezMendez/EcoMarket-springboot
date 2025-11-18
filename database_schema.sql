-- =========================================
-- EcoMarket Database Schema
-- Complete e-commerce platform for eco-friendly products
-- PostgreSQL 13+
-- =========================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================================
-- CORE ENTITIES
-- =========================================

-- Users and Authentication
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Roles for authorization
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- User-Role relationship (many-to-many)
CREATE TABLE users_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT REFERENCES users(id),
    PRIMARY KEY (user_id, role_id)
);

-- User addresses
CREATE TABLE user_addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    address_type VARCHAR(20) NOT NULL DEFAULT 'shipping', -- shipping, billing
    is_default BOOLEAN DEFAULT FALSE,
    full_name VARCHAR(200) NOT NULL,
    street_address TEXT NOT NULL,
    apartment VARCHAR(100),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL DEFAULT 'Colombia',
    phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Categories for products
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(120) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    parent_id BIGINT REFERENCES categories(id),
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Products
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(280) NOT NULL UNIQUE,
    description TEXT,
    short_description VARCHAR(500),
    sku VARCHAR(100) UNIQUE,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    compare_price DECIMAL(10,2) CHECK (compare_price >= price),
    cost_price DECIMAL(10,2),
    stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    low_stock_threshold INTEGER DEFAULT 10,
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    is_digital BOOLEAN DEFAULT FALSE,
    weight DECIMAL(8,3), -- in kg
    dimensions JSONB, -- {"length": 10, "width": 5, "height": 2}
    category_id BIGINT REFERENCES categories(id),
    brand VARCHAR(100),
    tags TEXT[], -- PostgreSQL array for tags
    seo_title VARCHAR(255),
    seo_description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Product images
CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    alt_text VARCHAR(255),
    sort_order INTEGER DEFAULT 0,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Product variants (sizes, colors, etc.)
CREATE TABLE product_variants (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    sku VARCHAR(100) UNIQUE,
    name VARCHAR(255) NOT NULL, -- e.g., "Size: M, Color: Red"
    price_modifier DECIMAL(10,2) DEFAULT 0, -- additional cost
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    attributes JSONB NOT NULL, -- {"size": "M", "color": "red"}
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Eco-friendly attributes
CREATE TABLE product_eco_attributes (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    is_organic BOOLEAN DEFAULT FALSE,
    is_recyclable BOOLEAN DEFAULT FALSE,
    is_biodegradable BOOLEAN DEFAULT FALSE,
    carbon_footprint DECIMAL(8,3), -- kg CO2 per unit
    water_usage DECIMAL(8,3), -- liters per unit
    certifications TEXT[], -- array of certification names
    origin_country VARCHAR(100),
    manufacturing_country VARCHAR(100),
    sustainability_score INTEGER CHECK (sustainability_score >= 0 AND sustainability_score <= 100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- E-COMMERCE FUNCTIONALITY
-- =========================================

-- Shopping cart
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    session_id VARCHAR(255), -- for guest users
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id),
    UNIQUE(session_id)
);

-- Cart items
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    variant_id BIGINT REFERENCES product_variants(id) ON DELETE SET NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(cart_id, product_id, variant_id)
);

-- Orders
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    status VARCHAR(50) NOT NULL DEFAULT 'pending', -- pending, confirmed, processing, shipped, delivered, cancelled
    payment_status VARCHAR(50) NOT NULL DEFAULT 'pending', -- pending, paid, failed, refunded
    shipping_status VARCHAR(50) NOT NULL DEFAULT 'not_shipped', -- not_shipped, preparing, shipped, delivered
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    shipping_amount DECIMAL(10,2) DEFAULT 0,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'COP',
    payment_method VARCHAR(50),
    payment_reference VARCHAR(255),
    shipping_address_id BIGINT REFERENCES user_addresses(id),
    billing_address_id BIGINT REFERENCES user_addresses(id),
    notes TEXT,
    ordered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Order items
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id),
    variant_id BIGINT REFERENCES product_variants(id),
    product_name VARCHAR(255) NOT NULL, -- snapshot at order time
    product_sku VARCHAR(100),
    unit_price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- REVIEWS AND RATINGS
-- =========================================

-- Product reviews
CREATE TABLE product_reviews (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    order_item_id BIGINT REFERENCES order_items(id), -- link to purchase
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(255),
    comment TEXT,
    is_verified_purchase BOOLEAN DEFAULT FALSE,
    is_approved BOOLEAN DEFAULT TRUE,
    helpful_votes INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id, user_id, order_item_id)
);

-- Review responses (from sellers/admins)
CREATE TABLE review_responses (
    id BIGSERIAL PRIMARY KEY,
    review_id BIGINT NOT NULL REFERENCES product_reviews(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    response TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- WISHLIST AND FAVORITES
-- =========================================

-- User wishlists
CREATE TABLE wishlists (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL DEFAULT 'Mi Lista de Deseos',
    is_default BOOLEAN DEFAULT TRUE,
    is_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, name)
);

-- Wishlist items
CREATE TABLE wishlist_items (
    id BIGSERIAL PRIMARY KEY,
    wishlist_id BIGINT NOT NULL REFERENCES wishlists(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(wishlist_id, product_id)
);

-- =========================================
-- PROMOTIONS AND DISCOUNTS
-- =========================================

-- Coupons/Discount codes
CREATE TABLE coupons (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    discount_type VARCHAR(20) NOT NULL, -- percentage, fixed_amount
    discount_value DECIMAL(10,2) NOT NULL,
    minimum_order_amount DECIMAL(10,2),
    maximum_discount_amount DECIMAL(10,2),
    usage_limit INTEGER, -- total usages allowed
    usage_count INTEGER DEFAULT 0,
    user_usage_limit INTEGER DEFAULT 1, -- usages per user
    starts_at TIMESTAMP,
    expires_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    applicable_categories BIGINT[], -- array of category IDs
    applicable_products BIGINT[], -- array of product IDs
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Coupon usage tracking
CREATE TABLE coupon_usages (
    id BIGSERIAL PRIMARY KEY,
    coupon_id BIGINT NOT NULL REFERENCES coupons(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    order_id BIGINT REFERENCES orders(id) ON DELETE SET NULL,
    discount_amount DECIMAL(10,2) NOT NULL,
    used_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(coupon_id, user_id, order_id)
);

-- =========================================
-- NOTIFICATIONS AND COMMUNICATION
-- =========================================

-- Notifications
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL, -- order_status, promotion, review_response, etc.
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    data JSONB, -- additional structured data
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Email templates
CREATE TABLE email_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    subject VARCHAR(255) NOT NULL,
    html_content TEXT NOT NULL,
    text_content TEXT,
    variables TEXT[], -- array of variable names
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- INVENTORY AND SUPPLIERS
-- =========================================

-- Suppliers/Vendors
CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    address TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Product suppliers relationship
CREATE TABLE product_suppliers (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    supplier_id BIGINT NOT NULL REFERENCES suppliers(id) ON DELETE CASCADE,
    supplier_sku VARCHAR(100),
    cost_price DECIMAL(10,2),
    lead_time_days INTEGER,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id, supplier_id)
);

-- Inventory movements (for tracking stock changes)
CREATE TABLE inventory_movements (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    variant_id BIGINT REFERENCES product_variants(id),
    movement_type VARCHAR(50) NOT NULL, -- purchase, sale, adjustment, return
    quantity INTEGER NOT NULL, -- positive for additions, negative for reductions
    reference_id BIGINT, -- order_id, purchase_id, etc.
    reference_type VARCHAR(50), -- order, purchase, manual_adjustment
    notes TEXT,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- ANALYTICS AND REPORTING
-- =========================================

-- Product views (for analytics)
CREATE TABLE product_views (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    session_id VARCHAR(255),
    ip_address INET,
    user_agent TEXT,
    viewed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Search queries (for analytics)
CREATE TABLE search_queries (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    session_id VARCHAR(255),
    query TEXT NOT NULL,
    filters JSONB, -- applied filters
    result_count INTEGER,
    searched_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- AUDIT AND LOGGING
-- =========================================

-- Audit log for important actions
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL, -- product, order, user, etc.
    entity_id BIGINT,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- INDEXES FOR PERFORMANCE
-- =========================================

-- Users indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_active ON users(is_active);

-- Products indexes
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_active ON products(is_active);
CREATE INDEX idx_products_featured ON products(is_featured);
CREATE INDEX idx_products_slug ON products(slug);
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_created_at ON products(created_at);

-- Orders indexes
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_payment_status ON orders(payment_status);
CREATE INDEX idx_orders_ordered_at ON orders(ordered_at);
CREATE INDEX idx_orders_total_amount ON orders(total_amount);

-- Categories indexes
CREATE INDEX idx_categories_parent ON categories(parent_id);
CREATE INDEX idx_categories_active ON categories(is_active);

-- Cart indexes
CREATE INDEX idx_cart_items_cart ON cart_items(cart_id);
CREATE INDEX idx_cart_items_product ON cart_items(product_id);

-- Reviews indexes
CREATE INDEX idx_product_reviews_product ON product_reviews(product_id);
CREATE INDEX idx_product_reviews_user ON product_reviews(user_id);
CREATE INDEX idx_product_reviews_rating ON product_reviews(rating);

-- Notifications indexes
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);

-- =========================================
-- TRIGGERS FOR UPDATED_AT
-- =========================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers to all tables with updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_categories_updated_at BEFORE UPDATE ON categories FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_carts_updated_at BEFORE UPDATE ON carts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_cart_items_updated_at BEFORE UPDATE ON cart_items FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_addresses_updated_at BEFORE UPDATE ON user_addresses FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =========================================
-- INITIAL DATA
-- =========================================

-- Insert default roles
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', 'Administrator with full access'),
('ROLE_USER', 'Regular user'),
('ROLE_SELLER', 'Seller/Vendor role')
ON CONFLICT (name) DO NOTHING;

-- Insert default admin user (password: AdminPass123)
INSERT INTO users (username, email, password, first_name, last_name, is_active) VALUES
('admin', 'admin@example.com', '$2a$10$8K3W2QJc8QJc8QJc8QJc8e8QJc8QJc8QJc8QJc8QJc8QJc8QJc8QJc8QJc8', 'Admin', 'User', true)
ON CONFLICT (username) DO NOTHING;

-- Assign admin role to admin user
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- Insert sample categories
INSERT INTO categories (name, slug, description, sort_order) VALUES
('Frutas y Verduras', 'frutas-verduras', 'Productos frescos y orgánicos', 1),
('Productos Lácteos', 'productos-lacteos', 'Leche, queso y derivados orgánicos', 2),
('Cereales y Granos', 'cereales-granos', 'Arroz, quinoa y otros granos', 3),
('Bebidas', 'bebidas', 'Jugos naturales y tés', 4),
('Snacks Saludables', 'snacks-saludables', 'Frutos secos y snacks orgánicos', 5),
('Cuidado Personal', 'cuidado-personal', 'Productos naturales para el cuidado', 6),
('Limpieza', 'limpieza', 'Productos de limpieza ecológicos', 7)
ON CONFLICT (name) DO NOTHING;

-- Insert sample products
INSERT INTO products (name, slug, description, price, stock_quantity, category_id, is_active, tags) VALUES
('Manzanas Orgánicas', 'manzanas-organicas', 'Manzanas frescas cultivadas sin pesticidas', 8500.00, 100,
 (SELECT id FROM categories WHERE slug = 'frutas-verduras'), true, ARRAY['orgánico', 'fruta', 'fresco']),
('Leche de Almendras', 'leche-almendras', 'Leche vegetal de almendras 100% natural', 12000.00, 50,
 (SELECT id FROM categories WHERE slug = 'bebidas'), true, ARRAY['vegano', 'almendras', 'natural']),
('Quinoa Orgánica', 'quinoa-organica', 'Quinoa premium cultivada orgánicamente', 25000.00, 75,
 (SELECT id FROM categories WHERE slug = 'cereales-granos'), true, ARRAY['orgánico', 'superfood', 'proteína'])
ON CONFLICT (slug) DO NOTHING;

-- Insert eco attributes for products
INSERT INTO product_eco_attributes (product_id, is_organic, carbon_footprint, origin_country, sustainability_score)
SELECT p.id, true, 0.5, 'Colombia', 95 FROM products p WHERE p.slug = 'manzanas-organicas'
UNION ALL
SELECT p.id, true, 1.2, 'Colombia', 88 FROM products p WHERE p.slug = 'leche-almendras'
UNION ALL
SELECT p.id, true, 0.8, 'Perú', 92 FROM products p WHERE p.slug = 'quinoa-organica';

-- =========================================
-- USEFUL VIEWS
-- =========================================

-- Product catalog view with category and eco info
CREATE OR REPLACE VIEW product_catalog AS
SELECT
    p.*,
    c.name as category_name,
    c.slug as category_slug,
    ea.is_organic,
    ea.carbon_footprint,
    ea.sustainability_score,
    ea.origin_country,
    COALESCE(pi.image_url, '/images/default-product.jpg') as primary_image_url
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
LEFT JOIN product_eco_attributes ea ON p.id = ea.product_id
LEFT JOIN product_images pi ON p.id = pi.product_id AND pi.is_primary = true
WHERE p.is_active = true;

-- Order summary view
CREATE OR REPLACE VIEW order_summary AS
SELECT
    o.*,
    u.username,
    u.email,
    u.first_name,
    u.last_name,
    sa.full_name as shipping_name,
    sa.city as shipping_city,
    sa.state as shipping_state,
    COUNT(oi.id) as item_count
FROM orders o
JOIN users u ON o.user_id = u.id
LEFT JOIN user_addresses sa ON o.shipping_address_id = sa.id
LEFT JOIN order_items oi ON o.id = oi.order_id
GROUP BY o.id, u.id, sa.id;

-- =========================================
-- FUNCTIONS
-- =========================================

-- Function to generate order number
CREATE OR REPLACE FUNCTION generate_order_number()
RETURNS TEXT AS $$
DECLARE
    order_num TEXT;
BEGIN
    SELECT 'ECO-' || TO_CHAR(CURRENT_TIMESTAMP, 'YYYYMMDD') || '-' || LPAD(NEXTVAL('orders_id_seq')::TEXT, 6, '0')
    INTO order_num;
    RETURN order_num;
END;
$$ LANGUAGE plpgsql;

-- Function to update product stock
CREATE OR REPLACE FUNCTION update_product_stock(p_product_id BIGINT, p_quantity INTEGER)
RETURNS VOID AS $$
BEGIN
    UPDATE products
    SET stock_quantity = stock_quantity + p_quantity,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = p_product_id;
END;
$$ LANGUAGE plpgsql;

-- Function to calculate order total
CREATE OR REPLACE FUNCTION calculate_order_total(p_order_id BIGINT)
RETURNS DECIMAL(10,2) AS $$
DECLARE
    total DECIMAL(10,2);
BEGIN
    SELECT COALESCE(SUM(total_price), 0) INTO total
    FROM order_items
    WHERE order_id = p_order_id;

    -- Update order total
    UPDATE orders
    SET total_amount = total,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = p_order_id;

    RETURN total;
END;
$$ LANGUAGE plpgsql;

-- =========================================
-- CONSTRAINTS AND VALIDATIONS
-- =========================================

-- Ensure order numbers are unique and follow format
ALTER TABLE orders ADD CONSTRAINT chk_order_number_format
CHECK (order_number ~ '^ECO-\d{8}-\d{6}$');

-- Ensure ratings are between 1 and 5
ALTER TABLE product_reviews ADD CONSTRAINT chk_rating_range
CHECK (rating >= 1 AND rating <= 5);

-- Ensure sustainability score is between 0 and 100
ALTER TABLE product_eco_attributes ADD CONSTRAINT chk_sustainability_score
CHECK (sustainability_score >= 0 AND sustainability_score <= 100);

-- Ensure discount value is positive
ALTER TABLE coupons ADD CONSTRAINT chk_discount_value_positive
CHECK (discount_value > 0);

-- =========================================
-- SAMPLE DATA FOR TESTING
-- =========================================

-- Insert sample user
INSERT INTO users (username, email, password, first_name, last_name, phone, is_active) VALUES
('juanperez', 'juan@example.com', '$2a$10$8K3W2QJc8QJc8QJc8QJc8e8QJc8QJc8QJc8QJc8QJc8QJc8QJc8QJc8QJc8', 'Juan', 'Pérez', '+573001234567', true)
ON CONFLICT (username) DO NOTHING;

-- Assign user role
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'juanperez' AND r.name = 'ROLE_USER'
ON CONFLICT DO NOTHING;

-- Insert sample address
INSERT INTO user_addresses (user_id, address_type, is_default, full_name, street_address, city, state, postal_code, country, phone)
SELECT u.id, 'shipping', true, 'Juan Pérez', 'Calle 123 #45-67', 'Bogotá', 'Cundinamarca', '110111', 'Colombia', '+573001234567'
FROM users u WHERE u.username = 'juanperez'
ON CONFLICT DO NOTHING;

-- =========================================
-- PERFORMANCE OPTIMIZATIONS
-- =========================================

-- Create partial indexes for active records
CREATE INDEX idx_products_active_price ON products(price) WHERE is_active = true;
CREATE INDEX idx_products_active_category ON products(category_id) WHERE is_active = true;

-- Create indexes for common queries
CREATE INDEX idx_orders_user_status ON orders(user_id, status);
CREATE INDEX idx_orders_date_range ON orders(ordered_at);
CREATE INDEX idx_product_reviews_product_rating ON product_reviews(product_id, rating);

-- =========================================
-- COMMENTS FOR DOCUMENTATION
-- =========================================

COMMENT ON TABLE users IS 'User accounts with authentication information';
COMMENT ON TABLE products IS 'Product catalog with pricing and inventory';
COMMENT ON TABLE orders IS 'Customer orders with payment and shipping information';
COMMENT ON TABLE categories IS 'Product categories in hierarchical structure';
COMMENT ON TABLE product_reviews IS 'Customer reviews and ratings for products';
COMMENT ON TABLE carts IS 'Shopping carts for users and guests';
COMMENT ON TABLE coupons IS 'Discount codes and promotional offers';
COMMENT ON TABLE notifications IS 'User notifications and alerts';
COMMENT ON TABLE inventory_movements IS 'Stock movement tracking for audit purposes';

-- =========================================
-- END OF SCHEMA
-- =========================================