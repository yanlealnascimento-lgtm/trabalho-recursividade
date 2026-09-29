import java.util.Arrays;
import java.util.stream.Collectors;

public class Rastreador {

    private static final String ESPACOS_POR_NIVEL = "  ";

    public static boolean rastreioAtivo = false;
    public static int profundidade = 0;

    public static void entrar(String nome, Object... argumentos) {
        if (rastreioAtivo) {
            String argumentosFormatados = Arrays.stream(argumentos)
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            System.out.println(indentacao() + nome + "(" + argumentosFormatados + ")");
            profundidade++;
        }
    }

    public static <T> T sair(T valor) {
        if (rastreioAtivo) {
            profundidade--;
            System.out.println(indentacao() + "-> " + valor);
        }
        return valor;
    }

    public static void reiniciar() {
        profundidade = 0;
    }

    private static String indentacao() {
        return ESPACOS_POR_NIVEL.repeat(Math.max(profundidade, 0));
    }
}
