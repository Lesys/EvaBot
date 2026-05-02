CREATE TABLE IF NOT EXISTS team_players (
	player_id INT NOT NULL,
	team_id INT NOT NULL,
	creation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	modification_time TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
	
	PRIMARY KEY(player_id, team_id),
	CONSTRAINT FK_TeamPlayers_ERPlayer FOREIGN KEY (player_id) REFERENCES er_player(id),
	CONSTRAINT FK_TeamPlayers_Team FOREIGN KEY (team_id) REFERENCES team(id)
);