import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Conversores {

    private static final String SEPARADORES_DE_LISTA = "[,;\\s]+";

    public static final Function<String, Object> INTEIRO = texto -> Integer.parseInt(texto.trim());

    public static final Function<String, Object> TEXTO = String::trim;

    public static final Function<String, Object> LISTA_DE_INTEIROS = Conversores::converterListaDeInteiros;

    public static final Function<String, Object> INTEIRO_NAO_NEGATIVO =
            validado(INTEIRO, numero -> (Integer) numero >= 0, "O valor nao pode ser negativo.");

    public static final Function<String, Object> LISTA_DE_INTEIROS_NAO_VAZIA =
            validado(LISTA_DE_INTEIROS, lista -> !((List<?>) lista).isEmpty(), "A lista precisa ter pelo menos um numero.");

    public static Function<String, Object> inteiroEntre(int minimo, int maximo) {
        return validado(INTEIRO, numero -> (Integer) numero >= minimo && (Integer) numero <= maximo,
                "O valor deve estar entre " + minimo + " e " + maximo + ".");
    }

    public static Function<String, Object> textoComTamanhoMaximo(int tamanhoMaximo) {
        return validado(TEXTO, texto -> ((String) texto).length() <= tamanhoMaximo,
                "O texto pode ter no maximo " + tamanhoMaximo + " caracteres.");
    }

    public static Function<String, Object> listaDeInteirosComTamanhoMaximo(int tamanhoMaximo) {
        return validado(LISTA_DE_INTEIROS, lista -> ((List<?>) lista).size() <= tamanhoMaximo,
                "A lista pode ter no maximo " + tamanhoMaximo + " numeros.");
    }

    private static Function<String, Object> validado(Function<String, Object> conversor, Predicate<Object> regra,
            String mensagemDeErro) {
        return texto -> {
            Object valor = conversor.apply(texto);
            if (!regra.test(valor)) {
                throw new IllegalArgumentException(mensagemDeErro);
            }
            return valor;
        };
    }

    private static List<Integer> converterListaDeInteiros(String texto) {
        String textoLimpo = texto.trim();
        if (textoLimpo.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(textoLimpo.split(SEPARADORES_DE_LISTA)).map(Integer::parseInt).collect(Collectors.toList());
    }
}
