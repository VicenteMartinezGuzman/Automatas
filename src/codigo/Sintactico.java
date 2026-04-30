/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author vicen
 */
package codigo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Sintactico {
 
    private List<Token> tokens;
    private HashMap<String, TipoDato> tablaVariables;
    private int i;
    private int linea;
 
    private static final int MAX_INTER = 999999999;
    private static final int MIN_INTER = -999999999;
 
    public Sintactico() {
        tablaVariables = new HashMap<>();
    }
 
    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }
 
    // ══════════════════════════════════════════════════════
    //  PUNTO DE ENTRADA
    // ══════════════════════════════════════════════════════
    public String analizar() {
        if (tokens == null || tokens.isEmpty()) {
            return "Primero analiza léxicamente el código.";
        }
 
        StringBuilder resultado = new StringBuilder();
        tablaVariables.clear();
        i = 0;
        linea = 1;
        
        saltarLineasYComentarios();
 
        if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Main) {
            return "Error sintáctico línea " + linea + ": se esperaba 'main' al inicio del programa.\n";
        }
        i++;
 
        if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Parentesis_a) {
            return "Error sintáctico línea " + linea + ": se esperaba '(' después de 'main'.\n";
        }
        i++;
 
        if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Parentesis_c) {
            return "Error sintáctico línea " + linea + ": se esperaba ')' después de '('.\n";
        }
        i++;
 
        if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Llave_a) {
            return "Error sintáctico línea " + linea + ": se esperaba '{' después de 'main()'.\n";
        }
        i++;
 
        while (i < tokens.size()) {
            saltarLineasYComentarios();
            if (i >= tokens.size()) break;
 
            Token t = tokens.get(i);
 
            if (t.getTkns() == Tokens.Llave_c) {
                i++;
                saltarLineasYComentarios();
                if (i < tokens.size()) {
                    resultado.append("Error sintáctico línea ").append(linea)
                             .append(": token inesperado '").append(tokens.get(i).getLexema())
                             .append("' después del cierre '}'; el programa ya terminó.\n");
                }
                return resultado.toString();
            }
 
            if (t.getTkns() == Tokens.Inter)         { resultado.append(analizarInter());         continue; }
            if (t.getTkns() == Tokens.Cad)           { resultado.append(analizarCad());           continue; }
            if (t.getTkns() == Tokens.Double)        { resultado.append(analizarDec());           continue; }
            if (t.getTkns() == Tokens.Identificador) { resultado.append(analizarIdentificador()); continue; }
 
            resultado.append("Error sintáctico línea ").append(linea)
                     .append(": instrucción inválida '").append(t.getLexema())
                     .append("' dentro de main; se esperaba 'inter', 'dec', 'cad' o una variable.\n");
            saltarHastaFinalizador();
        }
 
        resultado.append("Error sintáctico línea ").append(linea)
                 .append(": se esperaba '}' para cerrar el bloque 'main'.\n");
        return resultado.toString();
    }
 
    // ── Saltar líneas y comentarios ───────────────────────────────────────────
    private void saltarLineasYComentarios() {
        while (i < tokens.size()) {
            Tokens tkn = tokens.get(i).getTkns();
            if (tkn == Tokens.Linea) {
                linea++;
                i++;
            } else if (tkn == Tokens.Comentarios) {
                i++;
            } else {
                break;
            }
        }
    }
 
    // ══════════════════════════════════════════════════════
    //  INTER  (declaración)
    // ══════════════════════════════════════════════════════
    private String analizarInter() {
        int li = linea;
        i++; // saltar 'inter'
 
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": se esperaba el nombre de la variable después de 'inter' (ej: inter x||5$).\n";
        }
 
        if (tokens.get(i).getTkns() == Tokens.Numero) {
            String num = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": el nombre de variable no puede ser un número ('" + num + "'); "
                    + "use letras (ej: inter x||" + num + "$).\n";
        }
 
        if (tokens.get(i).getTkns() != Tokens.Identificador) {
            String encontrado = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": nombre de variable inválido '" + encontrado + "' después de 'inter'; "
                    + "solo se permiten letras y dígitos (sin comenzar con dígito).\n";
        }
 
        String nombreVar = tokens.get(i).getLexema();
 
        if (esReservada(nombreVar)) {
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": '" + nombreVar + "' es una palabra reservada del lenguaje; "
                    + "no puede usarse como nombre de variable.\n";
        }
 
        if (tablaVariables.containsKey(nombreVar)) {
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": variable '" + nombreVar + "' ya fue declarada anteriormente; "
                    + "no se permite redeclarar variables.\n";
        }
 
        i++;
 
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": es necesario inicializar la variable '" + nombreVar
                    + "'; ejemplo: inter " + nombreVar + "||0$\n";
        }
 
        if (tokens.get(i).getTkns() != Tokens.Igual) {
            String encontrado = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": se esperaba '||' pero se encontró '" + encontrado + "'. "
                    + "Recuerde: la asignación usa '||', no '='.\n";
        }
        i++;
 
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": falta el valor en la asignación de '" + nombreVar
                    + "'. Ejemplo válido: inter " + nombreVar + "||42$\n";
        }
 
        if (tokens.get(i).getTkns() == Tokens.Comillas) {
            String cadena = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": tipo incorrecto; no se puede asignar la cadena " + cadena
                    + " a la variable entera '" + nombreVar + "'. Use 'cad' para cadenas.\n";
        }
 
        if (tokens.get(i).getTkns() == Tokens.Multiplicacion
                || tokens.get(i).getTkns() == Tokens.Division) {
            String op = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": la expresión de '" + nombreVar + "' no puede comenzar con '" + op + "'.\n";
        }
 
        try {
            TipoDato resultado = evaluarExpresionInter(li);
 
            if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Finalizador) {
                String encontrado = i < tokens.size() ? tokens.get(i).getLexema() : "fin de archivo";
                saltarHastaFinalizador();
                return "Error sintáctico línea " + li
                        + ": se esperaba '$' para finalizar la instrucción pero se encontró '"
                        + encontrado + "'.\n";
            }
            i++;
 
            Inter interResultado = (Inter) resultado;
            if (interResultado.getValor() > MAX_INTER || interResultado.getValor() < MIN_INTER) {
                return "Error semántico línea " + li
                        + ": el valor " + interResultado.getValor()
                        + " excede el rango permitido para 'inter' (mín: " + MIN_INTER + ", máx: " + MAX_INTER + ").\n";
            }
 
            tablaVariables.put(nombreVar, resultado);
            return nombreVar + "=" + interResultado.getValor() + ", sin error semántico\n";
 
        } catch (Exception e) {
            saltarHastaFinalizador();
            return e.getMessage() + "\n";
        }
    }
 
    private TipoDato evaluarExpresionInter(int li) throws Exception {
        TipoDato izquierdo = obtenerValorInter(li);
 
        while (i < tokens.size() && esOperador(tokens.get(i).getTkns())) {
            Tokens operador = tokens.get(i).getTkns();
            i++;
 
            if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador)
                throw new Exception("Error semántico línea " + li
                        + ": expresión incompleta; falta el operando derecho después de '"
                        + operadorAString(operador) + "'.");
 
            if (tokens.get(i).getTkns() == Tokens.Comillas)
                throw new Exception("Error semántico línea " + li
                        + ": tipo incorrecto; no se puede operar un entero con una cadena de texto.");
 
            if (tokens.get(i).getTkns() == Tokens.OperadorDecimal)
                throw new Exception("Error semántico línea " + li
                        + ": tipo incorrecto; no se puede usar '.' en una expresión entera 'inter'.");
 
            TipoDato derecho = obtenerValorInter(li);
 
            switch (operador) {
                case Suma:           izquierdo = izquierdo.sumar(derecho);        break;
                case Resta:          izquierdo = izquierdo.restar(derecho);       break;
                case Multiplicacion: izquierdo = izquierdo.multiplicar(derecho);  break;
                case Division:       izquierdo = izquierdo.dividir(derecho);      break;
                default: throw new Exception("Error semántico línea " + li + ": operador no reconocido.");
            }
        }
 
        return izquierdo;
    }
 
    private TipoDato obtenerValorInter(int li) throws Exception {
        if (i >= tokens.size())
            throw new Exception("Error semántico línea " + li
                    + ": la expresión terminó inesperadamente; falta un valor entero.");
 
        Token t = tokens.get(i);
 
        // Menos unario
        if (t.getTkns() == Tokens.Resta) {
            i++;
            if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador)
                throw new Exception("Error semántico línea " + li
                        + ": se esperaba un número después de '-' pero la instrucción terminó.");
            Token sig = tokens.get(i);
            if (sig.getTkns() == Tokens.Numero) {
                i++;
                try {
                    int negativo = -Integer.parseInt(sig.getLexema());
                    if (negativo < MIN_INTER)
                        throw new Exception("Error semántico línea " + li
                                + ": el valor " + negativo + " está fuera del rango mínimo (" + MIN_INTER + ").");
                    return new Inter(negativo);
                } catch (NumberFormatException e) {
                    throw new Exception("Error semántico línea " + li
                            + ": '" + sig.getLexema() + "' no es un número entero válido.");
                }
            }
            if (sig.getTkns() == Tokens.Identificador) {
                i++;
                if (!tablaVariables.containsKey(sig.getLexema()))
                    throw new Exception("Error semántico línea " + li
                            + ": variable '" + sig.getLexema() + "' usada sin haber sido declarada.");
                TipoDato var = tablaVariables.get(sig.getLexema());
                if (!(var instanceof Inter))
                    throw new Exception("Error semántico línea " + li
                            + ": tipo incorrecto; la variable '" + sig.getLexema()
                            + "' es de tipo '" + tipoDeVariable(var) + "', no 'inter'; no se puede negar.");
                int negativo = -((Inter) var).getValor();
                if (negativo < MIN_INTER)
                    throw new Exception("Error semántico línea " + li
                            + ": el valor negativo " + negativo + " excede el límite mínimo (" + MIN_INTER + ").");
                return new Inter(negativo);
            }
            if (sig.getTkns() == Tokens.Comillas)
                throw new Exception("Error semántico línea " + li
                        + ": tipo incorrecto; no se puede negar una cadena de texto.");
            throw new Exception("Error semántico línea " + li
                    + ": token inesperado '" + sig.getLexema() + "' después de '-'.");
        }
 
        // Número positivo
        if (t.getTkns() == Tokens.Numero) {
            String lexema = t.getLexema();
            if (lexema.length() > 9)
                throw new Exception("Error semántico línea " + li
                        + ": el número '" + lexema + "' tiene " + lexema.length()
                        + " dígitos; el máximo para 'inter' es 9 dígitos.");
            i++;
            try {
                int num = Integer.parseInt(lexema);
                if (num > MAX_INTER || num < MIN_INTER)
                    throw new Exception("Error semántico línea " + li
                            + ": el valor " + num + " está fuera del rango 'inter' ("
                            + MIN_INTER + " a " + MAX_INTER + ").");
                return new Inter(num);
            } catch (NumberFormatException e) {
                throw new Exception("Error semántico línea " + li
                        + ": '" + lexema + "' no es un número entero válido.");
            }
        }
 
        // Variable
        if (t.getTkns() == Tokens.Identificador) {
            i++;
            if (!tablaVariables.containsKey(t.getLexema()))
                throw new Exception("Error semántico línea " + li
                        + ": variable '" + t.getLexema() + "' usada sin haber sido declarada.");
            TipoDato var = tablaVariables.get(t.getLexema());
            if (!(var instanceof Inter))
                throw new Exception("Error semántico línea " + li
                        + ": tipo incorrecto; la variable '" + t.getLexema()
                        + "' es de tipo '" + tipoDeVariable(var) + "', pero se esperaba 'inter'.");
            return var;
        }
 
        if (t.getTkns() == Tokens.OperadorDecimal)
            throw new Exception("Error semántico línea " + li
                    + ": se encontró '.' en una expresión entera 'inter'; "
                    + "los decimales no están permitidos (use 'dec').");
 
        if (t.getTkns() == Tokens.Comillas)
            throw new Exception("Error semántico línea " + li
                    + ": tipo incorrecto; no se puede usar una cadena en una expresión entera 'inter'.");
 
        throw new Exception("Error semántico línea " + li
                + ": token inesperado '" + t.getLexema() + "' dentro de una expresión 'inter'.");
    }
 
    // ══════════════════════════════════════════════════════
    //  DEC  (declaración)
    // ══════════════════════════════════════════════════════
    private String analizarDec() {
        int li = linea;
        i++; // saltar 'dec'
 
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": se esperaba el nombre de la variable después de 'dec' (ej: dec x||3.14$).\n";
        }
 
        if (tokens.get(i).getTkns() == Tokens.Numero) {
            String num = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": el nombre de variable no puede ser un número ('" + num + "'); "
                    + "use letras (ej: dec precio||" + num + ".0$).\n";
        }
 
        if (tokens.get(i).getTkns() != Tokens.Identificador) {
            String encontrado = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": nombre de variable inválido '" + encontrado + "' después de 'dec'.\n";
        }
 
        String nombreVar = tokens.get(i).getLexema();
 
        if (esReservada(nombreVar)) {
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": '" + nombreVar + "' es una palabra reservada; no puede usarse como nombre de variable.\n";
        }
 
        if (tablaVariables.containsKey(nombreVar)) {
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": variable '" + nombreVar + "' ya fue declarada anteriormente; no se permite redeclarar.\n";
        }
 
        i++;
 
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": es necesario inicializar la variable '" + nombreVar
                    + "'; ejemplo: dec " + nombreVar + "||0.0$\n";
        }
 
        if (tokens.get(i).getTkns() != Tokens.Igual) {
            String encontrado = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": se esperaba '||' para asignar un valor a '" + nombreVar
                    + "', pero se encontró '" + encontrado + "'.\n";
        }
        i++;
 
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": falta el valor en la asignación de '" + nombreVar
                    + "'. Ejemplo válido: dec " + nombreVar + "||3.14$\n";
        }
 
        if (tokens.get(i).getTkns() == Tokens.Comillas) {
            String cadena = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": tipo incorrecto; no se puede asignar la cadena " + cadena
                    + " a la variable decimal '" + nombreVar + "'. Use 'cad' para cadenas.\n";
        }
 
        if (tokens.get(i).getTkns() == Tokens.Multiplicacion
                || tokens.get(i).getTkns() == Tokens.Division) {
            String op = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": la expresión de '" + nombreVar + "' no puede comenzar con '" + op + "'.\n";
        }
 
        try {
            TipoDato resultado = evaluarExpresionDec(li);
 
            if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Finalizador) {
                String encontrado = i < tokens.size() ? tokens.get(i).getLexema() : "fin de archivo";
                saltarHastaFinalizador();
                return "Error sintáctico línea " + li
                        + ": se esperaba '$' para finalizar la instrucción pero se encontró '"
                        + encontrado + "'.\n";
            }
            i++;
 
            tablaVariables.put(nombreVar, resultado);
            Dec decResultado = (Dec) resultado;
            return nombreVar + "=" + decResultado.getValor() + ", sin error semántico\n";
 
        } catch (Exception e) {
            saltarHastaFinalizador();
            return e.getMessage() + "\n";
        }
    }
 
    private TipoDato evaluarExpresionDec(int li) throws Exception {
        TipoDato izquierdo = obtenerValorDec(li);
 
        while (i < tokens.size() && esOperador(tokens.get(i).getTkns())) {
            Tokens operador = tokens.get(i).getTkns();
            i++;
 
            if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador)
                throw new Exception("Error semántico línea " + li
                        + ": expresión incompleta; falta el operando derecho después de '"
                        + operadorAString(operador) + "'.");
 
            if (tokens.get(i).getTkns() == Tokens.Comillas)
                throw new Exception("Error semántico línea " + li
                        + ": tipo incorrecto; no se puede operar un decimal con una cadena de texto.");
 
            TipoDato derecho = obtenerValorDec(li);
 
            switch (operador) {
                case Suma:           izquierdo = izquierdo.sumar(derecho);        break;
                case Resta:          izquierdo = izquierdo.restar(derecho);       break;
                case Multiplicacion: izquierdo = izquierdo.multiplicar(derecho);  break;
                case Division:       izquierdo = izquierdo.dividir(derecho);      break;
                default: throw new Exception("Error semántico línea " + li + ": operador no reconocido.");
            }
        }
 
        return izquierdo;
    }
 
    private TipoDato obtenerValorDec(int li) throws Exception {
        if (i >= tokens.size())
            throw new Exception("Error semántico línea " + li
                    + ": la expresión terminó inesperadamente; falta un valor decimal.");
 
        Token t = tokens.get(i);
 
        // Menos unario
        if (t.getTkns() == Tokens.Resta) {
            i++;
            double num = leerNumeroDec(li);
            return new Dec(-num);
        }
 
        // Número o inicio con punto
        if (t.getTkns() == Tokens.Numero || t.getTkns() == Tokens.OperadorDecimal) {
            double num = leerNumeroDec(li);
            return new Dec(num);
        }
 
        // Variable dec declarada
        if (t.getTkns() == Tokens.Identificador) {
            i++;
            if (!tablaVariables.containsKey(t.getLexema()))
                throw new Exception("Error semántico línea " + li
                        + ": variable '" + t.getLexema() + "' usada sin haber sido declarada.");
            TipoDato var = tablaVariables.get(t.getLexema());
            if (!(var instanceof Dec))
                throw new Exception("Error semántico línea " + li
                        + ": tipo incorrecto; la variable '" + t.getLexema()
                        + "' es de tipo '" + tipoDeVariable(var) + "', pero se esperaba 'dec'.");
            return var;
        }
 
        if (t.getTkns() == Tokens.Comillas)
            throw new Exception("Error semántico línea " + li
                    + ": tipo incorrecto; no se puede usar una cadena en una expresión decimal 'dec'.");
 
        throw new Exception("Error semántico línea " + li
                + ": token inesperado '" + t.getLexema() + "' dentro de una expresión 'dec'.");
    }
 
    /**
     * Lee un literal numérico decimal del flujo de tokens.
     * Exige punto decimal en literales: 78 → error, 78.0 → ok.
     * Si ambas partes (entera y decimal) exceden el límite, reporta ambas.
     */
    private double leerNumeroDec(int li) throws Exception {
        StringBuilder parteEntera  = new StringBuilder();
        StringBuilder parteDecimal = new StringBuilder();
 
        if (i < tokens.size() && tokens.get(i).getTkns() == Tokens.Numero) {
            parteEntera.append(tokens.get(i).getLexema());
            i++;
        }
 
        boolean tienePunto = false;
        if (i < tokens.size() && tokens.get(i).getTkns() == Tokens.OperadorDecimal) {
            tienePunto = true;
            i++;
            if (i < tokens.size() && tokens.get(i).getTkns() == Tokens.Numero) {
                parteDecimal.append(tokens.get(i).getLexema());
                i++;
            } else {
                throw new Exception("Error semántico línea " + li
                        + ": se esperaban dígitos después del '.' en el valor decimal.");
            }
        }
 
        // ── Estricto: literales en 'dec' deben llevar punto decimal ──────────
        if (!tienePunto && parteEntera.length() > 0) {
            throw new Exception("Error semántico línea " + li
                    + ": no se puede asignar el valor entero '" + parteEntera
                    + "' a una variable de tipo 'dec'; use notación decimal (ej: "
                    + parteEntera + ".0).");
        }
 
        // ── Límites: reporta ambas partes si las dos exceden ─────────────────
        List<String> excesos = new ArrayList<>();
        if (parteEntera.length() > 9)
            excesos.add("la parte entera '" + parteEntera + "' excede los 9 dígitos permitidos");
        if (parteDecimal.length() > 8)
            excesos.add("la parte decimal '" + parteDecimal + "' excede los 8 dígitos permitidos");
        if (!excesos.isEmpty())
            throw new Exception("Error semántico línea " + li
                    + ": en 'dec', " + String.join(" y además ", excesos) + ".");
 
        String numeroStr = tienePunto ? parteEntera + "." + parteDecimal : parteEntera.toString();
 
        if (numeroStr.isEmpty() || numeroStr.equals("."))
            throw new Exception("Error semántico línea " + li
                    + ": no se encontró un valor numérico válido para 'dec'.");
 
        try {
            return Double.parseDouble(numeroStr);
        } catch (NumberFormatException e) {
            throw new Exception("Error semántico línea " + li
                    + ": formato decimal inválido '" + numeroStr + "'.");
        }
    }
 
    // ══════════════════════════════════════════════════════
    //  CAD  (declaración + concatenación con +)
    // ══════════════════════════════════════════════════════
    private String analizarCad() {
        int li = linea;
        i++; // saltar 'cad'
 
        // ── cad++$ o cad--$ : error específico ───────────────────────────────
        if (i < tokens.size()
                && (tokens.get(i).getTkns() == Tokens.Incremento
                 || tokens.get(i).getTkns() == Tokens.Decremento)) {
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": no se puede incrementar o decrementar el tipo 'cad'; "
                    + "esta operación solo es válida para 'inter' y 'dec'.\n";
        }
 
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": se esperaba el nombre de la variable después de 'cad' (ej: cad nombre||\"Hola\"$).\n";
        }
 
        if (tokens.get(i).getTkns() == Tokens.Numero) {
            String num = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": el nombre de variable no puede ser un número ('" + num + "'); "
                    + "use letras (ej: cad texto||\"valor\"$).\n";
        }
 
        if (tokens.get(i).getTkns() != Tokens.Identificador) {
            String encontrado = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": nombre de variable inválido '" + encontrado + "' después de 'cad'.\n";
        }
 
        String nombreVar = tokens.get(i).getLexema();
 
        if (esReservada(nombreVar)) {
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": '" + nombreVar + "' es una palabra reservada; no puede usarse como nombre de variable.\n";
        }
 
        if (tablaVariables.containsKey(nombreVar)) {
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": variable '" + nombreVar + "' ya fue declarada anteriormente; no se permite redeclarar.\n";
        }
 
        i++;
 
        // ── cad hola$ (sin || valor): error de inicialización ────────────────
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": es necesario inicializar la variable '" + nombreVar
                    + "'; declare su valor con ||: cad " + nombreVar + "||\"valor\"$\n";
        }
 
        if (tokens.get(i).getTkns() != Tokens.Igual) {
            String encontrado = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error sintáctico línea " + li
                    + ": se esperaba '||' para asignar un valor a '" + nombreVar
                    + "', pero se encontró '" + encontrado + "'.\n";
        }
        i++;
 
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": falta el valor en la asignación de '" + nombreVar
                    + "'. Ejemplo: cad " + nombreVar + "||\"texto\"$\n";
        }
 
        if (tokens.get(i).getTkns() == Tokens.Numero) {
            String num = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": tipo incorrecto; no se puede asignar el número " + num
                    + " a una variable 'cad'. Envuélvelo en comillas: \"" + num + "\"\n";
        }
 
        if (esOperadorNoPermitidoEnCad(tokens.get(i).getTkns())) {
            String op = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": operador '" + op + "' no válido al inicio de una expresión 'cad'. "
                    + "Solo se permite '+' para concatenar cadenas o variables.\n";
        }
 
        try {
            String valorFinal = evaluarExpresionCad(li);
 
            if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Finalizador) {
                String encontrado = i < tokens.size() ? tokens.get(i).getLexema() : "fin de archivo";
                saltarHastaFinalizador();
                return "Error sintáctico línea " + li
                        + ": se esperaba '$' para finalizar la instrucción pero se encontró '"
                        + encontrado + "'.\n";
            }
            i++;
 
            Cad cadena = new Cad(nombreVar, valorFinal);
            tablaVariables.put(nombreVar, cadena);
            return nombreVar + "=\"" + valorFinal + "\", sin error semántico\n";
 
        } catch (Exception e) {
            saltarHastaFinalizador();
            return e.getMessage() + "\n";
        }
    }
 
    /** Evalúa concatenación: primer_valor [+ siguiente_valor]* */
    private String evaluarExpresionCad(int li) throws Exception {
        StringBuilder resultado = new StringBuilder();
        resultado.append(obtenerValorCad(li));
 
        while (i < tokens.size() && tokens.get(i).getTkns() == Tokens.Suma) {
            i++; // saltar +
            if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador)
                throw new Exception("Error semántico línea " + li
                        + ": expresión incompleta en 'cad'; falta el operando derecho después de '+'.");
            resultado.append(obtenerValorCad(li));
        }
 
        if (i < tokens.size() && esOperadorNoPermitidoEnCad(tokens.get(i).getTkns()))
            throw new Exception("Error semántico línea " + li
                    + ": operador '" + tokens.get(i).getLexema() + "' no está permitido en tipo 'cad'. "
                    + "Solo se puede usar '+' para concatenar.");
 
        return resultado.toString();
    }
 
    private String obtenerValorCad(int li) throws Exception {
        if (i >= tokens.size())
            throw new Exception("Error semántico línea " + li
                    + ": la expresión terminó inesperadamente; se esperaba una cadena o variable 'cad'.");
 
        Token t = tokens.get(i);
 
        if (t.getTkns() == Tokens.Comillas) {
            i++;
            return t.getLexema().replace("\"", "");
        }
 
        if (t.getTkns() == Tokens.Identificador) {
            i++;
            if (!tablaVariables.containsKey(t.getLexema()))
                throw new Exception("Error semántico línea " + li
                        + ": variable '" + t.getLexema() + "' usada sin haber sido declarada.");
            TipoDato var = tablaVariables.get(t.getLexema());
            if (!(var instanceof Cad))
                throw new Exception("Error semántico línea " + li
                        + ": tipo incorrecto; la variable '" + t.getLexema()
                        + "' es de tipo '" + tipoDeVariable(var) + "', pero se esperaba 'cad' para concatenar.");
            return ((Cad) var).getValor();
        }
 
        if (t.getTkns() == Tokens.Numero)
            throw new Exception("Error semántico línea " + li
                    + ": tipo incorrecto; el número '" + t.getLexema()
                    + "' no puede usarse en una expresión 'cad'. Envuélvelo en comillas.");
 
        if (t.getTkns() == Tokens.OperadorDecimal)
            throw new Exception("Error semántico línea " + li
                    + ": tipo incorrecto; '.' no puede aparecer en una expresión 'cad'.");
 
        if (esOperadorNoPermitidoEnCad(t.getTkns()))
            throw new Exception("Error semántico línea " + li
                    + ": operador '" + t.getLexema() + "' no válido en tipo 'cad'. "
                    + "Solo '+' puede usarse para concatenar.");
 
        throw new Exception("Error semántico línea " + li
                + ": token inesperado '" + t.getLexema() + "' en expresión 'cad'.");
    }
 
    // ══════════════════════════════════════════════════════
    //  IDENTIFICADOR: incremento/decremento  O  reasignación
    // ══════════════════════════════════════════════════════
    private String analizarIdentificador() {
        int li = linea;
        String nombreVar = tokens.get(i).getLexema();
        i++; // saltar identificador
 
        if (!tablaVariables.containsKey(nombreVar)) {
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": variable '" + nombreVar + "' usada sin haber sido declarada.\n";
        }
 
        TipoDato var = tablaVariables.get(nombreVar);
 
        if (i >= tokens.size()) {
            return "Error sintáctico línea " + li
                    + ": instrucción incompleta para '" + nombreVar
                    + "'; se esperaba '||', '++' o '--'.\n";
        }
 
        Tokens siguiente = tokens.get(i).getTkns();
 
        // ── Reasignación ─────────────────────────────────────────────────────
        if (siguiente == Tokens.Igual) {
            return reasignar(nombreVar, var, li);
        }
 
        // ── Incremento / Decremento ───────────────────────────────────────────
        if (siguiente == Tokens.Incremento || siguiente == Tokens.Decremento) {
 
            if (var instanceof Cad) {
                saltarHastaFinalizador();
                return "Error semántico línea " + li
                        + ": no se puede incrementar o decrementar la variable '"
                        + nombreVar + "' de tipo 'cad'; "
                        + "esta operación solo es válida para 'inter' y 'dec'.\n";
            }
 
            if (!(var instanceof Inter) && !(var instanceof Dec)) {
                saltarHastaFinalizador();
                return "Error semántico línea " + li
                        + ": el operador de incremento/decremento no aplica a '"
                        + tipoDeVariable(var) + "'; solo es válido para 'inter' y 'dec'.\n";
            }
 
            Tokens op = siguiente;
            i++;
 
            if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Finalizador) {
                String encontrado = i < tokens.size() ? tokens.get(i).getLexema() : "fin de archivo";
                saltarHastaFinalizador();
                return "Error sintáctico línea " + li
                        + ": se esperaba '$' al final de la instrucción pero se encontró '"
                        + encontrado + "'.\n";
            }
            i++;
 
            if (var instanceof Inter) {
                int nuevo = ((Inter) var).getValor() + (op == Tokens.Incremento ? 1 : -1);
                tablaVariables.put(nombreVar, new Inter(nuevo));
                return nombreVar + "=" + nuevo + ", sin error semántico\n";
            } else {
                double nuevo = ((Dec) var).getValor() + (op == Tokens.Incremento ? 1.0 : -1.0);
                tablaVariables.put(nombreVar, new Dec(nuevo));
                return nombreVar + "=" + nuevo + ", sin error semántico\n";
            }
        }
 
        String encontrado = tokens.get(i).getLexema();
        saltarHastaFinalizador();
        return "Error sintáctico línea " + li
                + ": se esperaba '||', '++' o '--' después de '" + nombreVar
                + "', pero se encontró '" + encontrado + "'.\n";
    }
 
    /**
     * Reasigna un nuevo valor a una variable ya declarada.
     * Aplica las mismas reglas semánticas/estrictas que la declaración.
     */
    private String reasignar(String nombreVar, TipoDato var, int li) {
        i++; // saltar ||
 
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error semántico línea " + li
                    + ": falta el nuevo valor en la reasignación de '" + nombreVar + "'.\n";
        }
 
        // ── Reasignación inter ────────────────────────────────────────────────
        if (var instanceof Inter) {
            if (tokens.get(i).getTkns() == Tokens.Comillas) {
                String cad = tokens.get(i).getLexema();
                saltarHastaFinalizador();
                return "Error semántico línea " + li
                        + ": tipo incorrecto; no se puede asignar la cadena " + cad
                        + " a la variable entera '" + nombreVar + "'.\n";
            }
            try {
                TipoDato nuevo = evaluarExpresionInter(li);
                if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Finalizador) {
                    String encontrado = i < tokens.size() ? tokens.get(i).getLexema() : "fin de archivo";
                    saltarHastaFinalizador();
                    return "Error sintáctico línea " + li
                            + ": se esperaba '$' al final pero se encontró '" + encontrado + "'.\n";
                }
                i++;
                Inter interNuevo = (Inter) nuevo;
                if (interNuevo.getValor() > MAX_INTER || interNuevo.getValor() < MIN_INTER) {
                    return "Error semántico línea " + li
                            + ": el valor " + interNuevo.getValor()
                            + " excede el rango permitido para 'inter'.\n";
                }
                tablaVariables.put(nombreVar, nuevo);
                return nombreVar + "=" + interNuevo.getValor() + ", sin error semántico\n";
            } catch (Exception e) {
                saltarHastaFinalizador();
                return e.getMessage() + "\n";
            }
        }
 
        // ── Reasignación dec ──────────────────────────────────────────────────
        if (var instanceof Dec) {
            if (tokens.get(i).getTkns() == Tokens.Comillas) {
                String cad = tokens.get(i).getLexema();
                saltarHastaFinalizador();
                return "Error semántico línea " + li
                        + ": tipo incorrecto; no se puede asignar la cadena " + cad
                        + " a la variable decimal '" + nombreVar + "'.\n";
            }
            try {
                TipoDato nuevo = evaluarExpresionDec(li);
                if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Finalizador) {
                    String encontrado = i < tokens.size() ? tokens.get(i).getLexema() : "fin de archivo";
                    saltarHastaFinalizador();
                    return "Error sintáctico línea " + li
                            + ": se esperaba '$' al final pero se encontró '" + encontrado + "'.\n";
                }
                i++;
                tablaVariables.put(nombreVar, nuevo);
                Dec decNuevo = (Dec) nuevo;
                return nombreVar + "=" + decNuevo.getValor() + ", sin error semántico\n";
            } catch (Exception e) {
                saltarHastaFinalizador();
                return e.getMessage() + "\n";
            }
        }
 
        // ── Reasignación cad ──────────────────────────────────────────────────
        if (var instanceof Cad) {
            if (tokens.get(i).getTkns() == Tokens.Numero) {
                String num = tokens.get(i).getLexema();
                saltarHastaFinalizador();
                return "Error semántico línea " + li
                        + ": tipo incorrecto; no se puede asignar el número " + num
                        + " a la variable 'cad' '" + nombreVar + "'.\n";
            }
            if (esOperadorNoPermitidoEnCad(tokens.get(i).getTkns())) {
                String op = tokens.get(i).getLexema();
                saltarHastaFinalizador();
                return "Error semántico línea " + li
                        + ": operador '" + op + "' no válido en reasignación de 'cad'.\n";
            }
            try {
                String nuevoValor = evaluarExpresionCad(li);
                if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Finalizador) {
                    String encontrado = i < tokens.size() ? tokens.get(i).getLexema() : "fin de archivo";
                    saltarHastaFinalizador();
                    return "Error sintáctico línea " + li
                            + ": se esperaba '$' al final pero se encontró '" + encontrado + "'.\n";
                }
                i++;
                tablaVariables.put(nombreVar, new Cad(nombreVar, nuevoValor));
                return nombreVar + "=\"" + nuevoValor + "\", sin error semántico\n";
            } catch (Exception e) {
                saltarHastaFinalizador();
                return e.getMessage() + "\n";
            }
        }
 
        saltarHastaFinalizador();
        return "Error semántico línea " + li
                + ": no se puede reasignar la variable '" + nombreVar + "' de tipo desconocido.\n";
    }
 
    // ══════════════════════════════════════════════════════
    //  UTILIDADES
    // ══════════════════════════════════════════════════════
    private boolean esOperador(Tokens tkn) {
        return tkn == Tokens.Suma || tkn == Tokens.Resta
                || tkn == Tokens.Multiplicacion || tkn == Tokens.Division;
    }
 
    private boolean esOperadorNoPermitidoEnCad(Tokens tkn) {
        return tkn == Tokens.Resta || tkn == Tokens.Multiplicacion || tkn == Tokens.Division;
    }
 
    private String operadorAString(Tokens tkn) {
        switch (tkn) {
            case Suma:           return "+";
            case Resta:          return "-";
            case Multiplicacion: return "*";
            case Division:       return "/";
            default:             return "?";
        }
    }
 
    private String tipoDeVariable(TipoDato var) {
        if (var instanceof Inter) return "inter";
        if (var instanceof Dec)   return "dec";
        if (var instanceof Cad)   return "cad";
        return "desconocido";
    }
 
    private boolean esReservada(String nombre) {
        switch (nombre) {
            case "inter": case "dec": case "cad":
            case "main":  case "if":  case "else":
            case "while": case "for": case "do":
                return true;
            default:
                return false;
        }
    }
 
    private void saltarHastaFinalizador() {
        while (i < tokens.size() && tokens.get(i).getTkns() != Tokens.Finalizador) i++;
        if (i < tokens.size()) i++;
    }
}