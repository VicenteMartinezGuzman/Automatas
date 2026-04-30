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
        if (otro instanceof Inter) {
            throw new Exception("Error semántico: no se puede sumar 'dec' con 'inter'; "
                    + "declare ambas variables como 'dec' para operar decimales con enteros.");
        }
        if (otro instanceof Cad) {
            throw new Exception("Error semántico: no se puede sumar 'dec' con 'cad'; "
                    + "los tipos son incompatibles.");
        }
        throw new Exception("Error semántico: tipos incompatibles en operación suma 'dec'.");
    }
 
    @Override
    public TipoDato restar(TipoDato otro) throws Exception {
        if (otro instanceof Dec) {
            return new Dec(this.valor - ((Dec) otro).valor);
        }
        if (otro instanceof Inter) {
            throw new Exception("Error semántico: no se puede restar 'inter' de 'dec'; "
                    + "use variables del mismo tipo.");
        }
        if (otro instanceof Cad) {
            throw new Exception("Error semántico: no se puede restar 'cad' de 'dec'; "
                    + "los tipos son incompatibles.");
        }
        throw new Exception("Error semántico: tipos incompatibles en operación resta 'dec'.");
    }
 
    @Override
    public TipoDato multiplicar(TipoDato otro) throws Exception {
        if (otro instanceof Dec) {
            return new Dec(this.valor * ((Dec) otro).valor);
        }
        if (otro instanceof Inter) {
            throw new Exception("Error semántico: no se puede multiplicar 'dec' por 'inter'; "
                    + "use variables del mismo tipo.");
        }
        if (otro instanceof Cad) {
            throw new Exception("Error semántico: no se puede multiplicar 'dec' por 'cad'; "
                    + "los tipos son incompatibles.");
        }
        throw new Exception("Error semántico: tipos incompatibles en operación multiplicación 'dec'.");
    }
 
    @Override
    public TipoDato dividir(TipoDato otro) throws Exception {
        if (otro instanceof Dec) {
            double divisor = ((Dec) otro).valor;
            if (divisor == 0.0) {
                throw new Exception("Error semántico: división entre cero; "
                        + "el divisor no puede ser 0.0 en una operación 'dec'.");
            }
            return new Dec(this.valor / divisor);
        }
        if (otro instanceof Inter) {
            throw new Exception("Error semántico: no se puede dividir 'dec' entre 'inter'; "
                    + "use variables del mismo tipo.");
        }
        if (otro instanceof Cad) {
            throw new Exception("Error semántico: no se puede dividir 'dec' entre 'cad'; "
                    + "los tipos son incompatibles.");
        }
        throw new Exception("Error semántico: tipos incompatibles en operación división 'dec'.");
    }
 
    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}