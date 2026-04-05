import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import zzc.discord.evabot.ERPlayer;
import zzc.discord.evabot.Token;

public class ERRPlayerTest {
	
	@BeforeAll
	public static void getTokens() {
		Token.jdaToken = System.getenv("DISCORD_TOKEN");
		Token.erApiKey = System.getenv("ER_API_TOKEN");
	}
	
	@Test
	public void getERPlayerTest() {
		ERPlayer player = ERPlayer.getERPlayer("Lesys");
	}
}
