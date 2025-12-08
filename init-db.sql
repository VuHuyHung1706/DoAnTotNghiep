-- ============================================================================
-- CINEMA BOOKING SYSTEM - Database Initialization Script
-- ============================================================================
-- This script creates 5 databases with complete schema and test data
-- Databases: userdb, moviedb, cinemaadb, bookingdb, recommendationdb
-- ============================================================================

-- ============================================================================
-- 1. USERDB - User Management Database
-- ============================================================================
CREATE DATABASE IF NOT EXISTS userdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE userdb;

-- Table: accounts
DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts (
  username VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  password VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  status TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO accounts VALUES 
('admin','$2a$10$5vLrQpgrH9YQqpNBaXAs6uDOl9dIF1slFps1od26gnctf23fdQwW6',1,'2025-11-14 14:33:53'),
('staff','$2a$10$utvVRN9okPEEIHmdzygbA.dDWCf8E3pz9DEHBJ5nau2RYfdpfI6pO',1,'2025-11-14 14:33:53'),
('user1','$2a$10$XDh/E5pbNEtookzj7osTnOnZBbzwtpRQkVLhT3xxygQ5QdfFfLVbq',1,'2025-11-14 14:31:46'),
('user2','$2a$10$vPUnT3MRkG.F7uguM0lOOOb/rCn5BpaYBlkKelIDOf3iiXnoPJYlq',1,'2025-11-14 14:31:46'),
('user3','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2025-11-14 14:31:46'),
('user4','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2025-11-14 14:31:46'),
('user5','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2025-11-14 14:31:46'),
('user6','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2025-11-14 14:31:46'),
('user7','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2025-11-14 14:31:46'),
('user8','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2025-11-14 14:31:46'),
('user9','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2025-11-14 14:31:46'),
('user10','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2025-11-14 14:31:46'),
('user11','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2025-11-14 14:31:46'),
('user12','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2025-11-14 14:31:46'),
('user13','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2025-11-14 14:31:46'),
('user14','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2025-11-14 14:31:46'),
('user15','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',1,'2025-11-14 14:31:46');

-- Table: customers
DROP TABLE IF EXISTS customers;
CREATE TABLE customers (
  id INT NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  last_name VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  email VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  phone VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  gender TINYINT(1) DEFAULT NULL,
  date_of_birth DATE DEFAULT NULL,
  address VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  username VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY username (username),
  CONSTRAINT customers_ibfk_1 FOREIGN KEY (username) REFERENCES accounts (username) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO customers VALUES 
(1,'Nguyễn','Văn A','user1@gmail.com','0901234567',1,'1990-01-15','123 Nguyễn Huệ, Quận 1, TP.HCM','user1','2025-11-14 14:31:46'),
(2,'Trần','Thị B','user2@gmail.com','0902345678',0,'1992-03-20','456 Lê Lợi, Quận 3, TP.HCM','user2','2025-11-14 14:31:46'),
(3,'Lê','Văn C','user3@gmail.com','0903456789',1,'1988-07-10','789 Hai Bà Trưng, Quận 1, TP.HCM','user3','2025-11-14 14:31:46'),
(4,'Phạm','Thị D','user4@gmail.com','0904567890',0,'1995-11-25','321 Trần Hưng Đạo, Quận 5, TP.HCM','user4','2025-11-14 14:31:46'),
(5,'Hoàng','Văn E','user5@gmail.com','0905678901',1,'1991-05-30','654 Võ Văn Tần, Quận 3, TP.HCM','user5','2025-11-14 14:31:46'),
(6,'Vũ','Thị F','user6@gmail.com','0906789012',0,'1993-09-12','987 Cách Mạng Tháng 8, Quận 10, TP.HCM','user6','2025-11-14 14:31:46'),
(7,'Đặng','Văn G','user7@gmail.com','0907890123',1,'1989-02-18','147 Lý Thường Kiệt, Quận Tân Bình, TP.HCM','user7','2025-11-14 14:31:46'),
(8,'Bùi','Thị H','user8@gmail.com','0908901234',0,'1994-06-22','258 Nguyễn Trãi, Quận 1, TP.HCM','user8','2025-11-14 14:31:46'),
(9,'Đỗ','Văn I','user9@gmail.com','0909012345',1,'1987-12-05','369 Điện Biên Phủ, Quận 3, TP.HCM','user9','2025-11-14 14:31:46'),
(10,'Ngô','Thị K','user10@gmail.com','0910123456',0,'1996-04-14','741 Lê Văn Sỹ, Quận Phú Nhuận, TP.HCM','user10','2025-11-14 14:31:46'),
(11,'Dương','Văn L','user11@gmail.com','0911234567',1,'1990-08-28','852 Phan Xích Long, Quận Phú Nhuận, TP.HCM','user11','2025-11-14 14:31:46'),
(12,'Lý','Thị M','user12@gmail.com','0912345678',0,'1992-10-17','963 Nam Kỳ Khởi Nghĩa, Quận 1, TP.HCM','user12','2025-11-14 14:31:46'),
(13,'Võ','Văn N','user13@gmail.com','0913456789',1,'1991-03-09','159 Võ Thị Sáu, Quận 3, TP.HCM','user13','2025-11-14 14:31:46'),
(14,'Trương','Thị O','user14@gmail.com','0914567890',0,'1994-07-23','357 Cộng Hòa, Quận Tân Bình, TP.HCM','user14','2025-11-14 14:31:46'),
(15,'Phan','Văn P','user15@gmail.com','0915678901',1,'1989-11-11','753 Hoàng Văn Thụ, Quận Tân Bình, TP.HCM','user15','2025-11-14 14:31:46');

-- Table: managers
DROP TABLE IF EXISTS managers;
CREATE TABLE managers (
  id INT NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  last_name VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  email VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  phone VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  username VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  position ENUM('MANAGER','STAFF') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY username (username),
  CONSTRAINT managers_ibfk_1 FOREIGN KEY (username) REFERENCES accounts (username) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO managers VALUES 
(1,'Admin','System',NULL,NULL,'admin','2025-11-14 14:33:53','MANAGER'),
(2,'Staff','Staff',NULL,NULL,'staff','2025-11-14 14:33:53','STAFF');

-- Table: invalidated_tokens
DROP TABLE IF EXISTS invalidated_tokens;
CREATE TABLE invalidated_tokens (
  id VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  expiry_time TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 2. MOVIEDB - Movie Management Database
-- ============================================================================
CREATE DATABASE IF NOT EXISTS moviedb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE moviedb;

-- Table: movies
DROP TABLE IF EXISTS movies;
CREATE TABLE movies (
  id INT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  description VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  duration INT NOT NULL,
  language VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  poster VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  trailer VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  release_date DATE DEFAULT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO movies VALUES 
(1,'Mission: Impossible 8','Ethan Hunt và đội IMF đối mặt với nhiệm vụ nguy hiểm nhất từ trước đến nay',150,'English','http://localhost:8081/uploads/posters/61e2331f-6a31-4d7c-90ae-6caa3401e044.jpg','https://youtube.com/watch?v=xxx','2025-05-23','2025-11-18 08:17:44'),
(2,'Avengers: Secret Wars','Các siêu anh hùng Marvel đoàn kết chống lại mối đe dọa vũ trụ',180,'English','http://localhost:8081/uploads/posters/b3d566c9-3a81-4a94-8dda-b50df6e1d4bb.jpeg','https://youtube.com/watch?v=xxx','2025-05-01','2025-11-18 08:17:44'),
(3,'Dune: Part Three','Paul Atreides tiếp tục cuộc hành trình trở thành hoàng đế vũ trụ',165,'English','http://localhost:8081/uploads/posters/d9416117-6b15-403b-826d-3c95579fb9e1.png','https://youtube.com/watch?v=xxx','2025-03-15','2025-11-18 08:17:44'),
(4,'The Batman 2','Batman đối mặt với kẻ thù mới ở Gotham City',155,'English','http://localhost:8081/uploads/posters/12510c46-94c4-4d6d-a929-115b46db1082.jpg','https://youtube.com/watch?v=xxx','2025-06-10','2025-11-18 08:17:44'),
(5,'Joker: Folie à Deux','Câu chuyện về Joker và Harley Quinn',140,'English','http://localhost:8081/uploads/posters/31750239-9580-4c67-81a4-4ab2a435e715.webp','https://youtube.com/watch?v=xxx','2024-10-04','2025-11-18 08:17:44'),
(6,'Spider-Man: Beyond','Spider-Man khám phá đa vũ trụ mới',145,'English','http://localhost:8081/uploads/posters/9f6f53cf-1b7c-4f64-993f-029736174365.png','https://youtube.com/watch?v=xxx','2025-07-18','2025-11-18 08:17:44'),
(7,'Deadpool 3','Wade Wilson gia nhập vũ trụ điện ảnh Marvel',130,'English','http://localhost:8081/uploads/posters/99675cbf-0bec-46d4-999d-d09725b1e670.jpg','https://youtube.com/watch?v=xxx','2024-11-08','2025-11-18 08:17:44'),
(8,'Avatar 3','Jake Sully khám phá vùng đất mới trên Pandora',190,'English','http://localhost:8081/uploads/posters/d3d8acdf-5066-4566-bd8e-b62db3f4a9e8.jpg','https://youtube.com/watch?v=xxx','2025-12-19','2025-11-18 08:17:44'),
(9,'Fantastic Four','Nhóm bốn siêu anh hùng tuyệt vời bảo vệ trái đất',135,'English','http://localhost:8081/uploads/posters/858b6433-2d10-4cbd-879e-da870ae03e54.jpeg','https://youtube.com/watch?v=xxx','2025-02-14','2025-11-18 08:17:44'),
(10,'Blade','Thợ săn ma cà rồng trở lại',125,'English','http://localhost:8081/uploads/posters/ed112187-e612-46a6-9dc9-0e2eb095a168.jpg','https://youtube.com/watch?v=xxx','2025-09-06','2025-11-18 08:17:44'),
(11,'The Marvels 2','Captain Marvel và đồng đội đối đầu với thế lực tối',140,'English','http://localhost:8081/uploads/posters/1f13a3e7-6355-416f-894a-dffdb2f3d660.jpg','https://youtube.com/watch?v=xxx','2025-08-15','2025-11-18 08:17:44'),
(12,'Guardians of the Galaxy 4','Các vệ sĩ dải ngân hà trong nhiệm vụ mới',145,'English','http://localhost:8081/uploads/posters/71c49027-bc65-484d-92ee-154127f95a9d.webp','https://youtube.com/watch?v=xxx','2025-05-09','2025-11-18 08:17:44'),
(13,'Black Widow 2','Natasha Romanoff trong một nhiệm vụ bí mật',135,'English','http://localhost:8081/uploads/posters/97d2f829-3057-4729-9608-ad52328adf6e.jpg','https://youtube.com/watch?v=xxx','2025-11-21','2025-11-18 08:17:44'),
(14,'Thor: Love and Thunder 2','Thor tiếp tục hành trình với các vị thần',150,'English','http://localhost:8081/uploads/posters/d3137283-b2a3-46ef-a69c-d3402eafc799.avif','https://youtube.com/watch?v=xxx','2025-07-26','2025-11-18 08:17:44'),
(15,'Doctor Strange 3','Phù thủy tối thượng khám phá chiều kích mới',148,'English','http://localhost:8081/uploads/posters/4d6e5b17-bb3b-4661-b092-40b89f4881b9.png','https://youtube.com/watch?v=xxx','2025-03-28','2025-11-18 08:17:44'),
(16,'Shang-Chi 2','Shang-Chi đối mặt với quá khứ gia đình',132,'English','http://localhost:8081/uploads/posters/41a04f23-4ee8-4b5b-a037-3f8d8257000e.jpg','https://youtube.com/watch?v=xxx','2025-06-20','2025-11-18 08:17:44'),
(17,'Eternals 2','Các Eternals tái hợp để cứu Trái Đất',157,'English','http://localhost:8081/uploads/posters/e02268bc-bf1b-4dd6-84d3-a5fcf54b17ef.jpg','https://youtube.com/watch?v=xxx','2025-10-03','2025-11-18 08:17:44'),
(18,'Ant-Man 4','Scott Lang trong cuộc phiêu lưu lượng tử mới',125,'English','http://localhost:8081/uploads/posters/4ea91ac6-002d-4cb3-8e58-0ee883fcb989.jpeg','https://youtube.com/watch?v=xxx','2025-02-28','2025-11-18 08:17:44'),
(19,'Captain America: New World Order','Sam Wilson trong vai trò Captain America mới',142,'English','http://localhost:8081/uploads/posters/26bec75e-2c44-46f7-a74f-be6c52e4e428.jpg','https://youtube.com/watch?v=xxx','2025-04-11','2025-11-18 08:17:44'),
(20,'Thunderbolts','Nhóm phản anh hùng trong nhiệm vụ cứu rỗi',138,'English','http://localhost:8081/uploads/posters/dc9a08ab-ca7a-41be-819c-a35e1dc2835f.jpg','https://youtube.com/watch?v=xxx','2025-12-25','2025-11-18 08:17:44'),
(21,'The Incredibles 3','Gia đình siêu nhân trở lại với thử thách mới',120,'English','http://localhost:8081/uploads/posters/13a7649e-0633-49d6-b676-c2f94f868e5c.jpg','https://youtube.com/watch?v=xxx','2025-06-13','2025-11-18 08:17:44'),
(22,'Toy Story 5','Woody và Buzz Lightyear trong cuộc phiêu lưu mới',110,'English','http://localhost:8081/uploads/posters/403f2341-56ea-42ff-9da3-5bbe743246a9.webp','https://youtube.com/watch?v=xxx','2025-06-27','2025-11-18 08:17:44'),
(23,'Inside Out 3','Riley khám phá những cảm xúc mới ở tuổi trưởng thành',105,'English','http://localhost:8081/uploads/posters/c2a4a6a9-b06e-482c-9be9-20796b437449.jpg','https://youtube.com/watch?v=xxx','2025-09-19','2025-11-18 08:17:44'),
(24,'Frozen 3','Elsa và Anna trong hành trình phép thuật mới',115,'English','http://localhost:8081/uploads/posters/907ab812-1f60-45c5-be48-96dbd20cd646.jpeg','https://youtube.com/watch?v=xxx','2025-11-26','2025-11-18 08:17:44'),
(25,'Moana 2','Moana khám phá đại dương rộng lớn',118,'English','http://localhost:8081/uploads/posters/efdc5c8f-b8a4-41e7-8ec3-b09c6ede9714.jpeg','https://youtube.com/watch?v=xxx','2024-11-27','2025-11-18 08:17:44');

-- Table: genres
DROP TABLE IF EXISTS genres;
CREATE TABLE genres (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY name (name)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO genres VALUES 
(1,'Hành động'),
(2,'Phiêu lưu'),
(3,'Hài'),
(4,'Chính kịch'),
(5,'Kinh dị'),
(6,'Khoa học viễn tưởng'),
(7,'Lãng mạn'),
(8,'Hoạt hình'),
(9,'Thriller'),
(10,'Tội phạm');

-- Table: actors
DROP TABLE IF EXISTS actors;
CREATE TABLE actors (
  id INT NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  last_name VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  gender TINYINT(1) DEFAULT NULL,
  date_of_birth DATE DEFAULT NULL,
  nationality VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO actors VALUES 
(1,'Tom','Cruise',1,'1962-07-03','USA'),
(2,'Scarlett','Johansson',0,'1984-11-22','USA'),
(3,'Robert','Downey Jr.',1,'1965-04-04','USA'),
(4,'Jennifer','Lawrence',0,'1990-08-15','USA'),
(5,'Chris','Hemsworth',1,'1983-08-11','Australia'),
(6,'Emma','Stone',0,'1988-11-06','USA'),
(7,'Leonardo','DiCaprio',1,'1974-11-11','USA'),
(8,'Angelina','Jolie',0,'1975-06-04','USA'),
(9,'Brad','Pitt',1,'1963-12-18','USA'),
(10,'Margot','Robbie',0,'1990-07-02','Australia'),
(11,'Dwayne','Johnson',1,'1972-05-02','USA'),
(12,'Gal','Gadot',0,'1985-04-30','Israel'),
(13,'Ryan','Reynolds',1,'1976-10-23','Canada'),
(14,'Zendaya','Coleman',0,'1996-09-01','USA'),
(15,'Timothée','Chalamet',1,'1995-12-27','USA'),
(16,'Florence','Pugh',0,'1996-01-03','UK'),
(17,'Michael B.','Jordan',1,'1987-02-09','USA'),
(18,'Brie','Larson',0,'1989-10-01','USA'),
(19,'Jason','Momoa',1,'1979-08-01','USA'),
(20,'Lupita','Nyong\'o',0,'1983-03-01','Kenya');

-- Table: movie_genres
DROP TABLE IF EXISTS movie_genres;
CREATE TABLE movie_genres (
  movie_id INT NOT NULL,
  genre_id INT NOT NULL,
  PRIMARY KEY (movie_id, genre_id),
  KEY genre_id (genre_id),
  CONSTRAINT movie_genres_ibfk_1 FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE CASCADE,
  CONSTRAINT movie_genres_ibfk_2 FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO movie_genres VALUES 
(1,1),(2,1),(4,1),(6,1),(7,1),(8,1),(9,1),(10,1),(11,1),(12,1),(13,1),(14,1),(15,1),(16,1),(17,1),(18,1),(19,1),(20,1),
(1,2),(2,2),(3,2),(6,2),(8,2),(9,2),(11,2),(14,2),(15,2),(16,2),(19,2),(21,2),(22,2),(24,2),(25,2),
(7,3),(12,3),(14,3),(18,3),(22,3),(23,3),(25,3),
(3,4),(5,4),(16,4),(17,4),(23,4),
(10,5),
(2,6),(3,6),(6,6),(7,6),(8,6),(9,6),(11,6),(12,6),(15,6),(17,6),(18,6),
(24,7),
(21,8),(22,8),(23,8),(24,8),(25,8),
(1,9),(4,9),(5,9),(10,9),(13,9),(19,9),(20,9),
(4,10),(5,10),(13,10),(20,10);

-- Table: movie_actors
DROP TABLE IF EXISTS movie_actors;
CREATE TABLE movie_actors (
  movie_id INT NOT NULL,
  actor_id INT NOT NULL,
  PRIMARY KEY (movie_id, actor_id),
  KEY actor_id (actor_id),
  CONSTRAINT movie_actors_ibfk_1 FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE CASCADE,
  CONSTRAINT movie_actors_ibfk_2 FOREIGN KEY (actor_id) REFERENCES actors (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO movie_actors VALUES 
(1,1),(22,1),(2,2),(8,2),(13,2),(19,2),(2,3),(15,3),(18,3),(6,4),(16,4),(22,4),(24,4),
(2,5),(7,5),(14,5),(6,6),(14,6),(18,6),(23,6),(5,7),(1,8),(16,8),(17,8),(4,9),(5,10),
(21,11),(25,11),(12,12),(21,12),(7,13),(12,13),(20,13),(3,14),(8,14),(11,14),(15,14),(23,14),(24,14),(25,14),
(3,15),(4,16),(9,16),(13,16),(20,16),(9,17),(10,17),(19,17),(2,18),(11,18),(3,19),(10,20),(17,20);

-- Table: showtimes (10 sample showtimes)
DROP TABLE IF EXISTS showtimes;
CREATE TABLE showtimes (
  id INT NOT NULL AUTO_INCREMENT,
  movie_id INT NOT NULL,
  room_id INT NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  ticket_price INT NOT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY movie_id (movie_id),
  KEY idx_start_time (start_time),
  KEY idx_room_movie (room_id, movie_id),
  CONSTRAINT showtimes_ibfk_1 FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO showtimes VALUES 
(1,1,1,'2025-12-08 09:00:00','2025-12-08 11:30:00',100000,'2025-12-08 07:00:00'),
(2,2,2,'2025-12-08 09:30:00','2025-12-08 12:30:00',120000,'2025-12-08 07:00:00'),
(3,3,3,'2025-12-08 10:00:00','2025-12-08 12:45:00',110000,'2025-12-08 07:00:00'),
(4,4,4,'2025-12-08 10:30:00','2025-12-08 13:05:00',105000,'2025-12-08 07:00:00'),
(5,5,5,'2025-12-08 12:00:00','2025-12-08 14:20:00',100000,'2025-12-08 07:00:00'),
(6,6,6,'2025-12-08 13:00:00','2025-12-08 15:25:00',110000,'2025-12-08 07:00:00'),
(7,7,7,'2025-12-08 13:30:00','2025-12-08 15:40:00',105000,'2025-12-08 07:00:00'),
(8,8,8,'2025-12-08 13:30:00','2025-12-08 16:40:00',130000,'2025-12-08 07:00:00'),
(9,9,9,'2025-12-08 15:00:00','2025-12-08 17:15:00',100000,'2025-12-08 07:00:00'),
(10,10,10,'2025-12-08 16:00:00','2025-12-08 18:05:00',100000,'2025-12-08 07:00:00');

-- Table: reviews
DROP TABLE IF EXISTS reviews;
CREATE TABLE reviews (
  id INT NOT NULL AUTO_INCREMENT,
  movie_id INT NOT NULL,
  username VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  rating INT NOT NULL,
  comment TEXT COLLATE utf8mb4_unicode_ci,
  created_at DATETIME NOT NULL,
  updated_at DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_movie_user (movie_id, username),
  CONSTRAINT reviews_ibfk_1 FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE CASCADE,
  CONSTRAINT reviews_chk_1 CHECK (rating >= 1 AND rating <= 5)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO reviews VALUES 
(1,1,'user1',5,'Một bộ phim hành động tuyệt vời! Tom Cruise luôn làm tôi kinh ngạc.','2025-11-10 14:30:00',NULL),
(2,1,'user3',5,'Mission Impossible 8 là phần hay nhất trong series!','2025-11-11 09:15:00',NULL);

-- ============================================================================
-- 3. CINEMAADB - Cinema Management Database
-- ============================================================================
CREATE DATABASE IF NOT EXISTS cinemaadb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cinemaadb;

-- Table: cinemas
DROP TABLE IF EXISTS cinemas;
CREATE TABLE cinemas (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  address VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  phone VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO cinemas VALUES 
(1,'CGV Vincom Center','72 Lê Thánh Tôn, Quận 1, TP.HCM','1900 6017','2025-11-14 14:32:28'),
(2,'Lotte Cinema Diamond Plaza','34 Lê Duẩn, Quận 1, TP.HCM','1900 5454','2025-11-14 14:32:28'),
(3,'Galaxy Cinema Nguyễn Du','116 Nguyễn Du, Quận 1, TP.HCM','1900 2224','2025-11-14 14:32:28'),
(4,'BHD Star Cineplex 3/2','190 Đường 3 Tháng 2, Quận 10, TP.HCM','1900 2099','2025-11-14 14:32:28');

-- Table: rooms
DROP TABLE IF EXISTS rooms;
CREATE TABLE rooms (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  total_seats INT NOT NULL,
  cinema_id INT NOT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY cinema_id (cinema_id),
  CONSTRAINT rooms_ibfk_1 FOREIGN KEY (cinema_id) REFERENCES cinemas (id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO rooms VALUES 
(1,'Phòng 1 - CGV Vincom',56,1,'2025-11-14 14:32:28'),
(2,'Phòng 2 - CGV Vincom',56,1,'2025-11-14 14:32:28'),
(3,'Phòng 3 - CGV Vincom',56,1,'2025-11-14 14:32:28'),
(4,'Phòng 4 - CGV Vincom',56,1,'2025-11-14 14:32:28'),
(5,'Phòng 1 - Lotte Diamond',56,2,'2025-11-14 14:32:28'),
(6,'Phòng 2 - Lotte Diamond',56,2,'2025-11-14 14:32:28'),
(7,'Phòng 3 - Lotte Diamond',56,2,'2025-11-14 14:32:28'),
(8,'Phòng 4 - Lotte Diamond',56,2,'2025-11-14 14:32:28'),
(9,'Phòng 1 - Galaxy Nguyễn Du',56,3,'2025-11-14 14:32:28'),
(10,'Phòng 2 - Galaxy Nguyễn Du',56,3,'2025-11-14 14:32:28'),
(11,'Phòng 3 - Galaxy Nguyễn Du',56,3,'2025-11-14 14:32:28'),
(12,'Phòng 4 - Galaxy Nguyễn Du',56,3,'2025-11-14 14:32:28'),
(13,'Phòng 1 - BHD Star 3/2',56,4,'2025-11-14 14:32:28'),
(14,'Phòng 2 - BHD Star 3/2',56,4,'2025-11-14 14:32:28'),
(15,'Phòng 3 - BHD Star 3/2',56,4,'2025-11-14 14:32:28'),
(16,'Phòng 4 - BHD Star 3/2',56,4,'2025-11-14 14:32:28');

-- Table: seats (56 seats per room = 16 rooms * 56 = 896 seats)
DROP TABLE IF EXISTS seats;
CREATE TABLE seats (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  seat_row INT NOT NULL,
  seat_column INT NOT NULL,
  room_id INT NOT NULL,
  created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_room_seat (room_id, seat_row, seat_column),
  CONSTRAINT seats_ibfk_1 FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=897 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert seats for all rooms (56 seats each: 8 rows x 7 columns)
INSERT INTO seats (name, seat_row, seat_column, room_id, created_at) 
SELECT CONCAT(CHAR(64+row_num), col_num), row_num, col_num, room_id, '2025-11-14 14:32:28'
FROM (
  SELECT 1 as row_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8
) rows
CROSS JOIN (
  SELECT 1 as col_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7
) cols
CROSS JOIN (
  SELECT 1 as room_id UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8
  UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16
) rooms;

-- ============================================================================
-- 4. BOOKINGDB - Booking Management Database
-- ============================================================================
CREATE DATABASE IF NOT EXISTS bookingdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookingdb;

-- Table: invoices
DROP TABLE IF EXISTS invoices;
CREATE TABLE invoices (
  invoice_id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  total_amount INT NOT NULL,
  payment_status ENUM('PENDING','PAID','CANCELLED','REFUNDED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  vnpay_transaction_id VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  paid_at DATETIME DEFAULT NULL,
  PRIMARY KEY (invoice_id),
  KEY idx_username (username),
  KEY idx_status (payment_status)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO invoices VALUES 
(1,'user1',200000,'PAID','VNP20250114001','2025-01-14 08:30:00','2025-01-14 08:35:00'),
(2,'user2',360000,'PAID','VNP20250114002','2025-01-14 09:15:00','2025-01-14 09:20:00'),
(3,'user3',220000,'PAID','VNP20250114003','2025-01-15 10:00:00','2025-01-15 10:05:00'),
(4,'user1',210000,'PAID','VNP20250115001','2025-01-15 14:30:00','2025-01-15 14:35:00'),
(5,'user5',240000,'PENDING',NULL,'2025-01-16 11:00:00',NULL),
(6,'user4',300000,'PAID','VNP20250116001','2025-01-16 13:45:00','2025-01-16 13:50:00'),
(7,'user6',280000,'PAID','VNP20250117001','2025-01-17 09:20:00','2025-01-17 09:25:00'),
(8,'user7',200000,'CANCELLED',NULL,'2025-01-18 15:10:00',NULL),
(9,'user8',350000,'PAID','VNP20250118001','2025-01-18 16:30:00','2025-01-18 16:35:00');

-- Table: tickets
DROP TABLE IF EXISTS tickets;
CREATE TABLE tickets (
  id INT NOT NULL AUTO_INCREMENT,
  showtime_id INT NOT NULL,
  seat_id INT NOT NULL,
  invoice_id INT NOT NULL,
  price INT NOT NULL,
  status TINYINT(1) DEFAULT 0,
  qr_code VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  scanned_at DATETIME DEFAULT NULL,
  is_scanned TINYINT(1) DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY qr_code (qr_code),
  KEY idx_showtime (showtime_id),
  KEY idx_seat (seat_id),
  KEY idx_invoice (invoice_id),
  KEY idx_qr_code (qr_code),
  CONSTRAINT tickets_ibfk_1 FOREIGN KEY (invoice_id) REFERENCES invoices (invoice_id) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO tickets VALUES 
(1,1,25,1,100000,1,'QR-1-25-001','2025-01-14 08:35:00','2025-01-15 09:05:00',1),
(2,1,26,1,100000,1,'QR-1-26-002','2025-01-14 08:35:00','2025-01-15 09:05:00',1),
(3,2,50,2,120000,1,'QR-2-50-001','2025-01-14 09:20:00','2025-01-15 09:30:00',1),
(4,2,51,2,120000,1,'QR-2-51-002','2025-01-14 09:20:00','2025-01-15 09:30:00',1),
(5,2,52,2,120000,1,'QR-2-52-003','2025-01-14 09:20:00','2025-01-15 09:31:00',1),
(6,3,75,3,110000,0,'QR-3-75-001','2025-01-15 10:05:00',NULL,0),
(7,3,76,3,110000,0,'QR-3-76-002','2025-01-15 10:05:00',NULL,0),
(8,4,100,4,105000,1,'QR-4-100-001','2025-01-15 14:35:00','2025-01-16 10:00:00',1),
(9,4,101,4,105000,1,'QR-4-101-002','2025-01-15 14:35:00','2025-01-16 10:00:00',1);

-- ============================================================================
-- 5. RECOMMENDATIONDB - Recommendation Service Database
-- ============================================================================
CREATE DATABASE IF NOT EXISTS recommendationdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE recommendationdb;

-- Table: user_preferences
DROP TABLE IF EXISTS user_preferences;
CREATE TABLE user_preferences (
  id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL UNIQUE,
  preferred_genres VARCHAR(500),
  preferred_actors VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO user_preferences VALUES 
(1,'user1','1,2,6','1,3,7','2025-11-14 14:35:00','2025-11-14 14:35:00'),
(2,'user2','1,4,9','2,4,6','2025-11-14 14:35:00','2025-11-14 14:35:00'),
(3,'user3','2,6,8','5,10,14','2025-11-14 14:35:00','2025-11-14 14:35:00'),
(4,'user4','1,3,5','1,9,11','2025-11-14 14:35:00','2025-11-14 14:35:00'),
(5,'user5','4,7,8','2,6,18','2025-11-14 14:35:00','2025-11-14 14:35:00');

-- Table: viewing_history
DROP TABLE IF EXISTS viewing_history;
CREATE TABLE viewing_history (
  id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  movie_id INT NOT NULL,
  viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  rating INT,
  PRIMARY KEY (id),
  KEY idx_username (username),
  KEY idx_movie (movie_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO viewing_history VALUES 
(1,'user1',1,'2025-01-15 09:05:00',5),
(2,'user1',2,'2025-01-16 10:00:00',4),
(3,'user2',2,'2025-01-15 09:30:00',5),
(4,'user2',3,'2025-01-17 11:00:00',4),
(5,'user3',5,'2025-01-18 14:30:00',3),
(6,'user3',8,'2025-01-20 16:00:00',5);

-- ============================================================================
-- END OF DATABASE INITIALIZATION SCRIPT
-- ============================================================================
