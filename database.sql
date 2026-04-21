-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: room_management
-- ------------------------------------------------------
-- Server version	8.0.45

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
-- Table structure for table `contracts`
--

DROP TABLE IF EXISTS `contracts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contracts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `deposit` decimal(12,2) NOT NULL,
  `end_date` date NOT NULL,
  `monthly_rent` decimal(12,2) NOT NULL,
  `start_date` date NOT NULL,
  `status` enum('ACTIVE','EXPIRED','TERMINATED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `terminated_at` date DEFAULT NULL,
  `termination_note` text COLLATE utf8mb4_unicode_ci,
  `updated_at` datetime(6) DEFAULT NULL,
  `landlord_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  `tenant_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3iuurga8k6kbqj65g6j2ule0x` (`landlord_id`),
  KEY `FKju1b0xobla9t8oexrb8lpi8jq` (`room_id`),
  KEY `FKra7p26cb32ydditq6ab80pv6l` (`tenant_id`),
  CONSTRAINT `FK3iuurga8k6kbqj65g6j2ule0x` FOREIGN KEY (`landlord_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKju1b0xobla9t8oexrb8lpi8jq` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`),
  CONSTRAINT `FKra7p26cb32ydditq6ab80pv6l` FOREIGN KEY (`tenant_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contracts`
--

LOCK TABLES `contracts` WRITE;
/*!40000 ALTER TABLE `contracts` DISABLE KEYS */;
INSERT INTO `contracts` VALUES (1,'2026-04-21 04:55:42.734976',7000000.00,'2027-01-01',3500000.00,'2026-01-01','ACTIVE',NULL,NULL,'2026-04-21 04:55:42.734976',1,1,2),(2,'2026-04-21 04:55:42.739677',6600000.00,'2027-02-01',3300000.00,'2026-02-01','ACTIVE',NULL,NULL,'2026-04-21 04:55:42.739677',1,3,5),(3,'2026-04-21 04:55:42.742187',10000000.00,'2026-06-01',5000000.00,'2025-06-01','ACTIVE',NULL,NULL,'2026-04-21 04:55:42.742187',1,4,3),(4,'2026-04-21 04:55:42.742187',9600000.00,'2026-09-01',4800000.00,'2025-09-01','ACTIVE',NULL,NULL,'2026-04-21 04:55:42.742187',1,5,4),(5,'2026-04-21 04:55:42.746556',5800000.00,'2027-03-01',2900000.00,'2026-03-01','ACTIVE',NULL,NULL,'2026-04-21 04:55:42.746556',1,9,6),(6,'2026-04-21 04:55:42.748568',12000000.00,'2026-10-01',6000000.00,'2025-10-01','ACTIVE',NULL,NULL,'2026-04-21 04:55:42.748568',1,10,7),(7,'2026-04-21 04:55:42.748568',7600000.00,'2027-01-15',3800000.00,'2026-01-15','ACTIVE',NULL,NULL,'2026-04-21 04:55:42.748568',1,12,2),(8,'2026-04-21 04:55:42.753486',6000000.00,'2027-02-01',3000000.00,'2026-02-01','ACTIVE',NULL,NULL,'2026-04-21 04:55:42.753486',1,13,3),(9,'2026-04-21 04:55:42.755497',8400000.00,'2027-01-01',4200000.00,'2026-01-01','ACTIVE',NULL,NULL,'2026-04-21 04:55:42.755497',1,15,4),(10,'2026-04-21 04:55:42.758870',6400000.00,'2025-01-01',3200000.00,'2024-01-01','EXPIRED',NULL,NULL,'2026-04-21 04:55:42.758870',1,2,5),(11,'2026-04-21 04:55:42.760486',5600000.00,'2025-06-01',2800000.00,'2024-06-01','TERMINATED',NULL,NULL,'2026-04-21 04:55:42.760486',1,8,6);
/*!40000 ALTER TABLE `contracts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices`
--

DROP TABLE IF EXISTS `invoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoices` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `billing_month` varchar(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `due_date` date NOT NULL,
  `electric_amount` decimal(12,2) NOT NULL,
  `electric_price` decimal(10,2) NOT NULL,
  `electric_usage` double NOT NULL,
  `note` text COLLATE utf8mb4_unicode_ci,
  `paid_at` date DEFAULT NULL,
  `rent_amount` decimal(12,2) NOT NULL,
  `service_amount` decimal(10,2) NOT NULL,
  `status` enum('UNPAID','PAID','OVERDUE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `water_amount` decimal(12,2) NOT NULL,
  `water_price` decimal(10,2) NOT NULL,
  `water_usage` double NOT NULL,
  `contract_id` bigint NOT NULL,
  `meter_reading_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_invoice_contract_month` (`contract_id`,`billing_month`),
  KEY `FK2a4ruac2h8ea66fipnfy89cis` (`meter_reading_id`),
  CONSTRAINT `FK2a4ruac2h8ea66fipnfy89cis` FOREIGN KEY (`meter_reading_id`) REFERENCES `meter_readings` (`id`),
  CONSTRAINT `FKeads7q9fktwtsgdwmp1x16eqc` FOREIGN KEY (`contract_id`) REFERENCES `contracts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices`
--

LOCK TABLES `invoices` WRITE;
/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
INSERT INTO `invoices` VALUES (1,'2026-01','2026-04-21 04:55:42.817210','2026-02-05',122500.00,3500.00,35,NULL,'2026-02-02',3500000.00,200000.00,'PAID',3912500.00,'2026-04-21 04:55:42.921562',90000.00,15000.00,6,1,1),(2,'2026-02','2026-04-21 04:55:42.819205','2026-03-05',115500.00,3500.00,33,NULL,'2026-03-02',3500000.00,200000.00,'PAID',3920500.00,'2026-04-21 04:55:42.923579',105000.00,15000.00,7,1,2),(3,'2026-03','2026-04-21 04:55:42.822211','2026-04-05',129500.00,3500.00,37,NULL,NULL,3500000.00,200000.00,'UNPAID',3934500.00,'2026-04-21 04:55:42.822211',105000.00,15000.00,7,1,3),(4,'2026-02','2026-04-21 04:55:42.824231','2026-03-05',140000.00,3500.00,40,NULL,'2026-03-02',3300000.00,200000.00,'PAID',3745000.00,'2026-04-21 04:55:42.923579',105000.00,15000.00,7,2,4),(5,'2026-03','2026-04-21 04:55:42.825495','2026-04-05',133000.00,3500.00,38,NULL,NULL,3300000.00,200000.00,'UNPAID',3738000.00,'2026-04-21 04:55:42.825495',105000.00,15000.00,7,2,5),(6,'2026-01','2026-04-21 04:55:42.829193','2026-02-05',192500.00,3500.00,55,NULL,'2026-02-02',5000000.00,300000.00,'PAID',5612500.00,'2026-04-21 04:55:42.923579',120000.00,15000.00,8,3,6),(7,'2026-02','2026-04-21 04:55:42.831208','2026-03-05',192500.00,3500.00,55,NULL,'2026-03-02',5000000.00,300000.00,'PAID',5612500.00,'2026-04-21 04:55:42.923579',120000.00,15000.00,8,3,7),(8,'2026-03','2026-04-21 04:55:42.833199','2026-04-05',227500.00,3500.00,65,NULL,NULL,5000000.00,300000.00,'OVERDUE',5647500.00,'2026-04-21 04:55:42.833199',120000.00,15000.00,8,3,8),(9,'2026-01','2026-04-21 04:55:42.835205','2026-02-05',157500.00,3500.00,45,NULL,'2026-02-02',4800000.00,300000.00,'PAID',5362500.00,'2026-04-21 04:55:42.923579',105000.00,15000.00,7,4,9),(10,'2026-02','2026-04-21 04:55:42.837207','2026-03-05',175000.00,3500.00,50,NULL,'2026-03-02',4800000.00,300000.00,'PAID',5380000.00,'2026-04-21 04:55:42.923579',105000.00,15000.00,7,4,10),(11,'2026-03','2026-04-21 04:55:42.839200','2026-04-05',210000.00,3500.00,60,NULL,NULL,4800000.00,300000.00,'UNPAID',5415000.00,'2026-04-21 04:55:42.839200',105000.00,15000.00,7,4,11),(12,'2026-03','2026-04-21 04:55:42.841204','2026-04-05',105000.00,3500.00,30,NULL,NULL,2900000.00,150000.00,'UNPAID',3230000.00,'2026-04-21 04:55:42.841204',75000.00,15000.00,5,5,12),(13,'2026-01','2026-04-21 04:55:42.843420','2026-02-05',245000.00,3500.00,70,NULL,'2026-02-02',6000000.00,400000.00,'PAID',6795000.00,'2026-04-21 04:55:42.923579',150000.00,15000.00,10,6,13),(14,'2026-02','2026-04-21 04:55:42.845670','2026-03-05',262500.00,3500.00,75,NULL,'2026-03-02',6000000.00,400000.00,'PAID',6812500.00,'2026-04-21 04:55:42.924572',150000.00,15000.00,10,6,14),(15,'2026-03','2026-04-21 04:55:42.846672','2026-04-05',262500.00,3500.00,75,NULL,NULL,6000000.00,400000.00,'UNPAID',6812500.00,'2026-04-21 04:55:42.846672',150000.00,15000.00,10,6,15),(16,'2026-02','2026-04-21 04:55:42.848681','2026-03-05',147000.00,3500.00,42,NULL,'2026-03-02',3800000.00,250000.00,'PAID',4302000.00,'2026-04-21 04:55:42.924572',105000.00,15000.00,7,7,16),(17,'2026-03','2026-04-21 04:55:42.850428','2026-04-05',150500.00,3500.00,43,NULL,NULL,3800000.00,250000.00,'UNPAID',4305500.00,'2026-04-21 04:55:42.850428',105000.00,15000.00,7,7,17),(18,'2026-02','2026-04-21 04:55:42.852640','2026-03-05',112000.00,3500.00,32,NULL,'2026-03-02',3000000.00,150000.00,'PAID',3337000.00,'2026-04-21 04:55:42.924572',75000.00,15000.00,5,8,18),(19,'2026-03','2026-04-21 04:55:42.854652','2026-04-05',115500.00,3500.00,33,NULL,NULL,3000000.00,150000.00,'UNPAID',3355500.00,'2026-04-21 04:55:42.854652',90000.00,15000.00,6,8,19),(20,'2026-01','2026-04-21 04:55:42.856651','2026-02-05',175000.00,3500.00,50,NULL,'2026-02-02',4200000.00,280000.00,'PAID',4775000.00,'2026-04-21 04:55:42.924572',120000.00,15000.00,8,9,20),(21,'2026-02','2026-04-21 04:55:42.858923','2026-03-05',175000.00,3500.00,50,NULL,'2026-03-02',4200000.00,280000.00,'PAID',4775000.00,'2026-04-21 04:55:42.924572',120000.00,15000.00,8,9,21),(22,'2026-03','2026-04-21 04:55:42.860995','2026-04-05',203000.00,3500.00,58,NULL,NULL,4200000.00,280000.00,'UNPAID',4803000.00,'2026-04-21 04:55:42.860995',120000.00,15000.00,8,9,22);
/*!40000 ALTER TABLE `invoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meter_readings`
--

DROP TABLE IF EXISTS `meter_readings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `meter_readings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `billing_month` varchar(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `electric_current` double NOT NULL,
  `electric_previous` double NOT NULL,
  `note` text COLLATE utf8mb4_unicode_ci,
  `updated_at` datetime(6) DEFAULT NULL,
  `water_current` double NOT NULL,
  `water_previous` double NOT NULL,
  `recorded_by` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_meter_reading_room_month` (`room_id`,`billing_month`),
  KEY `FKnlr3cadcl4cgtbj7qf66obv4g` (`recorded_by`),
  CONSTRAINT `FKfcfh4ant2u95m90uf1ok8mb6m` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`),
  CONSTRAINT `FKnlr3cadcl4cgtbj7qf66obv4g` FOREIGN KEY (`recorded_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meter_readings`
--

LOCK TABLES `meter_readings` WRITE;
/*!40000 ALTER TABLE `meter_readings` DISABLE KEYS */;
INSERT INTO `meter_readings` VALUES (1,'2026-01','2026-04-21 04:55:42.762499',35,0,NULL,'2026-04-21 04:55:42.762499',6,0,1,1),(2,'2026-02','2026-04-21 04:55:42.767719',68,35,NULL,'2026-04-21 04:55:42.767719',13,6,1,1),(3,'2026-03','2026-04-21 04:55:42.769719',105,68,NULL,'2026-04-21 04:55:42.769719',20,13,1,1),(4,'2026-02','2026-04-21 04:55:42.770279',40,0,NULL,'2026-04-21 04:55:42.770279',7,0,1,3),(5,'2026-03','2026-04-21 04:55:42.774652',78,40,NULL,'2026-04-21 04:55:42.774652',14,7,1,3),(6,'2026-01','2026-04-21 04:55:42.777060',55,0,NULL,'2026-04-21 04:55:42.777060',8,0,1,4),(7,'2026-02','2026-04-21 04:55:42.779809',110,55,NULL,'2026-04-21 04:55:42.779809',16,8,1,4),(8,'2026-03','2026-04-21 04:55:42.781570',175,110,NULL,'2026-04-21 04:55:42.781570',24,16,1,4),(9,'2026-01','2026-04-21 04:55:42.783569',45,0,NULL,'2026-04-21 04:55:42.783569',7,0,1,5),(10,'2026-02','2026-04-21 04:55:42.786346',95,45,NULL,'2026-04-21 04:55:42.786346',14,7,1,5),(11,'2026-03','2026-04-21 04:55:42.787616',155,95,NULL,'2026-04-21 04:55:42.787616',21,14,1,5),(12,'2026-03','2026-04-21 04:55:42.789630',30,0,NULL,'2026-04-21 04:55:42.789630',5,0,1,9),(13,'2026-01','2026-04-21 04:55:42.791342',70,0,NULL,'2026-04-21 04:55:42.791342',10,0,1,10),(14,'2026-02','2026-04-21 04:55:42.792356',145,70,NULL,'2026-04-21 04:55:42.792356',20,10,1,10),(15,'2026-03','2026-04-21 04:55:42.796486',220,145,NULL,'2026-04-21 04:55:42.796486',30,20,1,10),(16,'2026-02','2026-04-21 04:55:42.798485',42,0,NULL,'2026-04-21 04:55:42.798485',7,0,1,12),(17,'2026-03','2026-04-21 04:55:42.803410',85,42,NULL,'2026-04-21 04:55:42.803410',14,7,1,12),(18,'2026-02','2026-04-21 04:55:42.805402',32,0,NULL,'2026-04-21 04:55:42.805402',5,0,1,13),(19,'2026-03','2026-04-21 04:55:42.808377',65,32,NULL,'2026-04-21 04:55:42.808377',11,5,1,13),(20,'2026-01','2026-04-21 04:55:42.810901',50,0,NULL,'2026-04-21 04:55:42.810901',8,0,1,15),(21,'2026-02','2026-04-21 04:55:42.812903',100,50,NULL,'2026-04-21 04:55:42.812903',16,8,1,15),(22,'2026-03','2026-04-21 04:55:42.813896',158,100,NULL,'2026-04-21 04:55:42.813896',24,16,1,15);
/*!40000 ALTER TABLE `meter_readings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `is_read` bit(1) NOT NULL,
  `reference_id` bigint DEFAULT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` enum('INVOICE_CREATED','INVOICE_PAID','INVOICE_OVERDUE','PAYMENT_RECEIVED','CONTRACT_CREATED','CONTRACT_EXPIRING','CONTRACT_TERMINATED','METER_READING_RECORDED','GENERAL') COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_notification_user_read` (`user_id`,`is_read`),
  KEY `idx_notification_created_at` (`created_at`),
  CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'Phòng 101 - Hạn 05/04/2026','2026-04-21 04:55:42.887813',_binary '\0',3,'Hóa đơn tháng 3/2026 đã tạo','INVOICE_CREATED',1),(2,'Phòng 201 đã thanh toán đủ tháng 2/2026','2026-04-21 04:55:42.888829',_binary '',7,'Thanh toán xác nhận','PAYMENT_RECEIVED',1),(3,'Hợp đồng Phòng 201 còn 60 ngày hết hạn','2026-04-21 04:55:42.890826',_binary '\0',3,'Hợp đồng sắp hết hạn','CONTRACT_EXPIRING',1),(4,'Phòng 201 tháng 3 đã quá hạn thanh toán','2026-04-21 04:55:42.892261',_binary '\0',8,'Hóa đơn quá hạn','INVOICE_OVERDUE',1),(5,'Phòng 401 - Hạn 05/04/2026','2026-04-21 04:55:42.893569',_binary '\0',15,'Hóa đơn tháng 3 - Phòng 401','INVOICE_CREATED',1),(6,'Phòng 401 đã thanh toán đủ tháng 2/2026','2026-04-21 04:55:42.895604',_binary '',14,'Thanh toán Phòng 401 tháng 2','PAYMENT_RECEIVED',1),(7,'Hợp đồng Phòng 401 còn 5 tháng hết hạn','2026-04-21 04:55:42.896604',_binary '',6,'Hợp đồng sắp hết hạn','CONTRACT_EXPIRING',1),(8,'Hóa đơn phòng 101 tháng 3 đã được tạo','2026-04-21 04:55:42.898927',_binary '\0',3,'Hóa đơn tháng 3/2026','INVOICE_CREATED',2),(9,'Hóa đơn tháng 3/2026 đến hạn 05/04/2026','2026-04-21 04:55:42.900432',_binary '',3,'Nhắc nhở thanh toán','INVOICE_OVERDUE',2),(10,'Hóa đơn phòng 201 tháng 3 đã được tạo','2026-04-21 04:55:42.901435',_binary '\0',8,'Hóa đơn tháng 3/2026','INVOICE_CREATED',3),(11,'Hóa đơn tháng 3/2026 đã quá hạn thanh toán','2026-04-21 04:55:42.903430',_binary '\0',8,'Hóa đơn quá hạn','INVOICE_OVERDUE',3),(12,'Hóa đơn phòng 202 tháng 3 đã được tạo','2026-04-21 04:55:42.905443',_binary '\0',11,'Hóa đơn tháng 3/2026','INVOICE_CREATED',4),(13,'Hóa đơn phòng 103 tháng 3 đã được tạo','2026-04-21 04:55:42.907363',_binary '\0',5,'Hóa đơn tháng 3/2026','INVOICE_CREATED',5),(14,'Hóa đơn phòng 303 tháng 3 đã được tạo','2026-04-21 04:55:42.907363',_binary '\0',12,'Hóa đơn tháng 3/2026','INVOICE_CREATED',6),(15,'Hóa đơn phòng 401 tháng 3 đã được tạo','2026-04-21 04:55:42.909937',_binary '\0',15,'Hóa đơn tháng 3/2026','INVOICE_CREATED',7);
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(12,2) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `note` text COLLATE utf8mb4_unicode_ci,
  `paid_at` date NOT NULL,
  `payment_method` enum('CASH','BANK_TRANSFER','MOMO','ZALOPAY','OTHER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `invoice_id` bigint NOT NULL,
  `recorded_by` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrbqec6be74wab8iifh8g3i50i` (`invoice_id`),
  KEY `FKa6nye1mj985yt9mg3umq45fnq` (`recorded_by`),
  CONSTRAINT `FKa6nye1mj985yt9mg3umq45fnq` FOREIGN KEY (`recorded_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKrbqec6be74wab8iifh8g3i50i` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,3912500.00,'2026-04-21 04:55:42.862991','Chuyển khoản đúng hạn','2026-02-02','BANK_TRANSFER',1,1),(2,3920500.00,'2026-04-21 04:55:42.866717','Chuyển khoản đúng hạn','2026-03-02','BANK_TRANSFER',2,1),(3,3745000.00,'2026-04-21 04:55:42.868724','Chuyển khoản đúng hạn','2026-03-02','BANK_TRANSFER',4,1),(4,5612500.00,'2026-04-21 04:55:42.871121','Chuyển khoản đúng hạn','2026-02-02','BANK_TRANSFER',6,1),(5,5612500.00,'2026-04-21 04:55:42.872628','Chuyển khoản đúng hạn','2026-03-02','BANK_TRANSFER',7,1),(6,5362500.00,'2026-04-21 04:55:42.874641','Chuyển khoản đúng hạn','2026-02-02','BANK_TRANSFER',9,1),(7,5380000.00,'2026-04-21 04:55:42.876149','Chuyển khoản đúng hạn','2026-03-02','BANK_TRANSFER',10,1),(8,6795000.00,'2026-04-21 04:55:42.877847','Chuyển khoản đúng hạn','2026-02-02','BANK_TRANSFER',13,1),(9,6812500.00,'2026-04-21 04:55:42.880202','Chuyển khoản đúng hạn','2026-03-02','BANK_TRANSFER',14,1),(10,4302000.00,'2026-04-21 04:55:42.881213','Chuyển khoản đúng hạn','2026-03-02','BANK_TRANSFER',16,1),(11,3337000.00,'2026-04-21 04:55:42.882220','Chuyển khoản đúng hạn','2026-03-02','BANK_TRANSFER',18,1),(12,4775000.00,'2026-04-21 04:55:42.884214','Chuyển khoản đúng hạn','2026-02-02','BANK_TRANSFER',20,1),(13,4775000.00,'2026-04-21 04:55:42.885799','Chuyển khoản đúng hạn','2026-03-02','BANK_TRANSFER',21,1);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `area` double DEFAULT NULL,
  `category` enum('SINGLE','DOUBLE','STUDIO','APARTMENT','DORMITORY') COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `elec_price` decimal(10,2) NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `service_price` decimal(10,2) NOT NULL,
  `status` enum('AVAILABLE','OCCUPIED','MAINTENANCE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `water_price` decimal(10,2) NOT NULL,
  `owner_id` bigint NOT NULL,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtnhxhxjvamaungwsm0q7e010` (`owner_id`),
  CONSTRAINT `FKtnhxhxjvamaungwsm0q7e010` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES (1,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'STUDIO','2026-04-21 04:55:42.686301','Phòng studio đầy đủ nội thất, ban công rộng',3500.00,3500000.00,200000.00,'OCCUPIED','Phòng 101','2026-04-21 04:55:42.686301',15000.00,1,'/uploads/rooms/room_studio_1.jpg'),(2,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'STUDIO','2026-04-21 04:55:42.692424','Phòng mới sơn, cửa sổ hướng Đông',3500.00,3200000.00,200000.00,'AVAILABLE','Phòng 102','2026-04-21 04:55:42.692424',15000.00,1,'/uploads/rooms/room_studio_2.jpg'),(3,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'STUDIO','2026-04-21 04:55:42.692424','Phòng yên tĩnh, gần thang máy',3500.00,3300000.00,200000.00,'OCCUPIED','Phòng 103','2026-04-21 04:55:42.692424',15000.00,1,'/uploads/rooms/room_studio_3.jpg'),(4,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'APARTMENT','2026-04-21 04:55:42.698371','Căn hộ 1 phòng ngủ, bếp riêng',3500.00,5000000.00,300000.00,'OCCUPIED','Phòng 201','2026-04-21 04:55:42.698371',15000.00,1,'/uploads/rooms/room_apartment_1.jpg'),(5,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'APARTMENT','2026-04-21 04:55:42.698866','Căn hộ view đẹp, tầng cao',3500.00,4800000.00,300000.00,'OCCUPIED','Phòng 202','2026-04-21 04:55:42.698866',15000.00,1,'/uploads/rooms/room_apartment_2.jpg'),(6,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'APARTMENT','2026-04-21 04:55:42.704268','Căn hộ mới bàn giao, nội thất cơ bản',3500.00,4500000.00,300000.00,'AVAILABLE','Phòng 203','2026-04-21 04:55:42.704268',15000.00,1,'/uploads/rooms/room_apartment_3.jpg'),(7,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'SINGLE','2026-04-21 04:55:42.706283','Đang sửa chữa điện nước',3500.00,2800000.00,150000.00,'MAINTENANCE','Phòng 301','2026-04-21 04:55:42.706283',15000.00,1,'/uploads/rooms/room_single_1.jpg'),(8,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'SINGLE','2026-04-21 04:55:42.709069','Phòng đơn tiện nghi, yên tĩnh',3500.00,2800000.00,150000.00,'AVAILABLE','Phòng 302','2026-04-21 04:55:42.711222',15000.00,1,'/uploads/rooms/room_single_2.jpg'),(9,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'SINGLE','2026-04-21 04:55:42.713390','Phòng đơn có ban công nhỏ',3500.00,2900000.00,150000.00,'OCCUPIED','Phòng 303','2026-04-21 04:55:42.713390',15000.00,1,'/uploads/rooms/room_single_1.jpg'),(10,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'APARTMENT','2026-04-21 04:55:42.714900','Penthouse mini, view toàn thành phố',3500.00,6000000.00,400000.00,'OCCUPIED','Phòng 401','2026-04-21 04:55:42.714900',15000.00,1,'/uploads/rooms/room_penthouse.jpg'),(11,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'APARTMENT','2026-04-21 04:55:42.719185','Căn hộ rộng rãi, 2 phòng ngủ',3500.00,5500000.00,350000.00,'AVAILABLE','Phòng 402','2026-04-21 04:55:42.719185',15000.00,1,'/uploads/rooms/room_apartment_1.jpg'),(12,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'STUDIO','2026-04-21 04:55:42.721907','Studio cao cấp, nội thất nhập khẩu',3500.00,3800000.00,250000.00,'OCCUPIED','Phòng 403','2026-04-21 04:55:42.721907',15000.00,1,'/uploads/rooms/room_studio_2.jpg'),(13,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'SINGLE','2026-04-21 04:55:42.725081','Phòng đơn thoáng mát',3500.00,3000000.00,150000.00,'OCCUPIED','Phòng 501','2026-04-21 04:55:42.725081',15000.00,1,'/uploads/rooms/room_single_2.jpg'),(14,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'SINGLE','2026-04-21 04:55:42.727802','Phòng đơn mới sơn lại',3500.00,3100000.00,150000.00,'AVAILABLE','Phòng 502','2026-04-21 04:55:42.727802',15000.00,1,'/uploads/rooms/room_single_1.jpg'),(15,'15 Nguyễn Trãi, Q1, TP.HCM',NULL,'APARTMENT','2026-04-21 04:55:42.727802','Căn hộ nhỏ, đầy đủ tiện nghi',3500.00,4200000.00,280000.00,'OCCUPIED','Phòng 503','2026-04-21 04:55:42.727802',15000.00,1,'/uploads/rooms/room_apartment_3.jpg'),(16,'Gia lm',NULL,'SINGLE','2026-04-21 05:31:05.735385','xn',3500.00,2500000.00,200000.00,'AVAILABLE','Phng vin01','2026-04-21 05:31:05.735385',15000.00,1,NULL);
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `enabled` bit(1) NOT NULL,
  `full_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('ROLE_ADMIN','ROLE_LANDLORD','ROLE_TENANT') COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-04-21 04:55:42.609053','landlord01@demo.com',_binary '','Nguyễn Văn An','$2a$10$TVGyH86JeVkSqTs6KGElp.SUoeAtQCwMKmUVEl1ixSY0XxfpBbtZy','0901234567','ROLE_LANDLORD','2026-04-21 04:55:42.609053','landlord01'),(2,'2026-04-21 04:55:42.665107','tenant01@demo.com',_binary '','Trần Thị Bình','$2a$10$TVGyH86JeVkSqTs6KGElp.SUoeAtQCwMKmUVEl1ixSY0XxfpBbtZy','0912345678','ROLE_TENANT','2026-04-21 04:55:42.665107','tenant01'),(3,'2026-04-21 04:55:42.670363','tenant02@demo.com',_binary '','Lê Văn Cường','$2a$10$TVGyH86JeVkSqTs6KGElp.SUoeAtQCwMKmUVEl1ixSY0XxfpBbtZy','0923456789','ROLE_TENANT','2026-04-21 04:55:42.670363','tenant02'),(4,'2026-04-21 04:55:42.672382','tenant03@demo.com',_binary '','Phạm Thị Dung','$2a$10$TVGyH86JeVkSqTs6KGElp.SUoeAtQCwMKmUVEl1ixSY0XxfpBbtZy','0934567890','ROLE_TENANT','2026-04-21 04:55:42.672382','tenant03'),(5,'2026-04-21 04:55:42.677341','tenant04@demo.com',_binary '','Hoàng Văn Em','$2a$10$TVGyH86JeVkSqTs6KGElp.SUoeAtQCwMKmUVEl1ixSY0XxfpBbtZy','0945678901','ROLE_TENANT','2026-04-21 04:55:42.677341','tenant04'),(6,'2026-04-21 04:55:42.679353','tenant05@demo.com',_binary '','Ngô Thị Phương','$2a$10$TVGyH86JeVkSqTs6KGElp.SUoeAtQCwMKmUVEl1ixSY0XxfpBbtZy','0956789012','ROLE_TENANT','2026-04-21 04:55:42.679353','tenant05'),(7,'2026-04-21 04:55:42.679353','tenant06@demo.com',_binary '','Vũ Đình Quang','$2a$10$TVGyH86JeVkSqTs6KGElp.SUoeAtQCwMKmUVEl1ixSY0XxfpBbtZy','0967890123','ROLE_TENANT','2026-04-21 04:55:42.679353','tenant06'),(8,'2026-04-21 04:55:42.684284','admin@demo.com',_binary '','Admin Hệ Thống','$2a$10$TVGyH86JeVkSqTs6KGElp.SUoeAtQCwMKmUVEl1ixSY0XxfpBbtZy','0900000000','ROLE_ADMIN','2026-04-21 04:55:42.684284','admin');
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

-- Dump completed on 2026-04-22  4:41:51
