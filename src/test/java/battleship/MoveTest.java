package battleship;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Testes da classe Move.
 *
 * PRESSUPOSTO: IGame.ShotResult é um record com a assinatura
 * ShotResult(boolean valid, boolean repeated, IShip ship, boolean sunk).
 * Se a ordem for diferente, ajustar apenas o método auxiliar result(...).
 */
class MoveTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream consola;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void redirecionarConsola() {
        consola = new ByteArrayOutputStream();
        System.setOut(new PrintStream(consola));
    }

    @AfterEach
    void repor() {
        System.setOut(originalOut);
    }

    // ---------------------------------------------------------------
    // Métodos auxiliares
    // ---------------------------------------------------------------
    /** Valor por omissão para métodos de interface não relevantes no teste. */
    private static Object valorPorOmissao(Class<?> tipo) {
        if (tipo == boolean.class) return false;
        if (tipo == int.class) return 0;
        if (tipo == long.class) return 0L;
        if (tipo == double.class) return 0.0;
        if (tipo == float.class) return 0f;
        if (tipo == short.class) return (short) 0;
        if (tipo == byte.class) return (byte) 0;
        if (tipo == char.class) return '\0';
        return null;
    }

    /** IShip falso (sem bibliotecas externas): só getCategory() devolve algo. */
    private IShip navio(String categoria) {
        return (IShip) Proxy.newProxyInstance(
                IShip.class.getClassLoader(),
                new Class<?>[]{IShip.class},
                (proxy, metodo, args) -> {
                    if (metodo.getName().equals("getCategory")) return categoria;
                    return valorPorOmissao(metodo.getReturnType());
                });
    }

    /** IPosition falso: usado apenas para preencher a lista de tiros. */
    private IPosition posicao() {
        return (IPosition) Proxy.newProxyInstance(
                IPosition.class.getClassLoader(),
                new Class<?>[]{IPosition.class},
                (proxy, metodo, args) -> valorPorOmissao(metodo.getReturnType()));
    }

    private IGame.ShotResult result(boolean valid, boolean repeated, IShip ship, boolean sunk) {
        return new IGame.ShotResult(valid, repeated, ship, sunk);
    }

    private IGame.ShotResult invalido() { return result(false, false, null, false); }
    private IGame.ShotResult repetido() { return result(true, true, null, false); }
    private IGame.ShotResult agua()     { return result(true, false, null, false); }
    private IGame.ShotResult acerto(IShip s) { return result(true, false, s, false); }
    private IGame.ShotResult afundou(IShip s) { return result(true, false, s, true); }

    private Move move(IGame.ShotResult... results) {
        return new Move(1, new ArrayList<>(), new ArrayList<>(List.of(results)));
    }

    private JsonNode json(String s) throws Exception {
        return mapper.readTree(s);
    }

    private String saida() {
        return consola.toString();
    }

    // ---------------------------------------------------------------
    // Construtor, getters e toString
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Getters devolvem os valores passados no construtor")
    void getters() {
        List<IPosition> tiros = new ArrayList<>();
        tiros.add(posicao());
        List<IGame.ShotResult> resultados = new ArrayList<>(List.of(agua(), agua()));

        Move m = new Move(7, tiros, resultados);

        assertEquals(7, m.getNumber());
        assertSame(tiros, m.getShots());
        assertSame(resultados, m.getShotResults());
    }

    @Test
    @DisplayName("toString mostra número, nº de tiros e nº de resultados")
    void toStringMostraDados() {
        List<IPosition> tiros = new ArrayList<>();
        tiros.add(posicao());
        Move m = new Move(3, tiros, new ArrayList<>(List.of(agua(), agua())));

        assertEquals("Move{number=3, shots=1, results=2}", m.toString());
    }

    // ---------------------------------------------------------------
    // JSON devolvido (verbose = false)
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Sem resultados: todos os tiros são exteriores")
    void semResultados() throws Exception {
        JsonNode n = json(move().processEnemyFire(false));

        assertEquals(0, n.get("validShots").asInt());
        assertEquals(0, n.get("repeatedShots").asInt());
        assertEquals(0, n.get("missedShots").asInt());
        assertEquals(Game.NUMBER_SHOTS, n.get("outsideShots").asInt());
        assertEquals(0, n.get("sunkBoats").size());
        assertEquals(0, n.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Tiro inválido é ignorado e conta como exterior")
    void tiroInvalido() throws Exception {
        JsonNode n = json(move(invalido()).processEnemyFire(false));

        assertEquals(0, n.get("validShots").asInt());
        assertEquals(Game.NUMBER_SHOTS, n.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Tiro repetido é contabilizado como repetido")
    void tiroRepetido() throws Exception {
        JsonNode n = json(move(repetido()).processEnemyFire(false));

        assertEquals(1, n.get("repeatedShots").asInt());
        assertEquals(0, n.get("validShots").asInt());
        assertEquals(Game.NUMBER_SHOTS - 1, n.get("outsideShots").asInt());
    }

    @Test
    @DisplayName("Tiro na água é contabilizado como falhado")
    void tiroNaAgua() throws Exception {
        JsonNode n = json(move(agua()).processEnemyFire(false));

        assertEquals(1, n.get("validShots").asInt());
        assertEquals(1, n.get("missedShots").asInt());
        assertEquals(0, n.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Acerto sem afundar aparece em hitsOnBoats")
    void acertoSemAfundar() throws Exception {
        JsonNode n = json(move(acerto(navio("Fragata"))).processEnemyFire(false));

        assertEquals(1, n.get("validShots").asInt());
        assertEquals(0, n.get("missedShots").asInt());
        assertEquals(0, n.get("sunkBoats").size());
        assertEquals(1, n.get("hitsOnBoats").size());
        assertEquals("Fragata", n.get("hitsOnBoats").get(0).get("type").asText());
        assertEquals(1, n.get("hitsOnBoats").get(0).get("hits").asInt());
    }

    @Test
    @DisplayName("Navio afundado aparece em sunkBoats e não em hitsOnBoats")
    void navioAfundado() throws Exception {
        JsonNode n = json(move(afundou(navio("Fragata"))).processEnemyFire(false));

        assertEquals(1, n.get("sunkBoats").size());
        assertEquals("Fragata", n.get("sunkBoats").get(0).get("type").asText());
        assertEquals(1, n.get("sunkBoats").get(0).get("count").asInt());
        assertEquals(0, n.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Vários acertos e afundanço no mesmo tipo: só aparece em sunkBoats")
    void acertosEAfundancoMesmoTipo() throws Exception {
        IShip f = navio("Fragata");
        JsonNode n = json(move(acerto(f), afundou(f)).processEnemyFire(false));

        assertEquals(1, n.get("sunkBoats").size());
        assertEquals(0, n.get("hitsOnBoats").size());
    }

    @Test
    @DisplayName("Dois navios do mesmo tipo afundados somam count = 2")
    void doisNaviosMesmoTipo() throws Exception {
        JsonNode n = json(move(afundou(navio("Fragata")), afundou(navio("Fragata"))).processEnemyFire(false));

        assertEquals(1, n.get("sunkBoats").size());
        assertEquals(2, n.get("sunkBoats").get(0).get("count").asInt());
    }

    @Test
    @DisplayName("Sem verbose nada é escrito com o prefixo 'Jogada nº'")
    void semVerboseNaoImprimeJogada() {
        move(agua()).processEnemyFire(false);

        assertFalse(saida().contains("Jogada nº"));
    }

    // ---------------------------------------------------------------
    // Mensagem verbose = true
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Verbose: só tiros inválidos mostra apenas tiros exteriores")
    void verboseSoInvalidos() {
        move(invalido()).processEnemyFire(true);

        String s = saida();
        assertTrue(s.contains("Jogada nº1 -> "));
        assertTrue(s.contains("exterior"));
        assertFalse(s.contains("válido"));
    }

    @Test
    @DisplayName("Verbose: um tiro repetido (singular)")
    void verboseUmRepetido() {
        move(repetido()).processEnemyFire(true);

        String s = saida();
        assertTrue(s.contains("1 tiro repetido"));
        assertFalse(s.contains("1 tiros repetidos"));
    }

    @Test
    @DisplayName("Verbose: vários tiros repetidos (plural)")
    void verboseVariosRepetidos() {
        move(repetido(), repetido()).processEnemyFire(true);

        assertTrue(saida().contains("2 tiros repetidos"));
    }

    @Test
    @DisplayName("Verbose: um tiro na água (singular)")
    void verboseUmaAgua() {
        move(agua()).processEnemyFire(true);

        String s = saida();
        assertTrue(s.contains("1 tiro válido: 1 tiro na água"));
    }

    @Test
    @DisplayName("Verbose: vários tiros na água (plural)")
    void verboseVariasAgua() {
        move(agua(), agua()).processEnemyFire(true);

        assertTrue(saida().contains("2 tiros válidos: 2 tiros na água"));
    }

    @Test
    @DisplayName("Verbose: um acerto sem afundar + tiro na água")
    void verboseAcertoEAgua() {
        move(acerto(navio("Fragata")), agua()).processEnemyFire(true);

        String s = saida();
        assertTrue(s.contains("1 tiro num(a) Fragata"));
        assertTrue(s.contains("1 tiro na água"));
    }

    @Test
    @DisplayName("Verbose: vários acertos no mesmo navio (plural)")
    void verboseVariosAcertos() {
        IShip f = navio("Fragata");
        move(acerto(f), acerto(f)).processEnemyFire(true);

        assertTrue(saida().contains("2 tiros num(a) Fragata"));
    }

    @Test
    @DisplayName("Verbose: navio afundado sem tiros na água")
    void verboseAfundadoSemAgua() {
        move(afundou(navio("Fragata"))).processEnemyFire(true);

        String s = saida();
        assertTrue(s.contains("1 Fragata ao fundo"));
        assertFalse(s.contains("na água"));
    }

    @Test
    @DisplayName("Verbose: acerto sem afundar e sem tiros na água")
    void verboseAcertoSemAgua() {
        move(acerto(navio("Galeao"))).processEnemyFire(true);

        String s = saida();
        assertTrue(s.contains("1 tiro num(a) Galeao"));
        assertFalse(s.contains("na água"));
    }

    @Test
    @DisplayName("Verbose: dois navios do mesmo tipo afundados (plural)")
    void verboseDoisAfundados() {
        move(afundou(navio("Fragata")), afundou(navio("Fragata"))).processEnemyFire(true);

        assertTrue(saida().contains("2 Fragatas ao fundo"));
    }

    @Test
    @DisplayName("Verbose: acertos + afundanço no mesmo tipo não repete a linha de acertos")
    void verboseAcertosEAfundancoMesmoTipo() {
        IShip f = navio("Fragata");
        move(acerto(f), afundou(f)).processEnemyFire(true);

        String s = saida();
        assertTrue(s.contains("1 Fragata ao fundo"));
        assertFalse(s.contains("num(a) Fragata"));
    }

    @Test
    @DisplayName("Verbose: tiro válido e tiro repetido na mesma jogada")
    void verboseValidoERepetido() {
        move(agua(), repetido()).processEnemyFire(true);

        assertTrue(saida().contains("1 tiro válido: 1 tiro na água, 1 tiro repetido"));
    }

    @Test
    @DisplayName("Verbose: tiros válidos, repetidos plural e exteriores")
    void verboseRepetidosPluralComValido() {
        move(agua(), repetido(), repetido()).processEnemyFire(true);

        assertTrue(saida().contains("2 tiros repetidos"));
    }

    @Test
    @DisplayName("Verbose: sem tiros exteriores quando todos os tiros são válidos")
    void verboseSemExteriores() {
        IGame.ShotResult[] todos = new IGame.ShotResult[Game.NUMBER_SHOTS];
        for (int i = 0; i < todos.length; i++) {
            todos[i] = agua();
        }
        move(todos).processEnemyFire(true);

        assertFalse(saida().contains("exterior"));
    }

    @Test
    @DisplayName("Verbose: um tiro exterior (singular) após tiros válidos")
    void verboseExteriorSingular() {
        IGame.ShotResult[] tiros = new IGame.ShotResult[Game.NUMBER_SHOTS - 1];
        for (int i = 0; i < tiros.length; i++) {
            tiros[i] = agua();
        }
        move(tiros).processEnemyFire(true);

        String s = saida();
        assertTrue(s.contains(", 1 tiro exterior"));
        assertFalse(s.contains("exteriores"));
    }

    @Test
    @DisplayName("Verbose: vários tiros exteriores (plural)")
    void verboseExterioresPlural() {
        move().processEnemyFire(true);

        assertTrue(saida().contains(Game.NUMBER_SHOTS + " tiros exteriores")
                || Game.NUMBER_SHOTS == 1);
    }
    @Test
    @DisplayName("Falha na serialização JSON lança RuntimeException com a causa original")
    void erroSerializacao() {
        Move m = new Move(1, new ArrayList<>(), new ArrayList<>()) {
            ObjectMapper criarObjectMapper() {
                return new ObjectMapper() {
                    @Override
                    public String writeValueAsString(Object value) throws JsonProcessingException {
                        throw new JsonProcessingException("falha simulada") { };
                    }
                };
            }
        };
        RuntimeException ex = assertThrows(RuntimeException.class, () -> m.processEnemyFire(false));

        assertEquals("Erro ao serializar o JSON dos resultados da jogada", ex.getMessage());
        assertTrue(ex.getCause() instanceof JsonProcessingException);
    }
}
/*
*   throw new RuntimeException(...) no catch de JsonProcessingException:
*    Só é executado se o ObjectMapper falhar, o mapa de resultados, que
*    contém apenas int, String e listas de mapas, que o Jackson serializa sempre sem erro.
*    O ObjectMapper é criado dentro do método, pelo que não é possível injetar uma falha
*    a partir do teste. Trata-se de um catch defensivo exigido pelo compilador (exceção
*    verificada), mas inalcançável na prática.
*/