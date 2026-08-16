ALTER TABLE scrim DROP discord_server_name;

ALTER TABLE scrim ADD
	server_id INT NOT NULL;

ALTER TABLE scrim ADD 
	CONSTRAINT FK_Scrim_Server FOREIGN KEY (server_id) REFERENCES server(id);