import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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
        int opcaoDoResolvedor = problemas.size() + 1;
        int escolha = exibirMenuELerEscolha(problemas, opcaoDoResolvedor);
        while (escolha != OPCAO_SAIR) {
            if (escolha == opcaoDoResolvedor) {
                ResolvedorDeRecorrencias.executar();
            } else {
                executarProblema(problemas.get(escolha - 1));
            }
            escolha = exibirMenuELerEscolha(problemas, opcaoDoResolvedor);
        }
        System.out.println("Programa encerrado.");
    }

    private static List<Problema> criarProblemas() {
        Parametro valorDeN = new Parametro("Informe o valor de n: ", Conversores.INTEIRO);
        Parametro texto = new Parametro("Informe o texto: ", Conversores.TEXTO);
        Parametro numero = new Parametro("Informe o numero: ", Conversores.INTEIRO);
        return List.of(
                new Problema("Fatorial", "f(n) = n * f(n-1), f(0) = 1", "f(n) = n!", "O(n) - linear",
                        argumentos -> Problemas.fatorial(inteiro(argumentos, 0)), valorDeN),
                new Problema("Fibonacci", "F(n) = F(n-1) + F(n-2), F(0)=0, F(1)=1",
                        "numero de chamadas cresce como 1.618^n (razao aurea)", "O(1.618^n) - exponencial",
                        argumentos -> Problemas.fibonacci(inteiro(argumentos, 0)), valorDeN),
                new Problema("Soma dos elementos de uma lista", "T(n) = T(n-1) + c", "T(n) = c*n + d", "O(n) - linear",
                        argumentos -> Problemas.somaLista(listaDeInteiros(argumentos, 0), 0),
                        new Parametro("Informe os numeros da lista (ex.: 1 2 3): ", Conversores.LISTA_DE_INTEIROS)),
                new Problema("Potencia (base elevada ao expoente)", "T(e) = T(e-1) + c  (e = expoente)",
                        "T(e) = c*e + d", "O(e) - linear no expoente",
                        argumentos -> Problemas.potencia(inteiro(argumentos, 0), inteiro(argumentos, 1)),
                        new Parametro("Informe a base: ", Conversores.INTEIRO),
                        new Parametro("Informe o expoente (inteiro nao negativo): ", Conversores.INTEIRO_NAO_NEGATIVO)),
                new Problema("Maximo divisor comum (Euclides)", "T(a,b) = T(b, a mod b) + c",
                        "sem formula fechada simples", "O(log min(a,b)) - logaritmico",
                        argumentos -> Problemas.mdc(inteiro(argumentos, 0), inteiro(argumentos, 1)),
                        new Parametro("Informe o primeiro numero: ", Conversores.INTEIRO),
                        new Parametro("Informe o segundo numero: ", Conversores.INTEIRO)),
                new Problema("Inverter texto", "T(n) = T(n-1) + c", "T(n) = c*n + d", "O(n) - linear",
                        argumentos -> Problemas.inverterTexto(texto(argumentos, 0)), texto),
                new Problema("Verificar palindromo", "T(n) = T(n-2) + c", "T(n) = c*(n/2) + d", "O(n) - linear",
                        argumentos -> Problemas.ehPalindromo(texto(argumentos, 0)) ? "sim, e palindromo" : "nao e palindromo",
                        texto),
                new Problema("Contar digitos de um numero", "T(v) = T(v/10) + c  (v = valor)",
                        "numero de digitos = log10(v) + 1", "O(log v) - logaritmico",
                        argumentos -> Problemas.contarDigitos(inteiro(argumentos, 0)), numero),
                new Problema("Soma dos digitos de um numero", "T(v) = T(v/10) + c", "percorre log10(v)+1 digitos",
                        "O(log v) - logaritmico",
                        argumentos -> Problemas.somaDigitos(inteiro(argumentos, 0)), numero),
                new Problema("Maior elemento de uma lista", "T(n) = T(n-1) + c", "T(n) = c*n + d", "O(n) - linear",
                        argumentos -> Problemas.maximoLista(listaDeInteiros(argumentos, 0), 0),
                        new Parametro("Informe os numeros da lista (ex.: 4 8 2): ", Conversores.LISTA_DE_INTEIROS_NAO_VAZIA)),
                new Problema("Busca binaria", "T(n) = T(n/2) + c", "T(n) = c*log2(n) + d", "O(log n) - logaritmico",
                        argumentos -> resolverBuscaBinaria(listaDeInteiros(argumentos, 0), inteiro(argumentos, 1)),
                        new Parametro("Informe os numeros da lista (ela sera ordenada antes da busca): ",
                                Conversores.LISTA_DE_INTEIROS),
                        new Parametro("Informe o numero a ser procurado: ", Conversores.INTEIRO)),
                new Problema("Torre de Hanoi", "T(n) = 2*T(n-1) + 1, T(0) = 0", "T(n) = 2^n - 1", "O(2^n) - exponencial",
                        argumentos -> resolverTorreDeHanoi(inteiro(argumentos, 0)),
                        new Parametro("Informe a quantidade de discos (0 a " + MAXIMO_DE_DISCOS_HANOI + "): ",
                                Conversores.inteiroEntre(0, MAXIMO_DE_DISCOS_HANOI))),
                new Problema("Permutacoes de um texto", "P(n) = n * P(n-1), P(0) = 1", "P(n) = n!", "O(n!) - fatorial",
                        argumentos -> listarComTitulo("Permutacoes encontradas", Problemas.permutacoes(texto(argumentos, 0))),
                        new Parametro("Informe o texto (no maximo " + TAMANHO_MAXIMO_PERMUTACOES + " caracteres): ",
                                Conversores.textoComTamanhoMaximo(TAMANHO_MAXIMO_PERMUTACOES))),
                new Problema("Subconjuntos de uma lista (conjunto das partes)", "T(n) = 2*T(n-1) + c", "2^n subconjuntos",
                        "O(2^n) - exponencial",
                        argumentos -> listarComTitulo("Subconjuntos encontrados",
                                formatarComoConjuntos(Problemas.subconjuntos(listaDeInteiros(argumentos, 0), 0))),
                        new Parametro("Informe os numeros da lista (no maximo " + TAMANHO_MAXIMO_SUBCONJUNTOS + "): ",
                                Conversores.listaDeInteirosComTamanhoMaximo(TAMANHO_MAXIMO_SUBCONJUNTOS))),
                new Problema("Funcao de Ackermann", "A(m,n) definida em dois indices (definicao de Ackermann)",
                        "sem formula fechada; cresce mais rapido que qualquer exponencial", "nao primitivo recursivo",
                        argumentos -> Problemas.ackermann(inteiro(argumentos, 0), inteiro(argumentos, 1)),
                        new Parametro("Informe m (0 a " + MAXIMO_M_ACKERMANN + ", cresce muito rapido): ",
                                Conversores.inteiroEntre(0, MAXIMO_M_ACKERMANN)),
                        new Parametro("Informe n (0 a " + MAXIMO_N_ACKERMANN + "): ",
                                Conversores.inteiroEntre(0, MAXIMO_N_ACKERMANN))),
                new Problema("Decimal para binario", "T(n) = T(n/2) + c", "gera log2(n) + 1 digitos", "O(log n) - logaritmico",
                        argumentos -> Problemas.decimalParaBinario(inteiro(argumentos, 0)),
                        new Parametro("Informe um numero inteiro nao negativo: ", Conversores.INTEIRO_NAO_NEGATIVO)),
                new Problema("Combinacao C(n, k) pelo triangulo de Pascal", "C(n,k) = C(n-1,k-1) + C(n-1,k)",
                        "valor = coeficiente binomial; numero de chamadas cresce exponencialmente",
                        "O(2^n) - exponencial (nesta versao recursiva)",
                        argumentos -> Problemas.combinacao(inteiro(argumentos, 0), inteiro(argumentos, 1)),
                        new Parametro("Informe n (0 a " + MAXIMO_N_COMBINACAO + "): ",
                                Conversores.inteiroEntre(0, MAXIMO_N_COMBINACAO)),
                        new Parametro("Informe k (inteiro nao negativo): ", Conversores.INTEIRO_NAO_NEGATIVO)));
    }

    private static int inteiro(List<Object> argumentos, int posicao) {
        return (Integer) argumentos.get(posicao);
    }

    private static String texto(List<Object> argumentos, int posicao) {
        return (String) argumentos.get(posicao);
    }

    @SuppressWarnings("unchecked")
    private static List<Integer> listaDeInteiros(List<Object> argumentos, int posicao) {
        return (List<Integer>) argumentos.get(posicao);
    }

    private static String resolverTorreDeHanoi(int discos) {
        List<String> movimentos = new ArrayList<>();
        long totalDeMovimentos = Problemas.torreDeHanoi(discos, TORRE_ORIGEM, TORRE_AUXILIAR, TORRE_DESTINO, movimentos);
        return "total de " + totalDeMovimentos + " movimento(s)" + listarComTitulo("Movimentos em ordem", movimentos);
    }

    private static String resolverBuscaBinaria(List<Integer> numeros, int alvo) {
        List<Integer> ordenados = numeros.stream().sorted().collect(Collectors.toList());
        int indice = Problemas.buscaBinaria(ordenados, alvo, 0, ordenados.size() - 1);
        if (indice == -1) {
            return alvo + " nao encontrado na lista ordenada " + ordenados + " (indice -1)";
        }
        return alvo + " encontrado no indice " + indice + " da lista ordenada " + ordenados;
    }

    private static String listarComTitulo(String titulo, List<?> itens) {
        StringBuilder listagem = new StringBuilder(System.lineSeparator() + titulo + " (" + itens.size() + "):");
        for (int posicao = 0; posicao < itens.size(); posicao++) {
            listagem.append(System.lineSeparator()).append("  ").append(posicao + 1).append(". ").append(itens.get(posicao));
        }
        return listagem.toString();
    }

    private static List<String> formatarComoConjuntos(List<List<Integer>> listas) {
        return listas.stream().map(lista -> lista.toString().replace('[', '{').replace(']', '}')).collect(Collectors.toList());
    }

    private static int exibirMenuELerEscolha(List<Problema> problemas, int opcaoDoResolvedor) {
        System.out.println();
        System.out.println("===== RECURSIVIDADE =====");
        for (int indice = 0; indice < problemas.size(); indice++) {
            System.out.println((indice + 1) + " - " + problemas.get(indice).getNome());
        }
        System.out.println(opcaoDoResolvedor + " - Resolvedor de recorrencias (analise generica)");
        System.out.println(OPCAO_SAIR + " - Sair");
        return lerOpcao("Escolha um problema: ", OPCAO_SAIR, opcaoDoResolvedor);
    }

    private static void executarProblema(Problema problema) {
        System.out.println();
        System.out.println("--- " + problema.getNome() + " ---");
        System.out.println("=== Analise da recorrencia ===");
        System.out.println("  Recorrencia:   " + problema.getRecorrencia());
        System.out.println("  Solucao:       " + problema.getSolucao());
        System.out.println("  Comportamento: " + problema.getComportamento());
        System.out.println();
        System.out.println(MODO_RESULTADO_DIRETO + " - Resultado direto");
        System.out.println(MODO_PASSO_A_PASSO + " - Passo a passo");
        boolean passoAPasso = lerOpcao("Escolha o modo: ", MODO_RESULTADO_DIRETO, MODO_PASSO_A_PASSO) == MODO_PASSO_A_PASSO;
        List<Object> argumentos = problema.getParametros().stream().map(Main::lerParametro).collect(Collectors.toList());
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

    private static Object resolver(Problema problema, List<Object> argumentos, boolean passoAPasso) {
        Rastreador.rastreioAtivo = passoAPasso;
        Rastreador.reiniciar();
        try {
            return problema.resolver(argumentos);
        } finally {
            Rastreador.rastreioAtivo = false;
            Rastreador.reiniciar();
        }
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

    private static int lerOpcao(String pergunta, int menorOpcao, int maiorOpcao) {
        while (true) {
            String textoDigitado = lerLinha(pergunta).trim();
            int opcao = textoDigitado.matches("[+-]?\\d{1,9}") ? Integer.parseInt(textoDigitado) : menorOpcao - 1;
            if (opcao >= menorOpcao && opcao <= maiorOpcao) {
                return opcao;
            }
            System.out.println("Opcao invalida. Digite um numero entre " + menorOpcao + " e " + maiorOpcao + ".");
        }
    }

    static String lerLinha(String pergunta) {
        System.out.print(pergunta);
        if (!entrada.hasNextLine()) {
            System.out.println();
            System.out.println("Entrada encerrada. Programa finalizado.");
            System.exit(0);
        }
        return entrada.nextLine();
    }
}
