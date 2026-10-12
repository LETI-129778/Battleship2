package battleship;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class JogadasPDF {

    public static void criarPDF(List<IMove> jogadas) {

        try {

            PDDocument documento = new PDDocument();
            PDPage pagina = new PDPage();

            documento.addPage(pagina);

            PDPageContentStream conteudo = new PDPageContentStream(documento, pagina);

            conteudo.beginText();

            conteudo.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA),12);

            conteudo.newLineAtOffset(50, 750);

            conteudo.showText("Historico de Jogadas");
            conteudo.newLineAtOffset(0, -20);

            int numeroJogada = 1;

            for (IMove jogada : jogadas) {

                StringBuilder linha = new StringBuilder();
                linha.append("Jogada ").append(numeroJogada).append(": ");

                for (IPosition tiro : jogada.getShots()) {
                    linha.append(tiro).append("  ");
                }

                conteudo.showText(linha.toString());
                conteudo.newLineAtOffset(0, -15);

                numeroJogada++;
            }

            conteudo.endText();
            conteudo.close();

            documento.save("jogadas.pdf");
            documento.close();

            System.out.println("PDF criado com sucesso: jogadas.pdf");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
