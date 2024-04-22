DROP TABLE IF EXISTS `event`;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50),
  `login` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `chat_id` bigint NOT NULL,
  PRIMARY KEY (`id`,`chat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
--
-- Dumping data for table `user`
--
LOCK TABLES `user` WRITE;
INSERT INTO `user` VALUES (NULL,'John','john','asd123',0);
UNLOCK TABLES;

CREATE TABLE `event` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `summary` text,
  `datetime` datetime NOT NULL,
  `duration` int NOT NULL,
  `type` tinyint NOT NULL,
  `is_end` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
--
-- Dumping data for table `event`
--
LOCK TABLES `event` WRITE;
INSERT INTO `event` VALUES (NULL,1,'Event 1','2024-04-23 15:00:00',60,0,0),(NULL,1,'Event 2','2024-04-27 12:00:00',60,1,0);
UNLOCK TABLES;
