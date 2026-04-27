/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codigo;
/**
 *
 * @author sandr
 */
public class Inter extends Operacion {
    private int valor;
    public Inter(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
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
        return a.intValue() + b.intValue();
    }

    @Override
    public Number restar(Number a, Number b) throws Exception {
        validar(a, b);
        return a.intValue() - b.intValue();
    }

    @Override
    public Number multiplicar(Number a, Number b) throws Exception {
        validar(a, b);
        return a.intValue() * b.intValue(); 
    }

    @Override
    public Number dividir(Number a, Number b) throws Exception {
        validar(a, b);

        if (b.intValue() == 0) {
            throw new ArithmeticException("Error semántico: división entre cero");
        }

        return a.intValue() / b.intValue();
    }

    public static Inter parseInter(String texto) throws Exception {
        if (texto == null || texto.isEmpty()) {
            throw new Exception("Error semántico: cadena vacía o null");
        }

        try {
            int num = Integer.parseInt(texto);
            return new Inter(num);
        } catch (NumberFormatException e) {
            throw new Exception("Error semántico: formato inválido " + texto);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}
