-- Opret Zipcode tabel
CREATE TABLE Zipcode (
                         zipcode INTEGER PRIMARY KEY,
                         city VARCHAR
);

-- Opret Admin tabel
CREATE TABLE admin (
                       admin_id SERIAL PRIMARY KEY,
                       username VARCHAR NOT NULL,
                       password VARCHAR NOT NULL
);

-- Opret Customer tabel
CREATE TABLE customer (
                          customer_id SERIAL PRIMARY KEY,
                          customer_name VARCHAR NOT NULL,
                          customer_email TEXT NOT NULL,
                          customer_address VARCHAR NOT NULL,
                          customer_zipcode INTEGER REFERENCES Zipcode(zipcode),
                          customer_phone VARCHAR NOT NULL
);

-- Opret order_status tabel
CREATE TABLE order_status (
                              status_id INTEGER PRIMARY KEY,
                              status VARCHAR NOT NULL
);

-- Opret Orders tabel
CREATE TABLE orders (
                        order_id SERIAL PRIMARY KEY,
                        customer_id INTEGER REFERENCES customer(customer_id),
                        carport_width INTEGER NOT NULL,
                        carport_length INTEGER NOT NULL,
                        roof VARCHAR NOT NULL,
                        customer_text VARCHAR,
                        admin_text VARCHAR,
                        status_id INTEGER REFERENCES order_status(status_id),
                        sales_price DOUBLE PRECISION DEFAULT 0.0,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Opret Material tabel
CREATE TABLE material (
                          material_id SERIAL PRIMARY KEY,
                          name VARCHAR NOT NULL,
                          unit VARCHAR NOT NULL,
                          price DOUBLE PRECISION NOT NULL
);

-- Opret Material Variant tabel
CREATE TABLE material_variant (
                                  material_variant_id SERIAL PRIMARY KEY,
                                  material_id INTEGER REFERENCES material(material_id),
                                  length INTEGER NOT NULL
);

-- Opret Component tabel
CREATE TABLE component (
                           component_id SERIAL PRIMARY KEY,
                           order_id INTEGER REFERENCES orders(order_id),
                           material_variant_id INTEGER REFERENCES material_variant(material_variant_id),
                           quantity INTEGER NOT NULL,
                           use_description VARCHAR
);

-- Opret SVG tabel
CREATE TABLE svg (
                     svg_id SERIAL PRIMARY KEY,
                     order_id INTEGER REFERENCES orders(order_id),
                     svg_data TEXT NOT NULL,
                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indsæt postnumre
INSERT INTO zipcode (zipcode, city) VALUES
                                        (8000, 'Aarhus'),
                                        (9000, 'Aalborg'),
                                        (2100, 'København Ø'),
                                        (5000, 'Odense'),
                                        (6000, 'Kolding'),
                                        (7000, 'Fredericia');

-- Indsæt admins
INSERT INTO admin (username, password) VALUES
                                           ('admin1', 'adminpass123'),
                                           ('admin2', 'adminpass456');

-- Indsæt customers
INSERT INTO customer (customer_name, customer_email, customer_address, customer_zipcode, customer_phone) VALUES
                                                                                                             ('Mikkel Dam Binau', 'mikkel@fug.dk', 'Carportvej 12', 8000, '12345678'),
                                                                                                             ('Test Kunde', 'test@kunde.dk', 'Testvej 23', 9000, '87654321');

-- Indsæt data i order_status
INSERT INTO order_status (status_id, status) VALUES
                                                 (1, 'unprocessed'),
                                                 (2, 'pending'),
                                                 (3, 'processed');

-- Indsæt materialer
INSERT INTO material (name, unit, price) VALUES
                                             ('25x200 mm. trykimp. Bræt', 'stk', 25.0),
                                             ('25x125 mm. trykimp. Bræt', 'stk', 20.0),
                                             ('38x73 mm. Lægte ubh.', 'stk', 18.0),
                                             ('45x95 mm. Reglar ub.', 'stk', 15.0),
                                             ('97x97 mm. trykimp. Stolpe', 'stk', 75.0),
                                             ('19x100 mm. trykimp. Bræt', 'stk', 16.0),
                                             ('Plastmo Ecolite blåtonet', 'stk', 50.0),
                                             ('Plastmo bundskruer 200 stk.', 'pakke', 25.0),
                                             ('Hulbånd 1x20 mm. 10 mtr.', 'rulle', 18.0),
                                             ('Universal 190 mm højre', 'stk', 10.0);

-- Indsæt material_variants
INSERT INTO material_variant (material_id, length) VALUES
                                                       (1, 360), (1, 420), (2, 360), (3, 420),
                                                       (4, 270), (5, 300), (6, 600), (7, 0),
                                                       (8, 0), (9, 0), (10, 0);

-- Indsæt testordre
INSERT INTO orders (
    customer_id, carport_width, carport_length, roof, customer_text, admin_text, status_id, sales_price
) VALUES
      (1, 300, 600, 'plastmo', 'Ønsker overdækning i høj kvalitet', NULL, 1, 0.0),
      (2, 350, 700, 'træ', 'Standard carport med trætag', NULL, 2, 5000.0);

-- Indsæt komponenter
INSERT INTO component (order_id, material_variant_id, quantity, use_description) VALUES
                                                                                     (1, 1, 5, 'Bundplader'),
                                                                                     (1, 2, 10, 'Vægelementer'),
                                                                                     (2, 3, 8, 'Lægter'),
                                                                                     (2, 4, 12, 'Reglar');

-- Indsæt SVG data
INSERT INTO svg (order_id, svg_data) VALUES
                                         (1, 'SVG data for ordre 1'),
                                         (2, 'SVG data for ordre 2');
