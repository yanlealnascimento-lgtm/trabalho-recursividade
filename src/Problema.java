import java.util.List;

public class Problema {

    private final String nome;
    private final List<Parametro> parametros;
    private final Solucao solucao;
    private final String recorrencia;
    private final String solucaoDaRecorrencia;
    private final String comportamento;

    public Problema(String nome, List<Parametro> parametros, Solucao solucao,
            String recorrencia, String solucaoDaRecorrencia, String comportamento) {
        this.nome = nome;
        this.parametros = parametros;
        this.solucao = solucao;
        this.recorrencia = recorrencia;
        this.solucaoDaRecorrencia = solucaoDaRecorrencia;
        this.comportamento = comportamento;
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
