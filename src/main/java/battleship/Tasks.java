package battleship;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * The type Tasks.
 */
public class Tasks {
	/**
	 * The constant LOGGER.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The constant GOODBYE_MESSAGE.
	 */
	private static final String GOODBYE_MESSAGE = "Bons ventos!";

	/**
	 * Strings to be used by the user
	 */
	private static final String AJUDA = "ajuda";
	private static final String GERAFROTA = "gerafrota";
	private static final String LEFROTA = "lefrota";
	private static final String DESISTIR = "desisto";
	private static final String RAJADA = "rajada";
	private static final String TIROS = "tiros";
	private static final String MAPA = "mapa";
	private static final String STATUS = "estado";
	private static final String SIMULA = "simula";
	private static final String TEMPO = "tempo";

	/**
	 * This task also tests the fighting element of a round of three shots
	 */
	public static void menu() {

		IFleet myFleet = null;
		IGame game = null;
		stopwatch timer = new stopwatch();
		boolean cronometroIniciado = false;

		menuHelp();

		System.out.print("> ");
		Scanner in = new Scanner(System.in);
		String command = in.next();

		while (!command.equals(DESISTIR)) {

			// Inicia o cronómetro na primeira ação de jogo
			if (!cronometroIniciado && (command.equals(GERAFROTA) || command.equals(LEFROTA))) {
				timer.iniciarCronometroJogo();
				cronometroIniciado = true;
			} else if (cronometroIniciado) {
				// Retoma o turno do jogador em cada nova interação/jogada
				timer.retomarTurnoJogador();
			}

			switch (command) {
				case GERAFROTA:
					myFleet = Fleet.createRandom();
					game = new Game(myFleet);
					game.printMyBoard(false, true);
					break;
				case LEFROTA:
					myFleet = buildFleet(in);
					game = new Game(myFleet);
					game.printMyBoard(false, true);
					break;
				case STATUS:
					if (myFleet != null) {
						myFleet.printStatus();
						if (cronometroIniciado) {
							System.out.println("Tempo acumulado até agora: " + timer.getTempoFormatado());
						}
					}
					break;
				case MAPA:
					if (myFleet != null)
						game.printMyBoard(false, true);
					break;
				case RAJADA:
					if (game != null) {
						// Turno do oponente: pausa o tempo do jogador
						timer.pausarTurnoJogador();

						game.readEnemyFire(in);

						// Retoma o tempo do jogador para a próxima jogada
						timer.retomarTurnoJogador();

						myFleet.printStatus();
						game.printMyBoard(true, false);

						if (game.getRemainingShips() == 0) {
							timer.pausarTurnoJogador();
							System.out.println("\n=================================================");
							System.out.println("FIM DO JOGO!");
							System.out.println("Tempo total acumulado de todas as jogadas: " + timer.getTempoFormatado());
							System.out.println("=================================================");
							game.over();
							System.exit(0);
						}
					}
					break;
				case SIMULA:
					if (game != null) {
						// Garante que o cronómetro está a contar durante toda a simulação
						timer.retomarTurnoJogador();

						while (game.getRemainingShips() > 0){
							game.randomEnemyFire();
							myFleet.printStatus();
							game.printMyBoard(true, false);
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							}
						}

						if (game.getRemainingShips() == 0) {
							timer.pausarTurnoJogador();
							System.out.println("\n=================================================");
							System.out.println("SIMULAÇÃO CONCLUÍDA!");
							System.out.println("Tempo total acumulado de todas as jogadas: " + timer.getTempoFormatado());
							System.out.println("=================================================");
							game.over();
							System.exit(0);
						}
					}
					break;
				case TIROS:
					if (game != null)
						game.printMyBoard(true, true);
					break;
				case TEMPO:
					if (cronometroIniciado) {
						System.out.println("Tempo acumulado de todas as tuas jogadas: " + timer.getTempoFormatado());
					} else {
						System.out.println("O cronómetro ainda não iniciou. Gera ou lê uma frota primeiro.");
					}
					break;
				case AJUDA:
					menuHelp();
					break;
				default:
					System.out.println("Que comando é esse??? Repete ...");
			}
			System.out.print("> ");
			command = in.next();
		}

		// Quando o jogador escreve "desisto"
		if (cronometroIniciado) {
			timer.pausarTurnoJogador();
			System.out.println("\n=================================================");
			System.out.println("JOGO TERMINADO POR DESISTÊNCIA.");
			System.out.println("Tempo total acumulado de todas as jogadas: " + timer.getTempoFormatado());
			System.out.println("=================================================");
		}
		System.out.println(GOODBYE_MESSAGE);
	}

	/**
	 * This function provides help information about the menu commands.
	 */
	public static void menuHelp() {
		System.out.println("======================= AJUDA DO MENU =========================");
		System.out.println("Digite um dos comandos abaixo para interagir com o jogo:");
		System.out.println("- " + GERAFROTA + ": Gera uma frota aleatória de navios.");
		System.out.println("- " + LEFROTA + ": Permite criar e carregar uma frota personalizada.");
		System.out.println("- " + STATUS + ": Mostra o status atual da frota e tempo de jogo.");
		System.out.println("- " + MAPA + ": Exibe o mapa da frota.");
		System.out.println("- " + RAJADA + ": Realiza uma rajada de disparos.");
		System.out.println("- " + SIMULA + ": Simula um jogo completo.");
		System.out.println("- " + TIROS + ": Lista os tiros válidos realizados (* = tiro em navio, o = tiro na água)");
		System.out.println("- " + TEMPO + ": Mostra o tempo acumulado das tuas jogadas.");
		System.out.println("- " + DESISTIR + ": Encerra o jogo.");
		System.out.println("===============================================================");
	}

	public static Fleet buildFleet(Scanner in) {
		assert in != null;
		Fleet fleet = new Fleet();
		int i = 0;
		while (i < Fleet.FLEET_SIZE) {
			IShip s = readShip(in);
			if (s != null) {
				boolean success = fleet.addShip(s);
				if (success)
					i++;
				else
					LOGGER.info("Falha na criacao de {} {} {}", s.getCategory(), s.getBearing(), s.getPosition());
			} else {
				LOGGER.info("Navio desconhecido!");
			}
		}
		LOGGER.info("{} navios adicionados com sucesso!", i);
		return fleet;
	}

	public static Ship readShip(Scanner in) {
		assert in != null;
		String shipKind = in.next();
		Position pos = readPosition(in);
		char c = in.next().charAt(0);
		Compass bearing = Compass.charToCompass(c);
		return Ship.buildShip(shipKind, bearing, pos);
	}

	public static Position readPosition(Scanner in) {
		assert in != null;
		int row = in.nextInt();
		int column = in.nextInt();
		return new Position(row, column);
	}

	public static IPosition readClassicPosition(@NotNull Scanner in) {
		if (!in.hasNext()) {
			throw new IllegalArgumentException("Nenhuma posição válida encontrada!");
		}

		String part1 = in.next();
		String part2 = null;

		if (in.hasNextInt()) {
			part2 = in.next();
		}

		String input = (part2 != null) ? part1 + part2 : part1;
		input = input.toUpperCase();

		if (input.matches("[A-Z]\\d+")) {
			char column = input.charAt(0);
			int row = Integer.parseInt(input.substring(1));
			return new Position(column, row);
		} else if (part2 != null && part1.matches("[A-Z]") && part2.matches("\\d+")) {
			char column = part1.charAt(0);
			int row = Integer.parseInt(part2);
			return new Position(column, row);
		} else {
			throw new IllegalArgumentException("Formato inválido. Use 'A3', 'A 3' ou similar.");
		}
	}
}