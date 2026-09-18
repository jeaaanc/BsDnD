CREATE TABLE user_account(
id BIGINT AUTO_INCREMENT PRIMARY KEY,
account_number VARCHAR(20) NOT NULL UNIQUE,
balance DECIMAL(15, 2) NOT NULL,
user_id BIGINT,
CONSTRAINT fk_account_bank_user FOREIGN KEY (user_id) REFERENCES bank_user(id)
);