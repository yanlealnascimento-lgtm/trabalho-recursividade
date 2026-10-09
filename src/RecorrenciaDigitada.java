import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecorrenciaDigitada {

    public static final int FORMA_DIVIDIR = 1;
    public static final int FORMA_SUBTRAIR = 2;
    public static final int FORMA_DOIS_RAMOS = 3;
    public static final int CUSTO_CONSTANTE = 1;
    public static final int CUSTO_LINEAR = 2;
    public static final int VALOR_PADRAO_DO_CASO_BASE = 1;
    private static final int PONTO_PADRAO_DO_CASO_BASE = 1;

    private static final String EXEMPLOS = System.lineSeparator()
            + "Exemplos: T(n) = 2T(n/2) + n  |  T(n) = T(n-1) + 3, se n > 1  |  T(n) = T(n/3) + T(2n/3) + n"
            + System.lineSeparator() + "Opcional: informe o caso base, ex.: T(n) = T(n-1) + n, T(1) = 1.";

    private static final Pattern CONDICAO = Pattern.compile("(se|para)?n>(=)?(\\d+)");
    private static final Pattern CASO_BASE_NA_RECORRENCIA = Pattern.compile("t\\((\\d+)\\)=(-?\\d+)");
    private static final Pattern TERMO_RECURSIVO = Pattern.compile("(\\d+)?\\*?t\\((.+)\\)");
    private static final Pattern ARGUMENTO_SUBTRAIR = Pattern.compile("n-(\\d+)");
    private static final Pattern ARGUMENTO_FRACAO_COM_N_NO_MEIO = Pattern.compile("(\\d+)?\\*?n/(\\d+)");
    private static final Pattern ARGUMENTO_FRACAO_ANTES_DE_N = Pattern.compile("\\(?(\\d+)/(\\d+)\\)?\\*?n");
    private static final Pattern CUSTO_CONSTANTE_DIGITADO = Pattern.compile("(\\d+)|c|(theta|o|[^\\w(])\\(1\\)");
    private static final Pattern CUSTO_LINEAR_DIGITADO = Pattern.compile("(\\d+|c)?\\*?n|(theta|o|[^\\w(])\\(n\\)");
    private static final Pattern CASO_BASE_PONTO_E_VALOR = Pattern.compile("(?:t\\((-?\\d+)\\)|(-?\\d+))=(-?\\d+)");
    private static final Pattern CASO_BASE_SO_PONTO = Pattern.compile("n=(-?\\d+)");
    private static final Pattern CASO_BASE_SO_VALOR = Pattern.compile("-?\\d+");

    public final int forma;
    public final int a;
    public final int divisorOuDeslocamento;
    public final int tipoDeCusto;
    public final int coeficienteDoCusto;
    public final int nDoCasoBase;
    public final int valorDoCasoBase;
    public final boolean casoBaseInformado;
    public final int[][] fracoesDosRamos;

    private RecorrenciaDigitada(int forma, int a, int divisorOuDeslocamento, int[] custo, int nDoCasoBase,
            int valorDoCasoBase, boolean casoBaseInformado, int[][] fracoesDosRamos) {
        this.forma = forma;
        this.a = a;
        this.divisorOuDeslocamento = divisorOuDeslocamento;
        this.tipoDeCusto = custo[0];
        this.coeficienteDoCusto = custo[1];
        this.nDoCasoBase = nDoCasoBase;
        this.valorDoCasoBase = valorDoCasoBase;
        this.casoBaseInformado = casoBaseInformado;
        this.fracoesDosRamos = fracoesDosRamos;
    }

    public RecorrenciaDigitada comCasoBase(String texto) {
        String limpo = texto.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
        try {
            Matcher pontoEValor = CASO_BASE_PONTO_E_VALOR.matcher(limpo);
            if (pontoEValor.matches()) {
                String ponto = pontoEValor.group(1) != null ? pontoEValor.group(1) : pontoEValor.group(2);
                return comCasoBase(pontoValido(ponto), Integer.parseInt(pontoEValor.group(3)), true);
            }
            Matcher soPonto = CASO_BASE_SO_PONTO.matcher(limpo);
            if (soPonto.matches()) {
                return comCasoBase(pontoValido(soPonto.group(1)), VALOR_PADRAO_DO_CASO_BASE, false);
            }
            if (CASO_BASE_SO_VALOR.matcher(limpo).matches()) {
                return comCasoBase(nDoCasoBase, Integer.parseInt(limpo), true);
            }
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException("Numero grande demais no caso base.");
        }
        throw new IllegalArgumentException("Caso base \"" + texto.trim()
                + "\" nao reconhecido. Use T(1)=1, T(0)=0, 0=0, n=0 ou apenas o valor.");
    }

    private RecorrenciaDigitada comCasoBase(int ponto, int valor, boolean informado) {
        return new RecorrenciaDigitada(forma, a, divisorOuDeslocamento, new int[] {tipoDeCusto, coeficienteDoCusto},
                ponto, valor, informado, fracoesDosRamos);
    }

    private static int pontoValido(String texto) {
        int ponto = Integer.parseInt(texto);
        if (ponto < 0) {
            throw new IllegalArgumentException("O ponto de parada do caso base nao pode ser negativo.");
        }
        return ponto;
    }

    public static RecorrenciaDigitada interpretar(String texto) {
        try {
            return interpretarSemExemplos(texto);
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException("Numero grande demais na recorrencia." + EXEMPLOS);
        } catch (IllegalArgumentException erro) {
            throw new IllegalArgumentException(erro.getMessage() + EXEMPLOS);
        }
    }

    private static RecorrenciaDigitada interpretarSemExemplos(String texto) {
        String restante = texto.toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "")
                .replace("\u0398", "theta")
                .replace("\u03b8", "theta")
                .replaceAll("[\u2212\u2013\u2014]", "-")
                .replaceAll("[\u00d7\u00b7]", "*")
                .replaceAll("[^\\x21-\\x7e]|\\?", "~")
                .replaceAll("~+", "~")
                .replaceAll("-+", "-")
                .replaceAll("n[~-]+(\\d)", "n-$1")
                .replaceAll("~\\(", "theta(");

        Integer nPelaCondicao = null;
        Matcher condicao = CONDICAO.matcher(restante);
        if (condicao.find()) {
            nPelaCondicao = Integer.parseInt(condicao.group(3)) - (condicao.group(2) != null ? 1 : 0);
            restante = condicao.replaceFirst("");
        }
        Integer nPeloCasoBase = null;
        Integer valorDoCasoBase = null;
        Matcher casoBase = CASO_BASE_NA_RECORRENCIA.matcher(restante);
        if (casoBase.find()) {
            nPeloCasoBase = Integer.parseInt(casoBase.group(1));
            valorDoCasoBase = Integer.parseInt(casoBase.group(2));
            restante = casoBase.replaceFirst("");
        }
        if (nPelaCondicao != null && nPeloCasoBase != null && !nPelaCondicao.equals(nPeloCasoBase)) {
            throw new IllegalArgumentException("O caso base T(" + nPeloCasoBase + ") nao combina com a condicao n > "
                    + nPelaCondicao + ".");
        }
        restante = restante.replaceAll("[,;]", "").replaceFirst("^t\\(n\\)=", "");
        if (restante.isEmpty() || restante.contains("=")) {
            throw new IllegalArgumentException("Nao consegui entender a recorrencia.");
        }

        Map<String, int[]> termosRecursivos = new LinkedHashMap<>();
        int[] custo = null;
        for (String termo : separarTermos(restante)) {
            Matcher recursivo = TERMO_RECURSIVO.matcher(termo);
            if (recursivo.matches()) {
                int coeficiente = recursivo.group(1) == null ? 1 : Integer.parseInt(recursivo.group(1));
                if (coeficiente < 1) {
                    throw new IllegalArgumentException("O coeficiente de T(...) deve ser pelo menos 1.");
                }
                int[] argumento = interpretarArgumento(recursivo.group(2));
                termosRecursivos.computeIfAbsent(argumento[0] + ":" + argumento[1] + ":" + argumento[2],
                        chave -> new int[] {argumento[0], argumento[1], argumento[2], 0})[3] += coeficiente;
            } else if (custo != null) {
                throw new IllegalArgumentException("Informe apenas um termo de custo f(n) (constante ou linear).");
            } else {
                custo = interpretarCusto(termo);
            }
        }
        if (termosRecursivos.isEmpty()) {
            throw new IllegalArgumentException("Falta o termo recursivo T(...).");
        }
        if (custo == null) {
            throw new IllegalArgumentException("Falta o custo f(n), ex.: + n ou + 1.");
        }

        int nDoCasoBase = nPeloCasoBase != null ? nPeloCasoBase
                : nPelaCondicao != null ? nPelaCondicao : PONTO_PADRAO_DO_CASO_BASE;
        int valor = valorDoCasoBase != null ? valorDoCasoBase : VALOR_PADRAO_DO_CASO_BASE;
        boolean informado = valorDoCasoBase != null;
        List<int[]> termos = new ArrayList<>(termosRecursivos.values());
        if (termos.size() == 1) {
            int[] termo = termos.get(0);
            if (termo[0] == FORMA_DIVIDIR && termo[1] != 1) {
                throw new IllegalArgumentException("Com um unico termo recursivo use T(n/b) ou T(n-d).");
            }
            int divisorOuDeslocamento = termo[0] == FORMA_SUBTRAIR ? termo[1] : termo[2];
            return new RecorrenciaDigitada(termo[0], termo[3], divisorOuDeslocamento, custo, nDoCasoBase, valor, informado,
                    new int[0][]);
        }
        if (termos.size() > 2) {
            throw new IllegalArgumentException("Esta versao aceita no maximo dois termos T(...) diferentes.");
        }
        for (int[] termo : termos) {
            if (termo[0] == FORMA_SUBTRAIR) {
                throw new IllegalArgumentException("Dois termos do tipo T(n-d) (como Fibonacci) nao sao cobertos por esta versao.");
            }
            if (termo[3] != 1) {
                throw new IllegalArgumentException("Na forma com dois ramos cada T(...) deve aparecer com coeficiente 1.");
            }
        }
        if (custo[0] != CUSTO_LINEAR) {
            throw new IllegalArgumentException("Na forma com dois ramos o custo precisa ser linear (c*n).");
        }
        return new RecorrenciaDigitada(FORMA_DOIS_RAMOS, 1, 0, custo, nDoCasoBase, valor, informado, new int[][] {
                {termos.get(0)[1], termos.get(0)[2]}, {termos.get(1)[1], termos.get(1)[2]}});
    }

    private static List<String> separarTermos(String expressao) {
        List<String> termos = new ArrayList<>();
        int profundidade = 0;
        int inicioDoTermo = 0;
        for (int posicao = 0; posicao < expressao.length(); posicao++) {
            char caractere = expressao.charAt(posicao);
            profundidade += caractere == '(' ? 1 : caractere == ')' ? -1 : 0;
            if (profundidade < 0) {
                throw new IllegalArgumentException("Parenteses desbalanceados.");
            }
            if (profundidade == 0 && caractere == '-') {
                throw new IllegalArgumentException("Use apenas somas (+) entre os termos da recorrencia.");
            }
            if (profundidade == 0 && caractere == '+') {
                termos.add(expressao.substring(inicioDoTermo, posicao));
                inicioDoTermo = posicao + 1;
            }
        }
        if (profundidade != 0) {
            throw new IllegalArgumentException("Parenteses desbalanceados.");
        }
        termos.add(expressao.substring(inicioDoTermo));
        if (termos.contains("")) {
            throw new IllegalArgumentException("Ha um termo vazio entre os sinais de +.");
        }
        return termos;
    }

    private static int[] interpretarArgumento(String argumento) {
        Matcher subtrair = ARGUMENTO_SUBTRAIR.matcher(argumento);
        if (subtrair.matches()) {
            int d = Integer.parseInt(subtrair.group(1));
            if (d < 1) {
                throw new IllegalArgumentException("Em T(n-d) o valor de d deve ser pelo menos 1.");
            }
            return new int[] {FORMA_SUBTRAIR, d, 0};
        }
        for (Pattern formatoDeFracao : List.of(ARGUMENTO_FRACAO_COM_N_NO_MEIO, ARGUMENTO_FRACAO_ANTES_DE_N)) {
            Matcher fracao = formatoDeFracao.matcher(argumento);
            if (fracao.matches()) {
                int numerador = fracao.group(1) == null ? 1 : Integer.parseInt(fracao.group(1));
                return fracaoValida(numerador, Integer.parseInt(fracao.group(2)));
            }
        }
        throw new IllegalArgumentException("Nao entendi o argumento T(" + argumento + "). Use n/b, n-d ou uma fracao de n.");
    }

    private static int[] fracaoValida(int numerador, int denominador) {
        if (numerador < 1 || denominador <= numerador) {
            throw new IllegalArgumentException("A fracao " + numerador + "/" + denominador
                    + " dentro de T(...) precisa estar entre 0 e 1.");
        }
        int divisorComum = maximoDivisorComum(numerador, denominador);
        return new int[] {FORMA_DIVIDIR, numerador / divisorComum, denominador / divisorComum};
    }

    private static int maximoDivisorComum(int primeiro, int segundo) {
        return segundo == 0 ? primeiro : maximoDivisorComum(segundo, primeiro % segundo);
    }

    private static int[] interpretarCusto(String termo) {
        for (int tipo : new int[] {CUSTO_CONSTANTE, CUSTO_LINEAR}) {
            Matcher custo = (tipo == CUSTO_CONSTANTE ? CUSTO_CONSTANTE_DIGITADO : CUSTO_LINEAR_DIGITADO).matcher(termo);
            if (custo.matches()) {
                String coeficienteDigitado = custo.group(1);
                int coeficiente = coeficienteDigitado == null || coeficienteDigitado.equals("c")
                        ? 1 : Integer.parseInt(coeficienteDigitado);
                if (coeficiente < 1) {
                    throw new IllegalArgumentException("O custo f(n) precisa ter coeficiente pelo menos 1.");
                }
                return new int[] {tipo, coeficiente};
            }
        }
        throw new IllegalArgumentException("Nao entendi o custo \"" + termo
                + "\". Use uma constante (ex.: 3) ou um termo linear (ex.: n, 2n, Theta(n)).");
    }
}
