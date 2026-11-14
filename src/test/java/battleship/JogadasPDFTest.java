package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for Fleet.
 * Author: LETI-129773
 * Date: 12/11/2026
 * Time: 16:00
 */

    public class JogadasPDFTest {

        private List<IMove> jogadas;

        /**
         * Set up common test data before each test.
         */
        @BeforeEach
        void setUp() {
            jogadas = new ArrayList<>();
        }

        /**
         * Clean up test data after each test.
         */
        @AfterEach
        void tearDown() {
            jogadas = null;
        }

        /**
         * Test to cover the default public constructor of the utility class JogadasPDF.
         */
        @Test
        void testJogadasPDFConstructor() {
            JogadasPDF util = new JogadasPDF();
            assertNotNull(util, "Error: JogadasPDF instance should not be null.");
        }

        /**
         * Test for the criarPDF method with a valid list of moves.
         */
        @Test
        void testCriarPDFComJogadas() {
            jogadas.add(new IMove() {
                @Override
                public List<IPosition> getShots() {
                    List<IPosition> tiros = new ArrayList<>();
                    tiros.add(new Position(1, 1));
                    tiros.add(new Position(1, 2));
                    return tiros;
                }

                @Override
                public int getNumber() {
                    return 1;
                }

                @Override
                public List<IGame.ShotResult> getShotResults() {
                    return new ArrayList<>();
                }

                @Override
                public String processEnemyFire(boolean hit) {
                    return "";
                }
            });

            assertDoesNotThrow(() -> {
                JogadasPDF.criarPDF(jogadas);
            }, "Error: criarPDF should not throw an exception with valid moves.");
        }

        /**
         * Test for the criarPDF method with an empty list of moves.
         */
        @Test
        void testCriarPDFComListaVazia() {
            assertDoesNotThrow(() -> {
                JogadasPDF.criarPDF(jogadas);
            }, "Error: criarPDF should handle empty move lists successfully.");
        }

        /**
         * Test to cover the IOException catch block by ensuring "jogadas.pdf"
         * is a directory, which forces document.save() to throw an IOException.
         */
        @Test
        void testCriarPDFIOException() {
            java.io.File fileOrDir = new java.io.File("jogadas.pdf");

            if (fileOrDir.exists()) {
                fileOrDir.delete();
            }

            fileOrDir.mkdir();

            assertDoesNotThrow(() -> {
                JogadasPDF.criarPDF(jogadas);
            }, "Error: criarPDF should handle IOException gracefully.");

            if (fileOrDir.exists()) {
                fileOrDir.delete();
            }
        }
    }