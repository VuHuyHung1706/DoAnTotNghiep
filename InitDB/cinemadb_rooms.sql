-- MySQL dump 10.13  Distrib 8.0.43, for macos15 (arm64)
--
-- Host: 127.0.0.1    Database: cinemadb
-- ------------------------------------------------------
-- Server version	8.4.7

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_seats` int NOT NULL,
  `cinema_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `cinema_id` (`cinema_id`),
  CONSTRAINT `rooms_ibfk_1` FOREIGN KEY (`cinema_id`) REFERENCES `cinemas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES (1,'Phòng 1 - CGV Vincom',56,1,'2025-11-14 14:32:28'),(2,'Phòng 2 - CGV Vincom',56,1,'2025-11-14 14:32:28'),(3,'Phòng 3 - CGV Vincom',56,1,'2025-11-14 14:32:28'),(4,'Phòng 4 - CGV Vincom',56,1,'2025-11-14 14:32:28'),(5,'Phòng 1 - Lotte Diamond',56,2,'2025-11-14 14:32:28'),(6,'Phòng 2 - Lotte Diamond',56,2,'2025-11-14 14:32:28'),(7,'Phòng 3 - Lotte Diamond',56,2,'2025-11-14 14:32:28'),(8,'Phòng 4 - Lotte Diamond',56,2,'2025-11-14 14:32:28'),(9,'Phòng 1 - Galaxy Nguyễn Du',56,3,'2025-11-14 14:32:28'),(10,'Phòng 2 - Galaxy Nguyễn Du',56,3,'2025-11-14 14:32:28'),(11,'Phòng 3 - Galaxy Nguyễn Du',56,3,'2025-11-14 14:32:28'),(12,'Phòng 4 - Galaxy Nguyễn Du',56,3,'2025-11-14 14:32:28'),(13,'Phòng 1 - BHD Star 3/2',56,4,'2025-11-14 14:32:28'),(14,'Phòng 2 - BHD Star 3/2',56,4,'2025-11-14 14:32:28'),(15,'Phòng 3 - BHD Star 3/2',56,4,'2025-11-14 14:32:28'),(16,'Phòng 4 - BHD Star 3/2',56,4,'2025-11-14 14:32:28');
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-05 12:09:57
