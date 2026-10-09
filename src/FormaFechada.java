import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

public class FormaFechada {

    private final List<Fracao> coeficientes;
    private final List<IntFunction<String>> termos;

    public FormaFechada(List<Fracao> coeficientes, List<IntFunction<String>> termos) {
        this.coeficientes = new ArrayList<>(coeficientes);
        this.termos = new ArrayList<>(termos);
    }

    public Fracao coeficiente(int posicao) {
        return coeficientes.get(posicao);
    }

    public FormaFechada comCoeficientes(List<Fracao> novosCoeficientes) {
        return new FormaFechada(novosCoeficientes, termos);
    }

    public boolean mesmosCoeficientesQue(FormaFechada outra) {
        for (int posicao = 0; posicao < coeficientes.size(); posicao++) {
            if (coeficientes.get(posicao).compararCom(outra.coeficientes.get(posicao)) != 0) {
                return false;
            }
        }
        return true;
    }

    public String em(int argumento) {
        StringBuilder soma = new StringBuilder();
        IntStream.range(0, coeficientes.size()).boxed()
                .sorted(Comparator.comparing(posicao -> termos.get(posicao).apply(argumento).isEmpty()))
                .filter(posicao -> !coeficientes.get(posicao).ehZero())
                .forEach(posicao -> {
                    Fracao coeficiente = coeficientes.get(posicao);
                    String sinal = coeficiente.ehNegativa() ? "-" : "+";
                    soma.append(soma.length() == 0 ? (coeficiente.ehNegativa() ? "-" : "") : " " + sinal + " ")
                            .append(termoComCoeficiente(coeficiente.absoluta(), termos.get(posicao).apply(argumento)));
                });
        return soma.length() == 0 ? "0" : soma.toString();
    }

    static String termoComCoeficiente(Fracao coeficiente, String termo) {
        if (termo.isEmpty()) {
            return coeficiente.toString();
        }
        return coeficiente.ehUm() ? termo : coeficiente + "*" + termo;
    }
}
