ALTER TABLE user_account DROP INDEX fk_account_bank_user;

ALTER TABLE user_account DROP COLUMN user_id;
ALTER TABLE user_account DROP COLUMN id;

ALTER TABLE bank_user DROP COLUMN id;