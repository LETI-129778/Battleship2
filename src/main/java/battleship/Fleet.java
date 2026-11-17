/**
 * 
 */
package battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * The type Fleet.
 */
public class Fleet implements IFleet
{
	// [REFACTORING] Local: Fleet | Cheiro: Duplicated Code (strings "caravela" e "barca") | Refatoração: Introduce Constant
	private static final String GALEAO = "galeao";
	private static final String FRAGATA = "fragata";
	private static final String NAU = "nau";
	private static final String CARAVELA = "caravela";
	private static final String BARCA = "barca";
	/**
	 * Creates a randomly generated fleet containing ships of various predefined types.
	 * Each ship is assigned a random bearing and position. If a ship cannot be added
	 * due to constraints (e.g., collision or boundary issues), it will be retried.
	 *
	 * @return a fully constructed and valid fleet as an instance of IFleet
	 */
	public static IFleet createRandom() {

		IFleet randomFleet = new Fleet();

		// Define the types of ships to be added
		String[] shipTypes =
				{GALEAO,                       // 1 galleon
						FRAGATA,                      // 1 frigate
						NAU, NAU,                     // 2 carracks
						CARAVELA, CARAVELA, CARAVELA, // 3 caravels
						BARCA, BARCA, BARCA, BARCA};  // 4 barges

		int fleetSize = 0;

		while (fleetSize < shipTypes.length) {

			// Build the ship
			Ship ship = Ship.buildShip(shipTypes[fleetSize], Compass.randomBearing(), Position.randomPosition());

			// Attempt to add the ship to the fleet
			if (ship != null && randomFleet.addShip(ship)) {
				fleetSize++; // Increment count if ship is successfully added
			}
		}
		return randomFleet;
	}


    // -----------------------------------------------------

	/**
	 * The Ships.
	 */
	private final List<IShip> ships;

	// -----------------------------------------------------ge
	/**
	 * Instantiates a new Fleet.
	 */
	public Fleet()
    {
	ships = new ArrayList<>();
    }

	/**
	 * Gets ships.
	 *
	 * @return the ships
	 */
	@Override
    public List<IShip> getShips()
    {
	return ships;
    }
	
	/**
	 * Add ship boolean.
	 *
	 * @param s the s
	 * @return the boolean
	 */
	/*
     * (non-Javadoc)
     * 
     * @see battleship.IFleet#addShip(battleship.IShip)
     */
    @Override
    public boolean addShip(IShip s)
    {
		assert s != null;

		boolean result = false;
		if ((ships.size() <= FLEET_SIZE) && (s.isInsideBoard()) && (!colisionRisk(s)))
		{
			ships.add(s);
			result = true;
		}
		return result;
    }

	/**
	 * Gets ships like.
	 *
	 * @param category the category
	 * @return the ships like
	 */
	/*
     * (non-Javadoc)
     * 
     * @see battleship.IFleet#getShipsLike(java.lang.String)
     */
    @Override
    public List<IShip> getShipsLike(String category)
    {
		assert category != null;

		return filterShips(s -> s.getCategory().equals(category));
    }

	/**
	 * Gets floating ships.
	 *
	 * @return the floating ships
	 */
	/*
     * (non-Javadoc)
     * 
     * @see battleship.IFleet#getFloatingShips()
     */
    @Override
    public List<IShip> getFloatingShips()
    {
		return filterShips(IShip::stillFloating);
    }

	/**
	 * Gets sunk ships.
	 *
	 * @return the sunk ships
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see battleship.IFleet#getSunkShips()
	 */
	@Override
	public List<IShip> getSunkShips()
	{
		return filterShips(s -> !s.stillFloating());
	}

	// [REFACTORING] Local: Fleet::getShipsLike, getFloatingShips, getSunkShips | Cheiro: Duplicated Code (mesmo ciclo de filtragem 3 vezes) | Refatoração: Extract Method
	/**
	 * Returns the ships of the fleet that satisfy the given condition.
	 *
	 * @param condition the filter to apply to each ship
	 * @return a new list with the matching ships
	 */
	private List<IShip> filterShips(Predicate<IShip> condition)
	{
		List<IShip> result = new ArrayList<>();
		for (IShip s : ships)
			if (condition.test(s))
				result.add(s);
		return result;
	}
	/**
	 * Ship at ship.
	 *
	 * @param pos the pos
	 * @return the ship
	 */
	/*
     * (non-Javadoc)
     * 
     * @see battleship.IFleet#shipAt(battleship.IPosition)
     */
    @Override
    public IShip shipAt(IPosition pos)
    {
		assert pos != null;

		for (IShip ship : ships)
			if (ship.occupies(pos))
				return ship;
		return null;
    }

	/**
	 * Colision risk boolean.
	 *
	 * @param s the s
	 * @return the boolean
	 */
	// [REFACTORING] Local: Fleet::colisionRisk | Cheiro: Loop com índice e retorno antecipado | Refatoração: Replace Loop with Pipeline
	private boolean colisionRisk(IShip s)
    {
		assert s != null;

		return ships.stream().anyMatch(other -> other.tooCloseTo(s));
	}

	/**
	 * This operation prints all the given ships
	 *
	 * @param ships The list of ships
	 */
	public void printShips(List<IShip> ships)
	{
		assert ships != null;

		for (IShip ship : ships)
			System.out.println(ship);
	}

	/**
	 * This operation shows the state of a fleet
	 */
	public void printStatus()
    {
		System.out.println("Estado da Frota: " + this.getFloatingShips().size() + " a flutuar, " + this.getSunkShips().size() + " afundados!");
    }

	/**
	 * This operation prints all the ships of a fleet belonging to a particular
	 * category
	 *
	 * @param category The category of ships of interest
	 */
	public void printShipsByCategory(String category)
    {
		assert category != null;

		printShips(getShipsLike(category));
    }

	/**
	 * This operation prints all the ships of a fleet but not yet shot
	 */
	public void printFloatingShips()
    {
	printShips(getFloatingShips());
    }

	/**
	 * This operation prints all the ships of a fleet
	 */
	void printAllShips()
    {
		printShips(ships);
    }
}