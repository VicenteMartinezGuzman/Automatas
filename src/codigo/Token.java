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

    public Tokens getTkns() {
        return tkns;
    }

    public void setTkns(Tokens tkns) {
        this.tkns = tkns;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
     

    @Override
    public String toString() {
        return lexema + " -> " + tipo;
    }
}