
package battleship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import org.apache.commons.lang3.time.StopWatch;

/**
 * Test class for class stopwatch.
 *
 * Author: ${user.name}
 * Date: 2026-11-10 15:52
 *
 * Cyclomatic Complexity:
 * - constructor: 1
 * - iniciarCronometroJogo(): 1
 * - pausarTurnoJogador(): 3
 * - retomarTurnoJogador(): 2
 * - getTempoFormatado(): 1
 *
 * Total CC: 8
 */
class stopwatchTest {

    private stopwatch cronometro;

    @BeforeEach
    void setUp() {
        cronometro = new stopwatch();
    }

    @AfterEach
    void tearDown() {
        cronometro = null;

        // Cleanup completed; the test instance is no longer referenced.
    }

    /**
     * Accesses the internal StopWatch for lifecycle verification.
     */
    private StopWatch getInternalStopWatch() throws Exception {
        Field field = stopwatch.class.getDeclaredField("cronometroJogador");
        field.setAccessible(true);

        return (StopWatch) field.get(cronometro);
    }

    // --------------------------------------------------
    // Constructor - CC = 1
    // --------------------------------------------------

    @Test
    @DisplayName("Constructor should initialize the stopwatch")
    void stopwatch() throws Exception {

        assertNotNull(
                getInternalStopWatch(),
                "Error: the internal stopwatch should be initialized"
        );
    }

    // --------------------------------------------------
    // iniciarCronometroJogo() - CC = 1
    // --------------------------------------------------

    @Test
    @DisplayName("Should start the player's stopwatch")
    void iniciarCronometroJogo() throws Exception {

        cronometro.iniciarCronometroJogo();

        assertAll(
                () -> assertTrue(
                        getInternalStopWatch().isStarted(),
                        "Error: stopwatch should be started"
                ),
                () -> assertFalse(
                        getInternalStopWatch().isSuspended(),
                        "Error: stopwatch should not be suspended after starting"
                )
        );
    }

    // --------------------------------------------------
    // pausarTurnoJogador() - CC = 3
    // --------------------------------------------------

    @Test
    @DisplayName("Should not suspend when stopwatch has not started")
    void pausarTurnoJogador1() throws Exception {

        cronometro.pausarTurnoJogador();

        assertFalse(
                getInternalStopWatch().isStarted(),
                "Error: stopwatch should remain unstarted"
        );
    }

    @Test
    @DisplayName("Should not suspend an already suspended stopwatch")
    void pausarTurnoJogador2() throws Exception {

        cronometro.iniciarCronometroJogo();
        cronometro.pausarTurnoJogador();

        cronometro.pausarTurnoJogador();

        assertAll(
                () -> assertTrue(
                        getInternalStopWatch().isStarted(),
                        "Error: stopwatch should remain started"
                ),
                () -> assertTrue(
                        getInternalStopWatch().isSuspended(),
                        "Error: stopwatch should remain suspended"
                )
        );
    }

    @Test
    @DisplayName("Should suspend a started and running stopwatch")
    void pausarTurnoJogador3() throws Exception {

        cronometro.iniciarCronometroJogo();

        cronometro.pausarTurnoJogador();

        assertTrue(
                getInternalStopWatch().isSuspended(),
                "Error: stopwatch should be suspended"
        );
    }

    // --------------------------------------------------
    // retomarTurnoJogador() - CC = 2
    // --------------------------------------------------

    @Test
    @DisplayName("Should resume a suspended stopwatch")
    void retomarTurnoJogador1() throws Exception {

        cronometro.iniciarCronometroJogo();
        cronometro.pausarTurnoJogador();

        cronometro.retomarTurnoJogador();

        assertAll(
                () -> assertTrue(
                        getInternalStopWatch().isStarted(),
                        "Error: stopwatch should remain started after resuming"
                ),
                () -> assertFalse(
                        getInternalStopWatch().isSuspended(),
                        "Error: stopwatch should no longer be suspended"
                )
        );
    }

    @Test
    @DisplayName("Should do nothing when stopwatch is not suspended")
    void retomarTurnoJogador2() throws Exception {

        cronometro.iniciarCronometroJogo();

        cronometro.retomarTurnoJogador();

        assertFalse(
                getInternalStopWatch().isSuspended(),
                "Error: stopwatch should remain running"
        );
    }

    // --------------------------------------------------
    // getTempoFormatado() - CC = 1
    // --------------------------------------------------

    @Test
    @DisplayName("Should return the formatted elapsed time")
    void getTempoFormatado() {

        String tempo = cronometro.getTempoFormatado();

        assertEquals(
                "00:00:00.000",
                tempo,
                "Error: expected initial formatted time 00:00:00.000"
        );
    }
}
