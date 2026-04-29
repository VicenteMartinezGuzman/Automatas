/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codigo;

/**
 *
 * @author aosr6
 */
public class Dec extends TipoDato {
    private double valor;

    public Dec (double valor) {
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    private void validar(Number a, Number b) throws Exception {
        if (a == null || b == null) {
            throw new Exception("Error semántico: valores null no permitidos");
        }
    }

    @Override
    public Number sumar(Number a, Number b) throws Exception {
        validar(a, b);
        return a.doubleValue() + b.doubleValue();
    }

    @Override
    public Number restar(Number a, Number b) throws Exception {
        validar(a, b);
        return a.doubleValue() - b.doubleValue();
    }

    @Override
    public Number multiplicar(Number a, Number b) throws Exception {
        validar(a, b);
        return a.doubleValue() * b.doubleValue();
    }

    @Override
    public Number dividir(Number a, Number b) throws Exception {
        validar(a, b);
        if (b.doubleValue() == 0.0) {
            throw new ArithmeticException("Error semántico: división entre cero");
        }
        return a.doubleValue() / b.doubleValue();
    }

    public static Doec parseDec(String texto) throws Exception {
        if (texto == null || texto.isEmpty()) {
            throw new Exception("Error semántico: cadena vacía o null");
        }

        try {
            double num = Double.parseDouble(texto);
            return new Doec(num);
        } catch (NumberFormatException e) {
            throw new Exception("Error semántico: formato decimal inválido " + texto);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }

    @Override
    public TipoDato sumar(TipoDato otro) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TipoDato restar(TipoDato otro) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TipoDato multiplicar(TipoDato otro) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TipoDato dividir(TipoDato otro) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
