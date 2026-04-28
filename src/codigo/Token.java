/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codigo;

/**
 *
 * @author vicen
 */
class Token {
    String lexema;
    int tipo;

    public Token(String lexema, int tipo) {
        this.lexema = lexema;
        this.tipo = tipo;
    }
    public Token(){
    }
    

    @Override
    public String toString() {
        return lexema + " -> " + tipo;
    }
}