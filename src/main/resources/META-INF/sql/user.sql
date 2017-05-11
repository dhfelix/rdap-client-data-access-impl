#getUserByUsername
SELECT * FROM {schema}.client_user WHERE usr_username = ?;

#storeUser
INSERT INTO {schema}.client_user VALUES ( null, ?, ?, ?, ?, ?, ?, ?, ?);

#updateUser
UPDATE {schema}.client_user SET usr_hashed_password = ?, usr_hash_salt = ?, 
	usr_hash_iterations = ?, usr_hash_algorithm = ?, usr_key_algorithm = ?, 
	usr_key_size = ?, usr_pbe_algorithm = ? WHERE usr_id = ? AND usr_username = ?;

#existUser
SELECT 1 FROM {schema}.client_user WHERE usr_username = ?;
