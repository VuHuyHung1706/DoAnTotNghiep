-- MySQL dump 10.13  Distrib 8.0.43, for macos15 (arm64)
--
-- Host: 127.0.0.1    Database: userdb
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
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gender` tinyint(1) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  CONSTRAINT `customers_ibfk_1` FOREIGN KEY (`username`) REFERENCES `accounts` (`username`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'Nguyễn','Văn A','user1@gmail.com','0901234567',1,'1990-01-15','123 Nguyễn Huệ, Quận 1, TP.HCM','user1','2025-11-14 14:31:46'),(2,'Trần','Thị B','user2@gmail.com','0902345678',0,'1992-03-20','456 Lê Lợi, Quận 3, TP.HCM','user2','2025-11-14 14:31:46'),(3,'Lê','Văn C','user3@gmail.com','0903456789',1,'1988-07-10','789 Hai Bà Trưng, Quận 1, TP.HCM','user3','2025-11-14 14:31:46'),(4,'Phạm','Thị D','user4@gmail.com','0904567890',0,'1995-11-25','321 Trần Hưng Đạo, Quận 5, TP.HCM','user4','2025-11-14 14:31:46'),(5,'Hoàng','Văn E','user5@gmail.com','0905678901',1,'1991-05-30','654 Võ Văn Tần, Quận 3, TP.HCM','user5','2025-11-14 14:31:46'),(6,'Vũ','Thị F','user6@gmail.com','0906789012',0,'1993-09-12','987 Cách Mạng Tháng 8, Quận 10, TP.HCM','user6','2025-11-14 14:31:46'),(7,'Đặng','Văn G','user7@gmail.com','0907890123',1,'1989-02-18','147 Lý Thường Kiệt, Quận Tân Bình, TP.HCM','user7','2025-11-14 14:31:46'),(8,'Bùi','Thị H','user8@gmail.com','0908901234',0,'1994-06-22','258 Nguyễn Trãi, Quận 1, TP.HCM','user8','2025-11-14 14:31:46'),(9,'Đỗ','Văn I','user9@gmail.com','0909012345',1,'1987-12-05','369 Điện Biên Phủ, Quận 3, TP.HCM','user9','2025-11-14 14:31:46'),(10,'Ngô','Thị K','user10@gmail.com','0910123456',0,'1996-04-14','741 Lê Văn Sỹ, Quận Phú Nhuận, TP.HCM','user10','2025-11-14 14:31:46'),(11,'Dương','Văn L','user11@gmail.com','0911234567',1,'1990-08-28','852 Phan Xích Long, Quận Phú Nhuận, TP.HCM','user11','2025-11-14 14:31:46'),(12,'Lý','Thị M','user12@gmail.com','0912345678',0,'1992-10-17','963 Nam Kỳ Khởi Nghĩa, Quận 1, TP.HCM','user12','2025-11-14 14:31:46'),(13,'Võ','Văn N','user13@gmail.com','0913456789',1,'1991-03-09','159 Võ Thị Sáu, Quận 3, TP.HCM','user13','2025-11-14 14:31:46'),(14,'Trương','Thị O','user14@gmail.com','0914567890',0,'1994-07-23','357 Cộng Hòa, Quận Tân Bình, TP.HCM','user14','2025-11-14 14:31:46'),(15,'Phan','Văn P','user15@gmail.com','0915678901',1,'1989-11-11','753 Hoàng Văn Thụ, Quận Tân Bình, TP.HCM','user15','2025-11-14 14:31:46');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
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
