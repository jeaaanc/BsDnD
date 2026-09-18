ALTER TABLE user_account ADD COLUMN active BOOLEAN DEFAULT TRUE;

UPDATE user_account SET active = TRUE WHERE active IS NULL;