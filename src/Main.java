import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final int OPCAO_SAIR = 0;
    private static final int MODO_RESULTADO_DIRETO = 1;
    private static final int MODO_PASSO_A_PASSO = 2;

    private static final int MAXIMO_DE_DISCOS_HANOI = 10;
    private static final int TAMANHO_MAXIMO_PERMUTACOES = 8;
    private static final int TAMANHO_MAXIMO_SUBCONJUNTOS = 10;
    private static final int MAXIMO_M_ACKERMANN = 3;
    private static final int MAXIMO_N_ACKERMANN = 6;
    private static final int MAXIMO_N_COMBINACAO = 20;
    private static final char TORRE_ORIGEM = 'A';
    private static final char TORRE_AUXILIAR = 'B';
    private static final char TORRE_DESTINO = 'C';

    private static final Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        List<Problema> problemas = criarProblemas();
        int escolha = exibirMenuELerEscolha(problemas);
        while (escolha != OPCAO_SAIR) {
            if (escolha == opcaoDoResolvedor(problemas)) {
                ResolvedorDeRecorrencias.executar();
            } else {
                executarProblema(problemas.get(escolha - 1));
            }
            escolha = exibirMenuELerEscolha(problemas);
        }
        System.out.println("Programa encerrado.");
    }

    private static List<Problema> criarProblemas() {
        List<Problema> problemas = new ArrayList<>();
        problemas.add(new Problema(
                "Fatorial",
                List.of(new Parametro("Informe o valor de n: ", Conversores.INTEIRO)),
                argumentos -> Problemas.fatorial((Integer) argumentos.get(0)),
                "f(n) = n * f(n-1), f(0) = 1",
                "f(n) = n!",
                "O(n) - linear"));
        problemas.add(new Problema(
                "Fibonacci",
                List.of(new Parametro("Informe o valor de n: ", Conversores.INTEIRO)),
                argumentos -> Problemas.fibonacci((Integer) argumentos.get(0)),
                "F(n) = F(n-1) + F(n-2), F(0)=0, F(1)=1",
                "numero de chamadas cresce como 1.618^n (razao aurea)",
                "O(1.618^n) - exponencial"));
        problemas.add(new Problema(
                "Soma dos elementos de uma lista",
                List.of(new Parametro("Informe os numeros da lista (ex.: 1 2 3): ", Conversores.LISTA_DE_INTEIROS)),
                argumentos -> Problemas.somaLista(comoListaDeInteiros(argumentos.get(0)), 0),
                "T(n) = T(n-1) + c",
                "T(n) = c*n + d",
                "O(n) - linear"));
        problemas.add(new Problema(
                "Potencia (base elevada ao expoente)",
                List.of(
                        new Parametro("Informe a base: ", Conversores.INTEIRO),
                        new Parametro("Informe o expoente (inteiro nao negativo): ", Conversores.INTEIRO_NAO_NEGATIVO)),
                argumentos -> Problemas.potencia((Integer) argumentos.get(0), (Integer) argumentos.get(1)),
                "T(e) = T(e-1) + c  (e = expoente)",
                "T(e) = c*e + d",
                "O(e) - linear no expoente"));
        problemas.add(new Problema(
                "Maximo divisor comum (Euclides)",
                List.of(
                        new Parametro("Informe o primeiro numero: ", Conversores.INTEIRO),
                        new Parametro("Informe o segundo numero: ", Conversores.INTEIRO)),
                argumentos -> Problemas.mdc((Integer) argumentos.get(0), (Integer) argumentos.get(1)),
                "T(a,b) = T(b, a mod b) + c",
                "sem formula fechada simples",
                "O(log min(a,b)) - logaritmico"));
        problemas.add(new Problema(
                "Inverter texto",
                List.of(new Parametro("Informe o texto: ", Conversores.TEXTO)),
                argumentos -> Problemas.inverterTexto((String) argumentos.get(0)),
                "T(n) = T(n-1) + c",
                "T(n) = c*n + d",
                "O(n) - linear"));
        problemas.add(new Problema(
                "Verificar palindromo",
                List.of(new Parametro("Informe o texto: ", Conversores.TEXTO)),
                argumentos -> Problemas.ehPalindromo((String) argumentos.get(0)) ? "sim, e palindromo" : "nao e palindromo",
                "T(n) = T(n-2) + c",
                "T(n) = c*(n/2) + d",
                "O(n) - linear"));
        problemas.add(new Problema(
                "Contar digitos de um numero",
                List.of(new Parametro("Informe o numero: ", Conversores.INTEIRO)),
                argumentos -> Problemas.contarDigitos((Integer) argumentos.get(0)),
                "T(v) = T(v/10) + c  (v = valor)",
                "numero de digitos = log10(v) + 1",
                "O(log v) - logaritmico"));
        problemas.add(new Problema(
                "Soma dos digitos de um numero",
                List.of(new Parametro("Informe o numero: ", Conversores.INTEIRO)),
                argumentos -> Problemas.somaDigitos((Integer) argumentos.get(0)),
                "T(v) = T(v/10) + c",
                "percorre log10(v)+1 digitos",
                "O(log v) - logaritmico"));
        problemas.add(new Problema(
                "Maior elemento de uma lista",
                List.of(new Parametro("Informe os numeros da lista (ex.: 4 8 2): ", Conversores.LISTA_DE_INTEIROS_NAO_VAZIA)),
                argumentos -> Problemas.maximoLista(comoListaDeInteiros(argumentos.get(0)), 0),
                "T(n) = T(n-1) + c",
                "T(n) = c*n + d",
                "O(n) - linear"));
        problemas.add(new Problema(
                "Busca binaria",
                List.of(
                        new Parametro("Informe os numeros da lista (ela sera ordenada antes da busca): ", Conversores.LISTA_DE_INTEIROS),
                        new Parametro("Informe o numero a ser procurado: ", Conversores.INTEIRO)),
                argumentos -> resolverBuscaBinaria(comoListaDeInteiros(argumentos.get(0)), (Integer) argumentos.get(1)),
                "T(n) = T(n/2) + c",
                "T(n) = c*log2(n) + d",
                "O(log n) - logaritmico"));
        problemas.add(new Problema(
                "Torre de Hanoi",
                List.of(new Parametro(
                        "Informe a quantidade de discos (0 a " + MAXIMO_DE_DISCOS_HANOI + "): ",
                        Conversores.inteiroEntre(0, MAXIMO_DE_DISCOS_HANOI))),
                argumentos -> resolverTorreDeHanoi((Integer) argumentos.get(0)),
                "T(n) = 2*T(n-1) + 1, T(0) = 0",
                "T(n) = 2^n - 1",
                "O(2^n) - exponencial"));
        problemas.add(new Problema(
                "Permutacoes de um texto",
                List.of(new Parametro(
                        "Informe o texto (no maximo " + TAMANHO_MAXIMO_PERMUTACOES + " caracteres): ",
                        Conversores.textoComTamanhoMaximo(TAMANHO_MAXIMO_PERMUTACOES))),
                argumentos -> listarComTitulo(
                        "Permutacoes encontradas",
                        Problemas.permutacoes((String) argumentos.get(0))),
                "P(n) = n * P(n-1), P(0) = 1",
                "P(n) = n!",
                "O(n!) - fatorial"));
        problemas.add(new Problema(
                "Subconjuntos de uma lista (conjunto das partes)",
                List.of(new Parametro(
                        "Informe os numeros da lista (no maximo " + TAMANHO_MAXIMO_SUBCONJUNTOS + "): ",
                        Conversores.listaDeInteirosComTamanhoMaximo(TAMANHO_MAXIMO_SUBCONJUNTOS))),
                argumentos -> listarComTitulo(
                        "Subconjuntos encontrados",
                        formatarComoConjuntos(Problemas.subconjuntos(comoListaDeInteiros(argumentos.get(0)), 0))),
                "T(n) = 2*T(n-1) + c",
                "2^n subconjuntos",
                "O(2^n) - exponencial"));
        problemas.add(new Problema(
                "Funcao de Ackermann",
                List.of(
                        new Parametro(
                                "Informe m (0 a " + MAXIMO_M_ACKERMANN + ", cresce muito rapido): ",
                                Conversores.inteiroEntre(0, MAXIMO_M_ACKERMANN)),
                        new Parametro(
                                "Informe n (0 a " + MAXIMO_N_ACKERMANN + "): ",
                                Conversores.inteiroEntre(0, MAXIMO_N_ACKERMANN))),
                argumentos -> Problemas.ackermann((Integer) argumentos.get(0), (Integer) argumentos.get(1)),
                "A(m,n) definida em dois indices (definicao de Ackermann)",
                "sem formula fechada; cresce mais rapido que qualquer exponencial",
                "nao primitivo recursivo"));
        problemas.add(new Problema(
                "Decimal para binario",
                List.of(new Parametro("Informe um numero inteiro nao negativo: ", Conversores.INTEIRO_NAO_NEGATIVO)),
                argumentos -> Problemas.decimalParaBinario((Integer) argumentos.get(0)),
                "T(n) = T(n/2) + c",
                "gera log2(n) + 1 digitos",
                "O(log n) - logaritmico"));
        problemas.add(new Problema(
                "Combinacao C(n, k) pelo triangulo de Pascal",
                List.of(
                        new Parametro(
                                "Informe n (0 a " + MAXIMO_N_COMBINACAO + "): ",
                                Conversores.inteiroEntre(0, MAXIMO_N_COMBINACAO)),
                        new Parametro("Informe k (inteiro nao negativo): ", Conversores.INTEIRO_NAO_NEGATIVO)),
                argumentos -> Problemas.combinacao((Integer) argumentos.get(0), (Integer) argumentos.get(1)),
                "C(n,k) = C(n-1,k-1) + C(n-1,k)",
                "valor = coeficiente binomial; numero de chamadas cresce exponencialmente",
                "O(2^n) - exponencial (nesta versao recursiva)"));
        return problemas;
    }

    private static String resolverTorreDeHanoi(int discos) {
        List<String> movimentos = new ArrayList<>();
        long totalDeMovimentos = Problemas.torreDeHanoi(
                discos, TORRE_ORIGEM, TORRE_AUXILIAR, TORRE_DESTINO, movimentos);
        return "total de " + totalDeMovimentos + " movimento(s)" + listarComTitulo("Movimentos em ordem", movimentos);
    }

    private static String listarComTitulo(String titulo, List<?> itens) {
        StringBuilder listagem = new StringBuilder();
        listagem.append(System.lineSeparator()).append(titulo).append(" (").append(itens.size()).append("):");
        for (int posicao = 0; posicao < itens.size(); posicao++) {
            listagem.append(System.lineSeparator())
                    .append("  ").append(posicao + 1).append(". ").append(itens.get(posicao));
        }
        return listagem.toString();
    }

    private static List<String> formatarComoConjuntos(List<List<Integer>> listas) {
        List<String> conjuntos = new ArrayList<>();
        for (List<Integer> lista : listas) {
            conjuntos.add(lista.toString().replace('[', '{').replace(']', '}'));
        }
        return conjuntos;
    }

    private static String resolverBuscaBinaria(List<Integer> numeros, int alvo) {
        List<Integer> numerosOrdenados = new ArrayList<>(numeros);
        Collections.sort(numerosOrdenados);
        int indiceEncontrado = Problemas.buscaBinaria(numerosOrdenados, alvo, 0, numerosOrdenados.size() - 1);
        if (indiceEncontrado == -1) {
            return alvo + " nao encontrado na lista ordenada " + numerosOrdenados + " (indice -1)";
        }
        return alvo + " encontrado no indice " + indiceEncontrado + " da lista ordenada " + numerosOrdenados;
    }

    @SuppressWarnings("unchecked")
    private static List<Integer> comoListaDeInteiros(Object valor) {
        return (List<Integer>) valor;
    }

    private static int exibirMenuELerEscolha(List<Problema> problemas) {
        System.out.println();
        System.out.println("===== RECURSIVIDADE =====");
        for (int indice = 0; indice < problemas.size(); indice++) {
            System.out.println((indice + 1) + " - " + problemas.get(indice).getNome());
        }
        System.out.println(opcaoDoResolvedor(problemas) + " - Resolvedor de recorrencias (analise generica)");
        System.out.println(OPCAO_SAIR + " - Sair");
        return lerOpcao("Escolha um problema: ", OPCAO_SAIR, opcaoDoResolvedor(problemas));
    }

    private static int opcaoDoResolvedor(List<Problema> problemas) {
        return problemas.size() + 1;
    }

    private static void executarProblema(Problema problema) {
        System.out.println();
        System.out.println("--- " + problema.getNome() + " ---");
        exibirAnaliseDaRecorrencia(problema);
        System.out.println(MODO_RESULTADO_DIRETO + " - Resultado direto");
        System.out.println(MODO_PASSO_A_PASSO + " - Passo a passo");
        int modo = lerOpcao("Escolha o modo: ", MODO_RESULTADO_DIRETO, MODO_PASSO_A_PASSO);
        List<Object> argumentos = lerArgumentos(problema.getParametros());
        boolean passoAPasso = modo == MODO_PASSO_A_PASSO;

        if (passoAPasso) {
            System.out.println();
            System.out.println("Passo a passo:");
        }
        try {
            Object resultado = resolver(problema, argumentos, passoAPasso);
            System.out.println();
            System.out.println("Resultado de " + problema.getNome() + " " + argumentos + " = " + resultado);
        } catch (StackOverflowError erro) {
            System.out.println("Nao foi possivel resolver: a recursao ficou profunda demais para os valores informados.");
        }
    }

    private static void exibirAnaliseDaRecorrencia(Problema problema) {
        System.out.println("=== Analise da recorrencia ===");
        System.out.println("  Recorrencia:   " + problema.getRecorrencia());
        System.out.println("  Solucao:       " + problema.getSolucao());
        System.out.println("  Comportamento: " + problema.getComportamento());
        System.out.println();
    }

    private static Object resolver(Problema problema, List<Object> argumentos, boolean passoAPasso) {
        if (passoAPasso) {
            Rastreador.rastreioAtivo = true;
            Rastreador.reiniciar();
        }
        try {
            return problema.resolver(argumentos);
        } finally {
            Rastreador.rastreioAtivo = false;
            Rastreador.reiniciar();
        }
    }

    private static List<Object> lerArgumentos(List<Parametro> parametros) {
        List<Object> argumentos = new ArrayList<>();
        for (Parametro parametro : parametros) {
            argumentos.add(lerParametro(parametro));
        }
        return argumentos;
    }

    static Object lerParametro(Parametro parametro) {
        while (true) {
            String textoDigitado = lerLinha(parametro.getPergunta());
            if (textoDigitado.trim().isEmpty()) {
                System.out.println("Nenhum valor informado. Tente novamente.");
                continue;
            }
            try {
                return parametro.converter(textoDigitado);
            } catch (NumberFormatException erro) {
                System.out.println("Valor invalido. Tente novamente.");
            } catch (IllegalArgumentException erro) {
                System.out.println(erro.getMessage() + " Tente novamente.");
            }
        }
    }

    static int lerOpcao(String pergunta, int menorOpcao, int maiorOpcao) {
        while (true) {
            String textoDigitado = lerLinha(pergunta).trim();
            if (ehOpcaoValida(textoDigitado, menorOpcao, maiorOpcao)) {
                return Integer.parseInt(textoDigitado);
            }
            System.out.println("Opcao invalida. Digite um numero entre " + menorOpcao + " e " + maiorOpcao + ".");
        }
    }

    private static boolean ehOpcaoValida(String textoDigitado, int menorOpcao, int maiorOpcao) {
        try {
            int opcao = Integer.parseInt(textoDigitado);
            return opcao >= menorOpcao && opcao <= maiorOpcao;
        } catch (NumberFormatException erro) {
            return false;
        }
    }

    private static String lerLinha(String pergunta) {
        System.out.print(pergunta);
        if (!entrada.hasNextLine()) {
            System.out.println();
            System.out.println("Entrada encerrada. Programa finalizado.");
            System.exit(0);
        }
        return entrada.nextLine();
    }
}
