--Table: player_items

--DROP TABLE IF EXISTS player_items;

CREATE TABLE player_items (
  player_name  varchar(30) NOT NULL,
  item_name    varchar(45) NOT NULL,
/* Keys */
  PRIMARY KEY (player_name, item_name),
/* Foreign keys */
  CONSTRAINT fk_player
  FOREIGN KEY (player_name)
  REFERENCES players(name)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE INDEX fk_player_idx
ON player_items
(player_name);
