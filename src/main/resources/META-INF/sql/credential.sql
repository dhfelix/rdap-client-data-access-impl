#getCredentialByUserIdAndServerId
SELECT * FROM {schema}.credential WHERE usr_id = ? AND cre_server_id = ?;

#getAllCredentialsByUserId
SELECT * FROM {schema}.credential WHERE usr_id = ?;

#storeCredential
INSERT INTO {schema}.credential VALUES (null, ?, ?, ?, ?);

#updateCredential
UPDATE {schema}.credential SET cre_username = ?, cre_encrypted_password = ?, 
	cre_server_id = ? WHERE usr_id = ? AND cre_id =  ?;

#deleteCredential
DELETE FROM {schema}.credential WHERE usr_id = ? AND cre_id = ?;