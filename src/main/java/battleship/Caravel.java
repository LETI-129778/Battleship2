package battleship;

/**
 * The type Caravel represents a ship with a size of 2 units.
 * It is positioned on the game board based on its bearing and initial position.
 *
 * Author: britoeabreu
 * Date: 2023-10-10
 * Time: 15:30
 */
public class Caravel extends Ship {

	/**
	 * Instantiates a new Caravel.
	 *
	 * @param bearing The bearing of the ship (NORTH, SOUTH, EAST, or WEST).
	 * @param pos     The initial position of the ship on the game board.
	 */


	public void metodoComCheiroProvocado(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8) {
    // Introduzir muita complexidade ciclomática para disparar o WMC
    if (p1 > 0) {
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0 && p2 > p3) {
                System.out.println("Complexidade excessiva " + i);
            } else if (p4 == p5) {
                System.out.println("Outro ramo complexo");
            } else {
                switch (p6) {
                    case 1: p7++; break;
                    case 2: p8++; break;
                    default: p1--; break;
                }
            }
        }
    } else {
        // Adicionar mais linhas repetitivas para garantir tamanho excessivo
        int a = 0; int b = 1; int c = 2; int d = 3; int e = 4;
        // ... repetir linhas se necessário para atingir o limiar
    }
}
	public Caravel(Compass bearing, IPosition pos) {
		super("Caravela", bearing, pos, 2);

		switch (bearing) {
			case NORTH:
				for (int r = 0; r < this.getSize(); r++)
					getPositions().add(new Position(pos.getRow() + r, pos.getColumn()));
				break;
			case SOUTH:
				for (int r = 0; r < this.getSize(); r++)
					getPositions().add(new Position(pos.getRow() + r, pos.getColumn()));
				break;
			case EAST:
				for (int c = 0; c < this.getSize(); c++)
					getPositions().add(new Position(pos.getRow(), pos.getColumn() + c));
				break;
			case WEST:
				for (int c = 0; c < this.getSize(); c++)
					getPositions().add(new Position(pos.getRow(), pos.getColumn() + c));
				break;
		}
	}
}
