package basictest;

import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DeckOfCardsTest {
//	RestAssured.baseURI;
	public String newDeckId;

	@BeforeClass
	public void startTest() {
		System.out.println("Starting test class");
		RestAssured.baseURI = "https://www.deckofcardsapi.com/"; // Set the base URI
	}

	@AfterClass
	public void endTest() {
		System.out.println("Ending test class");
	}

	@Test(enabled = true)
	public void verifyHostIsUp() {

		// Verify host is returns 200
		String responseString = given().when().get("").then().assertThat().statusCode(200).extract().response()
				.asString();

		System.out.println("Host is up with status: ");
	}

	@Test(enabled = true, dependsOnMethods = { "verifyHostIsUp" })
	public void getANewDeck() {

		String responseString = given().when().get("/api/deck/new/").then().assertThat().statusCode(200)
				.body("success", equalTo(true)).extract().response().asString();

		JsonPath jsonResponse = new JsonPath(responseString);
		newDeckId = jsonResponse.getString("deck_id");
		
		System.out.println("New Deck_Id :" + newDeckId);
	}

	@Test(enabled = true, dependsOnMethods = { "getANewDeck" })
	public void shuffleTheDeck() {

		newDeckId = "1ju3jkap5zhi";
		// Verify host is returns 200
		String responseString = given().when().get("/api/deck/" + newDeckId + "/shuffle/").then().assertThat()
				.statusCode(200).extract().response().asString();

		JsonPath jsonResponse = new JsonPath(responseString);

		System.out.println(responseString);
	}

	@Test(enabled = true, dependsOnMethods = { "shuffleTheDeck" })
	public void drawFromDeck() {

		int playerCount = 2;
		int drawCount = 3;

		String[] playerCards = new String[playerCount];

		for (int i = 0; i < playerCount; i++) {
			for (int j = 0; j < drawCount; j++) {

				String responseString = given().when().get("/api/deck/" + newDeckId + "/draw/?count=1").then()
						.assertThat().statusCode(200).extract().response().asString();

				JsonPath jsonResponse = new JsonPath(responseString);
				playerCards[i] = playerCards[i] != null
						? playerCards[i] + jsonResponse.getString("cards[0].value") + "|"
						: jsonResponse.getString("cards[0].value") + "|";

			}

			System.out.println("Player " + (i + 1) + " cards: " + playerCards[i]);

			if (playerCards[i].contains("ACE") && (playerCards[i].contains("JACK") || playerCards[i].contains("KING")
					|| playerCards[i].contains("QUEEN") || playerCards[i].contains("10"))) {

				System.out.println("Player: " + (i + 1) + " has blackjack 😀");
			}
		}
	}

}
