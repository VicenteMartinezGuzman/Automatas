/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codigo;

/**
 *
 * @author vicen
 */
public class Token {
    String lexema;
    int tipo;
    Tokens tkns;

    public Token(String lexema, int tipo, Tokens tkns) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.tkns=tkns;
    }
    public Token(){
    }

    private Tokens getTkns() {
        return tkns;
    }

    private void setTkns(Tokens tkns) {
        this.tkns = tkns;
    }

    private String getLexema() {
        return lexema;
    }

    private void setLexema(String lexema) {
        this.lexema = lexema;
    }

    private int getTipo() {
        return tipo;
    }

    private void setTipo(int tipo) {
        this.tipo = tipo;
    }
     

    @Override
    public String toString() {
        return lexema + " -> " + tipo;
    }
}