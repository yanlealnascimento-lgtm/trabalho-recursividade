import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.IntFunction;

public class ResolvedorDeRecorrencias {

    private static final int CUSTO_CONSTANTE = RecorrenciaDigitada.CUSTO_CONSTANTE;
    private static final int CUSTO_LINEAR = RecorrenciaDigitada.CUSTO_LINEAR;
    private static final int PONTO_DE_PARADA_AO_DIVIDIR = 1;

    private static final int DIVIDIR_N_LOG_N = 0;
    private static final int DIVIDIR_POTENCIA_DE_N = 1;
    private static final int DIVIDIR_N = 2;
    private static final int DIVIDIR_LOG_N = 3;
    private static final int DIVIDIR_CONSTANTE = 4;

    private static final int SUBTRAIR_EXPONENCIAL = 0;
    private static final int SUBTRAIR_N_AO_QUADRADO = 1;
    private static final int SUBTRAIR_N = 2;
    private static final int SUBTRAIR_CONSTANTE = 3;

    private static final String FORMATO_DO_ROTULO = "  %-24s%s";
    private static final String RECUO_DE_CONTINUACAO = String.format("%26s", "");
    private static final String[] ROTULOS_DA_SOLUCAO = {"Recorrencia:", "Passo 1 (expansao):", "Passo 2 (forma apos k):",
            "Passo 3 (achar k):", "Passo 4 (substituir):", "Forma fechada:", "Passo 5 (inducao):", "Comportamento:"};
    private static final String SEM_FORMA_FECHADA = "sem forma fechada simples";
    private static final String CONFERE = "  ok";
    private static final String NAO_CONFERE = "  NAO confere";

    public static void executar() {
        System.out.println();
        RecorrenciaDigitada digitada = lerRecorrencia();
        if (digitada.forma == RecorrenciaDigitada.FORMA_DOIS_RAMOS) {
            int[][] ramos = digitada.fracoesDosRamos;
            resolverFormaDoisRamos(ramos[0][0], ramos[0][1], ramos[1][0], ramos[1][1]);
            return;
        }
        if (!digitada.casoBaseInformado) {
            digitada = lerCasoBase(digitada);
        }
        if (digitada.forma == RecorrenciaDigitada.FORMA_SUBTRAIR) {
            resolverFormaSubtrair(digitada.a, digitada.divisorOuDeslocamento, digitada.tipoDeCusto,
                    digitada.coeficienteDoCusto, digitada.nDoCasoBase, digitada.valorDoCasoBase);
            return;
        }
        if (digitada.nDoCasoBase != PONTO_DE_PARADA_AO_DIVIDIR) {
            System.out.println("Para recorrencias que dividem o tamanho a parada e em n = " + PONTO_DE_PARADA_AO_DIVIDIR
                    + " (n/b^k = " + PONTO_DE_PARADA_AO_DIVIDIR + "), pois dividindo nunca se chega a T("
                    + digitada.nDoCasoBase + "). Usando T(" + PONTO_DE_PARADA_AO_DIVIDIR + ") = " + digitada.valorDoCasoBase + ".");
        }
        resolverFormaDividir(digitada.a, digitada.divisorOuDeslocamento, digitada.tipoDeCusto,
                digitada.coeficienteDoCusto, digitada.valorDoCasoBase);
    }

    private static RecorrenciaDigitada lerRecorrencia() {
        while (true) {
            String texto = Main.lerLinha("Digite a recorrencia: ");
            if (texto.trim().isEmpty()) {
                System.out.println("Nenhum valor informado. Tente novamente.");
                continue;
            }
            try {
                return RecorrenciaDigitada.interpretar(texto);
            } catch (IllegalArgumentException erro) {
                System.out.println(erro.getMessage() + " Tente novamente.");
            }
        }
    }

    private static RecorrenciaDigitada lerCasoBase(RecorrenciaDigitada digitada) {
        String casoBasePadrao = "T(" + digitada.nDoCasoBase + ")=" + RecorrenciaDigitada.VALOR_PADRAO_DO_CASO_BASE;
        while (true) {
            String texto = Main.lerLinha("Digite o caso base (ex: T(1)=1 ou T(0)=0), ou apenas Enter para usar "
                    + casoBasePadrao + ": ").trim();
            if (texto.isEmpty()) {
                System.out.println("Caso base nao informado: usando " + casoBasePadrao
                        + " (o valor do caso base nao altera o comportamento).");
                return digitada;
            }
            try {
                RecorrenciaDigitada comCasoBase = digitada.comCasoBase(texto);
                if (!comCasoBase.casoBaseInformado) {
                    System.out.println("Valor nao informado: usando T(" + comCasoBase.nDoCasoBase + ")="
                            + comCasoBase.valorDoCasoBase + ".");
                }
                return comCasoBase;
            } catch (IllegalArgumentException erro) {
                System.out.println(erro.getMessage() + " Tente novamente.");
            }
        }
    }

    private static void resolverFormaDividir(int a, int b, int tipoDeCusto, int c, int casoBase) {
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
        String potenciaDeN = potenciaDeN("n", a, b);
        String potenciaDeNDecimal = potenciaDeNComExpoenteDecimal(a, b);
        String expoenteExplicado = "expoente " + expoenteSimbolico + " = " + expoenteDecimal(a, b);
        String aElevadoAK = (a == 1 ? "1^k = 1" : a + "^k = " + a + "^(" + logDeN + ") = " + potenciaDeN);
        Fracao base = Fracao.inteiro(casoBase);
        Fracao zero = Fracao.inteiro(0);

        String formaAposK;
        String substituicao;
        String comportamento;
        List<Fracao> coeficientes;
        if (tipoDeCusto == CUSTO_CONSTANTE && a == 1) {
            formaAposK = prefixoAposK + multiplicar(c, "k") + "   (k parcelas iguais a " + c + ")";
            substituicao = emLinhas("T(n) = T(1) + " + multiplicar(c, "k") + " = " + casoBase + " + " + multiplicar(c, logDeN));
            coeficientes = List.of(zero, zero, zero, Fracao.inteiro(c), base);
            comportamento = "Theta(log n)";
        } else if (tipoDeCusto == CUSTO_CONSTANTE) {
            Fracao parcelaGeometrica = new Fracao(c, a - 1L);
            formaAposK = prefixoAposK + multiplicar(c, "(1 + " + a + " + " + a + "^2 + ... + " + a + "^(k-1))");
            substituicao = emLinhas(
                    aElevadoAK,
                    "T(n) = " + potenciaDeN + "*T(1) + " + multiplicar(c, "(" + potenciaDeN + " - 1)/(" + a + " - 1)"));
            coeficientes = List.of(zero, base.somar(parcelaGeometrica), zero, zero, parcelaGeometrica.negativa());
            comportamento = "Theta(" + potenciaDeNDecimal + ")   (" + expoenteExplicado + ")";
        } else if (a == b) {
            formaAposK = prefixoAposK + multiplicar(c, "n") + " + ... + " + multiplicar(c, "n") + "   (k parcelas)"
                    + " = " + parteRecursivaAposK + " + " + multiplicar(c, "k*n");
            substituicao = emLinhas(
                    "razao a/b = 1: cada um dos k niveis custa " + multiplicar(c, "n"),
                    aElevadoAK,
                    "T(n) = n*T(1) + " + multiplicar(c, "n*" + logDeN));
            coeficientes = List.of(Fracao.inteiro(c), zero, base, zero, zero);
            comportamento = "Theta(n log n)";
        } else {
            Fracao razao = new Fracao(a, b);
            String razaoTexto = razao.entreParentesesSeFracionaria();
            Fracao fatorDaSoma = new Fracao((long) c * b, Math.abs((long) b - a));
            String potenciaDeNNaSoma = potenciaDeN.isEmpty() ? "1" : potenciaDeN;
            formaAposK = prefixoAposK + multiplicar(c, "n*(1 + " + razaoTexto + " + " + razaoTexto + "^2 + ... + "
                    + razaoTexto + "^(k-1))");
            if (a < b) {
                substituicao = emLinhas(
                        "razao a/b = " + razao + " < 1: soma geometrica decrescente, dominada pelo primeiro termo",
                        aElevadoAK + "   e   " + razaoTexto + "^k = " + potenciaDeNNaSoma + "/n",
                        "T(n) = " + coeficiente(potenciaDeNNaSoma) + "T(1) + "
                                + FormaFechada.termoComCoeficiente(fatorDaSoma, "(n - " + potenciaDeNNaSoma + ")"));
                coeficientes = List.of(zero, base.subtrair(fatorDaSoma), fatorDaSoma, zero, zero);
                comportamento = "Theta(n)";
            } else {
                substituicao = emLinhas(
                        "razao a/b = " + razao + " > 1: soma geometrica crescente, dominada pelo ultimo nivel",
                        aElevadoAK + "   e   " + razaoTexto + "^k = " + potenciaDeN + "/n",
                        "T(n) = " + potenciaDeN + "*T(1) + "
                                + FormaFechada.termoComCoeficiente(fatorDaSoma, "(" + potenciaDeN + " - n)"));
                coeficientes = List.of(zero, base.somar(fatorDaSoma), fatorDaSoma.negativa(), zero, zero);
                comportamento = "Theta(" + potenciaDeNDecimal + ")   (" + expoenteExplicado + ")";
            }
        }

        FormaFechada formaFechada = new FormaFechada(coeficientes, termosDaFormaDividir(a, b));
        imprimirSolucao(recorrencia, expansao, formaAposK, achandoK, substituicao,
                "T(n) = " + formaFechada.em(1) + "   (n potencia de " + b + ")",
                inducaoDaFormaDividir(formaFechada, a, b, custo, casoBase),
                comportamento);
    }

    private static List<IntFunction<String>> termosDaFormaDividir(int a, int b) {
        IntFunction<String> argumento = divisor -> divisor == 1 ? "n" : "(n/" + divisor + ")";
        IntFunction<String> logaritmo = divisor -> "log_" + b + "(" + (divisor == 1 ? "n" : "n/" + divisor) + ")";
        List<IntFunction<String>> termos = new ArrayList<>();
        termos.add(divisor -> argumento.apply(divisor) + "*" + logaritmo.apply(divisor));
        termos.add(divisor -> potenciaDeN(argumento.apply(divisor), a, b));
        termos.add(argumento);
        termos.add(logaritmo);
        termos.add(divisor -> "");
        return termos;
    }

    private static String inducaoDaFormaDividir(FormaFechada formaFechada, int a, int b, CustoDoPasso custo, int casoBase) {
        Fracao razao = new Fracao(a, b);
        Fracao fatorA = Fracao.inteiro(a);
        Fracao nLogN = formaFechada.coeficiente(DIVIDIR_N_LOG_N);
        Fracao logN = formaFechada.coeficiente(DIVIDIR_LOG_N);
        FormaFechada aposAplicarRecorrencia = formaFechada.comCoeficientes(List.of(
                razao.multiplicar(nLogN),
                formaFechada.coeficiente(DIVIDIR_POTENCIA_DE_N),
                razao.multiplicar(formaFechada.coeficiente(DIVIDIR_N)).subtrair(razao.multiplicar(nLogN))
                        .somar(custo.parteLinear()),
                fatorA.multiplicar(logN),
                fatorA.multiplicar(formaFechada.coeficiente(DIVIDIR_CONSTANTE)).subtrair(fatorA.multiplicar(logN))
                        .somar(custo.parteConstante())));
        Fracao valorNaBase = formaFechada.coeficiente(DIVIDIR_POTENCIA_DE_N)
                .somar(formaFechada.coeficiente(DIVIDIR_N))
                .somar(formaFechada.coeficiente(DIVIDIR_CONSTANTE));

        List<String> propriedades = new ArrayList<>();
        if (!nLogN.ehZero() || !logN.ehZero()) {
            propriedades.add("log_" + b + "(n/" + b + ") = log_" + b + "(n) - 1");
        }
        String potenciaDeN = potenciaDeN("n", a, b);
        if (!formaFechada.coeficiente(DIVIDIR_POTENCIA_DE_N).ehZero() && !potenciaDeN.isEmpty()) {
            propriedades.add(a + "*" + potenciaDeN("(n/" + b + ")", a, b) + " = " + potenciaDeN);
        }

        return emLinhas(
                "Seja F(n) = " + formaFechada.em(1) + "   (n potencia de " + b + ")",
                "Base:     n = 1:  F(1) = " + valorNaBase + "  e  T(1) = " + casoBase
                        + conferencia(valorNaBase.compararCom(Fracao.inteiro(casoBase)) == 0),
                "Hipotese: T(m) = F(m) para toda potencia de " + b + " m < n",
                "Passo:    T(n) = " + coeficiente(a) + "T(n/" + b + ") + " + custo.em("n")
                        + " = " + coeficiente(a) + "[" + formaFechada.em(b) + "] + " + custo.em("n"),
                "            = " + aposAplicarRecorrencia.em(1)
                        + (propriedades.isEmpty() ? "" : "   (usando " + String.join(" e ", propriedades) + ")"),
                "            = F(n)" + conferencia(aposAplicarRecorrencia.mesmosCoeficientesQue(formaFechada)));
    }

    private static void resolverFormaSubtrair(int a, int d, int tipoDeCusto, int c, int nDoCasoBase, int casoBase) {
        String casoBaseTexto = "T(" + nDoCasoBase + ")";
        CustoDoPasso custo = new CustoDoPasso(tipoDeCusto, c);

        String distanciaAteABase = nDoCasoBase == 0 ? "n" : "n-" + nDoCasoBase;
        String k = d == 1 ? distanciaAteABase : (nDoCasoBase == 0 ? "n" : "(" + distanciaAteABase + ")") + "/" + d;
        String kEntreParenteses = k.equals("n") ? "n" : "(" + k + ")";
        String aElevadoAK = a + "^" + kEntreParenteses;
        String recorrencia = "T(n) = " + coeficiente(a) + "T(n-" + d + ") + " + custo.em("n")
                + ", se n > " + nDoCasoBase + ";  " + casoBaseTexto + " = " + casoBase;
        String expansao = "T(n) = " + coeficiente(a) + "[" + coeficiente(a) + "T(n-" + 2L * d + ") + "
                + custo.em("(n-" + d + ")") + "] + " + custo.em("n")
                + " = " + coeficiente(potencia(a, 2)) + "T(n-" + 2L * d + ") + "
                + custo.multiplicadoPor(a, "(n-" + d + ")") + " + " + custo.em("n");
        String kVezesD = multiplicar(d, "k");
        String ultimoDeslocamento = "(n-" + multiplicar(d, "(k-1)") + ")";
        String prefixoAposK = "T(n) = " + (a == 1 ? "" : a + "^k*") + "T(n-" + kVezesD + ") + ";
        String achandoK = "n - " + kVezesD + " = " + nDoCasoBase + "  =>  k = " + k
                + (d == 1 ? "" : "   (se " + d + " nao divide " + distanciaAteABase
                        + ", k = teto; o comportamento nao muda)");
        String comportamentoExponencial = "Theta(" + a + "^" + (d == 1 ? "n" : "(n/" + d + ")") + ")";

        Fracao base = Fracao.inteiro(casoBase);
        Fracao nBase = Fracao.inteiro(nDoCasoBase);
        Fracao zero = Fracao.inteiro(0);
        String formaAposK;
        String substituicao;
        String comportamento;
        List<Fracao> coeficientes;
        if (tipoDeCusto == CUSTO_CONSTANTE && a == 1) {
            Fracao inclinacao = new Fracao(c, d);
            formaAposK = prefixoAposK + multiplicar(c, "k") + "   (k parcelas iguais a " + c + ")";
            substituicao = emLinhas("T(n) = " + casoBaseTexto + " + " + multiplicar(c, "k") + " = " + casoBase + " + "
                    + multiplicar(c, kEntreParenteses));
            coeficientes = List.of(zero, zero, inclinacao, base.subtrair(inclinacao.multiplicar(nBase)));
            comportamento = "Theta(n)";
        } else if (tipoDeCusto == CUSTO_CONSTANTE) {
            Fracao parcelaGeometrica = new Fracao(c, a - 1L);
            formaAposK = prefixoAposK + multiplicar(c, "(1 + " + a + " + " + a + "^2 + ... + " + a + "^(k-1))");
            substituicao = emLinhas(
                    "T(n) = " + a + "^k*" + casoBaseTexto + " + " + multiplicar(c, "(" + a + "^k - 1)/(" + a + " - 1)")
                            + ",  com k = " + k,
                    "T(n) = " + aElevadoAK + "*" + casoBase + " + " + multiplicar(c, "(" + aElevadoAK + " - 1)/" + (a - 1)));
            coeficientes = List.of(base.somar(parcelaGeometrica), zero, zero, parcelaGeometrica.negativa());
            comportamento = comportamentoExponencial;
        } else if (a == 1) {
            Fracao coeficienteQuadratico = new Fracao(c, 2L * d);
            Fracao coeficienteLinear = new Fracao(c, 2);
            String somaDosCustos = "n + (n-" + d + ") + (n-" + 2L * d + ") + ... + " + ultimoDeslocamento;
            formaAposK = prefixoAposK + multiplicar(c, "[" + somaDosCustos + "]");
            substituicao = emLinhas(
                    "T(n) = " + casoBaseTexto + " + " + multiplicar(c, "(k*n - " + multiplicar(d, "k*(k-1)/2)"))
                            + ",  com k = " + k,
                    "soma de k termos que decrescem linearmente: cerca de n^2 no total");
            coeficientes = List.of(zero, coeficienteQuadratico, coeficienteLinear, base
                    .subtrair(coeficienteQuadratico.multiplicar(nBase).multiplicar(nBase))
                    .subtrair(coeficienteLinear.multiplicar(nBase)));
            comportamento = "Theta(n^2)";
        } else {
            Fracao parcelaLinear = new Fracao(c, a - 1L);
            Fracao parcelaConstante = new Fracao((long) a * c * d, 1)
                    .dividir(Fracao.inteiro(a - 1L).multiplicar(Fracao.inteiro(a - 1L)));
            String somaDosCustos = "n + " + a + "*(n-" + d + ") + " + a + "^2*(n-" + 2L * d + ") + ... + "
                    + a + "^(k-1)*" + ultimoDeslocamento;
            formaAposK = prefixoAposK + multiplicar(c, "[" + somaDosCustos + "]");
            substituicao = emLinhas(
                    "T(n) = " + a + "^k*" + casoBaseTexto + " + " + multiplicar(c, "[...]") + ",  com k = " + k,
                    "o termo " + aElevadoAK + " cresce mais rapido que a parte linear e domina a soma",
                    "T(n) = A*" + aElevadoAK + " + (alfa*n + beta), com alfa = " + parcelaLinear.negativa()
                            + ", beta = " + parcelaConstante.negativa() + " e A = " + casoBaseTexto
                            + " - alfa*" + nDoCasoBase + " - beta");
            coeficientes = List.of(
                    base.somar(parcelaLinear.multiplicar(nBase)).somar(parcelaConstante),
                    zero, parcelaLinear.negativa(), parcelaConstante.negativa());
            comportamento = comportamentoExponencial;
        }

        boolean expoenteDeslocado = nDoCasoBase % d != 0;
        Fracao valorDaExponencialNaBase = expoenteDeslocado
                ? Fracao.inteiro(1)
                : new Fracao(potencia(a, nDoCasoBase / d).longValueExact(), 1);
        List<Fracao> coeficientesFinais = new ArrayList<>(coeficientes);
        coeficientesFinais.set(SUBTRAIR_EXPONENCIAL, coeficientes.get(SUBTRAIR_EXPONENCIAL).dividir(valorDaExponencialNaBase));
        int deslocamentoDoExpoente = expoenteDeslocado ? nDoCasoBase : 0;
        FormaFechada formaFechada = new FormaFechada(
                coeficientesFinais, termosDaFormaSubtrair(a, d, deslocamentoDoExpoente));

        imprimirSolucao(recorrencia, expansao, formaAposK, achandoK, substituicao,
                "T(n) = " + formaFechada.em(0),
                inducaoDaFormaSubtrair(formaFechada, a, d, custo, nDoCasoBase, casoBase, valorDaExponencialNaBase,
                        deslocamentoDoExpoente),
                comportamento);
    }

    private static List<IntFunction<String>> termosDaFormaSubtrair(int a, int d, int deslocamentoDoExpoente) {
        IntFunction<String> argumento = deslocamento -> deslocamento == 0 ? "n" : "(n-" + deslocamento + ")";
        List<IntFunction<String>> termos = new ArrayList<>();
        termos.add(deslocamento -> {
            int deslocamentoTotal = deslocamento + deslocamentoDoExpoente;
            String numerador = deslocamentoTotal == 0 ? "n" : "(n-" + deslocamentoTotal + ")";
            return a + "^" + (d == 1 ? numerador : "(" + numerador + "/" + d + ")");
        });
        termos.add(deslocamento -> argumento.apply(deslocamento) + "^2");
        termos.add(argumento);
        termos.add(deslocamento -> "");
        return termos;
    }

    private static String inducaoDaFormaSubtrair(FormaFechada formaFechada, int a, int d, CustoDoPasso custo,
            int nDoCasoBase, int casoBase, Fracao valorDaExponencialNaBase, int deslocamentoDoExpoente) {
        Fracao fatorA = Fracao.inteiro(a);
        Fracao deslocamento = Fracao.inteiro(d);
        Fracao quadratico = formaFechada.coeficiente(SUBTRAIR_N_AO_QUADRADO);
        Fracao linear = formaFechada.coeficiente(SUBTRAIR_N);
        FormaFechada aposAplicarRecorrencia = formaFechada.comCoeficientes(List.of(
                formaFechada.coeficiente(SUBTRAIR_EXPONENCIAL),
                fatorA.multiplicar(quadratico),
                fatorA.multiplicar(linear)
                        .subtrair(Fracao.inteiro(2L * a * d).multiplicar(quadratico))
                        .somar(custo.parteLinear()),
                fatorA.multiplicar(quadratico).multiplicar(deslocamento).multiplicar(deslocamento)
                        .subtrair(fatorA.multiplicar(linear).multiplicar(deslocamento))
                        .somar(fatorA.multiplicar(formaFechada.coeficiente(SUBTRAIR_CONSTANTE)))
                        .somar(custo.parteConstante())));
        Fracao nBase = Fracao.inteiro(nDoCasoBase);
        Fracao valorNaBase = formaFechada.coeficiente(SUBTRAIR_EXPONENCIAL).multiplicar(valorDaExponencialNaBase)
                .somar(quadratico.multiplicar(nBase).multiplicar(nBase))
                .somar(linear.multiplicar(nBase))
                .somar(formaFechada.coeficiente(SUBTRAIR_CONSTANTE));
        IntFunction<String> exponencial = termosDaFormaSubtrair(a, d, deslocamentoDoExpoente).get(SUBTRAIR_EXPONENCIAL);
        String propriedade = formaFechada.coeficiente(SUBTRAIR_EXPONENCIAL).ehZero()
                ? ""
                : "   (usando " + a + "*" + exponencial.apply(d) + " = " + exponencial.apply(0) + ")";

        return emLinhas(
                "Seja F(n) = " + formaFechada.em(0)
                        + (d == 1 ? "" : "   (n = " + nDoCasoBase + " + multiplo de " + d + ")"),
                "Base:     n = " + nDoCasoBase + ":  F(" + nDoCasoBase + ") = " + valorNaBase + "  e  T(" + nDoCasoBase
                        + ") = " + casoBase + conferencia(valorNaBase.compararCom(Fracao.inteiro(casoBase)) == 0),
                "Hipotese: T(m) = F(m) para " + nDoCasoBase + " <= m < n",
                "Passo:    T(n) = " + coeficiente(a) + "T(n-" + d + ") + " + custo.em("n")
                        + " = " + coeficiente(a) + "[" + formaFechada.em(d) + "] + " + custo.em("n"),
                "            = " + aposAplicarRecorrencia.em(0) + propriedade,
                "            = F(n)" + conferencia(aposAplicarRecorrencia.mesmosCoeficientesQue(formaFechada)));
    }

    private static void resolverFormaDoisRamos(int p1, int q1, int p2, int q2) {
        Fracao primeira = new Fracao(p1, q1);
        Fracao segunda = new Fracao(p2, q2);
        Fracao soma = primeira.somar(segunda);
        String recorrencia = "T(n) = T(" + primeira + "*n) + T(" + segunda + "*n) + c*n";

        if (!soma.ehUm()) {
            imprimir(new String[] {"Recorrencia:", "Soma das fracoes:"}, recorrencia, primeira + " + " + segunda + " = " + soma);
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
                inducaoDaFormaDoisRamos(primeira, segunda, (double) p1 / q1, (double) p2 / q2),
                "Theta(n log n)");
    }

    private static String inducaoDaFormaDoisRamos(Fracao primeira, Fracao segunda, double valorDaPrimeira,
            double valorDaSegunda) {
        double entropia = -(valorDaPrimeira * logaritmoNaBase2(valorDaPrimeira)
                + valorDaSegunda * logaritmoNaBase2(valorDaSegunda));
        String entropiaTexto = String.format(Locale.US, "%.2f", entropia);
        String primeiraTexto = "(" + primeira + ")";
        String segundaTexto = "(" + segunda + ")";
        return emLinhas(
                "Palpite: T(n) = Theta(n log n).  Hipotese: T(m) <= k*m*log_2(m) para m < n",
                "T(n) <= k*" + primeiraTexto + "n*log_2(" + primeiraTexto + "n) + k*" + segundaTexto + "n*log_2("
                        + segundaTexto + "n) + c*n",
                "     = k*n*log_2(n) + k*n*[" + primeiraTexto + "*log_2" + primeiraTexto + " + " + segundaTexto
                        + "*log_2" + segundaTexto + "] + c*n   (pois " + primeira + " + " + segunda + " = 1)",
                "     = k*n*log_2(n) - " + entropiaTexto + "*k*n + c*n",
                "    <= k*n*log_2(n)   sempre que k >= c/" + entropiaTexto + CONFERE,
                "Limite inferior: com T(m) >= k*m*log_2(m) a mesma conta vale com >= quando k <= c/" + entropiaTexto + CONFERE,
                "Logo T(n) = Theta(n log n)");
    }

    private static double logaritmoNaBase2(double valor) {
        return Math.log(valor) / Math.log(2);
    }

    private static void imprimirSolucao(String... valores) {
        imprimir(ROTULOS_DA_SOLUCAO, valores);
    }

    private static void imprimir(String[] rotulos, String... valores) {
        System.out.println();
        System.out.println("=== Resolvedor de recorrencia ===");
        for (int posicao = 0; posicao < rotulos.length; posicao++) {
            System.out.println(String.format(FORMATO_DO_ROTULO, rotulos[posicao], valores[posicao]));
        }
    }

    private static String conferencia(boolean confere) {
        return confere ? CONFERE : NAO_CONFERE;
    }

    private static String emLinhas(String... linhas) {
        return String.join(System.lineSeparator() + RECUO_DE_CONTINUACAO, linhas);
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

    private static String potenciaDeN(String argumento, int a, int b) {
        int expoenteInteiro = expoenteInteiroExato(a, b);
        if (expoenteInteiro == 0) {
            return "";
        }
        if (expoenteInteiro == 1) {
            return argumento;
        }
        if (expoenteInteiro > 1) {
            return argumento + "^" + expoenteInteiro;
        }
        return argumento + "^(log_" + b + "(" + a + "))";
    }

    private static String potenciaDeNComExpoenteDecimal(int a, int b) {
        if (expoenteInteiroExato(a, b) >= 0) {
            return potenciaDeN("n", a, b);
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

        Fracao parteConstante() {
            return Fracao.inteiro(tipo == CUSTO_CONSTANTE ? coeficiente : 0);
        }

        Fracao parteLinear() {
            return Fracao.inteiro(tipo == CUSTO_LINEAR ? coeficiente : 0);
        }
    }
}
