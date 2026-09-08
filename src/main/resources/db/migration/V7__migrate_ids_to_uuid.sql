ALTER TABLE bank_user ADD COLUMN uuid BINARY(16);
ALTER TABLE user_account ADD COLUMN uuid BINARY(16);
ALTER TABLE user_account ADD COLUMN user_uuid BINARY(16);


UPDATE bank_user SET uuid = UUID_TO_BIN(UUID()) WHERE uuid IS NULL;
UPDATE user_account SET uuid = UUID_TO_BIN(UUID()) WHERE uuid IS NULL;

UPDATE user_account uc
    JOIN bank_user b ON uc.user_id = b.id
    SET uc.user_uuid = b.uuid;

ALTER TABLE user_account DROP FOREIGN KEY fk_account_bank_user;


ALTER TABLE bank_user MODIFY COLUMN uuid BINARY(16) NOT NULL;
ALTER TABLE bank_user MODIFY COLUMN id BIGINT NOT NULL;
ALTER TABLE bank_user DROP PRIMARY KEY;
ALTER TABLE bank_user ADD PRIMARY KEY (uuid);

ALTER TABLE user_account MODIFY COLUMN uuid BINARY(16) NOT NULL;
ALTER TABLE user_account MODIFY COLUMN id BIGINT NOT NULL;
ALTER TABLE user_account DROP PRIMARY KEY;
ALTER TABLE user_account ADD PRIMARY KEY (uuid);

ALTER TABLE user_account MODIFY COLUMN user_uuid BINARY(16) NOT NULL;
ALTER TABLE user_account ADD CONSTRAINT fk_user_account_bank_user
    FOREIGN KEY (user_uuid) REFERENCES bank_user(uuid);