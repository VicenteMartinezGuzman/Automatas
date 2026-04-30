/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codigo;
import codigo.sym;
import codigo.Tokens;
import static codigo.Tokens.Linea;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import codigo.Token;
import sun.jvm.hotspot.debugger.cdbg.Sym;

/**
 *
 * @author vicen
 */

public class Lexico {
 
    List<Token> tokens;
 
    public Lexico() {
        tokens = new ArrayList<>();
    }
 
    public void extraerCodigo(String linea) {
        int i = 0, contadorIgual = 0;
        StringBuilder signoIgual = new StringBuilder();
 
        while (i < linea.length()) {
            char c = linea.charAt(i);
 
            // ── Salto de línea ────────────────────────────────────────────────
            if (c == '\n') {
                tokens.add(new Token("\\n", sym.Linea, Tokens.Linea));
                i++;
                continue;
            }
 
            // ── Ignorar espacios ──────────────────────────────────────────────
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }
 
            // ── Símbolos especiales ───────────────────────────────────────────
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) {
 
                switch (c) {
                    case '(':
                        tokens.add(new Token("(", sym.Parentesis_a, Tokens.Parentesis_a));
                        break;
                    case ')':
                        tokens.add(new Token(")", sym.Parentesis_c, Tokens.Parentesis_c));
                        break;
                    case '{':
                        tokens.add(new Token("{", sym.Llave_a, Tokens.Llave_a));
                        break;
                    case '}':
                        tokens.add(new Token("}", sym.Llave_c, Tokens.Llave_c));
                        break;
                    case '$':
                        tokens.add(new Token("$", sym.Finalizador, Tokens.Finalizador));
                        break;
                    case '+':
                        if (i + 1 < linea.length() && linea.charAt(i + 1) == '+') {
                            tokens.add(new Token("++", sym.Incremento, Tokens.Incremento));
                            i++;
                        } else {
                            tokens.add(new Token("+", sym.Suma, Tokens.Suma));
                        }
                        break;
                    case '-':
                        if (i + 1 < linea.length() && linea.charAt(i + 1) == '-') {
                            tokens.add(new Token("--", sym.Decremento, Tokens.Decremento));
                            i++;
                        } else {
                            tokens.add(new Token("-", sym.Resta, Tokens.Resta));
                        }
                        break;
                    case '*':
                        tokens.add(new Token("*", sym.Multiplicacion, Tokens.Multiplicacion));
                        break;
                    case '/':
                        tokens.add(new Token("/", sym.Division, Tokens.Division));
                        break;
                    case '.':
                        tokens.add(new Token(".", sym.OperadorDecimal, Tokens.OperadorDecimal));
                        break;
                    case '|':
                        contadorIgual++;
                        signoIgual.append('|');
                        if (contadorIgual == 2) {
                            tokens.add(new Token("||", sym.Igual, Tokens.Igual));
                            contadorIgual = 0;
                            signoIgual.setLength(0);
                        }
                        break;
 
                    // ── Comentario: # hasta fin de línea ─────────────────────
                    case '#': {
                        StringBuilder comentario = new StringBuilder();
                        comentario.append('#');
                        i++;
                        while (i < linea.length() && linea.charAt(i) != '\n') {
                            comentario.append(linea.charAt(i));
                            i++;
                        }
                        // No avanzamos más: el '\n' lo procesa la iteración siguiente
                        tokens.add(new Token(comentario.toString(), sym.Comentarios, Tokens.Comentarios));
                        i--; // contrarrestar el i++ al final del switch
                        break;
                    }
 
                    // ── Cadena entre comillas ─────────────────────────────────
                    case '"': {
                        StringBuilder cadena = new StringBuilder();
                        cadena.append('"');
                        i++;
                        while (i < linea.length() && linea.charAt(i) != '"' && linea.charAt(i) != '\n') {
                            cadena.append(linea.charAt(i));
                            i++;
                        }
                        if (i < linea.length() && linea.charAt(i) == '"') {
                            cadena.append('"');
                            i++;
                        } else {
                            System.out.println("Error léxico: cadena sin cerrar en la línea actual");
                        }
                        tokens.add(new Token(cadena.toString(), sym.Comillas, Tokens.Comillas));
                        i--; // contrarrestar el i++ al final del switch
                        break;
                    }
 
                    default:
                        System.out.println("Símbolo no reconocido: " + c);
                }
                i++;
                continue;
            }
 
            // ── Números ───────────────────────────────────────────────────────
            if (Character.isDigit(c)) {
                StringBuilder num = new StringBuilder();
                while (i < linea.length() && Character.isDigit(linea.charAt(i))) {
                    num.append(linea.charAt(i));
                    i++;
                }
                tokens.add(new Token(num.toString(), sym.Numero, Tokens.Numero));
                continue;
            }
 
            // ── Identificadores / Palabras reservadas ─────────────────────────
            if (Character.isLetter(c)) {
                StringBuilder id = new StringBuilder();
                while (i < linea.length() && Character.isLetterOrDigit(linea.charAt(i))) {
                    id.append(linea.charAt(i));
                    i++;
                }
                String palabra = id.toString();
                switch (palabra) {
                    case "inter":
                        tokens.add(new Token(palabra, sym.Inter, Tokens.Inter));
                        break;
                    case "main":
                        tokens.add(new Token(palabra, sym.Reservado, Tokens.Main));
                        break;
                    case "dec":
                        tokens.add(new Token(palabra, sym.Dec, Tokens.Double));
                        break;
                    case "cad":
                        tokens.add(new Token(palabra, sym.Cadena, Tokens.Cad));
                        break;
                    default:
                        tokens.add(new Token(palabra, sym.Identificador, Tokens.Identificador));
                }
                continue;
            }
 
            i++;
        }
 
        System.out.println(tokens + " -> tokens extraídos");
    }
 
    public List<Token> getTokens() {
        return tokens;
    }
 
    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }
}