package zzc.discord.evabot;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Static class to get MMR of players via the Eternal Return API
 * @author Lesys
 *
 */
public class GetPlayerStats {
	/**
	 * String for the current season, starts empty and is filled at the first use of the "https://open-api.bser.io/v2/data/Season" APi request
	 */
	public static String season = "";
	
	/**
	 * Gets the current season (previous season if current is preseason) and put it in the static variable.
	 */
	protected static void getSeason() {
		System.err.println("Getting player stats: ");
		
	    try {
	    	// If season not initialized yet
	    	if (GetPlayerStats.season.equalsIgnoreCase("")) {
				HttpResponse<JsonNode> seasonResponse
				  = apiRequest("https://open-api.bser.io/v2/data/Season");
		
				System.out.println("Status: " + seasonResponse.getStatus());
				System.out.println("Body: " + seasonResponse.getBody());
		
				Map<String, Boolean> seasons = new HashMap<String, Boolean>();
		
				seasonResponse.getBody().getObject().getJSONArray("data").forEach(s -> seasons.put(((JSONObject)s).get("seasonID").toString(), ((JSONObject)s).getInt("isCurrent") == 0 ? false : true));
		//		seasonResponse.getBody().getObject().getJSONArray("data").getJSONObject(seasonResponse.getBody().getObject().getJSONArray("data").length()).get("seasonID");
				GetPlayerStats.season = seasons.keySet().stream().filter(key -> seasons.get(key)).findFirst().orElse("");
				
				// Even seasons are preseasons starting from Early Access Season 2, and we don't want those
				if (Integer.valueOf(GetPlayerStats.season) % 2 == 0) {
					GetPlayerStats.season = String.valueOf(Integer.valueOf(GetPlayerStats.season) - 1);
				}
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
	 * Gets the MMR of the player for the most recent season via the ER API
	 * @param name		Name of the player account
	 * @return			The MMR of the player
	 */
	public static JSONObject getPlayerStats(String name) {
	    try {
	    	GetPlayerStats.getSeason();
	    	
			HttpResponse<JsonNode> jsonResponse 
			  = apiRequest("https://open-api.bser.io/v1/user/nickname?query=" + name);
			
			System.out.println("Status: " + jsonResponse.getStatus());
			System.out.println("Body: " + jsonResponse.getBody());
			
			JSONObject obj = jsonResponse.getBody().getObject();
			
			String userNum = obj.getJSONObject("user").get("userNum").toString();
			
			System.out.println("UserNum: " + userNum);
				

			HttpResponse<JsonNode> rankResponse
			  = apiRequest("https://open-api.bser.io/v1/rank/" + userNum + "/" + GetPlayerStats.season + "/3");

			System.out.println("Status: " + rankResponse.getStatus());
			System.out.println("Body: " + rankResponse.getBody());
//			HttpResponse<JsonNode> statResponse
//			  = apiRequest("https://open-api.bser.io/v1/user/stats/" + userNum + "/" + season);
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
	
	/**
	 * Gets the server distribution for a player on the latest season
	 * @param name	Name of the player account we want to check
	 * @return		A Map object with the servers name on keys and the amount of game on each server on entries
	 */
	public static Map<String, Integer> serverDistribution(String name) {
		try {
			GetPlayerStats.getSeason();
			
			HttpResponse<JsonNode> jsonResponse;
				jsonResponse = apiRequest("https://open-api.bser.io/v1/user/nickname?query=" + name);
			
			System.out.println("Status: " + jsonResponse.getStatus());
			System.out.println("Body: " + jsonResponse.getBody());
			
			JSONObject obj = jsonResponse.getBody().getObject();
			
			String userNum = obj.getJSONObject("user").get("userNum").toString();
			
			ERPlayer player = ERPlayer.getERPlayer(name);
			LocalDateTime date = player.getLastGame() != null ? player.getLastGame().getDateTime() : null;
			
			long next = 0;
			HttpResponse<JsonNode> gamesResponse;
			List<GameLog> gameList = new ArrayList<GameLog>();
			boolean keepGoing = true;
			do {
				gamesResponse
				  = apiRequest("https://open-api.bser.io/v1/user/games/" + userNum + (next != 0 ? "?next=" + next : ""));
	
				System.out.println("Status: " + gamesResponse.getStatus());
				//System.out.println("Body: " + gamesResponse.getBody());
				//System.out.println("Number of games: " + gamesResponse.getBody().getObject().getJSONArray("userGames").length());
				try {
					next = gamesResponse.getBody().getObject().getLong("next");
				} catch (JSONException e) {
					next = 0;
				}
				Iterator<Object> iter = gamesResponse.getBody().getObject().getJSONArray("userGames").iterator();
				
				while (keepGoing && iter.hasNext()) {
					JSONObject o = (JSONObject)iter.next();
					if (o.getInt("matchingMode") == 3) {
						if (date == null || date != null && GetPlayerStats.getLocalDateTime(o.getString("startDtm")).isAfter(date))
							gameList.add(new GameLog(o));
						else
							keepGoing = false;
					}
				}
			} while (keepGoing && gameList.stream().noneMatch(gl -> !String.valueOf(gl.getSeasonId()).equalsIgnoreCase(GetPlayerStats.season)) && next != 0);
			
			player.addGames(gameList);
			List<GameLog> filteredList = player.getAllGames().stream().filter(gl -> String.valueOf(gl.getSeasonId()).equalsIgnoreCase(GetPlayerStats.season)).toList();
			
			Map<String, Integer> servers = new HashMap<String, Integer>();
			filteredList.stream().map(gl -> gl.getServer()).distinct().forEach(server -> servers.put(server, 0));
			servers.keySet().forEach(server -> servers.put(server, (int)filteredList.stream().filter(gl -> server.equalsIgnoreCase(gl.getServer())).count()));
			
			System.out.println("Number of games: " + servers.keySet().stream().map(key -> servers.get(key)).reduce(0, (a, b) -> a + b));
			servers.keySet().forEach(server -> System.out.println(server + " server: " + servers.get(server) + " games"));
			
			Bot.serializePlayers();
			
			return servers;
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new HashMap<String, Integer>();
		}
	}
	
	/**
	 * API request with a delay of 1 second because APIKey can't provide more than 1 request per second
	 * @param url	The URL of the API request
	 * @return		The JSON of the API response
	 * @throws UnirestException
	 */
	public static HttpResponse<JsonNode> apiRequest(String url) throws UnirestException {
		HttpResponse<JsonNode> resp = null;
		try {
			resp =  CompletableFuture.supplyAsync(() -> {
				try {
					return Unirest.get(url)
							  .header("accept", "application/json").header("x-api-key", Token.erApiKey)
							  .asJson();
				} catch (UnirestException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return null;
				}
			}, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)).get();
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return resp;
	}
	
	/**
	 * Changes the format of date retrieved from ER API to a LocalDateTime Object
	 * @param dateString	The String with the date formatted to ER API format
	 * @return				The LocalDateTime Object initialized to the dateString time
	 */
	public static LocalDateTime getLocalDateTime(String dateString) {
		return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
	}
}