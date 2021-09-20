CREATE TABLE IF NOT EXISTS location_data
(
	location	VARCHAR(128)	NOT NULL,
	category	VARCHAR(128)	NOT NULL,
	subcategory	VARCHAR(128)	NOT NULL,
	x			DOUBLE			NOT NULL,
	y			DOUBLE			NOT NULL,
	z			DOUBLE			NOT NULL,
	pitch		FLOAT			NOT NULL,
	yaw			FLOAT			NOT NULL,
	PRIMARY KEY (location)
);

CREATE TABLE IF NOT EXISTS location_request_data
(
	location	VARCHAR(128)	NOT NULL,
	x			DOUBLE			NOT NULL,
	y			DOUBLE			NOT NULL,
	z			DOUBLE			NOT NULL,
	pitch		FLOAT			NOT NULL,
	yaw			FLOAT			NOT NULL,
	PRIMARY KEY (location)
);
	
CREATE TABLE IF NOT EXISTS regions
(
	region		VARCHAR(15)		NOT NULL,
	region_x	INT				NOT NULL,
	region_z	INT				NOT NULL,
	public		TINYINT(1)		NOT NULL,
	locked		TINYINT(1)		NOT NULL,
	open		TINYINT(1)		NOT NULL,
	PRIMARY KEY (region)
);

CREATE TABLE IF NOT EXISTS region_owners
(
	id			INT				AUTO_INCREMENT,
	region		VARCHAR(15)		NOT NULL,
	uuid		CHAR(36)		NOT NULL,
	last_enter	BIGINT			NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS region_members
(
	id			INT				AUTO_INCREMENT,
	region		VARCHAR(15)		NOT NULL,
	uuid		CHAR(36)		NOT NULL,
	last_enter	BIGINT			NOT NULL,
	PRIMARY KEY (id)
);
	
CREATE TABLE IF NOT EXISTS players
(
	uuid		CHAR(36)		NOT NULL,
	name		VARCHAR(17)		NOT NULL,
	role		VARCHAR(32)		NOT NULL,
	last_join	BIGINT			NOT NULL,
	PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS requests
(
	id			INT				AUTO_INCREMENT,
	region		VARCHAR(15)		NOT NULL,
	owner		CHAR(36)		NOT NULL,
	uuid		CHAR(36)		NOT NULL,
	staff_ac	TINYINT(1)		NOT NULL,
	owner_ac	TINYINT(1)		NOT NULL,
	x			DOUBLE			NOT NULL,
	y			DOUBLE			NOT NULL,
	z			DOUBLE			NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS messages
(
	id			INT				AUTO_INCREMENT,
	uuid		CHAR(36)		NOT NULL,
	message		TEXT			NOT NULL,
	colour		BIGINT			NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS logs
(
	id			INT				AUTO_INCREMENT,
	region		VARCHAR(15)		NOT NULL,
	uuid		CHAR(36)		NOT NULL,
	role		VARCHAR(16)		NOT NULL,
	start_time	BIGINT			NOT NULL,
	end_time	BIGINT			NOT NULL,
	PRIMARY KEY (id)
);
	
