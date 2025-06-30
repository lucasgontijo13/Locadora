-- Inserir as Roles na nova tabela "tb_role"
INSERT INTO tb_role (id, authority) VALUES (1, 'ROLE_CLIENT');
INSERT INTO tb_role (id, authority) VALUES (2, 'ROLE_ADMIN');

-- Inserir Usuários na nova tabela "tb_user"
INSERT INTO tb_user (name, email, phone, username, password, created_at, updated_at) VALUES ('Maria Cliente', 'lucasgontijo111@gmail.com', '37999887766', 'lucas', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', NOW(), NOW());
INSERT INTO tb_user (name, email, phone, username, password, created_at, updated_at) VALUES ('Joao Admin', 'joao@email.com', '37988776655', 'joao', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', NOW(), NOW());
INSERT INTO tb_user (name, email, phone, username, password, created_at, updated_at) VALUES ('Ana Cliente', 'ana@email.com', '37999554433', 'ana', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', NOW(), NOW());

-- Associar Usuários com suas Roles (a tabela de junção não mudou de nome)
INSERT INTO user_role (user_id, role_id) VALUES (1, 2);
INSERT INTO user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO user_role (user_id, role_id) VALUES (3, 1);

-- Inserir Veículos na nova tabela "tb_vehicle"
INSERT INTO tb_vehicle (brand, model, license_plate, daily_rate, image_url, created_at, updated_at) VALUES ('Fiat', 'Mobi', 'ABC1D23', 95.50, 'https://example.com/mobi.jpg', NOW(), NOW());
INSERT INTO tb_vehicle (brand, model, license_plate, daily_rate, image_url, created_at, updated_at) VALUES ('Chevrolet', 'Onix', 'DEF4E56', 105.00, 'https://example.com/onix.jpg', NOW(), NOW());
INSERT INTO tb_vehicle (brand, model, license_plate, daily_rate, image_url, created_at, updated_at) VALUES ('Hyundai', 'HB20', 'GHI7F89', 110.75, 'https://example.com/hb20.jpg', NOW(), NOW());
INSERT INTO tb_vehicle (brand, model, license_plate, daily_rate, image_url, created_at, updated_at) VALUES ('Renault', 'Kwid', 'JKL0G12', 92.00, 'https://example.com/kwid.jpg', NOW(), NOW());

-- Inserir Aluguéis na nova tabela "tb_rental"
INSERT INTO tb_rental (user_id, vehicle_id, rental_date, return_date) VALUES (1, 2, '2024-08-10T14:00:00Z', '2024-08-15T12:00:00Z');
INSERT INTO tb_rental (user_id, vehicle_id, rental_date, return_date) VALUES (3, 2, '2024-09-01T10:00:00Z', '2024-09-04T10:00:00Z');
INSERT INTO tb_rental (user_id, vehicle_id, rental_date, return_date) VALUES (1, 3, '2024-10-20T11:00:00Z', '2024-10-25T11:00:00Z');