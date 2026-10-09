import java.util.List;

public class Problema {

    private final String nome;
    private final String recorrencia;
    private final String solucaoDaRecorrencia;
    private final String comportamento;
    private final Solucao solucao;
    private final List<Parametro> parametros;

    public Problema(String nome, String recorrencia, String solucaoDaRecorrencia, String comportamento,
            Solucao solucao, Parametro... parametros) {
        this.nome = nome;
        this.recorrencia = recorrencia;
        this.solucaoDaRecorrencia = solucaoDaRecorrencia;
        this.comportamento = comportamento;
        this.solucao = solucao;
        this.parametros = List.of(parametros);
    }

    public String getNome() {
        return nome;
    }

    public List<Parametro> getParametros() {
        return parametros;
    }

    public String getRecorrencia() {
        return recorrencia;
    }

    public String getSolucao() {
        return solucaoDaRecorrencia;
    }

    public String getComportamento() {
        return comportamento;
    }

    public Object resolver(List<Object> argumentos) {
        return solucao.resolver(argumentos);
    }
}
