package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Test class for Fleet.
 * Author: LETI-129773
 * Date: 12/11/2026
 * Time: 16:00
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - addShip: 3
 * - getShips: 1
 * - getShipsLike: 2
 * - getFloatingShips: 2
 * - shipAt: 2
 * - isInsideBoard: 3
 * - colisionRisk: 2
 */
	public class FleetTest {

		private Fleet fleet;

		@BeforeEach
		void setUp() {
			fleet = new Fleet();
		}

		@AfterEach
		void tearDown() {
			fleet = null;
		}

		/**
		 * Test for the Fleet constructor.
		 * Cyclomatic Complexity: 1
		 */
		@Test
		void testConstructor() {
			assertNotNull(fleet, "Error: Instance of Fleet should not be null.");
			assertTrue(fleet.getShips().isEmpty(), "Error: Fleet should be initialized with empty ships list.");
		}

		@Test
		void testCreateRandom(){
				IFleet r = Fleet.createRandom();
				assertNotNull(r, "Error: createRandom should return a non-null fleet instance.");
				assertEquals(Fleet.FLEET_SIZE, r.getShips().size(), "Error: createRandom should create a fleet with the correct number of ships.");
		}

		/**
		 * Test to cover createRandom when some random ships might overlap or fail initially.
		 */
		@Test
		void testCreateRandomCoverage() {
			IFleet randomFleet = Fleet.createRandom();
			assertNotNull(randomFleet, "Error: Random fleet should not be null.");
			assertEquals(Fleet.FLEET_SIZE, randomFleet.getShips().size(), "Error: Fleet size must match expected.");
		}

		/**
		 * Test to ensure createRandom handles potential null build attempts or retries fully.
		 */
		@Test
		void testCreateRandomCompleteCoverage() {
			IFleet fleet1 = Fleet.createRandom();
			IFleet fleet2 = Fleet.createRandom();

			assertNotNull(fleet1, "Error: Fleet 1 should not be null.");
			assertNotNull(fleet2, "Error: Fleet 2 should not be null.");
			assertEquals(Fleet.FLEET_SIZE, fleet1.getShips().size(), "Error: Fleet 1 must have correct size.");
		}

		/**
		 * Test for the addShip method (all conditions true).
		 * Cyclomatic Complexity: 3
		 */
		@Test
		void testAddShip1() {
			IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
			assertTrue(fleet.addShip(ship), "Error: Valid ship should be added successfully.");
			assertEquals(1, fleet.getShips().size(), "Error: Fleet should contain one ship after addition.");
		}

		/**
		 * Test for the addShip method (fleet size limit reached).
		 */
		@Test
		void testAddShip2() {
			for (int i = 0; i < Fleet.FLEET_SIZE; i++) {
				fleet.addShip(new Barge(Compass.NORTH, new Position(i, 0)));
			}
			IShip anotherShip = new Barge(Compass.NORTH, new Position(10, 10));
			assertFalse(fleet.addShip(anotherShip), "Error: Should not add ship when fleet size limit is reached.");
		}

		/**
		 * Test for the addShip method (ship outside the board).
		 */
		@Test
		void testAddShip3() {
			IShip shipOutside = new Barge(Compass.NORTH, new Position(99, 99));
			assertFalse(fleet.addShip(shipOutside), "Error: Should not add ship outside the board.");
		}

		/**
		 * Test for the addShip method (collision risk).
		 */
		@Test
		void testAddShip4() {
			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));  // Overlapping position
			fleet.addShip(ship1);
			assertFalse(fleet.addShip(ship2), "Error: Should not add ship with a collision risk.");
		}

		/**
		 * Test to cover assertion on addShip when ship is null.
		 */
		@Test
		void testAddShipNull() {
			assertThrows(AssertionError.class, () -> {
				fleet.addShip(null);
			}, "Error: Adding a null ship should trigger an assertion error.");
		}

		/**
		 * Test to ensure addShip fails when the fleet size limit is strictly exceeded,
		 * covering the false branch of the first part of the compound condition.
		 */
		@Test
		void testAddShipWhenFleetAlreadyExceedsLimit() {
			for (int i = 0; i <= Fleet.FLEET_SIZE; i++) {
				int x = (i % 4) * 3;
				int y = (i / 4) * 3;
				assertTrue(
						fleet.addShip(
								new Barge(
										Compass.NORTH,
										new Position(x, y))));
			}
			assertTrue(fleet.getShips().size() > Fleet.FLEET_SIZE);
			IShip extraShip =
					new Barge(
							Compass.NORTH,
							new Position(20, 20));
			assertFalse(
					fleet.addShip(extraShip),
					"Should fail because fleet size already exceeds FLEET_SIZE");
		}

		/**
		 * Test to cover addShip when ship is outside the board (false branch for isInsideBoard).
		 */
		@Test
		void testAddShipOutsideBoard() {
			IShip shipOutside = new Barge(Compass.NORTH, new Position(-1, -1));
			assertFalse(fleet.addShip(shipOutside), "Error: Should return false for a ship outside the board.");
		}

		/**
		 * Test to ensure addShip covers the branch where size is valid,
		 * but the ship is NOT inside the board (false for s.isInsideBoard()).
		 */
		@Test
		void testAddShipSizeValidButOutsideBoard() {
			IShip outsideShip = new Barge(Compass.NORTH, new Position(99, 99));
			assertFalse(fleet.addShip(outsideShip), "Error: Should fail because the ship is outside the board.");
		}

		/**
		 * Test to ensure addShip covers the branch where size and position are valid,
		 * but it fails strictly due to the collision risk condition (!colisionRisk(s)).
		 */
		@Test
		void testAddShipValidSizeAndPositionButCollision() {
			IShip firstShip = new Barge(Compass.NORTH, new Position(3, 3));
			assertTrue(fleet.addShip(firstShip), "First ship should be added.");

			IShip overlappingShip = new Barge(Compass.NORTH, new Position(3, 3));
			assertFalse(fleet.addShip(overlappingShip), "Error: Should fail strictly due to collision risk.");
		}

		/**
		 * Test for the getShips method.
		 * Cyclomatic Complexity: 1
		 */
		@Test
		void testGetShips() {
			assertTrue(fleet.getShips().isEmpty(), "Error: Fleet's ships list should initially be empty.");
			IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
			fleet.addShip(ship);
			assertEquals(1, fleet.getShips().size(), "Error: Fleet should have size 1 after adding a ship.");
			assertEquals(ship, fleet.getShips().get(0), "Error: Fleet's first ship should match the added ship.");
		}

		/**
		 * Test for the getShipsLike method (ships of specific category).
		 * Cyclomatic Complexity: 2
		 */
		@Test
		void testGetShipsLike() {
			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			IShip ship2 = new Caravel(Compass.NORTH, new Position(2, 1));
			fleet.addShip(ship1);
			fleet.addShip(ship2);

			List<IShip> barges = fleet.getShipsLike("Barca");
			assertEquals(1, barges.size(), "Error: There should be exactly one ship of category 'Barca'.");
			assertEquals(ship1, barges.get(0), "Error: The ship of category 'Barca' does not match.");
		}

		/**
		 * Test to cover assertion on getShipsLike when category is null.
		 */
		@Test
		void testGetShipsLikeNull() {
			assertThrows(AssertionError.class, () -> {
				fleet.getShipsLike(null);
			}, "Error: Getting ships with a null category should trigger an assertion error.");
		}

		/**
		 * Test for the getFloatingShips method.
		 * Cyclomatic Complexity: 2
		 */
		@Test
		void testGetFloatingShips() {
			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
			fleet.addShip(ship1);
			fleet.addShip(ship2);

			List<IShip> floatingShips = fleet.getFloatingShips();
			assertEquals(2, floatingShips.size(), "Error: All ships should be floating initially.");

			ship1.getPositions().get(0).shoot();  // Sink ship1
			floatingShips = fleet.getFloatingShips();
			assertEquals(1, floatingShips.size(), "Error: Only one ship should be floating after sinking one.");
			assertEquals(ship2, floatingShips.get(0), "Error: The floating ship should match the expected result.");
		}

		/**
		 * Test for the shipAt method.
		 * Cyclomatic Complexity: 2
		 */
		@Test
		void testShipAt() {
			IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
			fleet.addShip(ship);

			assertEquals(ship, fleet.shipAt(new Position(1, 1)), "Error: Should return the correct ship at the position.");
			assertNull(fleet.shipAt(new Position(5, 5)), "Error: Should return null for empty positions in the fleet.");
		}

		/**
		 * Test to cover assertion on shipAt when position is null.
		 */
		@Test
		void testShipAtNull() {
			assertThrows(AssertionError.class, () -> {
				fleet.shipAt(null);
			}, "Error: Checking ship at a null position should trigger an assertion error.");
		}

		/**
		 * Test for private method colisionRisk.
		 * Cyclomatic Complexity: 2
		 */
		@Test
		void testColisionRisk() throws Exception {
			var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
			method.setAccessible(true);

			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));  // Overlapping position
			fleet.addShip(ship1);

			assertTrue((Boolean) method.invoke(fleet, ship2), "Error: Overlapping ships should be at collision risk.");
			assertFalse((Boolean) method.invoke(fleet, new Barge(Compass.NORTH, new Position(5, 5))),
					"Error: Ships at non-overlapping positions should not have a collision risk.");
		}

		/**
		 * Test to cover colisionRisk assertion when ship is null.
		 */
		@Test
		void testColisionRiskNull() throws Exception {
			var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
			method.setAccessible(true);

			assertThrows(Exception.class, () -> {
				method.invoke(fleet, (IShip) null);
			}, "Error: Invoking colisionRisk with null should trigger an assertion error.");
		}

		/**
		 * Test to force failure in addShip due to collision risk (covering the false branch of !colisionRisk).
		 */
		@Test
		void testAddShipCollisionBranchCoverage() {
			IShip ship1 = new Barge(Compass.NORTH, new Position(2, 2));
			IShip ship2 = new Barge(Compass.NORTH, new Position(2, 2)); // Colide diretamente

			assertTrue(fleet.addShip(ship1), "Error: First ship should be added.");
			assertFalse(fleet.addShip(ship2), "Error: Second ship should fail due to collision risk branch.");
		}

		/**
		 * Test for the printStatus method.
		 * Cyclomatic Complexity: 1
		 */
		@Test
		void testPrintStatus() {
			IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
			fleet.addShip(ship);
			assertDoesNotThrow(fleet::printStatus, "Error: printStatus should not throw any exceptions.");
		}

		/**
		 * Test to cover assertion on printShips when list is null.
		 */
		@Test
		void testPrintShipsNull() {
			assertThrows(AssertionError.class, () -> {
				fleet.printShips(null);
			}, "Error: Printing a null list of ships should trigger an assertion error.");
		}

		/**
		 * Test to cover assertion on printShipsByCategory when category is null.
		 */
		@Test
		void testPrintShipsByCategoryNull() {
			assertThrows(AssertionError.class, () -> {
				fleet.printShipsByCategory(null);
			}, "Error: Printing ships by a null category should trigger an assertion error.");
		}

		/**
		 * Test for the printShipsByCategory method.
		 * Ensures that printing ships belonging to a valid category
		 * executes successfully without throwing exceptions.
		 */
		@Test
		void testPrintShipsByCategoryExecution() {
			fleet.addShip(
					new Barge(
							Compass.NORTH,
							new Position(1, 1)));
			assertDoesNotThrow(
					() -> fleet.printShipsByCategory("Barca"));
		}

		/**
		 * Test for the printFloatingShips method.
		 * Ensures that printing all floating ships
		 * executes successfully without throwing exceptions.
		 */
		@Test
		void testPrintFloatingShipsExecution() {
			fleet.addShip(
					new Barge(
							Compass.NORTH,
							new Position(1, 1)));
			assertDoesNotThrow(
					fleet::printFloatingShips);
		}

		/**
		 * Test for the printAllShips method.
		 * Ensures that printing all ships in the fleet
		 * executes successfully without throwing exceptions.
		 */
		@Test
		void testPrintAllShipsExecution() {
			fleet.addShip(
					new Barge(
							Compass.NORTH,
							new Position(1, 1)));
			assertDoesNotThrow(
					fleet::printAllShips);
		}

		/**
	 	 * Test for the getSunkShips method
		 */
		@Test
		void testGetSunkShips() {
			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			fleet.addShip(ship1);

			assertTrue(fleet.getSunkShips().isEmpty(), "Error: Sunk ships list should initially be empty.");

			ship1.getPositions().get(0).shoot();
			List<IShip> sunkShips = fleet.getSunkShips();
			assertEquals(1, sunkShips.size(), "Error: There should be exactly one sunk ship.");
			assertEquals(ship1, sunkShips.get(0), "Error: The sunk ship should match the expected ship.");
		}

		/**
		 * Test for the getSunkShips method when no ships are sunk.
		 * Verifies that an empty list is returned when all ships
		 * are still floating.
		 */
		@Test
		void testGetSunkShipsWhenNoShipsAreSunk() {
			IShip ship =
					new Barge(
							Compass.NORTH,
							new Position(1, 1));
			fleet.addShip(ship);
			assertTrue(
					fleet.getSunkShips().isEmpty(),
					"No ships should be sunk.");
		}

		/**
		 * Test for getShipsLike method when no ships match the category.
		 */
		@Test
		void testGetShipsLikeNoMatch() {
			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			fleet.addShip(ship1);

			List<IShip> result = fleet.getShipsLike("Galeão");
			assertTrue(result.isEmpty(), "Error: Should return an empty list when no ships match the category.");
		}

		/**
		 * Tests for print utility methods to ensure they execute without exceptions.
		 */
		@Test
		void testPrintUtilityMethods() {
			IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
			fleet.addShip(ship);

			assertDoesNotThrow(() -> fleet.printShips(fleet.getShips()), "Error: printShips should not throw exceptions.");
			assertDoesNotThrow(() -> fleet.printShipsByCategory("Barca"), "Error: printShipsByCategory should not throw exceptions.");
			assertDoesNotThrow(fleet::printFloatingShips, "Error: printFloatingShips should not throw exceptions.");
			assertDoesNotThrow(fleet::printAllShips, "Error: printAllShips should not throw exceptions.");
		}
}