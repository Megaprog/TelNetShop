--Table: players

--DROP TABLE IF EXISTS players;

CREATE TABLE players (
  name   varchar(30) NOT NULL,
  money  decimal(10,2) NOT NULL,
/* Keys */
  PRIMARY KEY (name)
) ENGINE = InnoDB;
