import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.GameLog;
import zzc.discord.evabot.GetPlayerStats;
import zzc.discord.evabot.MatchingMode;
import zzc.discord.evabot.Token;
import zzc.discord.evabot.exception.UserIdNotLinkedException;
import zzc.discord.evabot.util.UtilEmpty;

public class GetPlayerStatTest {
	
	@BeforeAll
	public static void getTokens() {
		Token.jdaToken = System.getenv("DISCORD_TOKEN");
		Token.erApiKey = System.getenv("ER_API_TOKEN");
	}
	
	// Unit Test
	
	@Test
	public void getSeasonTest() {
		GetPlayerStats.retrieveSeason();
		
		// Season 10 ROYAL is season 37
		assertEquals(GetPlayerStats.getSeason(), "37");
	}
	
	@Test
	public void getCharactersTest() {
		GetPlayerStats.retrieveCharacters();
		
		assert(!UtilEmpty.isEmptyOrNull(GetPlayerStats.characters));
	}
	
	@Test
	public void getUserIdTest() {
		String userId = GetPlayerStats.getUserId("Lesys");
		
		assert(!UtilEmpty.isEmptyOrNull(userId));
	}
	
	@Test
	@NullSource
	public void getCommonGamesEmptyPlayerTest(String playerName) {
		List<GameLog> list = GetPlayerStats.commonGames("Lesys", playerName, MatchingMode.RANKED);
		
		assert(UtilEmpty.isEmptyOrNull(list));
	}
	
	@Test
	@ValueSource(strings = {"Baumi"})
	public void getCommonGamesTest(String playerName) {
		List<GameLog> list = GetPlayerStats.commonGames("Lesys", playerName, MatchingMode.RANKED);
		
		assert(!UtilEmpty.isEmptyOrNull(list));
	}
	
	// Integration Test
	
	@Test
	public void getPlayerStatsUserIdTest() {
		String userId = GetPlayerStats.getUserId("Lesys");
		JSONObject body = null;
		
		try {
			body = GetPlayerStats.getPlayerStatsByUserId(userId);
			
			assert(body != null);
		} catch (UserIdNotLinkedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assert(false);
		}
	}
	
	@Test
	public void getPlayerStatsERPlayerTest() {
		ERPlayer player = ERPlayer.getERPlayer("Lesys");
		JSONObject body = null;
		
		body = GetPlayerStats.getPlayerStats(player);
		
		assert(body != null);		
	}
}
