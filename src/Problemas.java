import java.util.ArrayList;
import java.util.List;

public class Problemas {

    public static long fatorial(int n) {
        Rastreador.entrar("fatorial", n);
        if (n <= 1) {
            return Rastreador.sair(1L);
        }
        return Rastreador.sair(n * fatorial(n - 1));
    }

    public static long fibonacci(int n) {
        Rastreador.entrar("fibonacci", n);
        if (n <= 1) {
            return Rastreador.sair((long) n);
        }
        return Rastreador.sair(fibonacci(n - 1) + fibonacci(n - 2));
    }

    public static long somaLista(List<Integer> numeros, int indice) {
        Rastreador.entrar("somaLista", numeros, indice);
        if (indice >= numeros.size()) {
            return Rastreador.sair(0L);
        }
        return Rastreador.sair(numeros.get(indice) + somaLista(numeros, indice + 1));
    }

    public static long potencia(int base, int expoente) {
        Rastreador.entrar("potencia", base, expoente);
        if (expoente == 0) {
            return Rastreador.sair(1L);
        }
        return Rastreador.sair(base * potencia(base, expoente - 1));
    }

    public static int mdc(int a, int b) {
        Rastreador.entrar("mdc", a, b);
        if (b == 0) {
            return Rastreador.sair(a);
        }
        return Rastreador.sair(mdc(b, a % b));
    }

    public static String inverterTexto(String texto) {
        Rastreador.entrar("inverterTexto", texto);
        if (texto.length() <= 1) {
            return Rastreador.sair(texto);
        }
        return Rastreador.sair(inverterTexto(texto.substring(1)) + texto.charAt(0));
    }

    public static boolean ehPalindromo(String texto) {
        Rastreador.entrar("ehPalindromo", texto);
        if (texto.length() <= 1) {
            return Rastreador.sair(true);
        }
        char primeiraLetra = Character.toLowerCase(texto.charAt(0));
        char ultimaLetra = Character.toLowerCase(texto.charAt(texto.length() - 1));
        if (primeiraLetra != ultimaLetra) {
            return Rastreador.sair(false);
        }
        return Rastreador.sair(ehPalindromo(texto.substring(1, texto.length() - 1)));
    }

    public static int contarDigitos(long numero) {
        Rastreador.entrar("contarDigitos", numero);
        if (numero < 0) {
            return Rastreador.sair(contarDigitos(Math.abs(numero)));
        }
        if (numero < 10) {
            return Rastreador.sair(1);
        }
        return Rastreador.sair(1 + contarDigitos(numero / 10));
    }

    public static long somaDigitos(long numero) {
        Rastreador.entrar("somaDigitos", numero);
        if (numero < 0) {
            return Rastreador.sair(somaDigitos(Math.abs(numero)));
        }
        if (numero < 10) {
            return Rastreador.sair(numero);
        }
        return Rastreador.sair(numero % 10 + somaDigitos(numero / 10));
    }

    public static int maximoLista(List<Integer> numeros, int indice) {
        Rastreador.entrar("maximoLista", numeros, indice);
        if (indice == numeros.size() - 1) {
            return Rastreador.sair(numeros.get(indice));
        }
        return Rastreador.sair(Math.max(numeros.get(indice), maximoLista(numeros, indice + 1)));
    }

    public static int buscaBinaria(List<Integer> numeros, int alvo, int inicio, int fim) {
        Rastreador.entrar("buscaBinaria", numeros, alvo, inicio, fim);
        if (inicio > fim) {
            return Rastreador.sair(-1);
        }
        int meio = inicio + (fim - inicio) / 2;
        int valorDoMeio = numeros.get(meio);
        if (valorDoMeio == alvo) {
            return Rastreador.sair(meio);
        }
        if (valorDoMeio < alvo) {
            return Rastreador.sair(buscaBinaria(numeros, alvo, meio + 1, fim));
        }
        return Rastreador.sair(buscaBinaria(numeros, alvo, inicio, meio - 1));
    }

    public static long torreDeHanoi(int discos, char origem, char auxiliar, char destino, List<String> movimentos) {
        Rastreador.entrar("torreDeHanoi", discos, origem, auxiliar, destino);
        if (discos == 0) {
            return Rastreador.sair(0L);
        }
        long movimentosAntes = torreDeHanoi(discos - 1, origem, destino, auxiliar, movimentos);
        movimentos.add(origem + " -> " + destino);
        long movimentosDepois = torreDeHanoi(discos - 1, auxiliar, origem, destino, movimentos);
        return Rastreador.sair(movimentosAntes + 1 + movimentosDepois);
    }

    public static List<String> permutacoes(String texto) {
        Rastreador.entrar("permutacoes", texto);
        List<String> resultado = new ArrayList<>();
        if (texto.length() <= 1) {
            resultado.add(texto);
            return Rastreador.sair(resultado);
        }
        for (int posicao = 0; posicao < texto.length(); posicao++) {
            char caractereFixado = texto.charAt(posicao);
            String restante = texto.substring(0, posicao) + texto.substring(posicao + 1);
            for (String permutacaoDoRestante : permutacoes(restante)) {
                resultado.add(caractereFixado + permutacaoDoRestante);
            }
        }
        return Rastreador.sair(resultado);
    }

    public static List<List<Integer>> subconjuntos(List<Integer> numeros, int indice) {
        Rastreador.entrar("subconjuntos", numeros, indice);
        List<List<Integer>> resultado = new ArrayList<>();
        if (indice == numeros.size()) {
            resultado.add(new ArrayList<>());
            return Rastreador.sair(resultado);
        }
        List<List<Integer>> subconjuntosSemOElemento = subconjuntos(numeros, indice + 1);
        resultado.addAll(subconjuntosSemOElemento);
        for (List<Integer> subconjunto : subconjuntosSemOElemento) {
            List<Integer> subconjuntoComOElemento = new ArrayList<>();
            subconjuntoComOElemento.add(numeros.get(indice));
            subconjuntoComOElemento.addAll(subconjunto);
            resultado.add(subconjuntoComOElemento);
        }
        return Rastreador.sair(resultado);
    }

    public static int ackermann(int m, int n) {
        Rastreador.entrar("ackermann", m, n);
        if (m == 0) {
            return Rastreador.sair(n + 1);
        }
        if (n == 0) {
            return Rastreador.sair(ackermann(m - 1, 1));
        }
        return Rastreador.sair(ackermann(m - 1, ackermann(m, n - 1)));
    }

    public static String decimalParaBinario(int numero) {
        Rastreador.entrar("decimalParaBinario", numero);
        if (numero < 2) {
            return Rastreador.sair(String.valueOf(numero));
        }
        return Rastreador.sair(decimalParaBinario(numero / 2) + numero % 2);
    }

    public static long combinacao(int n, int k) {
        Rastreador.entrar("combinacao", n, k);
        if (k < 0 || k > n) {
            return Rastreador.sair(0L);
        }
        if (k == 0 || k == n) {
            return Rastreador.sair(1L);
        }
        return Rastreador.sair(combinacao(n - 1, k - 1) + combinacao(n - 1, k));
    }
}
