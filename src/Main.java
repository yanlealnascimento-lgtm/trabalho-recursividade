import java.util.Scanner;

public class Main {

    private static final int OPCAO_SAIR = 0;
    private static final int OPCAO_RESOLVEDOR = 1;

    private static final Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        while (exibirMenuELerEscolha() == OPCAO_RESOLVEDOR) {
            ResolvedorDeRecorrencias.executar();
        }
        System.out.println("Programa encerrado.");
    }

    private static int exibirMenuELerEscolha() {
        System.out.println();
        System.out.println("===== RECURSIVIDADE =====");
        System.out.println(OPCAO_RESOLVEDOR + " - Resolvedor de recorrencias (analise generica)");
        System.out.println(OPCAO_SAIR + " - Sair");
        return lerOpcao("Escolha uma opcao: ", OPCAO_SAIR, OPCAO_RESOLVEDOR);
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
