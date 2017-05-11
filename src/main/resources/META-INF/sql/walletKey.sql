#getWalletKeyByUserId
SELECT * FROM {schema}.wallet_key WHERE usr_id = ?;

#storeWalletKey
INSERT INTO {schema}.wallet_key VALUES (null, ?, ?, ?);

#updateWalletKey
UPDATE {schema}.wallet_key SET key_wallet_key = ?, key_wallet_key_algorithm = ? WHERE  key_id = ? AND usr_id = ?;
