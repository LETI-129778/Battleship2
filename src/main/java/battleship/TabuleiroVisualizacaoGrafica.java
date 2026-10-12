package battleship;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TabuleiroVisualizacaoGrafica extends Application {

    private static final int TAMANHO_CELULA = 35;
    private static final int LINHAS = 10;
    private static final int COLUNAS = 10;

    private static Rectangle[][] meuTabuleiro = new Rectangle[LINHAS][COLUNAS];
    private static Rectangle[][] advTabuleiro = new Rectangle[LINHAS][COLUNAS];

    @Override
    public void start(Stage primaryStage) {
        GridPane grelhaMeu = criarGrelha(meuTabuleiro);
        GridPane grelhaAdv = criarGrelha(advTabuleiro);

        Label tituloMeu = new Label("A Minha Frota");
        tituloMeu.setFont(new Font("Arial", 16));

        Label tituloAdv = new Label("Frota Inimiga");
        tituloAdv.setFont(new Font("Arial", 16));

        VBox painelMeu = new VBox(10, tituloMeu, grelhaMeu);
        VBox painelAdv = new VBox(10, tituloAdv, grelhaAdv);

        HBox layoutPrincipal = new HBox(50, painelMeu, painelAdv);
        layoutPrincipal.setPadding(new Insets(20));

        Scene scene = new Scene(layoutPrincipal, 850, 450);
        primaryStage.setTitle("Batalha Naval - Simulação Visual");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane criarGrelha(Rectangle[][] matriz) {
        GridPane grelha = new GridPane();
        for (int linha = 0; linha < LINHAS; linha++) {
            for (int coluna = 0; coluna < COLUNAS; coluna++) {
                Rectangle celula = new Rectangle(TAMANHO_CELULA, TAMANHO_CELULA);
                celula.setFill(Color.DARKBLUE); // Água desconhecida
                celula.setStroke(Color.BLACK); // Bordas
                matriz[linha][coluna] = celula;
                grelha.add(celula, coluna, linha);
            }
        }
        return grelha;
    }

    // Método universal para atualizar qualquer célula
    public static void pintarCelula(boolean isMeuTabuleiro, int linha, int coluna, Color cor) {
        Platform.runLater(() -> {
            Rectangle[][] matrizAlvo = isMeuTabuleiro ? meuTabuleiro : advTabuleiro;
            if (matrizAlvo[linha][coluna] != null) {
                matrizAlvo[linha][coluna].setFill(cor);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}