-- phpMyAdmin SQL Dump
-- version 4.2.12deb2+deb8u1
-- http://www.phpmyadmin.net
--
-- Servidor: localhost
-- Tiempo de generación: 08-12-2015 a las 18:59:48
-- Versión del servidor: 5.5.46-0+deb8u1
-- Versión de PHP: 5.6.14-0+deb8u1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de datos: `ejercito`
--

DELIMITER $$
--
-- Procedimientos
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `guardarCuartel`(p_nombre_cuartel varchar(50), p_localidad varchar(100), p_latitud Double, p_longitud Double, p_actividad tinyint(1))
BEGIN
	INSERT INTO cuartel (nombre_cuartel, localidad, latitud, longitud, actividad) VALUES (p_nombre_cuartel, p_localidad, p_latitud, p_longitud, p_actividad);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `guardarUnidad`(IN `p_nombre_unidad` VARCHAR(50), IN `p_tipo` VARCHAR(50), IN `p_no_tropas` INT(10), IN `p_fecha_creacion` DATE, IN `p_nombre_cuartel` VARCHAR(50))
BEGIN
declare id_cuartel int;
start transaction;
    set id_cuartel = (select id from cuartel where nombre_cuartel = p_nombre_cuartel);
    INSERT INTO unidad (nombre_unidad, tipo, no_tropas, fecha_creacion, id_cuartel) VALUES (p_nombre_unidad, p_tipo, p_no_tropas, p_fecha_creacion, id_cuartel);
commit;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cuartel`
--

CREATE TABLE IF NOT EXISTS `cuartel` (
`id` int(10) unsigned NOT NULL,
  `nombre_cuartel` varchar(50) NOT NULL,
  `latitud` double DEFAULT '0',
  `longitud` double DEFAULT '0',
  `actividad` tinyint(1) DEFAULT '1',
  `localidad` varchar(50) DEFAULT NULL,
  `uso` int(255) NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `soldado`
--

CREATE TABLE IF NOT EXISTS `soldado` (
`id` int(10) unsigned NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `rango` enum('Soldado','Soldado de Primera','Cabo','Cabo Primero','Cabo Mayor','Sargento','Sargento Primero','Brigada','Subteniente','Suboficial Mayor','Alférez','Teniente','Capitán','Comandante','Teniente Coronel','Coronel','General de Brigada','General de Division','Teniente General','General de Ejército') DEFAULT 'Soldado',
  `lugar_nacimiento` varchar(50) DEFAULT NULL,
  `id_unidad` int(10) unsigned DEFAULT NULL,
  `uso` int(255) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `unidad`
--

CREATE TABLE IF NOT EXISTS `unidad` (
`id` int(10) unsigned NOT NULL,
  `nombre_unidad` varchar(50) NOT NULL,
  `tipo` enum('Compañia','Batallon','Regimiento','Brigada','Division') NOT NULL,
  `no_tropas` int(10) unsigned DEFAULT '0',
  `fecha_creacion` date DEFAULT NULL,
  `id_cuartel` int(10) unsigned DEFAULT NULL,
  `uso` int(255) NOT NULL DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE IF NOT EXISTS `usuarios` (
  `usuario` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `rol` enum('administrador','tecnico','usuario') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`usuario`, `password`, `rol`) VALUES
('Dani', '7110eda4d09e062aa5e4a390b0a572ac0d2c0220', 'tecnico'),
('Guillermo', '7110eda4d09e062aa5e4a390b0a572ac0d2c0220', 'usuario'),
('Raider', '7110eda4d09e062aa5e4a390b0a572ac0d2c0220', 'administrador');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `cuartel`
--
ALTER TABLE `cuartel`
 ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `soldado`
--
ALTER TABLE `soldado`
 ADD PRIMARY KEY (`id`), ADD KEY `id_unidad` (`id_unidad`);

--
-- Indices de la tabla `unidad`
--
ALTER TABLE `unidad`
 ADD PRIMARY KEY (`id`), ADD KEY `id_cuartel` (`id_cuartel`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
 ADD UNIQUE KEY `usuario` (`usuario`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `cuartel`
--
ALTER TABLE `cuartel`
MODIFY `id` int(10) unsigned NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT de la tabla `soldado`
--
ALTER TABLE `soldado`
MODIFY `id` int(10) unsigned NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT de la tabla `unidad`
--
ALTER TABLE `unidad`
MODIFY `id` int(10) unsigned NOT NULL AUTO_INCREMENT;
--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `soldado`
--
ALTER TABLE `soldado`
ADD CONSTRAINT `soldado_ibfk_1` FOREIGN KEY (`id_unidad`) REFERENCES `unidad` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `unidad`
--
ALTER TABLE `unidad`
ADD CONSTRAINT `unidad_ibfk_1` FOREIGN KEY (`id_cuartel`) REFERENCES `cuartel` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
