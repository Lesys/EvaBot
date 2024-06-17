package zzc.discord.evabot;

/**
 * 
 * @author Lesys
 *
 * Class representing a team mate from different games to make stats 
 */
public class TeamMate implements Comparable<TeamMate> {
	/**
	 * Nickname of this team mate
	 */
	protected String nickname;
	
	/**
	 * Total wins overall with the other team mate
	 */
	protected int totalWins;

	/**
	 * Total games overall with the other team mate
	 */
	protected int totalGames;

	/**
	 * Total placement overall with the other team mate (addition of all the placements)
	 */
	protected int placement;

	/**
	 * Total kills overall with the other team mate
	 */
	protected int teamKill;

	/**
	 * Total of MMR gained with the other team mate
	 */
	protected int mmrGainInGame;
	
	/**
	 * Constructor of TeamMate
	 * @param nickname	Nickname of this team mate
	 */
	public TeamMate(String nickname) {
		this.nickname = nickname;
		this.totalWins = 0;
		this.totalGames = 0;;
		this.placement = 0;
		this.teamKill = 0;
		this.mmrGainInGame = 0;
	}
	
	/**
	 * Getter of nickname
	 * @return		The nickname of this team mate
	 */
	public String getNickname() {
		return this.nickname;
	}

	/**
	 * Getter of totalWins
	 * @return		The total of wins with the other team mate
	 */
	public int getTotalWins() {
		return this.totalWins;
	}

	/**
	 * Getter of totalGames
	 * @return		The total of games with the other team mate
	 */
	public int getTotalGames() {
		return this.totalGames;
	}

	/**
	 * Getter of placement
	 * @return		The total of placement with the other team mate
	 */
	public int getPlacement() {
		return this.placement;
	}

	/**
	 * Getter of teamKill
	 * @return		The total of kills with the other team mate
	 */
	public int getTeamKill() {
		return this.teamKill;
	}
	
	/**
	 * Getter of mmrGainInGame
	 * @return		The total of MMR gained with the other team mate
	 */
	public int getMmrGainInGame() {
		return this.mmrGainInGame;
	}

	/**
	 * Adds the wins to the total amount of wins
	 * @param wins	The wins to add
	 */
	public void addTotalWins(int wins) {
		this.totalWins += wins;
	}
	
	/**
	 * Adds the games to the total amount of games
	 * @param games		The games to add
	 */
	public void addTotalGames(int games) {
		this.totalGames += games;
	}

	/**
	 * Adds the placements to the total amount of placement
	 * @param placement		The placement to add
	 */
	public void addPlacement(int placement) {
		this.placement += placement;
	}

	/**
	 * Adds the teamKill to the total amount of teamKill
	 * @param teamKill		The teamKill to add
	 */
	public void addTeamKill(int teamKill) {
		this.teamKill += teamKill;
	}
	
	/**
	 * Adds the RP gained to the total MMR
	 * @param mmrGainInGame		The mmrGainInGame to add
	 */
	public void addMmrGainInGame(int mmrGainInGame) {
		this.mmrGainInGame += mmrGainInGame;
	}
	
	/**
	 * Calculates the ratio of won games
	 * @return		The ratio of won games
	 */
	public Double ratioWinGames() {
		return Math.floor(Double.valueOf(this.totalWins) / Double.valueOf(this.totalGames) * 100) / 100;
	}

	/**
	 * Calculates the average of team kills
	 * @return		The average of team kills
	 */
	public Double averageTK() {
		return Math.floor(Double.valueOf(this.teamKill) / Double.valueOf(this.totalGames) * 100) / 100;
	}

	/**
	 * Calculates the average of placement
	 * @return		The average of placement
	 */
	public Double averagePlacement() {
		return Math.floor(Double.valueOf(this.placement) / Double.valueOf(this.totalGames) * 100) / 100;
	}
	
	/**
	 * Calculates the average RP gained
	 * @return		The average RP gained
	 */
	public Double averageRpGains() {
		return Math.floor(Double.valueOf(this.mmrGainInGame) / Double.valueOf(this.totalGames) * 100) / 100;
	}
	
	@Override
	public int compareTo(TeamMate tm) {
		return this.ratioWinGames().compareTo(tm.ratioWinGames()) == 0 ? ((Integer)this.getTotalWins()).compareTo(tm.getTotalWins()) : this.ratioWinGames().compareTo(tm.ratioWinGames());
	}
}
