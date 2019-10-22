CREATE TABLE `SYS_Log` (
  `logdate` DATE NOT NULL,
  `logger` VARCHAR(45) NOT NULL,
  `level` VARCHAR(45) NOT NULL,
  `message` VARCHAR(2000) NOT NULL);
