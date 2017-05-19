#getByUsername
SELECT * FROM {schema}.wallet_user WHERE wusr_username = ?;

#store
INSERT INTO {schema}.wallet_user VALUES ( null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

#update
UPDATE {schema}.wallet_user SET wusr_hashed_password = ?, 
	wusr_hash_algorithm = ?, wusr_salt = ?,	wusr_iterations = ?, 
	wusr_pbe_algorithm = ?,	wusr_encrypted_wallet_key = ? , 
	wusr_key_algorithm = ?,	wusr_key_size = ?, wusr_cipher_algorithm = ? 
	WHERE wusr_id = ? AND wusr_username = ?;

#exist
SELECT 1 FROM {schema}.wallet_user WHERE wusr_username = ?;

#delete
DELETE FROM {schema}.wallet_user WHERE wusr_id = ? AND wusr_username = ?;
