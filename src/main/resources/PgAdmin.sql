CREATE TABLE Zipcode (
                         zipcode INTEGER PRIMARY KEY,
                         city VARCHAR
);

CREATE TABLE Users (
                       user_id SERIAL PRIMARY KEY,
                       name VARCHAR,
                       address VARCHAR,
                       zipcode INTEGER REFERENCES Zipcode(zipcode),
                       email VARCHAR,
                       password VARCHAR,
                       phone_number VARCHAR,
                       role VARCHAR
);

CREATE TABLE order_status (
                              status_id INTEGER PRIMARY KEY,
                              status VARCHAR
);

CREATE TABLE Orders (
                        order_id SERIAL PRIMARY KEY,
                        user_id INTEGER REFERENCES Users(user_id),
                        carport_width INTEGER,
                        carport_length INTEGER,
                        roof VARCHAR,
                        user_text VARCHAR,
                        status_id INTEGER REFERENCES order_status(status_id),
                        sales_price DOUBLE PRECISION,
                        created_at TIMESTAMP
);

CREATE TABLE material (
                          material_id SERIAL PRIMARY KEY,
                          name VARCHAR,
                          unit VARCHAR,
                          price DOUBLE PRECISION
);

CREATE TABLE material_variant (
                                  material_variant_id SERIAL PRIMARY KEY,
                                  material_id INTEGER REFERENCES material(material_id),
                                  length INTEGER
);

CREATE TABLE component (
                           component_id SERIAL PRIMARY KEY,
                           order_id INTEGER REFERENCES Orders(order_id),
                           material_variant_id INTEGER REFERENCES material_variant(material_variant_id),
                           quantity INTEGER,
                           use_description VARCHAR
);

CREATE TABLE svg (
                     svg_id SERIAL PRIMARY KEY,
                     order_id INTEGER REFERENCES Orders(order_id),
                     svg_data TEXT,
                     created_at TIMESTAMP
);

-- Indsæt data i order_status
INSERT INTO order_status (status_id, status) VALUES
                                                 (1, 'Forespørgsel'), (2, 'Betalt'), (3, 'Leveret');

-- Indsæt postnumre
INSERT INTO zipcode (zipcode, city) VALUES
                                        (8000, 'Aarhus'),
                                        (9000, 'Aalborg'),
                                        (2100, 'København Ø');

-- Indsæt testbrugere
INSERT INTO users (name, address, zipcode, email, password, phone_number, role) VALUES
                                                                                    ('Mikkel Dam Binau', 'Carportvej 12', 8000, 'mikkel@fug.dk', 'hashedpassword123', '12345678', 'customer'),
                                                                                    ('Admin User', 'Adminvej 1', 9000, 'admin@fug.dk', 'adminpass456', '87654321', 'admin');

-- Materialer og priser (forkortet til 10, kan udvides)
INSERT INTO material (material_id, name, unit, price) VALUES
                                                          (1, '25x200 mm. trykimp. Bræt', 'stk', 25.0),
                                                          (2, '25x125mm. trykimp. Bræt', 'stk', 20.0),
                                                          (3, '38x73 mm. Lægte ubh.', 'stk', 18.0),
                                                          (4, '45x95 mm. Reglar ub.', 'stk', 15.0),
                                                          (5, '97x97 mm. trykimp. Stolpe', 'stk', 75.0),
                                                          (6, '19x100 mm. trykimp. Bræt', 'stk', 16.0),
                                                          (7, 'Plastmo Ecolite blåtonet', 'stk', 50.0),
                                                          (8, 'Plastmo bundskruer 200 stk.', 'pakke', 25.0),
                                                          (9, 'Hulbånd 1x20 mm. 10 mtr.', 'rulle', 18.0),
                                                          (10, 'Universal 190 mm højre', 'stk', 10.0);

-- Længde-varianter af materialer
INSERT INTO material_variant (material_variant_id, material_id, length) VALUES
                                                                            (1, 1, 360),
                                                                            (2, 1, 420),
                                                                            (3, 2, 360),
                                                                            (4, 3, 420),
                                                                            (5, 4, 270),
                                                                            (6, 5, 300),
                                                                            (7, 6, 600),
                                                                            (8, 7, 0),
                                                                            (9, 8, 0),
                                                                            (10, 9, 0),
                                                                            (11, 10, 0);

-- Testordre
INSERT INTO orders (user_id, carport_width, carport_length, roof, user_text, status_id, sales_price, created_at) VALUES
    (1, 300, 600, 'plastmo', 'Ønsker overdækning i høj kvalitet', 1, 0.0, CURRENT_TIMESTAMP);
