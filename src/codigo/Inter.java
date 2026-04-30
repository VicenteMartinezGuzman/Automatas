/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codigo;
/**
 *
 * @author sandr
 */
public class Inter extends TipoDato {
    private int valor;
 
    public Inter(int valor) {
        this.valor = valor;
    }
 
    public int getValor() {
        return valor;
    }
 
    @Override
    public TipoDato sumar(TipoDato otro) throws Exception {
        if (otro instanceof Inter) {
            return new Inter(this.valor + ((Inter) otro).valor);
        }
        if (otro instanceof Dec) {
            throw new Exception("Error semántico: no se puede sumar 'inter' con 'dec'; "
                    + "declare la variable como 'dec' si necesita operar decimales con enteros.");
        }
        if (otro instanceof Cad) {
            throw new Exception("Error semántico: no se puede sumar 'inter' con 'cad'; "
                    + "los tipos son incompatibles.");
        }
        throw new Exception("Error semántico: tipos incompatibles en operación suma 'inter'.");
    }
 
    @Override
    public TipoDato restar(TipoDato otro) throws Exception {
        if (otro instanceof Inter) {
            return new Inter(this.valor - ((Inter) otro).valor);
        }
        if (otro instanceof Dec) {
            throw new Exception("Error semántico: no se puede restar 'dec' de 'inter'; "
                    + "use variables del mismo tipo.");
        }
        if (otro instanceof Cad) {
            throw new Exception("Error semántico: no se puede restar 'cad' de 'inter'; "
                    + "los tipos son incompatibles.");
        }
        throw new Exception("Error semántico: tipos incompatibles en operación resta 'inter'.");
    }
 
    @Override
    public TipoDato multiplicar(TipoDato otro) throws Exception {
        if (otro instanceof Inter) {
            return new Inter(this.valor * ((Inter) otro).valor);
        }
        if (otro instanceof Dec) {
            throw new Exception("Error semántico: no se puede multiplicar 'inter' por 'dec'; "
                    + "use variables del mismo tipo.");
        }
        if (otro instanceof Cad) {
            throw new Exception("Error semántico: no se puede multiplicar 'inter' por 'cad'; "
                    + "los tipos son incompatibles.");
        }
        throw new Exception("Error semántico: tipos incompatibles en operación multiplicación 'inter'.");
    }
 
    @Override
    public TipoDato dividir(TipoDato otro) throws Exception {
        if (otro instanceof Inter) {
            int divisor = ((Inter) otro).valor;
            if (divisor == 0) {
                throw new Exception("Error semántico: división entre cero; "
                        + "el divisor no puede ser 0 en una operación 'inter'.");
            }
            return new Inter(this.valor / divisor);
        }
        if (otro instanceof Dec) {
            throw new Exception("Error semántico: no se puede dividir 'inter' entre 'dec'; "
                    + "use variables del mismo tipo.");
        }
        if (otro instanceof Cad) {
            throw new Exception("Error semántico: no se puede dividir 'inter' entre 'cad'; "
                    + "los tipos son incompatibles.");
        }
        throw new Exception("Error semántico: tipos incompatibles en operación división 'inter'.");
    }
 
    @Override
    public String toString() {
        return " " + valor + " ";
    }
}