/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codigo;

public class Cad extends TipoDato {

    private String nombre;
    private String valor;

    // ── Constructores 
    public Cad() {
        this.nombre = "";
        this.valor = "";
    }

    public Cad(String nombre, String valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    // ── Getters 
    public String getNombre() { return nombre; }
    public String getValor()  { return valor; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setValor(String valor)   { this.valor = valor; }

    // ── OPERACIONES (NO PERMITIDAS) 
    @Override
    public TipoDato sumar(TipoDato b) throws Exception {
        throw new Exception("Error semantico: no se puede sumar tipo 'cad'");
    }

    @Override
    public TipoDato restar(TipoDato b) throws Exception {
        throw new Exception("Error semantico: no se puede restar tipo 'cad'");
    }

    @Override
    public TipoDato multiplicar(TipoDato b) throws Exception {
        throw new Exception("Error semantico: no se puede multiplicar tipo 'cad'");
    }

    @Override
    public TipoDato dividir(TipoDato b) throws Exception {
        throw new Exception("Error semantico: no se puede dividir tipo 'cad'");
    }

    // ── VALIDACIÓN SEMÁNTICA 
    public void validarSemantica(String valor, int linea) throws Exception {

        valor = valor.trim();

        // entero
        if (valor.matches("^-?\\d+$")) {
            throw new Exception("Error semantico linea " + linea +
                ": no puedes asignar entero a 'cad'");
        }

        // decimal
        if (valor.matches("^-?\\d+\\.\\d+$")) {
            throw new Exception("Error semantico linea " + linea +
                ": no puedes asignar decimal a 'cad'");
        }

        // booleano
        if (valor.equals("true") || valor.equals("false")) {
            throw new Exception("Error semantico linea " + linea +
                ": no puedes asignar booleano a 'cad'");
        }

        this.valor = valor.replace("\"", "");
    }

    // ── SALIDA 
    @Override
    public String toString() {
        return "cad " + nombre + " = \"" + valor + "\";";
    }
}