package battleship;

import org.apache.commons.lang3.time.StopWatch;

public class stopwatch {
    private final StopWatch cronometroJogador = new StopWatch();

    // Chamado na primeira vez que o jogador começa o seu turno no jogo
    public void iniciarCronometroJogo() {
        cronometroJogador.start();
    }

    // Chamado quando o turno passa para o Oponente (PAUSA o tempo sem reset)
    public void pausarTurnoJogador() {
        if (cronometroJogador.isStarted() && !cronometroJogador.isSuspended()) {
            cronometroJogador.suspend();
        }
    }

    // Chamado quando o turno volta para o Jogador (RETOMA de onde parou)
    public void retomarTurnoJogador() {
        if (cronometroJogador.isSuspended()) {
            cronometroJogador.resume();
        }
    }

    // Retorna o tempo formatado (ex: "00:02:15.450")
    public String getTempoFormatado() {
        return cronometroJogador.toString();
    }
}