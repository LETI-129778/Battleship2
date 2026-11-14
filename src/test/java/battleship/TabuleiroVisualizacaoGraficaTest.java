package battleship;

import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javafx.stage.Window;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TabuleiroVisualizacaoGraficaTest {

    private static final int LINHAS = 10;
    private static final int COLUNAS = 10;

    // ---------------------------------------------------------------
    // Arranque do toolkit JavaFX e utilitários
    // ---------------------------------------------------------------
    @BeforeAll
    static void iniciarJavaFX() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
            assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX não arrancou");
        } catch (IllegalStateException jaIniciado) {
            // Toolkit já iniciado por outra classe de teste
        }
    }

    @BeforeEach
    void limparTabuleiros() throws Exception {
        // Repõe as matrizes estáticas (ficam com células a null)
        setMatriz("meuTabuleiro", new Rectangle[LINHAS][COLUNAS]);
        setMatriz("advTabuleiro", new Rectangle[LINHAS][COLUNAS]);
    }

    private static void setMatriz(String nome, Rectangle[][] valor) throws Exception {
        Field f = TabuleiroVisualizacaoGrafica.class.getDeclaredField(nome);
        f.setAccessible(true);
        f.set(null, valor);
    }

    private static Rectangle[][] getMatriz(String nome) throws Exception {
        Field f = TabuleiroVisualizacaoGrafica.class.getDeclaredField(nome);
        f.setAccessible(true);
        return (Rectangle[][]) f.get(null);
    }

    /** Executa código na thread JavaFX e espera pela conclusão. */
    private static void correrNaFx(Runnable r) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> erro = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                r.run();
            } catch (Throwable t) {
                erro.set(t);
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS), "Timeout na thread JavaFX");
        if (erro.get() != null) {
            fail(erro.get());
        }
    }

    // ---------------------------------------------------------------
    // start()
    // ---------------------------------------------------------------
    @Test
    @DisplayName("start() cria a janela com título e cena")
    void startCriaJanela() throws Exception {
        AtomicReference<Stage> stageRef = new AtomicReference<>();
        correrNaFx(() -> {
            Stage stage = new Stage();
            new TabuleiroVisualizacaoGrafica().start(stage);
            stageRef.set(stage);
        });

        Stage stage = stageRef.get();
        assertEquals("Batalha Naval - Simulação Visual", stage.getTitle());
        assertNotNull(stage.getScene());
        assertEquals(850, stage.getScene().getWidth());
        assertEquals(450, stage.getScene().getHeight());
        correrNaFx(stage::close);
    }

    @Test
    @DisplayName("start() preenche os dois tabuleiros com 10x10 células azul-escuro")
    void startPreencheTabuleiros() throws Exception {
        correrNaFx(() -> {
            Stage stage = new Stage();
            new TabuleiroVisualizacaoGrafica().start(stage);
            stage.close();
        });

        for (String nome : new String[]{"meuTabuleiro", "advTabuleiro"}) {
            Rectangle[][] m = getMatriz(nome);
            for (int l = 0; l < LINHAS; l++) {
                for (int c = 0; c < COLUNAS; c++) {
                    assertNotNull(m[l][c], nome + "[" + l + "][" + c + "]");
                    assertEquals(Color.DARKBLUE, m[l][c].getFill());
                    assertEquals(Color.BLACK, m[l][c].getStroke());
                    assertEquals(35, m[l][c].getWidth());
                    assertEquals(35, m[l][c].getHeight());
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // criarGrelha() (privado -> reflexão)
    // ---------------------------------------------------------------
    @Test
    @DisplayName("criarGrelha() devolve uma grelha com 100 células e preenche a matriz")
    void criarGrelha() throws Exception {
        Rectangle[][] matriz = new Rectangle[LINHAS][COLUNAS];
        AtomicReference<GridPane> ref = new AtomicReference<>();

        correrNaFx(() -> {
            try {
                Method m = TabuleiroVisualizacaoGrafica.class
                        .getDeclaredMethod("criarGrelha", Rectangle[][].class);
                m.setAccessible(true);
                ref.set((GridPane) m.invoke(new TabuleiroVisualizacaoGrafica(), (Object) matriz));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });

        assertEquals(LINHAS * COLUNAS, ref.get().getChildren().size());
        assertNotNull(matriz[0][0]);
        assertNotNull(matriz[LINHAS - 1][COLUNAS - 1]);
        assertEquals(Color.DARKBLUE, matriz[5][5].getFill());
    }

    // ---------------------------------------------------------------
    // pintarCelula()
    // ---------------------------------------------------------------
    private void prepararTabuleiros() throws Exception {
        correrNaFx(() -> {
            Stage stage = new Stage();
            new TabuleiroVisualizacaoGrafica().start(stage);
            stage.close();
        });
    }

    @Test
    @DisplayName("pintarCelula() no meu tabuleiro altera só a célula do meu tabuleiro")
    void pintarMeuTabuleiro() throws Exception {
        prepararTabuleiros();

        TabuleiroVisualizacaoGrafica.pintarCelula(true, 2, 3, Color.RED);
        correrNaFx(() -> { }); // espera que o runLater anterior termine

        assertEquals(Color.RED, getMatriz("meuTabuleiro")[2][3].getFill());
        assertEquals(Color.DARKBLUE, getMatriz("advTabuleiro")[2][3].getFill());
    }

    @Test
    @DisplayName("pintarCelula() no tabuleiro adversário altera só a célula do adversário")
    void pintarAdvTabuleiro() throws Exception {
        prepararTabuleiros();

        TabuleiroVisualizacaoGrafica.pintarCelula(false, 9, 9, Color.GREEN);
        correrNaFx(() -> { });

        assertEquals(Color.GREEN, getMatriz("advTabuleiro")[9][9].getFill());
        assertEquals(Color.DARKBLUE, getMatriz("meuTabuleiro")[9][9].getFill());
    }

    @Test
    @DisplayName("pintarCelula() com célula inexistente (null) no meu tabuleiro não lança erro")
    void pintarCelulaNullMeu() throws Exception {
        // Matrizes sem células (repostas em @BeforeEach)
        TabuleiroVisualizacaoGrafica.pintarCelula(true, 0, 0, Color.RED);
        correrNaFx(() -> { });

        assertNull(getMatriz("meuTabuleiro")[0][0]);
    }

    @Test
    @DisplayName("pintarCelula() com célula inexistente (null) no tabuleiro adversário não lança erro")
    void pintarCelulaNullAdv() throws Exception {
        TabuleiroVisualizacaoGrafica.pintarCelula(false, 0, 0, Color.RED);
        correrNaFx(() -> { });

        assertNull(getMatriz("advTabuleiro")[0][0]);
    }
    @Test
    @DisplayName("main() invoca launch(args) sem bloquear o teste")
    void mainInvocaLaunch() throws Exception {
        Platform.setImplicitExit(false); // evita terminar o toolkit para os outros testes

        AtomicReference<Throwable> erro = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                TabuleiroVisualizacaoGrafica.main(new String[]{});
            } catch (Throwable e) {
                erro.set(e); // por exemplo IllegalStateException se o launch já foi chamado
            }
        });
        t.setDaemon(true);
        t.start();
        t.join(3000); // dá tempo para o launch ser executado

        // fecha a janela que o launch possa ter aberto
        Platform.runLater(() -> new ArrayList<>(Window.getWindows()).forEach(Window::hide));
        correrNaFx(() -> { });
    }
}