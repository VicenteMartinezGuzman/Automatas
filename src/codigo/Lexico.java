/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codigo;
import codigo.sym;
import codigo.Tokens;
import static codigo.Tokens.Linea;
import java.util.List;
import java.util.ArrayList;;
import java.util.HashMap;
import codigo.Token;
import sun.jvm.hotspot.debugger.cdbg.Sym;

/**
 *
 * @author vicen
 */

public class Lexico {

    List<Token> tokens = new ArrayList<>();

    public Lexico() {}

    public void extraerCodigo(String linea) {
        int i = 0,contador=0;

        while (i < linea.length()) {
            char c = linea.charAt(i);

            // Ignorar espacios
            if (Character.isWhitespace(c)&&c!='\n') {
                i++;
                continue;
            }
            if (c == '\r') {
            System.out.println("Salto de línea detectado");
            i++;
            continue;
            }

            // Símbolos especiales
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) {
                     
                switch (c) {
                    case '(':
                        tokens.add(new Token("(", sym.Parentesis_a,Tokens.Parentesis_a));
                        break;
                    case ')':
                        tokens.add(new Token(")", sym.Parentesis_c,Tokens.Parentesis_c));
                        break;
                    case '{':
                        tokens.add(new Token("{", sym.Llave_a,Tokens.Llave_a));
                        break;
                    case '}':
                        tokens.add(new Token("}", sym.Llave_c,Tokens.Llave_c));
                        break;
                    case '$':
                        tokens.add(new Token("$", sym.Finalizador,Tokens.Finalizador));
                        break;
                    default:
                        System.out.println("Símbolo no reconocido: " + c);
                }
                i++; 
                continue;
            }

            // Números
            if (Character.isDigit(c)) {
                StringBuilder num = new StringBuilder();

                while (i < linea.length() && Character.isDigit(linea.charAt(i))) {
                    num.append(linea.charAt(i));
                    i++;
                }

                tokens.add(new Token(num.toString(), sym.Numero,Tokens.Numero));
                continue;
            }

            // Identificadores / Palabras reservadas
            if (Character.isLetter(c)) {
                StringBuilder id = new StringBuilder();

                while (i < linea.length() && Character.isLetterOrDigit(linea.charAt(i))) {
                    id.append(linea.charAt(i));
                    i++;
                }

                String palabra = id.toString();

                switch (palabra) {
                    case "inter":
                        tokens.add(new Token(palabra, sym.Inter,Tokens.Inter));
                        break;
                    case "main":
                        tokens.add(new Token(palabra, sym.Reservado,Tokens.Reservado));
                        break;
                    
                    default:
                        tokens.add(new Token(palabra, sym.Identificador,Tokens.Identificador));
                }

                continue;
            }

            i++;
        }
        System.out.println(tokens);
    }
}