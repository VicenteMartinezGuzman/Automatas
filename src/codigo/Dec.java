package codigo;

public class Dec extends TipoDato {

    private double valor;

    public Dec(double valor) {
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public TipoDato sumar(TipoDato otro) throws Exception {
        if (otro instanceof Dec) {
            return new Dec(this.valor + ((Dec) otro).valor);
        }
        throw new Exception("Error semantico: tipos incompatibles en suma");
    }

    @Override
    public TipoDato restar(TipoDato otro) throws Exception {
        if (otro instanceof Dec) {
            return new Dec(this.valor - ((Dec) otro).valor);
        }
        throw new Exception("Error semantico: tipos incompatibles en resta");
    }

    @Override
    public TipoDato multiplicar(TipoDato otro) throws Exception {
        if (otro instanceof Dec) {
            return new Dec(this.valor * ((Dec) otro).valor);
        }
        throw new Exception("Error semantico: tipos incompatibles en multiplicacion");
    }

    @Override
    public TipoDato dividir(TipoDato otro) throws Exception {
        if (otro instanceof Dec) {
            double val = ((Dec) otro).valor;
            if (val == 0.0) throw new Exception("Division entre cero");
            return new Dec(this.valor / val);
        }
        throw new Exception("Error semantico: tipos incompatibles en division");
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}