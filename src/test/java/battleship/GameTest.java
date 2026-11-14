package battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Game.
 *
 * Author: britoeabreu
 * Date: 2024-03-19
 * Time: 15:30
 */
public class GameTest {

	private Game game;

	@BeforeEach
	void setUp() {
		game = new Game(new Fleet());
	}

	@AfterEach
	void tearDown() {
		game = null;
	}

	@Test
	void constructor() {
		assertNotNull(game, "Game instance should not be null after construction.");
		assertNotNull(game.getAlienMoves(), "Shots list should not be null after initialization.");
		assertTrue(game.getAlienMoves().isEmpty(), "Shots list should be empty upon initialization.");
		assertEquals(0, game.getInvalidShots(), "Invalid shots count should be zero upon initialization.");
		assertEquals(0, game.getRepeatedShots(), "Repeated shots count should be zero upon initialization.");
		assertEquals(0, game.getHits(), "Hits count should be zero upon initialization.");
		assertEquals(0, game.getSunkShips(), "Sunk ships count should be zero upon initialization.");
	}

	@Test
	void fire2() {
		Position invalidPosition = new Position(-1, 5);
		game.fireSingleShot(invalidPosition, false);
		assertEquals(1, game.getInvalidShots(), "Invalid shots counter should increase for an invalid shot.");
	}

	@Test
	void fire3() {
		Position position = new Position(2, 3);
		game.fireSingleShot(position, false);
		game.fireSingleShot(position, true);
		assertEquals(1, game.getRepeatedShots(), "Repeated shots counter should increase for a repeated shot.");
	}

	@Test
	void repeatedShot1() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		Position position = new Position(2, 3);
		assertTrue(game.repeatedShot(position), "Position (2,3) should be marked as repeated after firing.");
	}

	@Test
	void repeatedShot2() {
		Position position = new Position(2, 3);
		assertFalse(game.repeatedShot(position), "Position (2,3) should not be marked as repeated before firing.");
	}

	@Test
	void getAlienMoves() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		assertEquals(1, game.getAlienMoves().size(), "Shots list should contain one shot after firing once.");
	}

	@Test
	void getRemainingShips() {
		IFleet fleet = game.getMyFleet();
		Ship ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		Ship ship2 = new Frigate(Compass.EAST, new Position(5, 5));

		fleet.addShip(ship1);
		assertEquals(1, game.getRemainingShips(), "Just one ship was created!");
		fleet.addShip(ship2);
		assertEquals(2, game.getRemainingShips(), "Two ships were created!");
		ship2.sink();
		assertEquals(1, game.getRemainingShips(), "Remaining ships count should be 1 after sinking one of two ships.");
	}

	@Test
	void getAlienFleetAndGetMyMovesTest() {
		assertNotNull(game.getAlienFleet(), "Alien fleet should not be null.");
		assertNotNull(game.getMyMoves(), "My moves list should not be null.");
	}

	@Test
	void printBoardMethodsAndOverTest() {
		List<IPosition> positions = List.of(new Position(0, 0), new Position(0, 1), new Position(0, 2));
		game.fireShots(positions);

		assertDoesNotThrow(() -> {
			game.printMyBoard(true, true);
			game.printMyBoard(false, false);
			game.printAlienBoard(true, true);
			game.printAlienBoard(false, false);
			game.over();
		}, "Printing boards and game over banner should execute without exception.");
	}

	@Test
	void jsonShotsSerializationTest() {
		List<IPosition> positions = List.of(new Position(1, 1));
		String json = Game.jsonShots(positions);
		assertNotNull(json, "Serialized JSON string should not be null.");
		assertTrue(json.contains("row"), "JSON output should contain 'row'.");
		assertTrue(json.contains("column"), "JSON output should contain 'column'.");
	}

	@Test
	void randomEnemyFireTest() {
		String json = game.randomEnemyFire();
		assertNotNull(json, "Random enemy fire should return a serialized JSON string.");
	}

	@Test
	void readEnemyFireValidCombinedTest() {
		Scanner scanner = new Scanner("A1 B2 C3");
		String json = game.readEnemyFire(scanner);
		assertNotNull(json, "Read enemy fire with combined tokens should succeed.");
	}

	@Test
	void readEnemyFireValidSeparateTest() {
		Scanner scanner = new Scanner("A 1 B 2 C 3");
		String json = game.readEnemyFire(scanner);
		assertNotNull(json, "Read enemy fire with separate column/row tokens should succeed.");
	}

	@Test
	void readEnemyFireIncompletePositionExceptionTest() {
		Scanner scanner = new Scanner("A invalid B2 C3");
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(scanner),
				"Should throw IllegalArgumentException when column is not followed by a row.");
	}

	@Test
	void readEnemyFireWrongShotCountExceptionTest() {
		Scanner scanner = new Scanner("A1");
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(scanner),
				"Should throw IllegalArgumentException when shot count does not match NUMBER_SHOTS.");
	}

	@Test
	void fireShotsWrongSizeExceptionTest() {
		List<IPosition> invalidSize = List.of(new Position(0, 0));
		assertThrows(IllegalArgumentException.class, () -> game.fireShots(invalidSize),
				"Should throw IllegalArgumentException when firing incorrect number of shots.");
	}

	@Test
	void fireSingleShotHitAndSinkTest() {
		IFleet fleet = game.getMyFleet();
		Ship barge = new Barge(Compass.NORTH, new Position(3, 3));
		fleet.addShip(barge);

		game.fireSingleShot(new Position(3, 3), false);
		assertEquals(1, game.getHits(), "Hits count should increment to 1.");
		assertEquals(1, game.getSunkShips(), "Sunk ships count should increment to 1 after sinking barge.");
	}

	@Test
	void printShipsSunkShipAdjacentTest() {
		IFleet fleet = game.getMyFleet();
		Ship barge = new Barge(Compass.NORTH, new Position(3, 3));
		fleet.addShip(barge);
		barge.sink();

		assertDoesNotThrow(() -> game.printMyBoard(true, true),
				"Printing board with a sunk ship should render adjacent markers correctly.");
	}

	@Test
	void printBoardShotsHitShipMarkerTest() {
		IFleet fleet = game.getMyFleet();
		Ship barge = new Barge(Compass.NORTH, new Position(4, 4));
		fleet.addShip(barge);

		List<IPosition> positions = List.of(new Position(4, 4), new Position(0, 0), new Position(0, 1));
		game.fireShots(positions);

		assertDoesNotThrow(() -> game.printMyBoard(true, false),
				"Printing board showing shots hitting ships should execute successfully.");
	}

	@Test
	void printBoardShotsAdjacentMarkerHitTest() {
		IFleet fleet = game.getMyFleet();
		Ship barge = new Barge(Compass.NORTH, new Position(3, 3));
		fleet.addShip(barge);
		barge.sink();

		List<IPosition> positions = List.of(new Position(2, 3), new Position(0, 0), new Position(0, 1));
		game.fireShots(positions); // (Note: keeping your exact method calls)

		assertDoesNotThrow(() -> game.printMyBoard(true, false),
				"Printing board hitting adjacent marker should execute successfully.");
	}

	@Test
	void randomEnemyFireElseBranchTest() {
		List<IPosition> batch = new ArrayList<>();
		int totalFired = 0;
		// Fire 99 shots out of 100 to leave exactly 1 candidate shot (< NUMBER_SHOTS i.e., < 3)
		for (int r = 0; r < Game.BOARD_SIZE; r++) {
			for (int c = 0; c < Game.BOARD_SIZE; c++) {
				if (totalFired < 99) {
					batch.add(new Position(r, c));
					totalFired++;
					if (batch.size() == Game.NUMBER_SHOTS) {
						game.fireShots(batch);
						batch = new ArrayList<>();
					}
				}
			}
		}
		String json = game.randomEnemyFire();
		assertNotNull(json, "Random enemy fire when candidateShots < NUMBER_SHOTS should execute.");
	}



	@Test
	void testAssertionsFailures() {
		assertThrows(AssertionError.class, () -> Game.printBoard(null, new ArrayList<>(), true, true));
		assertThrows(AssertionError.class, () -> Game.printBoard(new Fleet(), null, true, true));
		assertThrows(AssertionError.class, () -> Game.jsonShots(null));
		assertThrows(AssertionError.class, () -> game.readEnemyFire(null));
		assertThrows(AssertionError.class, () -> game.fireShots(null));
		assertThrows(AssertionError.class, () -> game.fireSingleShot(null, false));
		assertThrows(AssertionError.class, () -> game.repeatedShot(null));
	}
}