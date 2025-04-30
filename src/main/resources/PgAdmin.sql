-- Slet gamle tabeller hvis de findes
DROP TABLE IF EXISTS bill_of_materials, material_variant, material, sug, "order", users, zipcode, role, status CASCADE;

-- Roller
CREATE TABLE role (
                      role_id SERIAL PRIMARY KEY,
                      role VARCHAR NOT NULL
);

-- Statusser
CREATE TABLE status (
                        status_id SERIAL PRIMARY KEY,
                        status VARCHAR NOT NULL
);

-- Postnumre
CREATE TABLE zipcode (
                         zipcode INTEGER PRIMARY KEY,
                         city VARCHAR NOT NULL
);

-- Brugere (inkl. Admin)
CREATE TABLE users (
                       user_id SERIAL PRIMARY KEY,
                       name VARCHAR NOT NULL,
                       address VARCHAR,
                       zipcode INTEGER REFERENCES zipcode(zipcode),
                       email VARCHAR NOT NULL UNIQUE,
                       password VARCHAR NOT NULL,
                       phone_number VARCHAR,
                       role INTEGER REFERENCES role(role_id)
);

-- Ordrer fra kunder
CREATE TABLE "order" (
                         order_id SERIAL PRIMARY KEY,
                         name VARCHAR NOT NULL,
                         email VARCHAR NOT NULL,
                         address VARCHAR,
                         phone_number VARCHAR,
                         suggestion TEXT,
                         carport_width INTEGER,
                         carport_length INTEGER,
                         roof VARCHAR,
                         user_text TEXT,
                         salesman_text TEXT,
                         status INTEGER REFERENCES status(status_id),
                         sales_price DOUBLE PRECISION,
                         created_at TIMESTAMP DEFAULT now()
);

-- Forslag på ordrer (sug)
CREATE TABLE sug (
                     sug_id SERIAL PRIMARY KEY,
                     order_id INTEGER REFERENCES "order"(order_id) ON DELETE CASCADE,
                     sug_data TEXT,
                     created_at TIMESTAMP DEFAULT now()
);

-- Materialer (produkter)
CREATE TABLE material (
                          material_id SERIAL PRIMARY KEY,
                          description VARCHAR NOT NULL,
                          unit VARCHAR NOT NULL,
                          price DOUBLE PRECISION
);

-- Materialevarianter (længder, mængder)
CREATE TABLE material_variant (
                                  material_variant_id SERIAL PRIMARY KEY,
                                  material_id INTEGER REFERENCES material(material_id) ON DELETE CASCADE,
                                  length INTEGER,
                                  quantity INTEGER,
                                  use_description VARCHAR
);

-- Sammenkobling af ordrer og materialer
CREATE TABLE bill_of_materials (
                                   bom_id SERIAL PRIMARY KEY,
                                   order_id INTEGER REFERENCES "order"(order_id) ON DELETE CASCADE,
                                   material_variant_id INTEGER REFERENCES material_variant(material_variant_id) ON DELETE CASCADE
);

--------------------------------------------------
-- Eksempel data
--------------------------------------------------

-- Roller
INSERT INTO role (role) VALUES ('admin'), ('customer');

-- Statusser
INSERT INTO status (status) VALUES ('Afventer'), ('Tilbud sendt'), ('Accepteret');

-- Postnumre
INSERT INTO zipcode (zipcode, city) VALUES (2800, 'Kongens Lyngby'), (2100, 'København Ø');

-- Brugere
INSERT INTO users (name, address, zipcode, email, password, phone_number, role) VALUES
    ('Admin', 'Fog Hovedkontor', 2800, 'admin@fog.dk', 'admin123', '12345678', 1);

-- Materialer
INSERT INTO material (description, unit, price) VALUES
                                                    ('25x200 mm. trykimp. Brædt', 'Stk', 180.60),
                                                    ('25x125 mm. trykimp. Brædt', 'Stk', 124.95),
                                                    ('38x73 mm. Lægte ubh.', 'Stk', 58.59),
                                                    ('45x95 mm. Reglar ub.', 'Stk', 51.30),
                                                    ('45x195 mm. spærtræ ubh.', 'Stk', 189.95),
                                                    ('97x97 mm. trykimp. Stolpe', 'Stk', 112.50),
                                                    ('19x100 mm. trykimp. Brædt', 'Stk', 84.95),
                                                    ('Plastmo Ecolite blåtonet', 'Stk', 119.00),
                                                    ('plastmo bundskruer 200 stk.', 'Pakke', 139.95),
                                                    ('hulbånd 1x20 mm. 10 mtr.', 'Rulle', 69.95),
                                                    ('universal 190 mm højre', 'Stk', 29.95),
                                                    ('universal 190 mm venstre', 'Stk', 29.95),
                                                    ('4,5 x 60 mm. skruer 200 stk.', 'Pakke', 160.00),
                                                    ('4,0 x 50 mm. beslagsskruer 250 stk.', 'Pakke', 250.00),
                                                    ('bræddebolt 10 x 120 mm.', 'Stk', 4.00),
                                                    ('firkantskiver 40x40x11mm', 'Stk', 2.00),
                                                    ('4,5 x 70 mm. Skruer 400 stk.', 'Pakke', 480.00),
                                                    ('4,5 x 50 mm. Skruer 300 stk.', 'Pakke', 300.00),
                                                    ('stalddørsgreb 50x75', 'Sæt', 199.00),
                                                    ('t hængsel 390 mm', 'Stk', 39.95),
                                                    ('vinkelbeslag 35', 'Stk', 5.00);

-- Materialevarianter (eksempler)
INSERT INTO material_variant (material_id, length, quantity, use_description) VALUES
                                                                                  (1, 360, 4, 'understernbrædder for og bagende'),
                                                                                  (1, 540, 4, 'understernbrædder siderne'),
                                                                                  (2, 360, 2, 'oversternbrædder forenden'),
                                                                                  (2, 540, 4, 'oversternbrædder siderne'),
                                                                                  (5, 600, 15, 'spær monteres på rem'),
                                                                                  (6, 300, 11, 'stolper nedgraves i jord'),
                                                                                  (8, 600, 6, 'tagplader monteres på spær'),
                                                                                  (8, 360, 6, 'tagplader monteres på spær');

--------------------------------------------------
-- Klar til brug!
--------------------------------------------------
