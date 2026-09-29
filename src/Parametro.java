import java.util.function.Function;

public class Parametro {

    private final String pergunta;
    private final Function<String, Object> conversor;

    public Parametro(String pergunta, Function<String, Object> conversor) {
        this.pergunta = pergunta;
        this.conversor = conversor;
    }

    public String getPergunta() {
        return pergunta;
    }

    public Object converter(String textoDigitado) {
        return conversor.apply(textoDigitado);
    }
}
