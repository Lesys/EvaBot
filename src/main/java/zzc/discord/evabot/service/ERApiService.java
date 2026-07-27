package zzc.discord.evabot.service;


import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import zzc.discord.evabot.Token;
import zzc.discord.evabot.dto.ERPlayerDTO;
import zzc.discord.evabot.dto.SeasonDTO;
import zzc.discord.evabot.exception.UserIdNotLinkedException;
import zzc.discord.evabot.util.UtilDate;
import zzc.discord.evabot.util.enumeration.MatchingMode;

@Service
public class ERApiService {
	protected final SeasonService seasonService;

	private Map<Integer, String> characters = new HashMap<Integer, String>();
	
	/**
	 * String for the current season, starts empty and is filled at the first use of the "https://open-api.bser.io/v2/data/Season" APi request
	 */
	private SeasonDTO season;
	
	public ERApiService (SeasonService seasonService) {
		this.seasonService = seasonService;
	}
	
	public SeasonDTO getSeason() {
		this.retrieveSeason();
		
		if (this.season == null) {
			throw new NullPointerException("[ERApiService] getSeason Season has not been retrieved correctly...");
		}
		
		return this.season;
	}
	
	/**
	 * Gets the current season (previous season if current is preseason) and put it in the variable.
	 * Lone Wolf's seasonId seems to be set to 0
	 */
	public void retrieveSeason() {
		try {
			// If season not initialized yet or has been initialized the day before
			if (this.season == null) {
				// Should be impossible to return null since it has been initialized with an entry
				this.season = this.seasonService.getDtoById(1);
			}
				
			// If season retrieved from database has not been updated to today's date (or has no activeSeasonId, which should be the case for only the first ever retrieve)
			if (!UtilDate.isSameDate(this.season.getLastDayUpdate(), new Date(), false) || this.season.getActiveSeasonId() == null) {
				System.out.println("[ERApiService] retrieveSeason Getting season: ");
				HttpResponse<JsonNode> seasonResponse = apiRequest("https://open-api.bser.io/v2/data/Season");
				
//				System.out.println("Status: " + seasonResponse.getStatus());
//				System.out.println("Body: " + seasonResponse.getBody());
				
				Map<String, Boolean> seasons = new HashMap<String, Boolean>();
				
				seasonResponse.getBody().getObject().getJSONArray("data")
						.forEach(s -> seasons.put(((JSONObject) s).get("seasonID").toString(),
								((JSONObject) s).getInt("isCurrent") == 0 ? false : true));
				
				String stringSeason = seasons.keySet().stream().filter(key -> seasons.get(key)).findFirst().orElse(null);
				
				if (stringSeason != null) {
					this.season.setRealSeasonId(Integer.valueOf(stringSeason));
					
					// Even seasons are preseasons starting from Early Access Season 2, and we don't want those
					if (this.season.getRealSeasonId() % 2 == 0) {
						this.season.setActiveSeasonId(this.season.getRealSeasonId() - 1);
					} else {
						this.season.setActiveSeasonId(this.season.getRealSeasonId());
					}
				} else {
					this.season.setRealSeasonId(null);
					this.season.setActiveSeasonId(null);
				}
				// Puts a new modificationTime so the update is forced in the database
				this.season.setLastDayUpdate(new Date());

				this.season = this.seasonService.getFormatter().entityToDto(this.seasonService.save(this.season));
			}
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Character retrieve is only done once every launch for the moment (when it's called for the first time), but might need to treat it like the season if the app is launched on a server
	 */
	public void retrieveCharacters() {
		try {
			// If characters not initialized yet
			if (this.characters.size() <= 0) {
				HttpResponse<JsonNode> characters;
				characters = apiRequest("https://open-api.bser.io/v2/data/Character");
				
				System.out.println("Status: " + characters.getStatus());
				System.out.println("Body characters: " + characters.getBody());
				
				characters.getBody().getObject().getJSONArray("data").forEach(c -> this.characters
						.put(((JSONObject) c).getInt("code"), ((JSONObject) c).get("name").toString()));
			}
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public String getUserId(String name) {
		System.out.println("[ERApiService] getUserId IGN retrieving uid: " + name);
		HttpResponse<JsonNode> jsonResponse;
		try {
			jsonResponse = apiRequest("https://open-api.bser.io/v1/user/nickname?query=" + name);
//			System.out.println("Status: " + jsonResponse.getStatus());
//			System.out.println("Body: " + jsonResponse.getBody());
			
			JSONObject obj = jsonResponse.getBody().getObject();
			
			String userId = obj.getJSONObject("user").get("userId").toString();
			
			System.out.println("userId : " + userId);
			
			return userId;
		} catch (UnirestException | JSONException e) {
			System.err.println("[ERApiService] getUserId Problem retrieving from API for user " + name);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Gets the MMR of the player for the most recent season via the ER API
	 * 
	 * @param name Name of the player account
	 * @return The MMR of the player
	 */
//	public JSONObject getPlayerStats(String name) {
//	    try {
//			String userId = GetPlayerStats.getUserId(name);
//
//			HttpResponse<JsonNode> rankResponse
//			  = apiRequest("https://open-api.bser.io/v1/rank/uid/" + userId + "/" + GetPlayerStats.getSeason() + "/3");
//
//			System.out.println("Status: " + rankResponse.getStatus());
//			System.out.println("Body: " + rankResponse.getBody());
////			HttpResponse<JsonNode> statResponse
////			  = apiRequest("https://open-api.bser.io/v1/user/stats/uid/" + userId + "/" + season);
////			System.out.println("Body: " + statResponse.getBody());
//			
////			String mmr = rankResponse.getBody().getObject().getJSONObject("userRank").get("mmr").toString();
//			return rankResponse.getBody().getObject().getJSONObject("userRank");
//		} catch (UnirestException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JSONException e) {
//			e.printStackTrace();
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//		}
//	    
//	    return null;
//	}
	
	/**
	 * Gets the MMR of the player for the most recent season via the ER API
	 * 
	 * @param player The player account
	 * @return The MMR of the player
	 */
	public JSONObject getPlayerRank(ERPlayerDTO player) {
		return this.getPlayerRankInfo(player, MatchingMode.RANKED);
	}
		

	/**
	 * Gets the MMR of the player for the most recent season via the ER API
	 * 
	 * @param player The player account
	 * @return The MMR of the player
	 */
	public JSONObject getPlayerRankInfo(ERPlayerDTO player, MatchingMode matchingMode) {
		if (matchingMode == null || player == null) return null;
		try {
			System.out.println("[ERApiService] getPlayerRank getting the rank and MMR for player " + player.getDakName());
			String userId = player.getUserId();
			
			HttpResponse<JsonNode> rankResponse = apiRequest("https://open-api.bser.io/v1/rank/uid/" + userId + "/" + this.getSeason().getActiveSeasonId() + "/" + matchingMode.getValue());
			
//			System.out.println("Status: " + rankResponse.getStatus());
//			System.out.println("Body: " + rankResponse.getBody());
			
			return rankResponse.getBody().getObject().getJSONObject("userRank");
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean updatePlayerRankInfo(ERPlayerDTO player) {
		JSONObject info = this.getPlayerRank(player);
		
		if (info != null) {
			player.setMmr(info.getInt("mmr"));
			player.setGlobalRank(info.getInt("rank"));
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Gets the MMR of the player for the most recent season via the ER API
	 * 
	 * @param userId The userId linking to the player account
	 * @return The MMR of the player
	 * @throws UserIdNotLinkedException If the userId is not reaching for a user, it means the player must have changed it's name
	 */
	public JSONObject getPlayerRankInfoByUserId(String userId) throws UserIdNotLinkedException {
		System.out.println("[ERApiService] getPlayerRankInfoByUserId getting the rank info from ranked games for player " + userId);
		return this.getPlayerRankInfoByUserId(userId, MatchingMode.RANKED);
	}

	
	/**
	 * Gets the MMR of the player for the most recent season via the ER API
	 * 
	 * @param userId The userId linking to the player account
	 * @return The MMR of the player
	 * @throws UserIdNotLinkedException If the userId is not reaching for a user, it means the player must have changed it's name
	 */
	public JSONObject getPlayerRankInfoByUserId(String userId, MatchingMode matchingMode) throws UserIdNotLinkedException {
		if (userId == null) {
			throw new UserIdNotLinkedException();
		}
		
		if (matchingMode == null) {
			System.err.println("[ERApiService] getPlayerRankInfoByUserId MatchingMode is null for userId " + userId);
			return null;
		}
		
		try {
			System.out.println("[ERApiService] getPlayerRankInfoByUserId getting the rank info from " + matchingMode + " games for player " + userId);
			
			HttpResponse<JsonNode> rankResponse = apiRequest("https://open-api.bser.io/v1/rank/uid/" + userId + "/" + this.getSeason().getActiveSeasonId() + "/" + matchingMode.getValue());
			
//			System.out.println("Status: " + rankResponse.getStatus());
//			System.out.println("Body: " + rankResponse.getBody());
			
			if (rankResponse.getStatus() == 404) {
				throw new UserIdNotLinkedException(rankResponse.getStatus());
			}
//			HttpResponse<JsonNode> statResponse
//			  = apiRequest("https://open-api.bser.io/v1/user/stats/uid/" + userId + "/" + season);
//			System.out.println("Body: " + statResponse.getBody());
			
//			String mmr = rankResponse.getBody().getObject().getJSONObject("userRank").get("mmr").toString();
			return rankResponse.getBody().getObject().getJSONObject("userRank");
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		return null;
	}
//	
//	/**
//	 * Gets the server distribution for a player on the latest season
//	 * 
//	 * @param name Name of the player account we want to check
//	 * @return A Map object with the servers name on keys and the amount of game on each server on entries
//	 */
//	public Map<String, Integer> serverDistribution(String name) {
//		try {
//			GetPlayerStats.retrieveGames(name);
//			ERPlayer player = ERPlayer.getERPlayer(name);
//			List<GameLog> filteredList = player.getAllRankedGames().stream()
//					.filter(gl -> String.valueOf(gl.getSeasonId()).equalsIgnoreCase(GetPlayerStats.getSeason()) || gl.getSeasonId() == 0).toList();
//			
//			Map<String, Integer> servers = new HashMap<String, Integer>();
//			filteredList.stream().map(gl -> gl.getServer()).distinct().forEach(server -> servers.put(server, 0));
//			servers.keySet().forEach(server -> servers.put(server,
//					(int) filteredList.stream().filter(gl -> server.equalsIgnoreCase(gl.getServer())).count()));
//			
//			System.out.println("Number of games: "
//					+ servers.keySet().stream().map(key -> servers.get(key)).reduce(0, (a, b) -> a + b));
//			servers.keySet()
//					.forEach(server -> System.out.println(server + " server: " + servers.get(server) + " games"));
//			
//			return servers;
//		} catch (UnirestException e) {
//			e.printStackTrace();
//			return new HashMap<String, Integer>();
//		}
//	}
//	
//	/**
//	 * Gets all the games 2 players played together in the current season
//	 * 
//	 * @param name1 The player name we want the games from
//	 * @param name2 The 2nd player name with which the 1st player played
//	 * @return List of all the games both players played together
//	 */
//	public List<GameLog> commonGames(String name1, String name2, MatchingMode matchingMode) {
//		try {
//			GetPlayerStats.retrieveGames(name1);
//			ERPlayer player = ERPlayer.getERPlayer(name1);
//			List<GameLog> filteredList = player.getAllGames(matchingMode).stream()
//					.filter(gl -> String.valueOf(gl.getSeasonId()).equalsIgnoreCase(GetPlayerStats.getSeason()) || gl.getSeasonId() == 0).toList();
//			List<GameLog> commonGames = filteredList.stream().filter(gl -> gl.getTeammates().stream()
//					.anyMatch(teammate -> teammate.getNickname().equalsIgnoreCase(name2))).toList();
//			
//			System.out.println("Number of games: " + commonGames.size());
//			
//			// ((JSONObject)gamesResponse.getBody().getObject().getJSONArray("userGames").get(0)).getLong("gameId")
//			
//			// ?nickname==name1 ==> get teamNumber nicknames, nickname contains name2, get
//			// gameRank
//			return commonGames;
//		} catch (UnirestException e) {
//			e.printStackTrace();
//			return new ArrayList<GameLog>();
//		}
//	}
//	
//	/**
//	 * Gets all the games of the player name with the ER API, retrieves the informations of the game (teammates...) and serializes it
//	 * 
//	 * @param name The player name we want the games from
//	 * @throws UnirestException
//	 */
//	public void retrieveNicknames(String name) throws UnirestException {
//		try {
//			GetPlayerStats.retrieveSeason();
//			GetPlayerStats.retrieveCharacters();
//			
//			HttpResponse<JsonNode> jsonResponse;
//			jsonResponse = apiRequest("https://open-api.bser.io/v1/user/nickname?query=" + name);
//			
//			System.out.println("Status: " + jsonResponse.getStatus());
//			System.out.println("Body: " + jsonResponse.getBody());
//			
//			JSONObject obj = jsonResponse.getBody().getObject();
//			
//			String userId = obj.getJSONObject("user").get("userId").toString();
//			
//			Bot.deserializeGameLog();
//			ERPlayer player = ERPlayer.getERPlayer(name);
//			player.setUserId(userId);
//			LocalDateTime date = player.getLastGame() != null ? player.getLastGame().getDateTime() : null;
//			
//			long next = 0;
//			HttpResponse<JsonNode> gamesResponse;
//			List<GameLog> gameList = new ArrayList<GameLog>();
//			// System.err.println("Last game id: " + player.getLastGame().getGameId());
//			System.err.println("Date: " + date);
//			do {
//				gamesResponse = apiRequest("https://open-api.bser.io/v1/user/games/uid/" + userId + (next != 0 ? "?next=" + next : ""));
//				try {
//					System.out.println("Status: " + gamesResponse.getStatus());
//					// System.out.println("Body: " + gamesResponse.getBody());
//					// System.out.println("Number of games: " +
//					// gamesResponse.getBody().getObject().getJSONArray("userGames").length());
//					
//					next = gamesResponse.getBody().getObject().getLong("next");
//				} catch (JSONException e) {
//					next = 0;
//				} catch (NullPointerException e) {
//					next = 0;
//				}
//				
//				if (gamesResponse != null) {
//					Iterator<Object> iter = gamesResponse.getBody().getObject().getJSONArray("userGames").iterator();
//					
//					while (iter.hasNext()) {
//						JSONObject o = (JSONObject) iter.next();
//						if (o.getInt("matchingMode") == 3) {
//							GameLog g = new GameLog(o);
//							if (!player.getHistoryPlayerName().stream().anyMatch(g.getNickname()::equalsIgnoreCase)) {
//								player.addHistoryPlayerName(g.getNickname());
//							}
//						}
//					}
//				}
//			} while (gameList.stream().noneMatch(
//					gl -> !String.valueOf(gl.getSeasonId()).equalsIgnoreCase(GetPlayerStats.getSeason()) && gl.getSeasonId() != 0) && next != 0);
//			
//			Bot.games.addAll(0, gameList);
//			
//			Bot.serializePlayers();
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * Gets all the games of the player name with the ER API, retrieves the informations of the game (teammates...) and serializes it
//	 * 
//	 * @param name The player name we want the games from
//	 * @throws UnirestException
//	 */
//	public void retrieveGames(String name) throws UnirestException {
//		try {
//			GetPlayerStats.retrieveCharacters();
//			
//			// Retrieve the UID
//			Bot.deserializeGameLog();
//			ERPlayer player = ERPlayer.getERPlayer(name);
//			
//			if (player == null) {
//				System.err.println("[retrieveGames] Player " + name + " not found by API call.");
//				return;
//			}
//			
//			String userId = player.getUserId();
//			
//			LocalDateTime date = player.getLastGame() != null ? player.getLastGame().getDateTime() : null;
//			
//			long next = 0;
//			HttpResponse<JsonNode> gamesResponse;
//			List<GameLog> gameList = new ArrayList<GameLog>();
//			boolean keepGoing = true;
//			// System.err.println("Last game id: " + player.getLastGame().getGameId());
//			System.err.println("Date: " + date);
//			
//			// Loop around all the games not yet retrieved
//			do {
//				gamesResponse = apiRequest("https://open-api.bser.io/v1/user/games/uid/" + userId + (next != 0 ? "?next=" + next : ""));
//				
//				System.out.println("Status: " + gamesResponse.getStatus());
//				// System.out.println("Body: " + gamesResponse.getBody());
//				// System.out.println("Number of games: " +
//				// gamesResponse.getBody().getObject().getJSONArray("userGames").length());
//				try {
//					next = gamesResponse.getBody().getObject().getLong("next");
//				} catch (JSONException e) {
//					next = 0;
//				}
//				Iterator<Object> iter = gamesResponse.getBody().getObject().getJSONArray("userGames").iterator();
//				
//				// Add every game that is not yet in the list based on the date
//				while (keepGoing && iter.hasNext()) {
//					JSONObject o = (JSONObject) iter.next();
//					if (date == null || date != null
//							&& GetPlayerStats.getLocalDateTime(o.getString("startDtm")).isAfter(date)) {
//						GameLog g = new GameLog(o);
//						if (!player.getHistoryPlayerName().stream().anyMatch(g.getNickname()::equalsIgnoreCase)) {
//							player.addHistoryPlayerName(g.getNickname());
//						}
//						gameList.add(g);
//					} else
//						keepGoing = false;
//				}
//			} while (keepGoing
//					&& gameList.stream()
//							.noneMatch(gl -> !String.valueOf(gl.getSeasonId()).equalsIgnoreCase(GetPlayerStats.getSeason()) && gl.getSeasonId() != 0)
//					&& next != 0);
//			
//			Bot.games.addAll(0, gameList);
//			
//			Bot.serializeGameLog();
//			// Filters all the games for the player (and all the games under an old nickname as well)
//			List<GameLog> playerGames = Bot.games.stream()
//					.filter(gl -> (!UtilEmpty.isEmptyOrNull(player.getHistoryPlayerName())
//							&& player.getHistoryPlayerName().contains(gl.getNickname()))
//							|| gl.getNickname().equalsIgnoreCase(name))
//					.toList();
//			
//			// Filters to only keep current season's games
//			List<GameLog> filteredList = playerGames.stream()
//					.filter(gl -> String.valueOf(gl.getSeasonId()).equalsIgnoreCase(GetPlayerStats.getSeason()) || gl.getSeasonId() == 0).toList();
//			AtomicInteger counter = new AtomicInteger(1);
//			
//			// Retrieves each single game to get specific informations about it (character played, teammates ...)
//			filteredList.stream()
//					.filter(gl -> (date == null || (date != null && gl.getDateTime().isAfter(date)
//							&& (gl.getTeammates().size() <= 0 || gl.getMmrGainInGame() <= 0
//									|| gl.getCharacterPlayed() == null || gl.getCharacterPlayed().isEmpty()))))
//					.forEach(gl -> {
//						HttpResponse<JsonNode> game;
//						try {
//							game = apiRequest("https://open-api.bser.io/v1/games/" + gl.getGameId());
//							// System.out.println("Body: " + game.getBody());
//						} catch (UnirestException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							game = null;
//						}
//						if (game != null) {
//							if (gl.getCharacterPlayed() == null || gl.getCharacterPlayed().isEmpty()) {
//								JSONObject jsonObj = (JSONObject) StreamSupport
//										.stream(game.getBody().getObject().getJSONArray("userGames").spliterator(),
//												false)
//										.filter(o -> ((JSONObject) o).getString("nickname")
//												.equalsIgnoreCase(gl.nickname))
//										.findFirst().get();
//								gl.setCharacterPlayed(GetPlayerStats.characters.get(jsonObj.getInt("characterNum")));
//								System.err.println("Character name: "
//										+ GetPlayerStats.characters.get(jsonObj.getInt("characterNum")));
//							}
//							
//							StreamSupport
//									.stream(game.getBody().getObject().getJSONArray("userGames").spliterator(), false)
//									.filter(o -> ((JSONObject) o).getInt("teamNumber") == gl.getTeamnumber())
//									.forEach(o -> {
//										if (!player.getHistoryPlayerName()
//												.contains(((JSONObject) o).getString("nickname"))
//												&& !gl.getTeammates()
//														.contains(((JSONObject) o).getString("nickname"))) {
//											TeamMate tm = new TeamMate(((JSONObject) o).getString("nickname"));
//											// userId (or userNum) no longer shown so we can't get that
////							tm.setPlayerId(Long.toString(((JSONObject)o).getLong("userId")));
//											gl.addTeammates(tm);
//										}
//									});
//							// System.err.println("Teammates: " + gl.getTeammates());
//							System.err.println("Status game " + counter.getAndIncrement() + " / " + filteredList.size()
//									+ ": " + game.getStatus());
//						}
//					});
//			
//			Bot.serializeGameLog();
//			Bot.serializePlayers();
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * Retrieves a single game informations by it's gameId
//	 * @param gameId	The ID of the game to retrieve
//	 * @return			The JSON object with the game informations
//	 */
//	public JSONObject retrieveGameInfo(String gameId) {
//		try {
//			HttpResponse<JsonNode> game = apiRequest("https://open-api.bser.io/v1/games/" + gameId);
//			
//			return game.getBody().getObject();
//		} catch (UnirestException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	/**
	 * API request with a delay of 1 second because APIKey can't provide more than 1 request per second
	 * 
	 * @param url The URL of the API request
	 * @return The JSON of the API response
	 * @throws UnirestException
	 */
	public HttpResponse<JsonNode> apiRequest(String url) throws UnirestException {
		HttpResponse<JsonNode> resp = null;
		boolean redo;
		do {
			redo = false;
			try {
				resp = CompletableFuture.supplyAsync(() -> {
					try {
						return Unirest.get(url).header("accept", "application/json").header("x-api-key", Token.erApiKey).asJson();
					} catch (UnirestException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return null;
					}
				}, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)).get();
			} catch (IllegalArgumentException | ExecutionException e) {
				System.err.println(e.getMessage());
			} catch (InterruptedException e1) {
				// In case it got interrupted, the request will be done one more time
				System.err.println("Missed 2 heartbeats, will redo the command for " + url);
				redo = true;
			}
		} while (redo);
		
		return resp;
	}
	
}
