package battleship;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Ship.
 */
public class ShipTest {

    private Ship ship, shipBorder;

    @BeforeEach
    void setUp() {
        ship = new Barge(Compass.NORTH, new Position(5, 5));
        shipBorder = new Barge(Compass.NORTH, new Position(0, 0));
    }

    @AfterEach
    void tearDown() {
        ship = null;
        shipBorder = null;
    }

    @Test
    void testConstructor() {
        assertNotNull(ship);
        assertEquals("Barca", ship.getCategory());
        assertEquals(Compass.NORTH, ship.getBearing());
        assertEquals(1, ship.getSize());
        assertFalse(ship.getPositions().isEmpty());
    }

    @Test
    void testGetCategory() {
        assertEquals("Barca", ship.getCategory());
    }

    @Test
    void testGetSize() {
        assertEquals(1, ship.getSize());
    }

    @Test
    void testGetBearing() {
        assertEquals(Compass.NORTH, ship.getBearing());
    }

    @Test
    void testGetPositions() {
        List<IPosition> positions = ship.getPositions();

        assertNotNull(positions);
        assertEquals(1, positions.size());
        assertEquals(5, positions.get(0).getRow());
        assertEquals(5, positions.get(0).getColumn());
    }

    @Test
    void testStillFloating1() {
        assertTrue(ship.stillFloating());
    }

    @Test
    void testStillFloating2() {
        ship.getPositions().get(0).shoot();

        assertFalse(ship.stillFloating());
    }

    @Test
    void testStillFloatingWithSomePositionsHit() {
        Ship multiShip = new Frigate(Compass.NORTH, new Position(5, 5));

        multiShip.getPositions().get(0).shoot();

        assertTrue(multiShip.stillFloating());
    }

    @Test
    void testShoot1() {
        Position target = new Position(5, 5);

        ship.shoot(target);

        assertTrue(ship.getPositions().get(0).isHit());
    }

    @Test
    void testShoot2() {
        Position target = new Position(0, 0);

        ship.shoot(target);

        assertFalse(ship.getPositions().get(0).isHit());
    }

    @Test
    void testShootAtNonMatchingPosition() {
        Ship multiShip = new Frigate(Compass.NORTH, new Position(5, 5));

        multiShip.shoot(new Position(5, 6));

        for (IPosition position : multiShip.getPositions()) {
            assertFalse(position.isHit());
        }
    }

    @Test
    void testShootAtEveryPosition() {
        Ship multiShip = new Frigate(Compass.NORTH, new Position(5, 5));

        for (IPosition position : multiShip.getPositions()) {
            multiShip.shoot(position);
        }

        for (IPosition position : multiShip.getPositions()) {
            assertTrue(position.isHit());
        }
    }

    @Test
    void testSink() {
        ship.sink();

        assertTrue(ship.getPositions().get(0).isHit());
    }

    @Test
    void testOccupies1() {
        Position pos = new Position(5, 5);

        assertTrue(ship.occupies(pos));
    }

    @Test
    void testOccupies2() {
        Position pos = new Position(1, 1);

        assertFalse(ship.occupies(pos));
    }

    @Test
    void testTooCloseToShip1() {
        Ship nearbyShip = new Barge(Compass.NORTH, new Position(5, 6));

        assertTrue(ship.tooCloseTo(nearbyShip));
    }

    @Test
    void testTooCloseToShip2() {
        Ship farShip = new Barge(Compass.NORTH, new Position(10, 10));

        assertFalse(ship.tooCloseTo(farShip));
    }

    @Test
    void testTooCloseToShipWithEmptyPositions() {
        Ship emptyShip = new Barge(Compass.NORTH, new Position(10, 10));
        emptyShip.getPositions().clear();

        assertFalse(ship.tooCloseTo(emptyShip));
    }

    @Test
    void testTooCloseToShipWithLaterPositionAdjacent() {
        Ship otherShip = new Galleon(
                Compass.EAST,
                new Position(5, 8)
        );

        assertTrue(ship.tooCloseTo(otherShip));
    }

    @Test
    void testTooCloseToPosition1() {
        Position pos = new Position(5, 6);

        assertTrue(ship.tooCloseTo(pos));
    }

    @Test
    void testTooCloseToPosition2() {
        Position pos = new Position(7, 7);

        assertFalse(ship.tooCloseTo(pos));
    }

    @Test
    void testTooCloseToPositionWithMultiplePositions() {
        Ship multiShip = new Frigate(Compass.NORTH, new Position(5, 5));

        assertTrue(multiShip.tooCloseTo(new Position(6, 6)));
        assertFalse(multiShip.tooCloseTo(new Position(10, 10)));
    }

    @Test
    void testGetTopMostPos() {
        assertEquals(5, ship.getTopMostPos());
    }

    @Test
    void testGetBottomMostPos() {
        assertEquals(5, ship.getBottomMostPos());
    }

    @Test
    void testGetLeftMostPos() {
        assertEquals(5, ship.getLeftMostPos());
    }

    @Test
    void testGetRightMostPos() {
        assertEquals(5, ship.getRightMostPos());
    }

    @Test
    void testGetExtremePositionsWithMultiplePositions() {
        Ship multiShip = new Frigate(Compass.NORTH, new Position(5, 5));

        assertEquals(5, multiShip.getTopMostPos());
        assertEquals(8, multiShip.getBottomMostPos());
        assertEquals(5, multiShip.getLeftMostPos());
        assertEquals(5, multiShip.getRightMostPos());
    }

    @Test
    void testGetExtremePositionsWithHorizontalShip() {
        Ship multiShip = new Frigate(Compass.EAST, new Position(5, 5));

        assertEquals(5, multiShip.getTopMostPos());
        assertEquals(5, multiShip.getBottomMostPos());
        assertEquals(5, multiShip.getLeftMostPos());
        assertEquals(8, multiShip.getRightMostPos());
    }

    @Test
    void testIsInsideBoard() {
        assertTrue(ship.isInsideBoard());
        assertTrue(shipBorder.isInsideBoard());

        Ship outsideShip = new Barge(Compass.NORTH, new Position(-1, 0));

        assertFalse(outsideShip.isInsideBoard());
    }

    @Test
    void testIsInsideBoardWithShipAtRightBoundary() {
        Ship inside = new Frigate(
                Compass.EAST,
                new Position(5, Game.BOARD_SIZE - 4)
        );

        assertTrue(inside.isInsideBoard());
    }

    @Test
    void testIsInsideBoardOutsideRightBoundary() {
        Ship outside = new Frigate(
                Compass.EAST,
                new Position(5, Game.BOARD_SIZE - 3)
        );

        assertFalse(outside.isInsideBoard());
    }

    @Test
    void testIsInsideBoardOutsideBottomBoundary() {
        Ship outside = new Frigate(
                Compass.NORTH,
                new Position(Game.BOARD_SIZE - 3, 5)
        );

        assertFalse(outside.isInsideBoard());
    }

    @Test
    void testToString() {
        assertEquals(
                "[Barca n " + ship.getPosition() + "]",
                ship.toString()
        );
    }

    @Test
    void testBuildShip() {
        Position pos = new Position(5, 5);

        assertInstanceOf(Barge.class,
                Ship.buildShip("barca", Compass.NORTH, pos));

        assertInstanceOf(Caravel.class,
                Ship.buildShip("caravela", Compass.NORTH, pos));

        assertInstanceOf(Carrack.class,
                Ship.buildShip("nau", Compass.NORTH, pos));

        assertInstanceOf(Frigate.class,
                Ship.buildShip("fragata", Compass.NORTH, pos));

        assertInstanceOf(Galleon.class,
                Ship.buildShip("galeao", Compass.NORTH, pos));

        assertNull(Ship.buildShip("invalid", Compass.NORTH, pos));
    }

    @Test
    void testGetAdjacentPositions() {
        assertEquals(
                8,
                ship.getAdjacentPositions().size()
        );

        assertEquals(
                3,
                shipBorder.getAdjacentPositions().size()
        );
    }

    @Test
    void testGetAdjacentPositionsExcludesOccupiedAndDuplicates() {
        Ship multiPositionShip =
                new Caravel(Compass.NORTH, new Position(5, 5));

        IPosition occupied =
                multiPositionShip.getPositions().get(0);

        List<IPosition> result =
                multiPositionShip.getAdjacentPositions();

        assertFalse(result.contains(occupied));

        assertEquals(
                result.size(),
                result.stream().distinct().count()
        );
    }

    @Test
    void testGetTopMostPosWhenLaterPositionIsHigher() {
        Ship testShip = new Galleon(Compass.NORTH, new Position(5, 5));

        // Make the first position lower than the other positions.
        testShip.getPositions().set(0, new Position(10, 10));

        assertEquals(5, testShip.getTopMostPos());
    }

    @Test
    void testGetLeftMostPosWhenLaterPositionIsFurtherLeft() {
        Ship testShip = new Galleon(
                Compass.EAST,
                new Position(5, 8)
        );

        assertEquals(6, testShip.getLeftMostPos());
    }

    // Additional tests for assertion branches

    @Test
    void testBuildShipWithNullShipKind() {
        assertThrows(AssertionError.class, () ->
                Ship.buildShip(null, Compass.NORTH, new Position(5, 5))
        );
    }

    @Test
    void testBuildShipWithNullBearing() {
        assertThrows(AssertionError.class, () ->
                Ship.buildShip("barca", null, new Position(5, 5))
        );
    }

    @Test
    void testBuildShipWithNullPosition() {
        assertThrows(AssertionError.class, () ->
                Ship.buildShip("barca", Compass.NORTH, null)
        );
    }

    @Test
    void testOccupiesWithNullPosition() {
        assertThrows(AssertionError.class, () ->
                ship.occupies(null)
        );
    }

    @Test
    void testTooCloseToShipWithNull() {
        assertThrows(AssertionError.class, () ->
                ship.tooCloseTo((IShip) null)
        );
    }

    @Test
    void testTooCloseToPositionWithNull() {
        assertThrows(AssertionError.class, () ->
                ship.tooCloseTo((IPosition) null)
        );
    }

    @Test
    void testShootWithNullPosition() {
        assertThrows(AssertionError.class, () ->
                ship.shoot(null)
        );
    }

    @Test
    void testShootWithPositionOutsideBoard() {
        Position outside = new Position(-1, 5);

        assertThrows(AssertionError.class, () ->
                ship.shoot(outside)
        );
    }

    // Additional tests for isInsideBoard condition branches

    @Test
    void testIsInsideBoardOutsideLeftBoundary() {
        Ship outside = new Barge(
                Compass.NORTH,
                new Position(5, -1)
        );

        assertFalse(outside.isInsideBoard());
    }

    @Test
    void testIsInsideBoardOutsideRightBoundaryWithBarge() {
        Ship outside = new Barge(
                Compass.NORTH,
                new Position(5, Game.BOARD_SIZE)
        );

        assertFalse(outside.isInsideBoard());
    }

    @Test
    void testIsInsideBoardOutsideTopBoundaryWithBarge() {
        Ship outside = new Barge(
                Compass.NORTH,
                new Position(-1, 5)
        );

        assertFalse(outside.isInsideBoard());
    }

    @Test
    void testIsInsideBoardOutsideBottomBoundaryWithBarge() {
        Ship outside = new Barge(
                Compass.NORTH,
                new Position(Game.BOARD_SIZE, 5)
        );

        assertFalse(outside.isInsideBoard());
    }
}