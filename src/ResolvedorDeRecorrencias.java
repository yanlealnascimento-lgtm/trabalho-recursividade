import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class ResolvedorDeRecorrencias {

    private static final int FORMA_DIVIDIR = 1;
    private static final int FORMA_SUBTRAIR = 2;
    private static final int FORMA_DOIS_RAMOS = 3;

    private static final int CUSTO_CONSTANTE = 1;
    private static final int CUSTO_LINEAR = 2;

    private static final int MENOR_VALOR_DE_A = 1;
    private static final int MENOR_VALOR_DE_B = 2;
    private static final int MENOR_VALOR_DE_D = 1;
    private static final int MENOR_VALOR_DE_C = 1;
    private static final int MENOR_TERMO_DA_FRACAO = 1;

    private static final String FORMATO_DO_ROTULO = "  %-24s%s";
    private static final String RECUO_DE_CONTINUACAO = String.format("%26s", "");
    private static final String SEM_FORMA_FECHADA = "sem forma fechada simples";

    public static void executar() {
        System.out.println();
        System.out.println("--- Resolvedor de recorrencias ---");
        System.out.println("Escolha a forma da recorrencia:");
        System.out.println(" " + FORMA_DIVIDIR + ") T(n) = a*T(n/b) + f(n)        (dividir o tamanho)");
        System.out.println(" " + FORMA_SUBTRAIR + ") T(n) = a*T(n-d) + f(n)        (subtrair do tamanho)");
        System.out.println(" " + FORMA_DOIS_RAMOS + ") T(n) = T(p1/q1 * n) + T(p2/q2 * n) + c*n   (dois ramos)");
        int forma = Main.lerOpcao("Escolha a forma: ", FORMA_DIVIDIR, FORMA_DOIS_RAMOS);
        if (forma == FORMA_DIVIDIR) {
            resolverFormaDividir();
        } else if (forma == FORMA_SUBTRAIR) {
            resolverFormaSubtrair();
        } else {
            resolverFormaDoisRamos();
        }
    }

    private static void resolverFormaDividir() {
        int a = lerInteiro("Informe a (inteiro >= " + MENOR_VALOR_DE_A + "): ", MENOR_VALOR_DE_A);
        int b = lerInteiro("Informe b (inteiro >= " + MENOR_VALOR_DE_B + "): ", MENOR_VALOR_DE_B);
        int tipoDeCusto = lerTipoDeCusto();
        int c = lerCoeficienteDoCusto();
        int casoBase = lerValor("Informe o valor do caso base T(1): ");
        CustoDoPasso custo = new CustoDoPasso(tipoDeCusto, c);

        String logDeN = "log_" + b + "(n)";
        String recorrencia = "T(n) = " + coeficiente(a) + "T(n/" + b + ") + " + custo.em("n") + ",  T(1) = " + casoBase;
        String expansao = "T(n) = " + coeficiente(a) + "[" + coeficiente(a) + "T(n/" + potencia(b, 2) + ") + "
                + custo.em("(n/" + b + ")") + "] + " + custo.em("n")
                + " = " + coeficiente(potencia(a, 2)) + "T(n/" + potencia(b, 2) + ") + "
                + custo.multiplicadoPor(a, "(n/" + b + ")") + " + " + custo.em("n");
        String parteRecursivaAposK = (a == 1 ? "" : a + "^k*") + "T(n/" + b + "^k)";
        String prefixoAposK = "T(n) = " + parteRecursivaAposK + " + ";
        String achandoK = "n/" + b + "^k = 1  =>  " + b + "^k = n  =>  k = " + logDeN;

        String expoenteSimbolico = "log_" + b + "(" + a + ")";
        String potenciaDeN = potenciaDeNComExpoenteLogaritmico(a, b);
        String potenciaDeNDecimal = potenciaDeNComExpoenteDecimal(a, b);
        String expoenteExplicado = "expoente " + expoenteSimbolico + " = " + expoenteDecimal(a, b);
        String aElevadoAK = (a == 1 ? "1^k = 1" : a + "^k = " + a + "^(" + logDeN + ") = " + potenciaDeN);

        if (tipoDeCusto == CUSTO_CONSTANTE) {
            if (a == 1) {
                imprimirSolucao(recorrencia, expansao,
                        prefixoAposK + multiplicar(c, "k") + "   (k parcelas iguais a " + c + ")",
                        achandoK,
                        emLinhas("T(n) = T(1) + " + multiplicar(c, "k") + " = " + casoBase + " + " + multiplicar(c, logDeN)),
                        "T(n) = " + montarSoma(List.of(Fracao.inteiro(c), Fracao.inteiro(casoBase)), List.of(logDeN, ""))
                                + "   (n potencia de " + b + ")",
                        "Theta(log n)");
                return;
            }
            Fracao parcelaGeometrica = new Fracao(c, a - 1L);
            imprimirSolucao(recorrencia, expansao,
                    prefixoAposK + multiplicar(c, "(1 + " + a + " + " + a + "^2 + ... + " + a + "^(k-1))"),
                    achandoK,
                    emLinhas(
                            aElevadoAK,
                            "T(n) = " + potenciaDeN + "*T(1) + " + multiplicar(c, "(" + potenciaDeN + " - 1)/(" + a + " - 1)")),
                    "T(n) = " + montarSoma(
                            List.of(Fracao.inteiro(casoBase).somar(parcelaGeometrica), parcelaGeometrica.negativa()),
                            List.of(potenciaDeN, "")) + "   (n potencia de " + b + ")",
                    "Theta(" + potenciaDeNDecimal + ")   (" + expoenteExplicado + ")");
            return;
        }

        Fracao razao = new Fracao(a, b);
        String razaoTexto = razao.entreParentesesSeFracionaria();
        if (a == b) {
            imprimirSolucao(recorrencia, expansao,
                    prefixoAposK + multiplicar(c, "n") + " + ... + " + multiplicar(c, "n") + "   (k parcelas)"
                            + " = " + parteRecursivaAposK + " + " + multiplicar(c, "k*n"),
                    achandoK,
                    emLinhas(
                            "razao a/b = 1: cada um dos k niveis custa " + multiplicar(c, "n"),
                            aElevadoAK,
                            "T(n) = n*T(1) + " + multiplicar(c, "n*" + logDeN)),
                    "T(n) = " + montarSoma(List.of(Fracao.inteiro(c), Fracao.inteiro(casoBase)), List.of("n*" + logDeN, "n"))
                            + "   (n potencia de " + b + ")",
                    "Theta(n log n)");
            return;
        }

        String formaAposK = prefixoAposK + multiplicar(c, "n*(1 + " + razaoTexto + " + " + razaoTexto + "^2 + ... + "
                + razaoTexto + "^(k-1))");
        Fracao fatorDaSoma = new Fracao((long) c * b, Math.abs((long) b - a));
        String potenciaDeNNaSoma = potenciaDeN.isEmpty() ? "1" : potenciaDeN;
        if (a < b) {
            imprimirSolucao(recorrencia, expansao, formaAposK, achandoK,
                    emLinhas(
                            "razao a/b = " + razao + " < 1: soma geometrica decrescente, dominada pelo primeiro termo",
                            aElevadoAK + "   e   " + razaoTexto + "^k = " + potenciaDeNNaSoma + "/n",
                            "T(n) = " + coeficiente(potenciaDeNNaSoma) + "T(1) + " + termoComCoeficiente(fatorDaSoma, "(n - " + potenciaDeNNaSoma + ")")),
                    "T(n) = " + montarSoma(
                            List.of(fatorDaSoma, Fracao.inteiro(casoBase).subtrair(fatorDaSoma)),
                            List.of("n", potenciaDeN)) + "   (n potencia de " + b + ")",
                    "Theta(n)");
            return;
        }
        imprimirSolucao(recorrencia, expansao, formaAposK, achandoK,
                emLinhas(
                        "razao a/b = " + razao + " > 1: soma geometrica crescente, dominada pelo ultimo nivel",
                        aElevadoAK + "   e   " + razaoTexto + "^k = " + potenciaDeN + "/n",
                        "T(n) = " + potenciaDeN + "*T(1) + " + termoComCoeficiente(fatorDaSoma, "(" + potenciaDeN + " - n)")),
                "T(n) = " + montarSoma(
                        List.of(Fracao.inteiro(casoBase).somar(fatorDaSoma), fatorDaSoma.negativa()),
                        List.of(potenciaDeN, "n")) + "   (n potencia de " + b + ")",
                "Theta(" + potenciaDeNDecimal + ")   (" + expoenteExplicado + ")");
    }

    private static void resolverFormaSubtrair() {
        int a = lerInteiro("Informe a (inteiro >= " + MENOR_VALOR_DE_A + "): ", MENOR_VALOR_DE_A);
        int d = lerInteiro("Informe d (inteiro >= " + MENOR_VALOR_DE_D + "): ", MENOR_VALOR_DE_D);
        int tipoDeCusto = lerTipoDeCusto();
        int c = lerCoeficienteDoCusto();
        int casoBase = lerValor("Informe o valor do caso base T(0): ");
        CustoDoPasso custo = new CustoDoPasso(tipoDeCusto, c);

        String k = d == 1 ? "n" : "n/" + d;
        String kEntreParenteses = d == 1 ? "n" : "(n/" + d + ")";
        String aElevadoAK = a + "^" + kEntreParenteses;
        String recorrencia = "T(n) = " + coeficiente(a) + "T(n-" + d + ") + " + custo.em("n") + ",  T(0) = " + casoBase;
        String expansao = "T(n) = " + coeficiente(a) + "[" + coeficiente(a) + "T(n-" + 2L * d + ") + "
                + custo.em("(n-" + d + ")") + "] + " + custo.em("n")
                + " = " + coeficiente(potencia(a, 2)) + "T(n-" + 2L * d + ") + "
                + custo.multiplicadoPor(a, "(n-" + d + ")") + " + " + custo.em("n");
        String kVezesD = multiplicar(d, "k");
        String ultimoDeslocamento = "(n-" + multiplicar(d, "(k-1)") + ")";
        String prefixoAposK = "T(n) = " + (a == 1 ? "" : a + "^k*") + "T(n-" + kVezesD + ") + ";
        String achandoK = "n - " + kVezesD + " = 0  =>  k = " + k
                + (d == 1 ? "" : "   (se " + d + " nao divide n, k = teto(n/" + d + "); o comportamento nao muda)");

        if (tipoDeCusto == CUSTO_CONSTANTE) {
            if (a == 1) {
                imprimirSolucao(recorrencia, expansao,
                        prefixoAposK + multiplicar(c, "k") + "   (k parcelas iguais a " + c + ")",
                        achandoK,
                        emLinhas("T(n) = T(0) + " + multiplicar(c, "k") + " = " + casoBase + " + " + multiplicar(c, kEntreParenteses)),
                        "T(n) = " + montarSoma(List.of(new Fracao(c, d), Fracao.inteiro(casoBase)), List.of("n", "")),
                        "Theta(n)");
                return;
            }
            Fracao parcelaGeometrica = new Fracao(c, a - 1L);
            imprimirSolucao(recorrencia, expansao,
                    prefixoAposK + multiplicar(c, "(1 + " + a + " + " + a + "^2 + ... + " + a + "^(k-1))"),
                    achandoK,
                    emLinhas(
                            "T(n) = " + a + "^k*T(0) + " + multiplicar(c, "(" + a + "^k - 1)/(" + a + " - 1)") + ",  com k = " + k,
                            "T(n) = " + aElevadoAK + "*" + casoBase + " + " + multiplicar(c, "(" + aElevadoAK + " - 1)/" + (a - 1))),
                    "T(n) = " + montarSoma(
                            List.of(Fracao.inteiro(casoBase).somar(parcelaGeometrica), parcelaGeometrica.negativa()),
                            List.of(aElevadoAK, "")),
                    "Theta(" + aElevadoAK + ")");
            return;
        }

        if (a == 1) {
            String somaDosCustos = "n + (n-" + d + ") + (n-" + 2L * d + ") + ... + " + ultimoDeslocamento;
            imprimirSolucao(recorrencia, expansao,
                    prefixoAposK + multiplicar(c, "[" + somaDosCustos + "]"),
                    achandoK,
                    emLinhas(
                            "T(n) = T(0) + " + multiplicar(c, "(k*n - " + multiplicar(d, "k*(k-1)/2)")) + ",  com k = " + k,
                            "soma de k termos que decrescem linearmente: cerca de n^2 no total"),
                    "T(n) = " + montarSoma(
                            List.of(new Fracao(c, 2L * d), new Fracao(c, 2), Fracao.inteiro(casoBase)),
                            List.of("n^2", "n", "")),
                    "Theta(n^2)");
            return;
        }

        Fracao parcelaLinear = new Fracao(c, a - 1L);
        Fracao parcelaConstante = new Fracao((long) a * c * d, 1).dividir(Fracao.inteiro(a - 1L).multiplicar(Fracao.inteiro(a - 1L)));
        String somaDosCustos = "n + " + a + "*(n-" + d + ") + " + a + "^2*(n-" + 2L * d + ") + ... + " + a + "^(k-1)*" + ultimoDeslocamento;
        imprimirSolucao(recorrencia, expansao,
                prefixoAposK + multiplicar(c, "[" + somaDosCustos + "]"),
                achandoK,
                emLinhas(
                        "T(n) = " + a + "^k*T(0) + " + multiplicar(c, "[...]") + ",  com k = " + k,
                        "o termo " + aElevadoAK + " cresce mais rapido que a parte linear e domina a soma",
                        "T(n) = A*" + aElevadoAK + " + (alfa*n + beta), com alfa = " + parcelaLinear.negativa()
                                + ", beta = " + parcelaConstante.negativa() + " e A = T(0) - beta"),
                "T(n) = " + montarSoma(
                        List.of(Fracao.inteiro(casoBase).somar(parcelaConstante), parcelaLinear.negativa(), parcelaConstante.negativa()),
                        List.of(aElevadoAK, "n", "")),
                "Theta(" + aElevadoAK + ")");
    }

    private static void resolverFormaDoisRamos() {
        int p1 = lerInteiro("Informe p1 (numerador da primeira fracao, inteiro >= " + MENOR_TERMO_DA_FRACAO + "): ",
                MENOR_TERMO_DA_FRACAO);
        int q1 = lerInteiro("Informe q1 (denominador da primeira fracao, maior que " + p1 + "): ", p1 + 1);
        int p2 = lerInteiro("Informe p2 (numerador da segunda fracao, inteiro >= " + MENOR_TERMO_DA_FRACAO + "): ",
                MENOR_TERMO_DA_FRACAO);
        int q2 = lerInteiro("Informe q2 (denominador da segunda fracao, maior que " + p2 + "): ", p2 + 1);

        Fracao primeira = new Fracao(p1, q1);
        Fracao segunda = new Fracao(p2, q2);
        Fracao soma = primeira.somar(segunda);
        String recorrencia = "T(n) = T(" + primeira + "*n) + T(" + segunda + "*n) + c*n";

        if (!soma.ehUm()) {
            System.out.println();
            System.out.println("=== Resolvedor de recorrencia ===");
            System.out.println(String.format(FORMATO_DO_ROTULO, "Recorrencia:", recorrencia));
            System.out.println(String.format(FORMATO_DO_ROTULO, "Soma das fracoes:", primeira + " + " + segunda + " = " + soma));
            System.out.println();
            System.out.println("Esta versao so resolve o caso em que as fracoes somam 1.");
            System.out.println("Para fracoes que nao somam 1 a analise deve ser feita a mao pela soma por nivel:");
            System.out.println("  nivel i custa c*n*s^i, com s = " + soma + "; some os niveis ate o ramo mais longo chegar a 1.");
            return;
        }

        Fracao menor = primeira.compararCom(segunda) <= 0 ? primeira : segunda;
        Fracao maior = menor == primeira ? segunda : primeira;
        String ramoCurto = "log_" + menor.inversa().entreParentesesSeFracionaria() + "(n)";
        String ramoLongo = "log_" + maior.inversa().entreParentesesSeFracionaria() + "(n)";
        String expansao = "T(n) = [T(" + primeira.multiplicar(primeira) + "*n) + T(" + primeira.multiplicar(segunda)
                + "*n) + c*" + primeira + "*n] + [T(" + segunda.multiplicar(primeira) + "*n) + T("
                + segunda.multiplicar(segunda) + "*n) + c*" + segunda + "*n] + c*n";

        imprimirSolucao(recorrencia, expansao,
                emLinhas(
                        "nivel i custa c*n*(" + primeira + " + " + segunda + ")^i = c*n*" + soma + "^i = c*n",
                        "T(n) = (soma dos T nos 2^k ramos) + k*c*n"),
                emLinhas(
                        "ramo mais curto: " + menor.entreParentesesSeFracionaria() + "^k*n = 1  =>  k = " + ramoCurto,
                        "ramo mais longo: " + maior.entreParentesesSeFracionaria() + "^k*n = 1  =>  k = " + ramoLongo),
                emLinhas(
                        "ha entre " + ramoCurto + " e " + ramoLongo + " niveis, cada um custando no maximo c*n",
                        "c*n*" + ramoCurto + " <= T(n) <= c*n*" + ramoLongo + "  (os dois sao Theta(log n))"),
                SEM_FORMA_FECHADA,
                "Theta(n log n)");
    }

    private static void imprimirSolucao(String recorrencia, String expansao, String formaAposK, String achandoK,
            String substituicao, String formaFechada, String comportamento) {
        System.out.println();
        System.out.println("=== Resolvedor de recorrencia ===");
        System.out.println(String.format(FORMATO_DO_ROTULO, "Recorrencia:", recorrencia));
        System.out.println(String.format(FORMATO_DO_ROTULO, "Passo 1 (expansao):", expansao));
        System.out.println(String.format(FORMATO_DO_ROTULO, "Passo 2 (forma apos k):", formaAposK));
        System.out.println(String.format(FORMATO_DO_ROTULO, "Passo 3 (achar k):", achandoK));
        System.out.println(String.format(FORMATO_DO_ROTULO, "Passo 4 (substituir):", substituicao));
        System.out.println(String.format(FORMATO_DO_ROTULO, "Forma fechada:", formaFechada));
        System.out.println(String.format(FORMATO_DO_ROTULO, "Comportamento:", comportamento));
    }

    private static String emLinhas(String... linhas) {
        return String.join(System.lineSeparator() + RECUO_DE_CONTINUACAO, linhas);
    }

    private static int lerTipoDeCusto() {
        System.out.println("Tipo de custo f(n):");
        System.out.println(" " + CUSTO_CONSTANTE + " = constante c");
        System.out.println(" " + CUSTO_LINEAR + " = linear c*n");
        return Main.lerOpcao("Escolha o tipo de custo: ", CUSTO_CONSTANTE, CUSTO_LINEAR);
    }

    private static int lerCoeficienteDoCusto() {
        return lerInteiro("Informe o valor de c (inteiro >= " + MENOR_VALOR_DE_C + "): ", MENOR_VALOR_DE_C);
    }

    private static int lerInteiro(String pergunta, int minimo) {
        return lerComConversor(pergunta, Conversores.inteiroNoMinimo(minimo));
    }

    private static int lerValor(String pergunta) {
        return lerComConversor(pergunta, Conversores.INTEIRO);
    }

    private static int lerComConversor(String pergunta, Function<String, Object> conversor) {
        return (Integer) Main.lerParametro(new Parametro(pergunta, conversor));
    }

    private static String coeficiente(Object valor) {
        return "1".equals(valor.toString()) ? "" : valor + "*";
    }

    private static String multiplicar(long fator, String termo) {
        return fator == 1 ? termo : fator + "*" + termo;
    }

    private static BigInteger potencia(long base, int expoente) {
        return BigInteger.valueOf(base).pow(expoente);
    }

    private static String montarSoma(List<Fracao> coeficientes, List<String> termos) {
        StringBuilder soma = new StringBuilder();
        for (int indice = 0; indice < coeficientes.size(); indice++) {
            Fracao coeficiente = coeficientes.get(indice);
            if (coeficiente.ehZero()) {
                continue;
            }
            if (soma.length() == 0) {
                soma.append(coeficiente.ehNegativa() ? "-" : "");
            } else {
                soma.append(coeficiente.ehNegativa() ? " - " : " + ");
            }
            soma.append(termoComCoeficiente(coeficiente.absoluta(), termos.get(indice)));
        }
        return soma.length() == 0 ? "0" : soma.toString();
    }

    private static String termoComCoeficiente(Fracao coeficiente, String termo) {
        if (termo.isEmpty()) {
            return coeficiente.toString();
        }
        return coeficiente.ehUm() ? termo : coeficiente + "*" + termo;
    }

    private static String potenciaDeNComExpoenteLogaritmico(int a, int b) {
        int expoenteInteiro = expoenteInteiroExato(a, b);
        if (expoenteInteiro == 0) {
            return "";
        }
        if (expoenteInteiro == 1) {
            return "n";
        }
        if (expoenteInteiro > 1) {
            return "n^" + expoenteInteiro;
        }
        return "n^(log_" + b + "(" + a + "))";
    }

    private static String potenciaDeNComExpoenteDecimal(int a, int b) {
        int expoenteInteiro = expoenteInteiroExato(a, b);
        if (expoenteInteiro >= 0) {
            return potenciaDeNComExpoenteLogaritmico(a, b);
        }
        return "n^" + expoenteDecimal(a, b);
    }

    private static String expoenteDecimal(int a, int b) {
        return String.format(Locale.US, "%.2f", Math.log(a) / Math.log(b));
    }

    private static int expoenteInteiroExato(int a, int b) {
        int expoente = (int) Math.round(Math.log(a) / Math.log(b));
        return potencia(b, expoente).equals(BigInteger.valueOf(a)) ? expoente : -1;
    }

    private static class CustoDoPasso {

        private final int tipo;
        private final int coeficiente;

        CustoDoPasso(int tipo, int coeficiente) {
            this.tipo = tipo;
            this.coeficiente = coeficiente;
        }

        String em(String argumento) {
            return tipo == CUSTO_CONSTANTE ? String.valueOf(coeficiente) : multiplicar(coeficiente, argumento);
        }

        String multiplicadoPor(long fator, String argumento) {
            long fatorTotal = fator * coeficiente;
            return tipo == CUSTO_CONSTANTE ? String.valueOf(fatorTotal) : multiplicar(fatorTotal, argumento);
        }
    }
}
