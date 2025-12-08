-- MySQL dump 10.13  Distrib 8.0.43, for macos15 (arm64)
--
-- Host: 127.0.0.1    Database: moviedb
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
-- Table structure for table `movies`
--

DROP TABLE IF EXISTS `movies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movies` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duration` int NOT NULL,
  `language` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `poster` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `trailer` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `release_date` date DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movies`
--

LOCK TABLES `movies` WRITE;
/*!40000 ALTER TABLE `movies` DISABLE KEYS */;
INSERT INTO `movies` VALUES (1,'Mission: Impossible 8','Ethan Hunt và đội IMF đối mặt với nhiệm vụ nguy hiểm nhất từ trước đến nay',150,'English','http://localhost:8081/uploads/posters/61e2331f-6a31-4d7c-90ae-6caa3401e044.jpg','https://youtube.com/watch?v=xxx','2025-05-23','2025-11-18 08:17:44'),(2,'Avengers: Secret Wars','Các siêu anh hùng Marvel đoàn kết chống lại mối đe dọa vũ trụ',180,'English','http://localhost:8081/uploads/posters/b3d566c9-3a81-4a94-8dda-b50df6e1d4bb.jpeg','https://youtube.com/watch?v=xxx','2025-05-01','2025-11-18 08:17:44'),(3,'Dune: Part Three','Paul Atreides tiếp tục cuộc hành trình trở thành hoàng đế vũ trụ',165,'English','http://localhost:8081/uploads/posters/d9416117-6b15-403b-826d-3c95579fb9e1.png','https://youtube.com/watch?v=xxx','2025-03-15','2025-11-18 08:17:44'),(4,'The Batman 2','Batman đối mặt với kẻ thù mới ở Gotham City',155,'English','http://localhost:8081/uploads/posters/12510c46-94c4-4d6d-a929-115b46db1082.jpg','https://youtube.com/watch?v=xxx','2025-06-10','2025-11-18 08:17:44'),(5,'Joker: Folie à Deux','Câu chuyện về Joker và Harley Quinn',140,'English','http://localhost:8081/uploads/posters/31750239-9580-4c67-81a4-4ab2a435e715.webp','https://youtube.com/watch?v=xxx','2024-10-04','2025-11-18 08:17:44'),(6,'Spider-Man: Beyond','Spider-Man khám phá đa vũ trụ mới',145,'English','http://localhost:8081/uploads/posters/9f6f53cf-1b7c-4f64-993f-029736174365.png','https://youtube.com/watch?v=xxx','2025-07-18','2025-11-18 08:17:44'),(7,'Deadpool 3','Wade Wilson gia nhập vũ trụ điện ảnh Marvel',130,'English','http://localhost:8081/uploads/posters/99675cbf-0bec-46d4-999d-d09725b1e670.jpg','https://youtube.com/watch?v=xxx','2024-11-08','2025-11-18 08:17:44'),(8,'Avatar 3','Jake Sully khám phá vùng đất mới trên Pandora',190,'English','http://localhost:8081/uploads/posters/d3d8acdf-5066-4566-bd8e-b62db3f4a9e8.jpg','https://youtube.com/watch?v=xxx','2025-12-19','2025-11-18 08:17:44'),(9,'Fantastic Four','Nhóm bốn siêu anh hùng tuyệt vời bảo vệ trái đất',135,'English','http://localhost:8081/uploads/posters/858b6433-2d10-4cbd-879e-da870ae03e54.jpeg','https://youtube.com/watch?v=xxx','2025-02-14','2025-11-18 08:17:44'),(10,'Blade','Thợ săn ma cà rồng trở lại',125,'English','http://localhost:8081/uploads/posters/ed112187-e612-46a6-9dc9-0e2eb095a168.jpg','https://youtube.com/watch?v=xxx','2025-09-06','2025-11-18 08:17:44'),(11,'The Marvels 2','Captain Marvel và đồng đội đối đầu với thế lực tối',140,'English','http://localhost:8081/uploads/posters/1f13a3e7-6355-416f-894a-dffdb2f3d660.jpg','https://youtube.com/watch?v=xxx','2025-08-15','2025-11-18 08:17:44'),(12,'Guardians of the Galaxy 4','Các vệ sĩ dải ngân hà trong nhiệm vụ mới',145,'English','http://localhost:8081/uploads/posters/71c49027-bc65-484d-92ee-154127f95a9d.webp','https://youtube.com/watch?v=xxx','2025-05-09','2025-11-18 08:17:44'),(13,'Black Widow 2','Natasha Romanoff trong một nhiệm vụ bí mật',135,'English','http://localhost:8081/uploads/posters/97d2f829-3057-4729-9608-ad52328adf6e.jpg','https://youtube.com/watch?v=xxx','2025-11-21','2025-11-18 08:17:44'),(14,'Thor: Love and Thunder 2','Thor tiếp tục hành trình với các vị thần',150,'English','http://localhost:8081/uploads/posters/d3137283-b2a3-46ef-a69c-d3402eafc799.avif','https://youtube.com/watch?v=xxx','2025-07-26','2025-11-18 08:17:44'),(15,'Doctor Strange 3','Phù thủy tối thượng khám phá chiều kích mới',148,'English','http://localhost:8081/uploads/posters/4d6e5b17-bb3b-4661-b092-40b89f4881b9.png','https://youtube.com/watch?v=xxx','2025-03-28','2025-11-18 08:17:44'),(16,'Shang-Chi 2','Shang-Chi đối mặt với quá khứ gia đình',132,'English','http://localhost:8081/uploads/posters/41a04f23-4ee8-4b5b-a037-3f8d8257000e.jpg','https://youtube.com/watch?v=xxx','2025-06-20','2025-11-18 08:17:44'),(17,'Eternals 2','Các Eternals tái hợp để cứu Trái Đất',157,'English','http://localhost:8081/uploads/posters/e02268bc-bf1b-4dd6-84d3-a5fcf54b17ef.jpg','https://youtube.com/watch?v=xxx','2025-10-03','2025-11-18 08:17:44'),(18,'Ant-Man 4','Scott Lang trong cuộc phiêu lưu lượng tử mới',125,'English','http://localhost:8081/uploads/posters/4ea91ac6-002d-4cb3-8e58-0ee883fcb989.jpeg','https://youtube.com/watch?v=xxx','2025-02-28','2025-11-18 08:17:44'),(19,'Captain America: New World Order','Sam Wilson trong vai trò Captain America mới',142,'English','http://localhost:8081/uploads/posters/26bec75e-2c44-46f7-a74f-be6c52e4e428.jpg','https://youtube.com/watch?v=xxx','2025-04-11','2025-11-18 08:17:44'),(20,'Thunderbolts','Nhóm phản anh hùng trong nhiệm vụ cứu rỗi',138,'English','http://localhost:8081/uploads/posters/dc9a08ab-ca7a-41be-819c-a35e1dc2835f.jpg','https://youtube.com/watch?v=xxx','2025-12-25','2025-11-18 08:17:44'),(21,'The Incredibles 3','Gia đình siêu nhân trở lại với thử thách mới',120,'English','http://localhost:8081/uploads/posters/13a7649e-0633-49d6-b676-c2f94f868e5c.jpg','https://youtube.com/watch?v=xxx','2025-06-13','2025-11-18 08:17:44'),(22,'Toy Story 5','Woody và Buzz Lightyear trong cuộc phiêu lưu mới',110,'English','http://localhost:8081/uploads/posters/403f2341-56ea-42ff-9da3-5bbe743246a9.webp','https://youtube.com/watch?v=xxx','2025-06-27','2025-11-18 08:17:44'),(23,'Inside Out 3','Riley khám phá những cảm xúc mới ở tuổi trưởng thành',105,'English','http://localhost:8081/uploads/posters/c2a4a6a9-b06e-482c-9be9-20796b437449.jpg','https://youtube.com/watch?v=xxx','2025-09-19','2025-11-18 08:17:44'),(24,'Frozen 3','Elsa và Anna trong hành trình phép thuật mới',115,'English','http://localhost:8081/uploads/posters/907ab812-1f60-45c5-be48-96dbd20cd646.jpeg','https://youtube.com/watch?v=xxx','2025-11-26','2025-11-18 08:17:44'),(25,'Moana 2','Moana khám phá đại dương rộng lớn',118,'English','http://localhost:8081/uploads/posters/efdc5c8f-b8a4-41e7-8ec3-b09c6ede9714.jpeg','https://youtube.com/watch?v=xxx','2024-11-27','2025-11-18 08:17:44');
/*!40000 ALTER TABLE `movies` ENABLE KEYS */;
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
