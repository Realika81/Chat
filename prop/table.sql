CREATE DATABASE Users;
use Users;

create table User(
	id varchar(30) not null,
	clave varchar(20) not null,
	nombre varchar(20) not null,
	Primary Key(id)
);

create table Message(
	id int NOT NULL AUTO_INCREMENT,
	sender varchar(30) not null,
	message varchar(255) not null,
	receiver varchar(30) not null,
    	Primary Key (id)
);

ALTER TABLE Message AUTO_INCREMENT=1;
ALTER TABLE Message ADD Foreign Key (sender) REFERENCES User(id);
ALTER TABLE Message ADD Foreign Key (receiver) REFERENCES User(id);