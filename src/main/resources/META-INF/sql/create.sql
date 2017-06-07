CREATE SCHEMA IF NOT EXISTS `rdap2`;


CREATE TABLE IF NOT EXISTS `rdap2`.`wallet_user` (
  `wusr_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `wusr_username` VARCHAR(255) NOT NULL,
  `wusr_hashed_password` VARCHAR(255) NOT NULL,
  `wusr_hash_algorithm` VARCHAR(100) NOT NULL,
  `wusr_salt` VARCHAR(100) NOT NULL,
  `wusr_iterations` INT NOT NULL,
  `wusr_pbe_algorithm` VARCHAR(100) NOT NULL,
  `wusr_encrypted_wallet_key` VARCHAR(255) NOT NULL,
  `wusr_key_algorithm` VARCHAR(100) NOT NULL,
  `wusr_key_size` INT NOT NULL,
  `wusr_cipher_algorithm` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`wusr_id`),
  UNIQUE INDEX `wusr_id_UNIQUE` (`wusr_id` ASC),
  UNIQUE INDEX `wusr_username_UNIQUE` (`wusr_username` ASC));
  CREATE INDEX IF NOT EXISTS `wusr_username_index` ON `rdap2`.`wallet_user`(`wusr_username` ASC);


-- -----------------------------------------------------
-- Table `rdap2`.`credential`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rdap2`.`credential` ;

CREATE TABLE IF NOT EXISTS `rdap2`.`credential` (
  `cre_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `wusr_id` BIGINT UNSIGNED NOT NULL,
  `cre_username` VARCHAR(100) NOT NULL,
  `cre_encrypted_password` VARCHAR(255) NOT NULL,
  `cre_server_id` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`cre_id`, `wusr_id`),
  UNIQUE INDEX `cre_id_UNIQUE` (`cre_id` ASC),
  FOREIGN KEY ( `wusr_id` ) 
  REFERENCES `rdap2`.`wallet_user` (`wusr_id`));
   CREATE INDEX IF NOT EXISTS `fk_credential_wallet_user1_idx` ON `rdap2`.`credential` (`wusr_id` ASC);
  
