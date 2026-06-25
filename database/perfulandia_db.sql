CREATE DATABASE IF NOT EXISTS perfulandia_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE perfulandia_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS envios;
DROP TABLE IF EXISTS boletas;
DROP TABLE IF EXISTS pagos;
DROP TABLE IF EXISTS detalle_pedido;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS inventario;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS proveedores;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS usuarios;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    rol VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_creacion DATETIME NOT NULL
);

CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    telefono VARCHAR(30),
    activo BOOLEAN NOT NULL,
    version BIGINT
);

CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    estado VARCHAR(20) NOT NULL
);

CREATE TABLE proveedores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    rut VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(30),
    email VARCHAR(120) NOT NULL UNIQUE,
    direccion VARCHAR(200),
    estado VARCHAR(20) NOT NULL,
    version BIGINT
);

CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    precio DECIMAL(12,2) NOT NULL,
    marca VARCHAR(80),
    estado VARCHAR(20) NOT NULL,
    categoria_id BIGINT NOT NULL,
    proveedor_id BIGINT
);

CREATE TABLE inventario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL UNIQUE,
    stock_actual INT NOT NULL,
    stock_minimo INT,
    ubicacion VARCHAR(100),
    estado VARCHAR(20) NOT NULL,
    fecha_actualizacion DATETIME NOT NULL
);

CREATE TABLE pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    fecha DATETIME NOT NULL,
    estado VARCHAR(30) NOT NULL
);

CREATE TABLE detalle_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_detalle_pedido_pedido
        FOREIGN KEY (pedido_id)
        REFERENCES pedidos(id)
        ON DELETE CASCADE
);

CREATE TABLE pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    monto DECIMAL(12,2) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    fecha DATETIME NOT NULL
);

CREATE TABLE boletas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    pago_id BIGINT NOT NULL UNIQUE,
    total DECIMAL(12,2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_emision DATETIME NOT NULL
);

CREATE TABLE envios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    cliente_id BIGINT NOT NULL,
    direccion_entrega VARCHAR(200) NOT NULL,
    comuna VARCHAR(80) NOT NULL,
    ciudad VARCHAR(80) NOT NULL,
    ubicacion_actual VARCHAR(150),
    estado VARCHAR(30) NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    ultima_actualizacion DATETIME NOT NULL,
    version BIGINT
);

INSERT INTO usuarios (username, password, email, rol, estado, fecha_creacion) VALUES
('admin_sistema', '$2a$10$7QJ8kXhR0cJQ4x4i7NLz7O4O7udWqN1xK5drXyukmP0m6IKL4fl0u', 'admin@perfulandia.cl', 'ADMIN', 'ACTIVO', '2026-06-01 09:00:00'),
('vendedor_01', '$2a$10$7QJ8kXhR0cJQ4x4i7NLz7O4O7udWqN1xK5drXyukmP0m6IKL4fl0u', 'vendedor01@perfulandia.cl', 'VENDEDOR', 'ACTIVO', '2026-06-01 09:15:00'),
('bodega_01', '$2a$10$7QJ8kXhR0cJQ4x4i7NLz7O4O7udWqN1xK5drXyukmP0m6IKL4fl0u', 'bodega01@perfulandia.cl', 'BODEGA', 'ACTIVO', '2026-06-01 09:30:00'),
('cliente_demo', '$2a$10$7QJ8kXhR0cJQ4x4i7NLz7O4O7udWqN1xK5drXyukmP0m6IKL4fl0u', 'cliente.demo@perfulandia.cl', 'CLIENTE', 'ACTIVO', '2026-06-01 10:00:00'),
('usuario_inactivo', '$2a$10$7QJ8kXhR0cJQ4x4i7NLz7O4O7udWqN1xK5drXyukmP0m6IKL4fl0u', 'inactivo@perfulandia.cl', 'CLIENTE', 'INACTIVO', '2026-06-01 10:30:00');

INSERT INTO clientes (nombre, apellidos, email, telefono, activo, version) VALUES
('Camila', 'Rojas Fuentes', 'camila.rojas@example.com', '+56911112222', true, 0),
('Matías', 'Pérez Soto', 'matias.perez@example.com', '+56922223333', true, 0),
('Valentina', 'Morales Díaz', 'valentina.morales@example.com', '+56933334444', true, 0),
('Ignacio', 'Vega Herrera', 'ignacio.vega@example.com', '+56944445555', true, 0),
('Fernanda', 'Castillo Rivas', 'fernanda.castillo@example.com', '+56955556666', true, 0),
('Tomás', 'Silva Araya', 'tomas.silva@example.com', '+56966667777', true, 0),
('Antonia', 'Muñoz Lagos', 'antonia.munoz@example.com', '+56977778888', true, 0),
('Sebastián', 'Torres Medina', 'sebastian.torres@example.com', '+56988889999', true, 0),
('Javiera', 'Contreras Peña', 'javiera.contreras@example.com', '+56999990000', false, 0),
('Benjamín', 'Navarro Cortés', 'benjamin.navarro@example.com', '+56910101010', true, 0);

INSERT INTO categorias (nombre, descripcion, estado) VALUES
('Perfumes Mujer', 'Fragancias femeninas de distintas marcas y aromas', 'ACTIVA'),
('Perfumes Hombre', 'Fragancias masculinas para uso diario y ocasiones especiales', 'ACTIVA'),
('Perfumes Unisex', 'Fragancias diseñadas para todo tipo de público', 'ACTIVA'),
('Maquillaje', 'Productos cosméticos para rostro, ojos y labios', 'ACTIVA'),
('Cuidado Facial', 'Cremas, tónicos, sérums y productos para el rostro', 'ACTIVA'),
('Cuidado Corporal', 'Cremas, lociones y productos para el cuidado del cuerpo', 'ACTIVA'),
('Cuidado Capilar', 'Shampoo, acondicionadores y tratamientos para el cabello', 'ACTIVA'),
('Desodorantes', 'Desodorantes corporales en spray, roll-on y barra', 'ACTIVA'),
('Sets de Regalo', 'Pack de productos preparados para regalo', 'ACTIVA'),
('Fragancias Premium', 'Perfumes de alta gama y edición especial', 'ACTIVA'),
('Fragancias Económicas', 'Perfumes y colonias de bajo costo', 'ACTIVA'),
('Colonias', 'Colonias frescas para uso diario', 'ACTIVA'),
('Accesorios de Belleza', 'Brochas, espejos, cosmetiqueros y accesorios', 'ACTIVA'),
('Cremas Hidratantes', 'Productos hidratantes para piel seca y normal', 'ACTIVA'),
('Protección Solar', 'Bloqueadores solares y productos post solar', 'ACTIVA'),
('Aromaterapia', 'Aceites, difusores y productos aromáticos', 'ACTIVA'),
('Productos Naturales', 'Productos elaborados con ingredientes naturales', 'ACTIVA'),
('Ofertas', 'Categoría para productos en promoción', 'ACTIVA'),
('Liquidación', 'Productos con descuento por liquidación de stock', 'ACTIVA'),
('Categoría Descontinuada', 'Categoría antigua actualmente no disponible', 'INACTIVA');

INSERT INTO proveedores (nombre, rut, telefono, email, direccion, estado, version) VALUES
('Distribuidora Fragancias Premium SpA', '76.123.456-7', '+56223456789', 'contacto@fraganciaspremium.cl', 'Av. Providencia 1200, Santiago', 'ACTIVO', 0),
('Importadora Belleza Global Ltda', '77.234.567-8', '+56224567890', 'ventas@bellezaglobal.cl', 'Av. Las Condes 4500, Santiago', 'ACTIVO', 0),
('Perfumes Internacionales Chile', '78.345.678-9', '+56225678901', 'contacto@perfumesint.cl', 'Camino El Alba 3000, Las Condes', 'ACTIVO', 0),
('Cosmética Natural del Sur', '79.456.789-0', '+56412345678', 'info@cosmeticanatural.cl', 'Av. Alemania 850, Temuco', 'ACTIVO', 0),
('Proveedor Liquidaciones Express', '80.567.890-1', '+56226789012', 'liquidaciones@express.cl', 'San Diego 980, Santiago', 'INACTIVO', 0);

INSERT INTO productos (nombre, descripcion, precio, marca, estado, categoria_id, proveedor_id) VALUES
('Dior Sauvage Eau de Parfum', 'Fragancia masculina intensa y elegante', 89990.00, 'Dior', 'ACTIVO', 2, 1),
('Chanel No. 5', 'Fragancia femenina clásica y sofisticada', 119990.00, 'Chanel', 'ACTIVO', 1, 2),
('Versace Eros', 'Perfume masculino fresco y juvenil', 74990.00, 'Versace', 'ACTIVO', 2, 3),
('Carolina Herrera Good Girl', 'Fragancia femenina moderna y elegante', 99990.00, 'Carolina Herrera', 'ACTIVO', 1, 4),
('Calvin Klein One', 'Fragancia unisex fresca para uso diario', 45990.00, 'Calvin Klein', 'ACTIVO', 3, 5),
('Lancôme La Vie Est Belle', 'Perfume femenino dulce y floral', 109990.00, 'Lancôme', 'ACTIVO', 1, 2),
('Paco Rabanne Invictus', 'Fragancia masculina deportiva y fresca', 69990.00, 'Paco Rabanne', 'ACTIVO', 2, 3),
('Victoria Secret Body Mist', 'Body mist floral para uso diario', 19990.00, 'Victoria Secret', 'ACTIVO', 6, 1),
('Crema Hidratante Nivea Soft', 'Crema hidratante para rostro y cuerpo', 6990.00, 'Nivea', 'ACTIVO', 14, 2),
('Protector Solar La Roche-Posay SPF50', 'Protector solar facial de alta protección', 21990.00, 'La Roche-Posay', 'ACTIVO', 15, 3),
('Set Regalo Perfume Mujer', 'Pack de perfume y crema corporal', 39990.00, 'Perfulandia', 'ACTIVO', 9, 1),
('Perfume Liquidación Floral', 'Fragancia floral en liquidación de temporada', 9990.00, 'Perfulandia', 'INACTIVO', 19, 5);

INSERT INTO inventario (producto_id, stock_actual, stock_minimo, ubicacion, estado, fecha_actualizacion) VALUES
(1, 25, 5, 'Bodega Central - Estante A1', 'DISPONIBLE', '2026-06-01 09:00:00'),
(2, 12, 3, 'Bodega Central - Estante A2', 'DISPONIBLE', '2026-06-01 09:10:00'),
(3, 8, 4, 'Bodega Central - Estante B1', 'DISPONIBLE', '2026-06-01 09:20:00'),
(4, 3, 5, 'Bodega Central - Estante B2', 'BAJO_STOCK', '2026-06-01 09:30:00'),
(5, 40, 10, 'Bodega Central - Estante C1', 'DISPONIBLE', '2026-06-01 09:40:00'),
(6, 0, 5, 'Bodega Central - Estante C2', 'SIN_STOCK', '2026-06-01 09:50:00'),
(7, 18, 6, 'Bodega Central - Estante D1', 'DISPONIBLE', '2026-06-01 10:00:00'),
(8, 7, 5, 'Bodega Central - Estante D2', 'DISPONIBLE', '2026-06-01 10:10:00'),
(9, 2, 5, 'Bodega Central - Estante E1', 'BAJO_STOCK', '2026-06-01 10:20:00'),
(10, 30, 8, 'Bodega Central - Estante E2', 'DISPONIBLE', '2026-06-01 10:30:00'),
(11, 15, 5, 'Bodega Central - Estante F1', 'DISPONIBLE', '2026-06-01 10:40:00'),
(12, 5, 5, 'Bodega Central - Estante F2', 'DISPONIBLE', '2026-06-01 10:50:00');

INSERT INTO pedidos (cliente_id, fecha, estado) VALUES
(1, '2026-06-01 10:15:00', 'CONFIRMADO'),
(2, '2026-06-01 11:30:00', 'CONFIRMADO'),
(3, '2026-06-02 09:20:00', 'PENDIENTE'),
(1, '2026-06-02 15:45:00', 'CANCELADO'),
(4, '2026-06-03 12:10:00', 'CONFIRMADO'),
(5, '2026-06-04 09:00:00', 'CONFIRMADO');

INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad, precio_unitario) VALUES
(1, 1, 1, 89990.00),
(1, 5, 2, 45990.00),
(2, 2, 1, 119990.00),
(3, 4, 1, 99990.00),
(4, 8, 3, 19990.00),
(5, 10, 1, 21990.00),
(5, 11, 1, 39990.00),
(6, 5, 1, 45990.00);

INSERT INTO pagos (pedido_id, monto, estado, fecha) VALUES
(1, 181970.00, 'APROBADO', '2026-06-01 10:25:00'),
(2, 119990.00, 'APROBADO', '2026-06-01 11:40:00'),
(3, 99990.00, 'PENDIENTE', '2026-06-02 09:30:00'),
(4, 59970.00, 'RECHAZADO', '2026-06-02 16:00:00'),
(5, 61980.00, 'APROBADO', '2026-06-03 12:25:00'),
(6, 45990.00, 'APROBADO', '2026-06-04 09:15:00');

INSERT INTO boletas (pedido_id, cliente_id, pago_id, total, estado, fecha_emision) VALUES
(1, 1, 1, 181970.00, 'EMITIDA', '2026-06-01 10:30:00'),
(2, 2, 2, 119990.00, 'EMITIDA', '2026-06-01 11:45:00'),
(5, 4, 5, 61980.00, 'EMITIDA', '2026-06-03 12:30:00');

INSERT INTO envios (
    pedido_id,
    cliente_id,
    direccion_entrega,
    comuna,
    ciudad,
    ubicacion_actual,
    estado,
    fecha_creacion,
    ultima_actualizacion,
    version
) VALUES
(1, 1, 'Av. Concha y Toro 1234', 'Puente Alto', 'Santiago', 'Bodega Central', 'PENDIENTE', '2026-06-01 10:35:00', '2026-06-01 10:35:00', 0),
(2, 2, 'Los Aromos 456', 'La Florida', 'Santiago', 'Centro de distribución', 'EN_PREPARACION', '2026-06-01 11:50:00', '2026-06-01 12:20:00', 0),
(3, 3, 'Av. Providencia 789', 'Providencia', 'Santiago', 'En ruta hacia comuna destino', 'EN_CAMINO', '2026-06-02 09:40:00', '2026-06-02 13:10:00', 0),
(4, 1, 'Santa Rosa 1111', 'San Miguel', 'Santiago', 'Entregado al cliente', 'ENTREGADO', '2026-06-02 16:10:00', '2026-06-03 10:00:00', 0),
(5, 4, 'Camino El Alba 2020', 'Las Condes', 'Santiago', 'Bodega Central', 'PENDIENTE', '2026-06-03 12:30:00', '2026-06-03 12:30:00', 0);