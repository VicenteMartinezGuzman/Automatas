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
        throw new Exception("Error semántico: tipos incompatibles");
    }

    @Override
    public TipoDato restar(TipoDato otro) throws Exception {
        if (otro instanceof Inter) {
            return new Inter(this.valor - ((Inter) otro).valor);
        }
        throw new Exception("Error semántico: tipos incompatibles");
    }

    @Override
    public TipoDato multiplicar(TipoDato otro) throws Exception {
        if (otro instanceof Inter) {
            return new Inter(this.valor * ((Inter) otro).valor);
        }
        throw new Exception("Error semántico: tipos incompatibles");
    }

    @Override
    public TipoDato dividir(TipoDato otro) throws Exception {
        if (otro instanceof Inter) {
            int val = ((Inter) otro).valor;
            if (val == 0) throw new Exception("División entre cero");
            return new Inter(this.valor / val);
        }
        throw new Exception("Error semántico: tipos incompatibles");
    }
    
    @Override
    public String toString() {
        return  " "+valor+" ";
    }
}