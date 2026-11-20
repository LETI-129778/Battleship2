package battleship;

import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TasksTest
 *
 * @author LETI-129773
 * Date: 13/11/2026
 * Time: 23:30
 */
class TasksTest {

	private final InputStream standardIn = System.in;

	/**
	 * Inicializa o Toolkit do JavaFX de forma segura para testes unitários.
	 */
	@BeforeAll
	static void initJavaFX() {
		try {
			Platform.startup(() -> {});
		} catch (IllegalStateException e) {

		}
	}

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
		System.setIn(standardIn);
	}

	/**
	 * Test for the menuHelp method to ensure it executes without errors.
	 */
	@Test
	void taskA() {
		assertDoesNotThrow(Tasks::menuHelp, "Error: menuHelp should execute without exceptions.");
	}

	/**
	 * Test for readPosition method using simulated console input.
	 */
	@Test
	void taskB() {
		String simulatedInput = "3 4";
		System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
		Scanner scanner = new Scanner(System.in);

		Position pos = Tasks.readPosition(scanner);
		assertNotNull(pos, "Error: readPosition should return a valid position.");
		assertEquals(3, pos.getRow(), "Error: Row should match.");
		assertEquals(4, pos.getColumn(), "Error: Column should match.");
	}

	/**
	 * Test for readClassicPosition method with standard letter-number format (e.g., "A3").
	 */
	@Test
	void taskC() {
		String simulatedInput = "A3";
		Scanner scanner = new Scanner(simulatedInput);

		IPosition pos = Tasks.readClassicPosition(scanner);
		assertNotNull(pos, "Error: readClassicPosition should return a valid position.");
	}

	/**
	 * Test for readClassicPosition method with space-separated format (e.g., "A 3").
	 */
	@Test
	void taskD() {
		String simulatedInput = "A 3";
		Scanner scanner = new Scanner(simulatedInput);

		IPosition pos = Tasks.readClassicPosition(scanner);
		assertNotNull(pos, "Error: readClassicPosition should handle space-separated format.");
	}

	/**
	 * Test to cover exceptions in readClassicPosition when input is invalid or empty.
	 */
	@Test
	void readClassicPositionInvalid() {
		Scanner scanner = new Scanner("");
		assertThrows(IllegalArgumentException.class, () -> {
			Tasks.readClassicPosition(scanner);
		}, "Error: Should throw IllegalArgumentException when no input is found.");

		Scanner scannerInvalid = new Scanner("1234");
		assertThrows(IllegalArgumentException.class, () -> {
			Tasks.readClassicPosition(scannerInvalid);
		}, "Error: Should throw IllegalArgumentException for invalid format.");
	}

	/**
	 * Test for readClassicPosition with separate letter and number tokens.
	 */
	@Test
	void readClassicPositionSeparateTokens() {
		Scanner scanner = new Scanner("A 3");
		IPosition pos = Tasks.readClassicPosition(scanner);

		assertNotNull(pos, "Error: Should parse separate letter and number correctly.");
	}

	/**
	 * Test for readClassicPosition with separate tokens matching specific row expectations.
	 */
	@Test
	void readClassicPositionSeparateTokensMatch() {
		Scanner scanner = new Scanner("A 3");
		IPosition pos = Tasks.readClassicPosition(scanner);
		assertNotNull(pos, "Position should not be null");
		assertEquals(0, pos.getRow(), "Row should match expected value");
	}

	/**
	 * Test for buildFleet method using simulated input with well-spaced positions.
	 */
	@Test
	void buildFleet() {
		StringBuilder sb = new StringBuilder();
		int totalTentativas = Fleet.FLEET_SIZE * 5;
		for (int i = 0; i < totalTentativas; i++) {
			int linha = (i * 3) % 10;
			int coluna = ((i / 10) * 3) % 10;
			sb.append("barca ").append(linha).append(" ").append(coluna).append(" n\n");
		}

		Scanner scanner = new Scanner(sb.toString());
		Fleet fleet = Tasks.buildFleet(scanner);

		assertNotNull(fleet, "Error: buildFleet should return a valid Fleet instance.");
		assertEquals(Fleet.FLEET_SIZE, fleet.getShips().size(), "Error: Fleet size should match expected.");
	}

	/**
	 * Test for buildFleet when an unknown ship type is provided.
	 */
	@Test
	void buildFleetUnknownShip() {
		StringBuilder sb = new StringBuilder();
		sb.append("navioInvalido 0 0 n\n");

		int totalTentativas = Fleet.FLEET_SIZE * 5;
		for (int i = 0; i < totalTentativas; i++) {
			int linha = (i * 3) % 10;
			int coluna = ((i / 10) * 3) % 10;
			sb.append("barca ").append(linha).append(" ").append(coluna).append(" n\n");
		}

		Scanner scanner = new Scanner(sb.toString());
		Fleet fleet = Tasks.buildFleet(scanner);

		assertNotNull(fleet, "Error: buildFleet should return a fleet even with unknown ships.");
	}

	/**
	 * Test for readShip method using simulated input.
	 */
	@Test
	void readShip() {
		String simulatedInput = "barca 1 1 n";
		Scanner scanner = new Scanner(simulatedInput);

		IShip ship = Tasks.readShip(scanner);
		assertNotNull(ship, "Error: readShip should build and return a valid ship instance.");
		assertEquals("Barca", ship.getCategory(), "Error: Ship category should match.");
	}

	/**
	 * Test for readPosition method directly.
	 */
	@Test
	void readPosition() {
		Scanner scanner = new Scanner("5 7");
		Position pos = Tasks.readPosition(scanner);

		assertNotNull(pos, "Error: Position should not be null.");
		assertEquals(5, pos.getRow(), "Error: Row must be 5.");
		assertEquals(7, pos.getColumn(), "Error: Column must be 7.");
	}

	/**
	 * Test for readPosition assertion when scanner is null.
	 */
	@Test
	void readPositionNull() {
		assertThrows(AssertionError.class, () -> {
			Tasks.readPosition(null);
		}, "Error: Passing null scanner should trigger an assertion error.");
	}

	/**
	 * Test for buildFleet assertion when scanner is null.
	 */
	@Test
	void buildFleetNull() {
		assertThrows(AssertionError.class, () -> {
			Tasks.buildFleet(null);
		}, "Error: Passing null scanner to buildFleet should trigger an assertion error.");
	}

	/**
	 * Test for menu termination right away using the "desisto" command.
	 */
	@Test
	void firingRound() {
		String simulatedInput = "desisto";
		System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

		assertDoesNotThrow(() -> {
			Tasks.menu();
		}, "Error: menu should handle 'desisto' command gracefully and terminate.");
	}

	/**
	 * Test for menu running a sequence of commands to cover game loop branches.
	 */
	@Test
	void testMenuFullSequence() {
		String simulatedInput = "gerafrota\nstatus\nmapa\ntiros\ntempo\najuda\ndesisto\n";
		System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

		assertDoesNotThrow(() -> {
			Tasks.menu();
		}, "Error: menu should handle full command sequence smoothly.");
	}

	/**
	 * Test for menu with 'lefrota' and 'tempo' commands before and after timer initialization.
	 */
	@Test
	void testMenuLeFrotaETempo() {
		StringBuilder sb = new StringBuilder();
		sb.append("tempo\n");
		for (int i = 0; i < Fleet.FLEET_SIZE * 3; i++) {
			int linha = (i * 3) % 10;
			int coluna = ((i / 10) * 3) % 10;
			sb.append("barca ").append(linha).append(" ").append(coluna).append(" n\n");
		}
		sb.append("tempo\ndesisto\n");

		System.setIn(new ByteArrayInputStream(sb.toString().getBytes()));

		assertDoesNotThrow(() -> {
			Tasks.menu();
		}, "Error: menu with lefrota and tempo should execute correctly.");
	}

	/**
	 * Test for menu with 'lefrota' and 'status' commands with timer running.
	 */
	@Test
	void testMenuLeFrotaAndStatusWithTimer() {
		StringBuilder sb = new StringBuilder();
		sb.append("lefrota\n");
		int totalTentativas = Fleet.FLEET_SIZE * 5;
		for (int i = 0; i < totalTentativas; i++) {
			int linha = (i * 3) % 10;
			int coluna = ((i / 10) * 3) % 10;
			sb.append("barca ").append(linha).append(" ").append(coluna).append(" n\n");
		}
		sb.append("status\n");
		sb.append("desisto\n");

		System.setIn(new ByteArrayInputStream(sb.toString().getBytes()));

		assertDoesNotThrow(() -> {
			Tasks.menu();
		}, "Error: menu with lefrota and status should execute successfully.");
	}

	/**
	 * Test for menu mapa command execution when an active fleet exists.
	 */
	@Test
	void testMenuMapaWithFleet() {
		StringBuilder sb = new StringBuilder();
		sb.append("gerafrota\n");
		sb.append("mapa\n");
		sb.append("desisto\n");

		System.setIn(new ByteArrayInputStream(sb.toString().getBytes()));

		assertDoesNotThrow(() -> {
			Tasks.menu();
		}, "Error: menu mapa should execute when fleet exists.");
	}

	/**
	 * Test for menu rajada command execution.
	 * Covers enemy fire reading using exactly three valid positions.
	 */
	@Test
	void testMenuRajada() {
		StringBuilder sb = new StringBuilder();
		sb.append("gerafrota\n");
		sb.append("rajada A1 B2 C3\n");
		sb.append("desisto\n");

		System.setIn(
				new ByteArrayInputStream(
						sb.toString().getBytes()));

		assertDoesNotThrow(
				() -> Tasks.menu(),
				"Error: menu rajada should execute correctly.");
	}

	/**
	 * Test for an unknown command in the menu to cover the default switch branch.
	 */
	@Test
	void testMenuUnknownCommand() {
		String simulatedInput = "comandoInvalido\ndesisto\n";
		System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

		assertDoesNotThrow(() -> {
			Tasks.menu();
		}, "Error: menu should handle unknown commands gracefully.");
	}

	/**
	 * Test for readShip with an unknown ship type.
	 * Verifies that readShip returns null when the ship
	 * type is not recognized by Ship.buildShip.
	 */
	@Test
	void readShipUnknownShip() {

		Scanner scanner =
				new Scanner("invalido 1 1 n");

		Ship ship =
				Tasks.readShip(scanner);

		assertNull(
				ship,
				"Unknown ship type should return null.");
	}

	/**
	 * Test for readShip when the scanner is null.
	 * Verifies that the assertion protecting the method
	 * is triggered correctly.
	 */
	@Test
	void readShipNull() {

		assertThrows(
				AssertionError.class,
				() -> Tasks.readShip(null),
				"Null scanner should trigger assertion.");
	}

	/**
	 * Test for readClassicPosition when scanner is null.
	 * Verifies that the @NotNull contract is enforced.
	 */
	@Test
	void readClassicPositionNull() {

		assertThrows(
				IllegalArgumentException.class,
				() -> Tasks.readClassicPosition(null));
	}

	/**
	 * Test for buildFleet when some ships fail placement.
	 * Covers the branch where addShip returns false
	 * before eventually building a complete fleet.
	 */
	@Test
	void buildFleetFailedPlacement() {

		StringBuilder sb = new StringBuilder();

		sb.append("barca 0 0 n\n");
		sb.append("barca 0 0 n\n");
		sb.append("barca 0 0 n\n");

		for (int i = 0; i < Fleet.FLEET_SIZE * 5; i++) {

			int row = (i * 3) % 10;
			int col = ((i / 10) * 3) % 10;

			sb.append("barca ")
					.append(row)
					.append(" ")
					.append(col)
					.append(" n\n");
		}

		Scanner scanner =
				new Scanner(sb.toString());

		Fleet fleet =
				Tasks.buildFleet(scanner);

		assertNotNull(fleet);
		assertEquals(
				Fleet.FLEET_SIZE,
				fleet.getShips().size());
	}

	/**
	 * Test for the menu tempo command before the timer starts.
	 * Verifies execution of the branch shown when no fleet
	 * has been generated or loaded yet.
	 */
	@Test
	void testMenuTempoWithoutTimer() {

		String input =
				"tempo\n" +
						"desisto\n";

		System.setIn(
				new ByteArrayInputStream(
						input.getBytes()));

		assertDoesNotThrow(
				Tasks::menu);
	}

	/**
	 * Test for the estado command before any fleet exists.
	 * Covers the branch where myFleet is null.
	 */
	@Test
	void testMenuStatusWithoutFleet() {

		String input =
				"estado\n" +
						"desisto\n";

		System.setIn(
				new ByteArrayInputStream(
						input.getBytes()));

		assertDoesNotThrow(
				Tasks::menu);
	}

	/**
	 * Test for the mapa command before any fleet exists.
	 * Covers the branch where myFleet is null.
	 */
	@Test
	void testMenuMapaWithoutFleet() {

		String input =
				"mapa\n" +
						"desisto\n";

		System.setIn(
				new ByteArrayInputStream(
						input.getBytes()));

		assertDoesNotThrow(
				Tasks::menu);
	}

	/**
	 * Test for the tiros command before a game exists.
	 * Covers the branch where game is null.
	 */
	@Test
	void testMenuTirosWithoutGame() {

		String input =
				"tiros\n" +
						"desisto\n";

		System.setIn(
				new ByteArrayInputStream(
						input.getBytes()));

		assertDoesNotThrow(
				Tasks::menu);
	}

	/**
	 * Test for the rajada command before a game exists.
	 * Covers the branch where game is null.
	 */
	@Test
	void testMenuRajadaWithoutGame() {

		String input =
				"rajada\n" +
						"desisto\n";

		System.setIn(
				new ByteArrayInputStream(
						input.getBytes()));

		assertDoesNotThrow(
				Tasks::menu);
	}

	/**
	 * Test for the simula command before a game exists.
	 * Covers the branch where game is null.
	 */
	@Test
	void testMenuSimulaWithoutGame() {

		String input =
				"simula\n" +
						"desisto\n";

		System.setIn(
				new ByteArrayInputStream(
						input.getBytes()));

		assertDoesNotThrow(
				Tasks::menu);
	}

	/**
	 * Test for readClassicPosition using separated
	 * column and row tokens.
	 */
	@Test
	void readClassicPositionElseIfBranchCoverage() {

		Scanner scanner =
				new Scanner("B 7");

		IPosition pos =
				Tasks.readClassicPosition(scanner);

		assertNotNull(
				pos,
				"Position should be successfully parsed.");
	}

	/**
	 * Covers the branch:
	 * part2 != null &&
	 * part1.matches("[A-Z]") &&
	 * part2.matches("\\d+")
	 */
	@Test
	void readClassicPositionExplicitElseIfBranch() {

		Scanner scanner = new Scanner("C 8");

		IPosition pos =
				Tasks.readClassicPosition(scanner);

		assertNotNull(pos);
	}

	/**
	 * Covers the explicit else-if branch of readClassicPosition.
	 */
	@Test
	void readClassicPositionExplicitElseIfReturn() {

		Scanner scanner = new Scanner("Z 9");

		IPosition pos =
				Tasks.readClassicPosition(scanner);

		assertNotNull(pos);
	}

	/**
	 * Covers lowercase separated coordinates.
	 */
	@Test
	void readClassicPositionLowerCaseElseIf() {

		Scanner scanner = new Scanner("a 3");

		IPosition pos =
				Tasks.readClassicPosition(scanner);

		assertNotNull(pos);
	}

	/**
	 * Test for status command after timer initialization.
	 * Covers the branch where cronometroIniciado is true.
	 */
	@Test
	void testStatusAfterGerafrota() {

		String input =
				"gerafrota\n" +
						"estado\n" +
						"desisto\n";

		System.setIn(
				new ByteArrayInputStream(
						input.getBytes()));

		assertDoesNotThrow(
				Tasks::menu);
	}

	/**
	 * Test for readClassicPosition with invalid second token.
	 * Covers the branch where the second token is not numeric.
	 */
	@Test
	void readClassicPositionInvalidSecondToken() {

		Scanner scanner = new Scanner("A X");

		assertThrows(
				IllegalArgumentException.class,
				() -> Tasks.readClassicPosition(scanner));
	}

	/**
	 * Test for readClassicPosition with invalid first token.
	 * Covers the branch where the first token is not a valid column.
	 */
	@Test
	void readClassicPositionInvalidFirstToken() {

		Scanner scanner = new Scanner("11 3");

		assertThrows(
				IllegalArgumentException.class,
				() -> Tasks.readClassicPosition(scanner));
	}

	/**
	 * Test for atualizarInterfaceGrafica using reflection.
	 * Covers the branch where game.getMyMoves() contains a move
	 * with a shot that missed a ship.
	 */
	@Test
	void testAtualizarInterfaceGraficaMyMovesWaterShot() throws Exception {

		Fleet fleet = new Fleet();

		fleet.addShip(
				new Barge(
						Compass.NORTH,
						new Position(1, 1)));

		Game game = new Game(fleet);

		List<IPosition> shots = new ArrayList<>();
		shots.add(new Position(5, 5));

		List<IGame.ShotResult> results = new ArrayList<>();
		results.add(
				new IGame.ShotResult(
						true,
						false,
						null,
						false));

		Move move =
				new Move(
						1,
						shots,
						results);

		game.getMyMoves().add(move);

		var method =
				Tasks.class.getDeclaredMethod(
						"atualizarInterfaceGrafica",
						IGame.class,
						IFleet.class);

		method.setAccessible(true);

		assertDoesNotThrow(
				() -> method.invoke(
						null,
						game,
						fleet));
	}

	/**
	 * Test for atualizarInterfaceGrafica using reflection.
	 * Covers the branch where a shot hits a ship.
	 */
	@Test
	void testAtualizarInterfaceGraficaMyMovesHitShot() throws Exception {

		Fleet fleet = new Fleet();

		IShip hitShip =
				new Barge(
						Compass.NORTH,
						new Position(1, 1));

		fleet.addShip(hitShip);

		Game game = new Game(fleet);

		List<IPosition> shots = new ArrayList<>();
		shots.add(new Position(1, 1));

		List<IGame.ShotResult> results = new ArrayList<>();
		results.add(
				new IGame.ShotResult(
						true,
						false,
						hitShip,
						false));

		Move move =
				new Move(
						1,
						shots,
						results);

		game.getMyMoves().add(move);

		var method =
				Tasks.class.getDeclaredMethod(
						"atualizarInterfaceGrafica",
						IGame.class,
						IFleet.class);

		method.setAccessible(true);

		assertDoesNotThrow(
				() -> method.invoke(
						null,
						game,
						fleet));
	}

	@Test
	void testAtualizarInterfaceGraficaHitAndMiss() throws Exception {

		Fleet fleet = new Fleet();

		IShip ship =
				new Barge(
						Compass.NORTH,
						new Position(1, 1));

		fleet.addShip(ship);

		Game game = new Game(fleet);

		List<IPosition> shots =
				new ArrayList<>();

		shots.add(new Position(1, 1));
		shots.add(new Position(5, 5));

		List<IGame.ShotResult> results =
				new ArrayList<>();

		results.add(
				new IGame.ShotResult(
						true,
						false,
						ship,
						false));

		results.add(
				new IGame.ShotResult(
						true,
						false,
						null,
						false));

		Move move =
				new Move(
						1,
						shots,
						results);

		game.getMyMoves().add(move);

		var method =
				Tasks.class.getDeclaredMethod(
						"atualizarInterfaceGrafica",
						IGame.class,
						IFleet.class);

		method.setAccessible(true);

		method.invoke(
				null,
				game,
				fleet);
	}

}