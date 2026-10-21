-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: food_delivery_db
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addresses` (
  `address_type` varchar(31) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `city` varchar(255) NOT NULL,
  `street_address` varchar(255) NOT NULL,
  `zip_code` varchar(255) NOT NULL,
  `gate_code` varchar(255) DEFAULT NULL,
  `floor_and_company` varchar(255) DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq0roe5usabjpwwflsnuscjerx` (`customer_id`),
  CONSTRAINT `FKq0roe5usabjpwwflsnuscjerx` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES ('OFFICE',1,'malabe','malabe','10115',NULL,'srilanka postoffice',5),('OFFICE',2,'malabe','kaduwela','10113',NULL,'srilanka postoffice',5);
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `customer_id` bigint NOT NULL,
  `food_item_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKstvfmrq4hk9go8cd0yq27kcqk` (`customer_id`),
  KEY `FKjr4thtqm8skn4hjhlv3phxbbm` (`food_item_id`),
  CONSTRAINT `FKjr4thtqm8skn4hjhlv3phxbbm` FOREIGN KEY (`food_item_id`) REFERENCES `food_items` (`id`),
  CONSTRAINT `FKstvfmrq4hk9go8cd0yq27kcqk` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food_items`
--

DROP TABLE IF EXISTS `food_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_items` (
  `item_type` varchar(31) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `available` bit(1) NOT NULL,
  `base_price` double NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `image_url` varchar(1000) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `owner_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKg4vepfgekhlm0svtse9yc3fx6` (`owner_id`),
  CONSTRAINT `FKg4vepfgekhlm0svtse9yc3fx6` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_items`
--

LOCK TABLES `food_items` WRITE;
/*!40000 ALTER TABLE `food_items` DISABLE KEYS */;
INSERT INTO `food_items` VALUES ('MAIN_COURSE',2,_binary '',120.01,'as','https://www.google.com/search?sca_esv=080dae4805299e94&sxsrf=APpeQns0A0MOi4xp-h8DCGcLVlWvQnnl2Q:1790608829437&udm=2&fbs=ABfTbFWJM45OT8tYQ-Wm0dppyf0Z9T6r4gX-2KK-0yDqj3SYrLJ2xyDUajqKjjpoIFj7RbjHml24nwejyPETJ1aF1Qefxvj52CfGvy6LiSfbkxG7CJYLMlb_AFNpjZhCryCVy-ix4uS-micoNnXvy9Q_GDwDVC5XVaq8j4zL3FPwTgTdn4EkQrCScZAJQtqwDLZXz0_3zdF48wBslk7YDsdqNh8D1bm6m6vRFuvLaJNj8NNoEo0Zx3k&q=eag+roti&sa=X&ved=2ahUKEwiH19bZyZGXAxVXUGcHHcAsCkYQtKgLegQIFxAB&biw=1366&bih=599&dpr=1#sv=CAMSURoyKhBlLVZ5emJsb0ZGMFZMZHJNMg5WeXpibG9GRjBWTGRyTToOMHV3TFZaclB3TXFDdk0gBCoXCgFzEhBlLVZ5emJsb0ZGMFZMZHJNGAEwARgHIJPUwc8ESggQARgBIAEoAQ','eag',7);
/*!40000 ALTER TABLE `food_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_records`
--

DROP TABLE IF EXISTS `order_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `delivery_address` varchar(255) DEFAULT NULL,
  `items_summary` varchar(1000) NOT NULL,
  `order_date` datetime(6) NOT NULL,
  `order_status` varchar(255) NOT NULL,
  `total_amount` double NOT NULL,
  `customer_id` bigint NOT NULL,
  `restaurant_id` bigint NOT NULL,
  `rider_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcdx7y19wwxq3orfgmgoav4hpo` (`customer_id`),
  KEY `FKjxm09v55tln81ojeosstmp4y6` (`restaurant_id`),
  KEY `FKro3s5qfw52aqerdrge8yjw78x` (`rider_id`),
  CONSTRAINT `FKcdx7y19wwxq3orfgmgoav4hpo` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKjxm09v55tln81ojeosstmp4y6` FOREIGN KEY (`restaurant_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKro3s5qfw52aqerdrge8yjw78x` FOREIGN KEY (`rider_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_records`
--

LOCK TABLES `order_records` WRITE;
/*!40000 ALTER TABLE `order_records` DISABLE KEYS */;
INSERT INTO `order_records` VALUES (1,'kaduwela, malabe 10113','3x eag','2026-09-28 16:04:31.836818','PLACED',396.033,5,7,NULL);
/*!40000 ALTER TABLE `order_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_methods`
--

DROP TABLE IF EXISTS `payment_methods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_methods` (
  `card_type` varchar(31) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `card_number` varchar(16) NOT NULL,
  `cardholder_name` varchar(255) NOT NULL,
  `cvv` varchar(255) NOT NULL,
  `expiry_date` varchar(255) NOT NULL,
  `credit_limit` double DEFAULT NULL,
  `bank_name` varchar(255) DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt0bj3di1jb13i6oul70e33ibx` (`customer_id`),
  CONSTRAINT `FKt0bj3di1jb13i6oul70e33ibx` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_methods`
--

LOCK TABLES `payment_methods` WRITE;
/*!40000 ALTER TABLE `payment_methods` DISABLE KEYS */;
INSERT INTO `payment_methods` VALUES ('CREDIT',1,'1563489653546592','anuwan','232','02/27',300000,NULL,5);
/*!40000 ALTER TABLE `payment_methods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `review_type` varchar(31) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comments` varchar(1000) DEFAULT NULL,
  `rating` int NOT NULL,
  `customer_id` bigint NOT NULL,
  `rider_id` bigint DEFAULT NULL,
  `food_item_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkquncb1glvrldaui8v52xfd5q` (`customer_id`),
  KEY `FK5nkqef57n8iqhtlwbhfojq3j7` (`rider_id`),
  KEY `FKg8qdm4qqi01fio5hswpea8jdy` (`food_item_id`),
  CONSTRAINT `FK5nkqef57n8iqhtlwbhfojq3j7` FOREIGN KEY (`rider_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKg8qdm4qqi01fio5hswpea8jdy` FOREIGN KEY (`food_item_id`) REFERENCES `food_items` (`id`),
  CONSTRAINT `FKkquncb1glvrldaui8v52xfd5q` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `role_type` varchar(31) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `default_delivery_address` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `restaurant_name` varchar(255) DEFAULT NULL,
  `vehicle_plate_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('ADMIN',1,'admin@test.com','Super Admin','password','admin',NULL,NULL,NULL,NULL),('CUSTOMER',2,'customer@test.com','Alice Customer','password','customer','123 Test St',NULL,NULL,NULL),('RESTAURANT_OWNER',3,'owner@test.com','Bob Owner','password','owner',NULL,NULL,'Bob\'s Burgers',NULL),('RIDER',4,'rider@test.com','Charlie Rider','password','rider',NULL,NULL,NULL,'XYZ-9876'),('CUSTOMER',5,'dissayuru@gmail.com','anura','anura','anura','malabe',NULL,NULL,NULL),('RESTAURANT_OWNER',7,'dissayru@gmail.com','nuwan','nuwan','nuwan',NULL,NULL,'malabe',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-20 18:39:00
