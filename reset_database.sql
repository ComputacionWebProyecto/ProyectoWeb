-- Script para limpiar y reiniciar contadores de Gateway, Edge y Activity
-- Ejecutar en H2 Database o tu base de datos correspondiente

-- 1. Eliminar todos los registros de las tablas dependientes primero
DELETE FROM edge;
DELETE FROM activity;
DELETE FROM gateway;

-- 2. Reiniciar los contadores de auto-incremento (H2 Database)
ALTER TABLE edge ALTER COLUMN id RESTART WITH 1;
ALTER TABLE activity ALTER COLUMN id RESTART WITH 1;
ALTER TABLE gateway ALTER COLUMN id RESTART WITH 1;

-- 3. Verificar que las tablas están vacías
SELECT COUNT(*) AS total_edges FROM edge;
SELECT COUNT(*) AS total_activities FROM activity;
SELECT COUNT(*) AS total_gateways FROM gateway;

-- NOTA: Si usas otra base de datos, ajusta la sintaxis:
--
-- PostgreSQL:
-- ALTER SEQUENCE edge_id_seq RESTART WITH 1;
-- ALTER SEQUENCE activity_id_seq RESTART WITH 1;
-- ALTER SEQUENCE gateway_id_seq RESTART WITH 1;
--
-- MySQL:
-- ALTER TABLE edge AUTO_INCREMENT = 1;
-- ALTER TABLE activity AUTO_INCREMENT = 1;
-- ALTER TABLE gateway AUTO_INCREMENT = 1;
--
-- SQL Server:
-- DBCC CHECKIDENT ('edge', RESEED, 0);
-- DBCC CHECKIDENT ('activity', RESEED, 0);
-- DBCC CHECKIDENT ('gateway', RESEED, 0);

