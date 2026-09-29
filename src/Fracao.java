import java.math.BigInteger;

public class Fracao {

    private final BigInteger numerador;
    private final BigInteger denominador;

    public Fracao(long numerador, long denominador) {
        this(BigInteger.valueOf(numerador), BigInteger.valueOf(denominador));
    }

    private Fracao(BigInteger numerador, BigInteger denominador) {
        if (denominador.signum() == 0) {
            throw new IllegalArgumentException("O denominador nao pode ser zero.");
        }
        BigInteger sinal = BigInteger.valueOf(denominador.signum());
        BigInteger divisor = numerador.gcd(denominador);
        this.numerador = numerador.divide(divisor).multiply(sinal);
        this.denominador = denominador.divide(divisor).multiply(sinal);
    }

    public static Fracao inteiro(long valor) {
        return new Fracao(valor, 1);
    }

    public Fracao somar(Fracao outra) {
        return new Fracao(
                numerador.multiply(outra.denominador).add(outra.numerador.multiply(denominador)),
                denominador.multiply(outra.denominador));
    }

    public Fracao subtrair(Fracao outra) {
        return somar(outra.negativa());
    }

    public Fracao multiplicar(Fracao outra) {
        return new Fracao(numerador.multiply(outra.numerador), denominador.multiply(outra.denominador));
    }

    public Fracao dividir(Fracao outra) {
        return new Fracao(numerador.multiply(outra.denominador), denominador.multiply(outra.numerador));
    }

    public Fracao negativa() {
        return new Fracao(numerador.negate(), denominador);
    }

    public Fracao inversa() {
        return new Fracao(denominador, numerador);
    }

    public Fracao absoluta() {
        return new Fracao(numerador.abs(), denominador);
    }

    public boolean ehZero() {
        return numerador.signum() == 0;
    }

    public boolean ehNegativa() {
        return numerador.signum() < 0;
    }

    public boolean ehUm() {
        return numerador.equals(denominador);
    }

    public boolean ehInteira() {
        return denominador.equals(BigInteger.ONE);
    }

    public int compararCom(Fracao outra) {
        return numerador.multiply(outra.denominador).compareTo(outra.numerador.multiply(denominador));
    }

    public String entreParentesesSeFracionaria() {
        return ehInteira() ? toString() : "(" + this + ")";
    }

    @Override
    public String toString() {
        return ehInteira() ? numerador.toString() : numerador + "/" + denominador;
    }
}
