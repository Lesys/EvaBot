package zzc.discord.evabot.dto;

public class HistoryNameDTO extends AbstractDTO {	
	protected String nickname;
	
	protected Integer player_id;
	
	public HistoryNameDTO() {
		
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Integer getPlayer_id() {
		return player_id;
	}

	public void setPlayer_id(Integer player_id) {
		this.player_id = player_id;
	}
}