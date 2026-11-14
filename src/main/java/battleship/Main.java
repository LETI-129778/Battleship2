/**
 * 
 */
package battleship;

public class Main
{
	/**
	 * Main.
	 *
	 * @param args the args
	 */
	public static void main(String[] args)
    {
		System.out.println("***  Battleship  ***");

		// Visualização Gráfica do tabuleiro de jogo
		new Thread(() -> {
			TabuleiroVisualizacaoGrafica.main(args);
		}).start();

		Tasks.menu();
    }
}
