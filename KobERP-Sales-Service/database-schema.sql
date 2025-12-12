-- Create stock_items table
CREATE TABLE IF NOT EXISTS stock_items (
    id BIGSERIAL PRIMARY KEY,
    quantity INTEGER NOT NULL,
    item_name VARCHAR(255),
    unit_price DECIMAL(10, 2),
    description VARCHAR(500)
);

-- Create sales table
CREATE TABLE IF NOT EXISTS sales (
    id BIGSERIAL PRIMARY KEY,
    stock_id BIGINT NOT NULL,
    sale_price DECIMAL(10, 2) NOT NULL,
    profit DECIMAL(10, 2),
    last_sale_date DATE,
    sale_quantity VARCHAR(255),
    document_uploaded BOOLEAN NOT NULL DEFAULT FALSE,
    customer_name VARCHAR(255),
    customer_phone VARCHAR(255),
    uploaded_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_stock_item FOREIGN KEY (stock_id) REFERENCES stock_items(id)
);

-- Create indexes for better performance
CREATE INDEX idx_sales_stock_id ON sales(stock_id);
CREATE INDEX idx_sales_uploaded_at ON sales(uploaded_at);
CREATE INDEX idx_sales_last_sale_date ON sales(last_sale_date);

-- Insert sample stock items
INSERT INTO stock_items (quantity, item_name, unit_price, description) VALUES
(100, 'Product A', 50.00, 'Sample product A'),
(200, 'Product B', 75.00, 'Sample product B'),
(150, 'Product C', 100.00, 'Sample product C');
