import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Conversores {

    private static final String SEPARADORES_DE_LISTA = "[,;\\s]+";

    public static final Function<String, Object> INTEIRO = Conversores::converterInteiro;

    public static final Function<String, Object> INTEIRO_NAO_NEGATIVO = Conversores::converterInteiroNaoNegativo;

    public static final Function<String, Object> LISTA_DE_INTEIROS = Conversores::converterListaDeInteiros;

    public static final Function<String, Object> LISTA_DE_INTEIROS_NAO_VAZIA = Conversores::converterListaDeInteirosNaoVazia;

    public static final Function<String, Object> TEXTO = Conversores::converterTexto;

    public static Function<String, Object> inteiroEntre(int minimo, int maximo) {
        return texto -> converterInteiroEntre(texto, minimo, maximo);
    }

    public static Function<String, Object> inteiroNoMinimo(int minimo) {
        return texto -> converterInteiroNoMinimo(texto, minimo);
    }

    public static Function<String, Object> textoComTamanhoMaximo(int tamanhoMaximo) {
        return texto -> converterTextoComTamanhoMaximo(texto, tamanhoMaximo);
    }

    public static Function<String, Object> listaDeInteirosComTamanhoMaximo(int tamanhoMaximo) {
        return texto -> converterListaDeInteirosComTamanhoMaximo(texto, tamanhoMaximo);
    }

    private static Integer converterInteiroEntre(String texto, int minimo, int maximo) {
        Integer numero = converterInteiro(texto);
        if (numero < minimo || numero > maximo) {
            throw new IllegalArgumentException("O valor deve estar entre " + minimo + " e " + maximo + ".");
        }
        return numero;
    }

    private static Integer converterInteiroNoMinimo(String texto, int minimo) {
        Integer numero = converterInteiro(texto);
        if (numero < minimo) {
            throw new IllegalArgumentException("O valor deve ser no minimo " + minimo + ".");
        }
        return numero;
    }

    private static String converterTextoComTamanhoMaximo(String texto, int tamanhoMaximo) {
        String textoLimpo = converterTexto(texto);
        if (textoLimpo.length() > tamanhoMaximo) {
            throw new IllegalArgumentException("O texto pode ter no maximo " + tamanhoMaximo + " caracteres.");
        }
        return textoLimpo;
    }

    private static List<Integer> converterListaDeInteirosComTamanhoMaximo(String texto, int tamanhoMaximo) {
        List<Integer> numeros = converterListaDeInteiros(texto);
        if (numeros.size() > tamanhoMaximo) {
            throw new IllegalArgumentException("A lista pode ter no maximo " + tamanhoMaximo + " numeros.");
        }
        return numeros;
    }

    private static Integer converterInteiro(String texto) {
        return Integer.parseInt(texto.trim());
    }

    private static Integer converterInteiroNaoNegativo(String texto) {
        Integer numero = converterInteiro(texto);
        if (numero < 0) {
            throw new IllegalArgumentException("O valor nao pode ser negativo.");
        }
        return numero;
    }

    private static List<Integer> converterListaDeInteiros(String texto) {
        String textoLimpo = texto.trim();
        if (textoLimpo.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(textoLimpo.split(SEPARADORES_DE_LISTA))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private static List<Integer> converterListaDeInteirosNaoVazia(String texto) {
        List<Integer> numeros = converterListaDeInteiros(texto);
        if (numeros.isEmpty()) {
            throw new IllegalArgumentException("A lista precisa ter pelo menos um numero.");
        }
        return numeros;
    }

    private static String converterTexto(String texto) {
        return texto.trim();
    }
}
