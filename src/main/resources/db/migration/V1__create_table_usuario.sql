CREATE TABLE bank_user(
id bigint NOT NULL auto_increment,
name VARCHAR (100) NOT NULL,
lastname VARCHAR (100) NOT NULL,
cpf VARCHAR (11) NOT NULL UNIQUE,
password VARCHAR (100) NOT NULL,
phone_number VARCHAR (20) NOT NULL,

PRIMARY KEY (id)
);