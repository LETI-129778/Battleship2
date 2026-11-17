package battleship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.*;

/**
 * Shot
 *
 * @author Your Name
 * Date: 20/02/2026
 * Time: 19:39
 */
public class Move implements IMove {

	// [REFACTORING] Local: Move | Cheiro: Duplicated Code (literal " tiro" repetido 6 vezes) | Refatoração: Introduce Constant
	private static final String TIRO = " tiro";
	// [REFACTORING] Local: Move | Cheiro: Duplicated Code (sufixo "s" e separador ", " repetidos) | Refatoração: Introduce Constant
	private static final String PLURAL = "s";
	private static final String SEPARATOR = ", ";
	//-------------------------------------------------------------------
	private final int number;
	private final List<IPosition> shots;
	private final List<IGame.ShotResult> shotResults;

	//-------------------------------------------------------------------
	public Move(int moveNumber, List<IPosition> moveShots, List<IGame.ShotResult> moveResults) {
		this.number = moveNumber;
		this.shots = moveShots;
		this.shotResults = moveResults;
	}

	@Override
	public String toString() {
		return "Move{" +
				"number=" + number +
				", shots=" + shots.size() +
				", results=" + shotResults.size() +
				'}';
	}

	@Override
	public int getNumber() {
		return this.number;
	}

	@Override
	public List<IPosition> getShots() {
		return this.shots;
	}

	@Override
	public List<IGame.ShotResult> getShotResults() {
		return this.shotResults;
	}

	//-------------------------------------------------------------------

	/**
	 * Contadores acumulados a partir dos resultados dos tiros.
	 */
	// [REFACTORING] Local: Move::processEnemyFire | Cheiro: Data Clumps (5 contadores soltos a passar entre métodos) | Refatoração: Extract Class
	private static class ShotStats {
		int validShots = 0;
		int repeatedShots = 0;
		int missedShots = 0;
		final Map<String, Integer> sunkBoatsCount = new HashMap<>();
		final Map<String, Integer> hitsPerBoat = new HashMap<>();
	}

	//-------------------------------------------------------------------

	/**
	 * Processes the results of enemy fire on the game board, analyzing the outcomes of shots,
	 * such as valid shots, repeated shots, missed shots, hits on ships, and sunk ships. It can
	 * also display a detailed summary of the shot results if verbose mode is activated.
	 *
	 * @param verbose a boolean indicating whether a detailed summary should be printed to the console
	 *                for the processed enemy fire data.
	 * @return a JSON-formatted string that encapsulates the results, including counts of valid shots,
	 * repeated shots, missed shots, shots outside the game board, and details of hits and
	 * sunk ships.
	 */
	@Override
	public String processEnemyFire(boolean verbose) {
		// [REFACTORING] Local: Move::processEnemyFire | Cheiro: Long Method / High Complexity (complexidade 102) | Refatoração: Extract Method
		// Métodos extraídos: computeStats, registerResult, buildSummary, appendValidShotsSummary,
		// appendSunkBoats, appendHitBoats, buildResponse, buildSunkBoatsList, buildBoatHitsList, toJson

		ShotStats stats = computeStats();
		int outsideShots = Game.NUMBER_SHOTS - stats.validShots - stats.repeatedShots;

		if (verbose) {
			System.out.println("Jogada nº" + this.number + " -> " + buildSummary(stats, outsideShots));
		}

		String jsonString = toJson(buildResponse(stats, outsideShots));

		System.out.println(jsonString);
		System.out.println();

		return jsonString;
	}
	//------------------------- Estatísticas ----------------------------
	// [REFACTORING] Extract Method (de processEnemyFire)
	private ShotStats computeStats () {
		ShotStats stats = new ShotStats();
		for (IGame.ShotResult result : this.shotResults) {
			registerResult(stats, result);
		}
		return stats;
	}

	// [REFACTORING] Extract Method (de processEnemyFire)
	private void registerResult (ShotStats stats, IGame.ShotResult result){
		if (!result.valid()) {
			return; // Tiro inválido - apenas ignorar
		}
		if (result.repeated()) {
			stats.repeatedShots++;
			return;
		}
		stats.validShots++;
		if (result.ship() == null) {
			stats.missedShots++; // Tiro na água
			return;
		}
		String boatName = result.ship().getCategory();
		stats.hitsPerBoat.merge(boatName, 1, Integer::sum);
		if (result.sunk()) {
			stats.sunkBoatsCount.merge(boatName, 1, Integer::sum);
		}
	}

	//------------------------- Mensagem verbose ------------------------
	// [REFACTORING] Extract Method (de processEnemyFire)
	private String buildSummary (ShotStats stats,int outsideShots){
		StringBuilder output = new StringBuilder();
		if (stats.validShots == 0 && stats.repeatedShots > 0) {
			output.append(formatShots(stats.repeatedShots, "repetido", PLURAL));
		} else {
			appendValidShotsSummary(output, stats);
		}

		if (outsideShots > 0) {
			if (!output.isEmpty()) {
				output.append(SEPARATOR);
			}
			output.append(formatShots(outsideShots, "exterior", "es"));
		}
		return output.toString();
	}

	// [REFACTORING] Extract Method (de processEnemyFire)
	private void appendValidShotsSummary (StringBuilder output, ShotStats stats){
		if (stats.validShots > 0) {
			output.append(formatShots(stats.validShots, "válido", PLURAL)).append(": ");
		}
		appendSunkBoats(output, stats);
		appendHitBoats(output, stats);
		if (stats.missedShots > 0) {
			output.append(stats.missedShots).append(TIRO).append(plural(stats.missedShots)).append(" na água");
		} else if (!stats.sunkBoatsCount.isEmpty() || !stats.hitsPerBoat.isEmpty()) {
			output.setLength(output.length() - 2); // Remover o "+" final
		}

		if (stats.repeatedShots > 0) {
			output.append(SEPARATOR).append(formatShots(stats.repeatedShots, "repetido", PLURAL));
		}
	}

	// [REFACTORING] Extract Method (de processEnemyFire)
	private void appendSunkBoats (StringBuilder output, ShotStats stats){
		for (Map.Entry<String, Integer> entry : stats.sunkBoatsCount.entrySet()) {
			int count = entry.getValue();
			output.append(count).append(" ").append(entry.getKey()).append(plural(count))
				.append(" ao fundo").append(" + ");
		}
	}

	// [REFACTORING] Extract Method (de processEnemyFire)
	private void appendHitBoats (StringBuilder output, ShotStats stats){
		for (Map.Entry<String, Integer> entry : stats.hitsPerBoat.entrySet()) {
			if (!stats.sunkBoatsCount.containsKey(entry.getKey())) {
				int hits = entry.getValue();
				output.append(hits).append(TIRO).append(plural(hits))
						.append(" num(a) ").append(entry.getKey()).append(" + ");
			}
		}
	}

	// [REFACTORING] Extract Method (elimina a repetição de n > 1 ? "s" : "")
	private static String plural ( int n){
		return n > 1 ? PLURAL : "";
	}

	/** Ex.: formatShots(3, "válido", "s") -> "3 tiros válidos" */
	// [REFACTORING] Extract Method (elimina a repetição de "N tiro(s) adjetivo(s)")
	private static String formatShots ( int n, String adjective, String pluralSuffix){
		return n + TIRO + plural(n) + " " + adjective + (n > 1 ? pluralSuffix : "");
	}

	//------------------------- JSON ------------------------------------
	// [REFACTORING] Extract Method (de processEnemyFire)
	private Map<String, Object> buildResponse (ShotStats stats,int outsideShots){
		Map<String, Object> response = new HashMap<>();
		response.put("validShots", stats.validShots);
		response.put("outsideShots", outsideShots);
		response.put("repeatedShots", stats.repeatedShots);
		response.put("missedShots", stats.missedShots);
		response.put("sunkBoats", buildSunkBoatsList(stats));
		response.put("hitsOnBoats", buildBoatHitsList(stats));
		return response;
	}

	// [REFACTORING] Extract Method (de processEnemyFire)
	private List<Map<String, Object>> buildSunkBoatsList (ShotStats stats){
		List<Map<String, Object>> sunkBoats = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : stats.sunkBoatsCount.entrySet()) {
			Map<String, Object> boat = new HashMap<>();
				boat.put("type", entry.getKey());
				boat.put("count", entry.getValue());
				sunkBoats.add(boat);
		}
		return sunkBoats;
	}

	// [REFACTORING] Extract Method (de processEnemyFire)
	private List<Map<String, Object>> buildBoatHitsList (ShotStats stats){
		List<Map<String, Object>> boatHits = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : stats.hitsPerBoat.entrySet()) {
			if (!stats.sunkBoatsCount.containsKey(entry.getKey())) {
				Map<String, Object> boat = new HashMap<>();
				boat.put("type", entry.getKey());
				boat.put("hits", entry.getValue());
				boatHits.add(boat);
			}
		}
		return boatHits;
	}

	// [REFACTORING] Extract Method (de processEnemyFire)
	private String toJson (Map < String, Object > response){
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return objectMapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Erro ao serializar o JSON dos resultados da jogada", e);
		}
	}
}