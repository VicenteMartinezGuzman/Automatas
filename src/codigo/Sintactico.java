/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author vicen
 */
package codigo;

import java.util.HashMap;
import java.util.List;

public class Sintactico {

    private List<Token> tokens;
    private HashMap<String, TipoDato> tablaVariables;
    private int i;
    private int linea; // rastrear línea actual

    // Límite de 6 dígitos para inter
    private static final int MAX_INTER = 999999999;
    private static final int MIN_INTER = -999999999;

    public Sintactico() {
        tablaVariables = new HashMap<>();
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public String analizar() {
    if (tokens == null || tokens.isEmpty()) {
        return "Primero analiza léxicamente el código.";
    }

    StringBuilder resultado = new StringBuilder();
    tablaVariables.clear();
    i = 0;
    linea = 1;

    // ── 1. Debe empezar con main ──────────────────────────────────────────
    saltarLineas();
    if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Main) {
        return "Error sintáctico línea " + linea + ": se esperaba 'main' al inicio del programa\n";
    }
    i++; // saltar main

    // ── 2. Debe seguir ( ──────────────────────────────────────────────────
    if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Parentesis_a) {
        return "Error sintáctico línea " + linea + ": se esperaba '(' después de 'main'\n";
    }
    i++; // saltar (

    // ── 3. Debe seguir ) ──────────────────────────────────────────────────
    if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Parentesis_c) {
        return "Error sintáctico línea " + linea + ": se esperaba ')' después de '('\n";
    }
    i++; // saltar )

    // ── 4. Debe seguir { ──────────────────────────────────────────────────
    if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Llave_a) {
        return "Error sintáctico línea " + linea + ": se esperaba '{' después de 'main()'\n";
    }
    i++; // saltar {

    // ── 5. Analizar cuerpo del programa ───────────────────────────────────
    while (i < tokens.size()) {
        saltarLineas();

        if (i >= tokens.size()) break;

        Token t = tokens.get(i);

        // Fin del programa con }
        if (t.getTkns() == Tokens.Llave_c) {
            i++; // saltar }

            // 6. No debe haber nada después del }
            saltarLineas();
            if (i < tokens.size()) {
                resultado.append("Error sintáctico línea " + linea 
                        + ": no se esperaba '" + tokens.get(i).getLexema() 
                        + "' después del cierre '}'\n");
            }

            return resultado.toString();
        }

        // inter
        if (t.getTkns() == Tokens.Inter) {
            resultado.append(analizarInter());
            continue;
        }

        // cad
        if (t.getTkns() == Tokens.Cad) {
          resultado.append(analizarCad());
           continue;
          }


        // dec
        if (t.getTkns() == Tokens.Double) {
        resultado.append(analizarDec());
        continue;
         }
        // incremento / decremento
        if (t.getTkns() == Tokens.Identificador) {
         resultado.append(analizarIncDec());
        continue;
         }

        // Instrucción inesperada dentro del cuerpo
        resultado.append("Error sintáctico línea " + linea
                + ": instrucción inválida '" + t.getLexema() + "' dentro de main\n");
        saltarHastaFinalizador();
    }

    // Si llegamos aquí sin encontrar } es un error
    resultado.append("Error sintáctico línea " + linea + ": se esperaba '}' para cerrar 'main'\n");
    return resultado.toString();
}

// ── Método auxiliar para saltar tokens de línea ───────────────────────────
private void saltarLineas() {
    while (i < tokens.size() && tokens.get(i).getTkns() == Tokens.Linea) {
        linea++;
        i++;
    }
}

    // ══════════════════════════════════════════════════════
    //  INTER
    // ══════════════════════════════════════════════════════
    private String analizarInter() {
        int lineaInstruccion = linea;
        i++; // saltar 'inter'

        // 1. Debe seguir un identificador
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error sintáctico línea " + lineaInstruccion 
                    + ": se esperaba un identificador después de 'inter'\n";
        }

        if (tokens.get(i).getTkns() != Tokens.Identificador) {
            String encontrado = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error sintáctico línea " + lineaInstruccion 
                    + ": identificador inválido '" + encontrado + "' después de 'inter'\n";
        }

        String nombreVar = tokens.get(i).getLexema();

        // 2. El identificador no puede ser palabra reservada
        if (esReservada(nombreVar)) {
            saltarHastaFinalizador();
            return "Error semántico línea " + lineaInstruccion 
                    + ": '" + nombreVar + "' es una palabra reservada, no puede ser identificador\n";
        }

        // 3. Variable ya declarada
        if (tablaVariables.containsKey(nombreVar)) {
            saltarHastaFinalizador();
            return "Error semántico línea " + lineaInstruccion 
                    + ": variable '" + nombreVar + "' ya fue declarada anteriormente\n";
        }

        i++; // saltar identificador

        // 4. Debe seguir ||
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error sintáctico línea " + lineaInstruccion 
                    + ": se esperaba '||' después de '" + nombreVar + "'\n";
        }

        if (tokens.get(i).getTkns() != Tokens.Igual) {
            String encontrado = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error sintáctico línea " + lineaInstruccion 
                    + ": se esperaba '||' pero se encontró '" + encontrado + "'\n";
        }
        i++; // saltar ||

        // 5. Expresión no puede estar vacía
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error semántico línea " + lineaInstruccion 
                    + ": expresión vacía en la asignación de '" + nombreVar + "'\n";
        }

        // 6. No puede asignarse una cadena a inter
        if (tokens.get(i).getTkns() == Tokens.Comillas) {
            saltarHastaFinalizador();
            return "Error semántico línea " + lineaInstruccion 
                    + ": no se puede asignar una cadena de texto a la variable entera '" + nombreVar + "'\n";
        }

        // 7. Evaluar expresión
        try {
            TipoDato resultado = evaluarExpresionInter(lineaInstruccion);

            // 8. Debe terminar con $
            if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Finalizador) {
                String encontrado = i < tokens.size() ? tokens.get(i).getLexema() : "fin de archivo";
                saltarHastaFinalizador();
                return "Error sintáctico línea " + lineaInstruccion 
                        + ": se esperaba '$' al final pero se encontró '" + encontrado + "'\n";
            }
            i++; // saltar $

            // 9. Verificar límite de 6 bits (999999)
            Inter interResultado = (Inter) resultado;
            if (interResultado.getValor() > MAX_INTER || interResultado.getValor() < MIN_INTER) {
                return "Error semántico línea " + lineaInstruccion 
                        + ": el valor " + interResultado.getValor() 
                        + " excede el límite permitido de 6 dígitos (máx: 999999, mín: -999999)\n";
            }

            tablaVariables.put(nombreVar, resultado);
            return nombreVar + "=" + interResultado.getValor() + ", sin error semántico\n";

        } catch (Exception e) {
            saltarHastaFinalizador();
            return e.getMessage() + "\n";
        }
    }

    private TipoDato evaluarExpresionInter(int lineaInstruccion) throws Exception {

    // Manejar menos unario al inicio: -87+50, -50-60
    TipoDato izquierdo = obtenerValorInter(lineaInstruccion);

    while (i < tokens.size() && esOperador(tokens.get(i).getTkns())) {
        Tokens operador = tokens.get(i).getTkns();
        i++; // saltar operador

        // Falta operando derecho
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": expresión incompleta, falta operando después de '"
                    + operadorAString(operador) + "'");
        }

        // No puede operarse con cadena
        if (tokens.get(i).getTkns() == Tokens.Comillas) {
            throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": no se puede operar un entero con una cadena de texto");
        }

        // El operando derecho también puede ser negativo: 50- -20
        TipoDato derecho = obtenerValorInter(lineaInstruccion);

        switch (operador) {
            case Suma:           izquierdo = izquierdo.sumar(derecho);        break;
            case Resta:          izquierdo = izquierdo.restar(derecho);       break;
            case Multiplicacion: izquierdo = izquierdo.multiplicar(derecho);  break;
            case Division:       izquierdo = izquierdo.dividir(derecho);      break;
            default: throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": operador desconocido");
        }
    }

    return izquierdo;
}

    private TipoDato obtenerValorInter(int lineaInstruccion) throws Exception {
    if (i >= tokens.size()) {
        throw new Exception("Error semántico línea " + lineaInstruccion
                + ": se esperaba un valor pero la expresión terminó inesperadamente");
    }

    Token t = tokens.get(i);

    // ── Menos unario: -87, -50, -(variable) ──────────────────────────────
    if (t.getTkns() == Tokens.Resta) {
        i++; // saltar el '-'

        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": se esperaba un número después del signo '-'");
        }

        Token siguiente = tokens.get(i);

        // -número  → ejemplo: -87
        if (siguiente.getTkns() == Tokens.Numero) {
            i++;
            try {
                int num = Integer.parseInt(siguiente.getLexema());
                int negativo = -num;
                if (negativo < MIN_INTER) {
                    throw new Exception("Error semántico línea " + lineaInstruccion
                            + ": el número " + negativo
                            + " excede el límite mínimo permitido (-999999)");
                }
                return new Inter(negativo); // tu clase Inter con valor negativo
            } catch (NumberFormatException e) {
                throw new Exception("Error semántico línea " + lineaInstruccion
                        + ": número inválido después de '-'");
            }
        }

        // -(variable) → ejemplo: -resultado
        if (siguiente.getTkns() == Tokens.Identificador) {
            i++;
            if (!tablaVariables.containsKey(siguiente.getLexema())) {
                throw new Exception("Error semántico línea " + lineaInstruccion
                        + ": variable '" + siguiente.getLexema() + "' no ha sido declarada");
            }
            TipoDato var = tablaVariables.get(siguiente.getLexema());
            if (!(var instanceof Inter)) {
                throw new Exception("Error semántico línea " + lineaInstruccion
                        + ": la variable '" + siguiente.getLexema() + "' no es de tipo 'inter'");
            }
            int negativo = -((Inter) var).getValor();
            if (negativo < MIN_INTER) {
                throw new Exception("Error semántico línea " + lineaInstruccion
                        + ": el valor negativo " + negativo + " excede el límite mínimo (-999999)");
            }
            return new Inter(negativo);
        }

        // -cadena → error
        if (siguiente.getTkns() == Tokens.Comillas) {
            throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": no se puede negar una cadena de texto");
        }

        throw new Exception("Error semántico línea " + lineaInstruccion
                + ": token inesperado '" + siguiente.getLexema() + "' después de '-'");
    }

    // ── Número positivo ───────────────────────────────────────────────────
    if (t.getTkns() == Tokens.Numero) {

    String lexema = t.getLexema();

    // ✅ VALIDAR LONGITUD (9 dígitos)
    if (lexema.length() > 9) {
        throw new Exception("Error semántico línea " + lineaInstruccion
                + ": el número excede los 9 dígitos permitidos");
    }

    i++;
    try {
        int num = Integer.parseInt(lexema);

        if (num > MAX_INTER || num < MIN_INTER) {
            throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": el número " + num + " está fuera del rango permitido");
        }

        return new Inter(num);

    } catch (NumberFormatException e) {
        throw new Exception("Error semántico línea " + lineaInstruccion
                + ": número inválido '" + lexema + "'");
    }
}

    // ── Variable ──────────────────────────────────────────────────────────
    if (t.getTkns() == Tokens.Identificador) {
        i++;
        if (!tablaVariables.containsKey(t.getLexema())) {
            throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": variable '" + t.getLexema() + "' no ha sido declarada");
        }
        TipoDato var = tablaVariables.get(t.getLexema());
        if (!(var instanceof Inter)) {
            throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": la variable '" + t.getLexema() + "' no es de tipo 'inter'");
        }
        return var;
    }

    // ── Decimal mal usado ─────────────────────────────────────────────────
    if (t.getTkns() == Tokens.OperadorDecimal) {
        throw new Exception("Error semántico línea " + lineaInstruccion
                + ": se encontró '.' en tipo 'inter', los decimales no están permitidos");
    }

    // ── Cadena en expresión entera ────────────────────────────────────────
    if (t.getTkns() == Tokens.Comillas) {
        throw new Exception("Error semántico línea " + lineaInstruccion
                + ": no se puede usar una cadena en una expresión entera");
    }

    throw new Exception("Error semántico línea " + lineaInstruccion
            + ": token inesperado '" + t.getLexema() + "' en la expresión entera");
}
    // ══════════════════════════════════════════════════════
//  DEC
// ══════════════════════════════════════════════════════
private String analizarDec() {
    int lineaInstruccion = linea;
    i++; // saltar 'dec'

    // 1. Debe seguir un identificador
    if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
        saltarHastaFinalizador();
        return "Error sintáctico línea " + lineaInstruccion
                + ": se esperaba un identificador después de 'dec'\n";
    }

    if (tokens.get(i).getTkns() != Tokens.Identificador) {
        String encontrado = tokens.get(i).getLexema();
        saltarHastaFinalizador();
        return "Error sintáctico línea " + lineaInstruccion
                + ": identificador inválido '" + encontrado + "' después de 'dec'\n";
    }

    String nombreVar = tokens.get(i).getLexema();

    // 2. No puede ser palabra reservada
    if (esReservada(nombreVar)) {
        saltarHastaFinalizador();
        return "Error semántico línea " + lineaInstruccion
                + ": '" + nombreVar + "' es una palabra reservada\n";
    }

    // 3. Variable ya declarada
    if (tablaVariables.containsKey(nombreVar)) {
        saltarHastaFinalizador();
        return "Error semántico línea " + lineaInstruccion
                + ": variable '" + nombreVar + "' ya fue declarada anteriormente\n";
    }

    i++; // saltar identificador

    // 4. Debe seguir ||
    if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Igual) {
        String encontrado = i < tokens.size() ? tokens.get(i).getLexema() : "fin de archivo";
        saltarHastaFinalizador();
        return "Error sintáctico línea " + lineaInstruccion
                + ": se esperaba '||' pero se encontró '" + encontrado + "'\n";
    }
    i++; // saltar ||

    // 5. Expresión no puede estar vacía
    if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
        saltarHastaFinalizador();
        return "Error semántico línea " + lineaInstruccion
                + ": expresión vacía en la asignación de '" + nombreVar + "'\n";
    }

    // 6. No puede asignarse cadena a dec
    if (tokens.get(i).getTkns() == Tokens.Comillas) {
        saltarHastaFinalizador();
        return "Error semántico línea " + lineaInstruccion
                + ": no se puede asignar una cadena a la variable decimal '" + nombreVar + "'\n";
    }

    // 7. Evaluar expresión decimal
    try {
        TipoDato resultado = evaluarExpresionDec(lineaInstruccion);

        // 8. Debe terminar con $
        if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Finalizador) {
            String encontrado = i < tokens.size() ? tokens.get(i).getLexema() : "fin de archivo";
            saltarHastaFinalizador();
            return "Error sintáctico línea " + lineaInstruccion
                    + ": se esperaba '$' al final pero se encontró '" + encontrado + "'\n";
        }
        i++; // saltar $

        tablaVariables.put(nombreVar, resultado);
        Dec decResultado = (Dec) resultado;
        return nombreVar + "=" + decResultado.getValor() + ", sin error semántico\n";

    } catch (Exception e) {
        saltarHastaFinalizador();
        return e.getMessage() + "\n";
    }
}

private TipoDato evaluarExpresionDec(int lineaInstruccion) throws Exception {
    TipoDato izquierdo = obtenerValorDec(lineaInstruccion);

    while (i < tokens.size() && esOperador(tokens.get(i).getTkns())) {
        Tokens operador = tokens.get(i).getTkns();
        i++;

        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": expresión incompleta, falta operando después de '"
                    + operadorAString(operador) + "'");
        }

        if (tokens.get(i).getTkns() == Tokens.Comillas) {
            throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": no se puede operar un decimal con una cadena");
        }

        TipoDato derecho = obtenerValorDec(lineaInstruccion);

        // Usar métodos de tu clase Dec
        switch (operador) {
            case Suma:           izquierdo = izquierdo.sumar(derecho);        break;
            case Resta:          izquierdo = izquierdo.restar(derecho);       break;
            case Multiplicacion: izquierdo = izquierdo.multiplicar(derecho);  break;
            case Division:       izquierdo = izquierdo.dividir(derecho);      break;
            default: throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": operador desconocido");
        }
    }

    return izquierdo;
}

private TipoDato obtenerValorDec(int lineaInstruccion) throws Exception {
    if (i >= tokens.size()) {
        throw new Exception("Error semántico línea " + lineaInstruccion
                + ": se esperaba un valor decimal pero la expresión terminó");
    }

    Token t = tokens.get(i);

    // ── Menos unario: -3.14 ───────────────────────────────────────────────
    if (t.getTkns() == Tokens.Resta) {
        i++;
        double num = leerNumeroDec(lineaInstruccion);
        return new Dec(-num);
    }

    // ── Número decimal o entero positivo ─────────────────────────────────
    if (t.getTkns() == Tokens.Numero || t.getTkns() == Tokens.OperadorDecimal) {
        double num = leerNumeroDec(lineaInstruccion);
        return new Dec(num);
    }

    // ── Variable dec ya declarada ─────────────────────────────────────────
    if (t.getTkns() == Tokens.Identificador) {
        i++;
        if (!tablaVariables.containsKey(t.getLexema())) {
            throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": variable '" + t.getLexema() + "' no ha sido declarada");
        }
        TipoDato var = tablaVariables.get(t.getLexema());
        if (!(var instanceof Dec)) {
            throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": la variable '" + t.getLexema() + "' no es de tipo 'dec'");
        }
        return var;
    }

    // ── Cadena en expresión decimal ───────────────────────────────────────
    if (t.getTkns() == Tokens.Comillas) {
        throw new Exception("Error semántico línea " + lineaInstruccion
                + ": no se puede usar una cadena en una expresión decimal");
    }

    throw new Exception("Error semántico línea " + lineaInstruccion
            + ": token inesperado '" + t.getLexema() + "' en expresión decimal");
}

// Lee entero o decimal: 3  /  3.14  /  .5
private double leerNumeroDec(int lineaInstruccion) throws Exception {
    StringBuilder parteEntera = new StringBuilder();
    StringBuilder parteDecimal = new StringBuilder();

    // ── Parte entera ─────────────────────────────
    if (i < tokens.size() && tokens.get(i).getTkns() == Tokens.Numero) {
        parteEntera.append(tokens.get(i).getLexema());
        i++;
    }

    // ── Punto decimal ────────────────────────────
    boolean tienePunto = false;

    if (i < tokens.size() && tokens.get(i).getTkns() == Tokens.OperadorDecimal) {
        tienePunto = true;
        i++;

        // ── Parte decimal ────────────────────────
        if (i < tokens.size() && tokens.get(i).getTkns() == Tokens.Numero) {
            parteDecimal.append(tokens.get(i).getLexema());
            i++;
        } else {
            throw new Exception("Error semántico línea " + lineaInstruccion
                    + ": se esperaba dígitos después del '.'");
        }
    }

    // ── Validaciones 🔥 ─────────────────────────

    // 👉 máximo 8 dígitos en parte entera
    if (parteEntera.length() > 9) {
        throw new Exception("Error semántico línea " + lineaInstruccion
                + ": la parte entera excede 8 dígitos");
    }

    // 👉 máximo 2 dígitos en parte decimal
    if (parteDecimal.length() > 8) {
        throw new Exception("Error semántico línea " + lineaInstruccion
                + ": la parte decimal excede 2 dígitos");
    }

    // ── Construcción del número ────────────────
    String numeroStr;

    if (tienePunto) {
        numeroStr = parteEntera + "." + parteDecimal;
    } else {
        numeroStr = parteEntera.toString();
    }

    try {
        return Double.parseDouble(numeroStr);
    } catch (NumberFormatException e) {
        throw new Exception("Error semántico línea " + lineaInstruccion
                + ": formato decimal inválido '" + numeroStr + "'");
    }
}    // ══════════════════════════════════════════════════════
    //  CAD
    // ══════════════════════════════════════════════════════
    private String analizarCad() {
        int lineaInstruccion = linea;
        i++; // saltar 'cad'

        // 1. Debe seguir un identificador
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error sintáctico línea " + lineaInstruccion 
                    + ": se esperaba un identificador después de 'cad'\n";
        }

        if (tokens.get(i).getTkns() != Tokens.Identificador) {
            String encontrado = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error sintáctico línea " + lineaInstruccion 
                    + ": identificador inválido '" + encontrado + "' después de 'cad'\n";
        }

        String nombreVar = tokens.get(i).getLexema();

        // 2. No puede ser palabra reservada
        if (esReservada(nombreVar)) {
            saltarHastaFinalizador();
            return "Error semántico línea " + lineaInstruccion 
                    + ": '" + nombreVar + "' es una palabra reservada\n";
        }

        // 3. Variable ya declarada
        if (tablaVariables.containsKey(nombreVar)) {
            saltarHastaFinalizador();
            return "Error semántico línea " + lineaInstruccion 
                    + ": variable '" + nombreVar + "' ya fue declarada anteriormente\n";
        }

        i++; // saltar identificador

        // 4. Debe seguir ||
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error sintáctico línea " + lineaInstruccion 
                    + ": se esperaba '||' después de '" + nombreVar + "'\n";
        }

        if (tokens.get(i).getTkns() != Tokens.Igual) {
            String encontrado = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error sintáctico línea " + lineaInstruccion 
                    + ": se esperaba '||' pero se encontró '" + encontrado + "'\n";
        }
        i++; // saltar ||

        // 5. Expresión no puede estar vacía
        if (i >= tokens.size() || tokens.get(i).getTkns() == Tokens.Finalizador) {
            saltarHastaFinalizador();
            return "Error semántico línea " + lineaInstruccion 
                    + ": expresión vacía en la asignación de '" + nombreVar + "'\n";
        }

        // 6. No puede asignarse un número a cad
        if (tokens.get(i).getTkns() == Tokens.Numero) {
            saltarHastaFinalizador();
            return "Error semántico línea " + lineaInstruccion 
                    + ": no se puede asignar un número entero a la variable '" + nombreVar + "'\n";
        }

        // 7. No puede asignarse decimal a cad
        if (tokens.get(i).getTkns() == Tokens.OperadorDecimal) {
            saltarHastaFinalizador();
            return "Error semántico línea " + lineaInstruccion 
                    + ": no se puede asignar un decimal a la variable 'cad' '" + nombreVar + "'\n";
        }

        // 8. No puede haber operaciones aritméticas en cad
        if (esOperador(tokens.get(i).getTkns())) {
            saltarHastaFinalizador();
            return "Error semántico línea " + lineaInstruccion 
                    + ": no se permiten operaciones aritméticas en tipo 'cad'\n";
        }

        // 9. Debe ser una cadena entre comillas
        if (tokens.get(i).getTkns() != Tokens.Comillas) {
            String encontrado = tokens.get(i).getLexema();
            saltarHastaFinalizador();
            return "Error semántico línea " + lineaInstruccion 
                    + ": se esperaba una cadena entre comillas pero se encontró '" + encontrado + "'\n";
        }

        String valorCad = tokens.get(i).getLexema();
        i++; // saltar comillas/cadena

        // 10. No puede haber operadores después de la cadena
        if (i < tokens.size() && esOperador(tokens.get(i).getTkns())) {
            saltarHastaFinalizador();
            return "Error semántico línea " + lineaInstruccion 
                    + ": no se pueden concatenar cadenas con operadores aritméticos en 'cad'\n";
        }

        // 11. Debe terminar con $
        if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Finalizador) {
            String encontrado = i < tokens.size() ? tokens.get(i).getLexema() : "fin de archivo";
            saltarHastaFinalizador();
            return "Error sintáctico línea " + lineaInstruccion 
                    + ": se esperaba '$' al final pero se encontró '" + encontrado + "'\n";
        }
        i++; // saltar $

        // 12. Guardar en tabla como Cad
        Cad cadena = new Cad(nombreVar, valorCad.replace("\"", ""));
        tablaVariables.put(nombreVar, cadena);
        return nombreVar + "=\"" + cadena.getValor() + "\", sin error semántico\n";
    }

    private String analizarIncDec() {
    int lineaInstruccion = linea;

    String nombreVar = tokens.get(i).getLexema();

    // 1. Verificar que exista
    if (!tablaVariables.containsKey(nombreVar)) {
        saltarHastaFinalizador();
        return "Error semántico línea " + lineaInstruccion
                + ": variable '" + nombreVar + "' no ha sido declarada\n";
    }

    TipoDato var = tablaVariables.get(nombreVar);

    // 2. Solo aplica a inter y dec
    if (!(var instanceof Inter) && !(var instanceof Dec)) {
        saltarHastaFinalizador();
        return "Error semántico línea " + lineaInstruccion
                + ": incremento/decremento solo válido para 'inter' y 'dec'\n";
    }

    i++; // saltar identificador

    if (i >= tokens.size()) {
        return "Error sintáctico línea " + lineaInstruccion
                + ": se esperaba '++' o '--'\n";
    }

    Tokens op = tokens.get(i).getTkns();

    // 3. Validar operador
    if (op != Tokens.Incremento && op != Tokens.Decremento) {
        return "Error sintáctico línea " + lineaInstruccion
                + ": se esperaba '++' o '--'\n";
    }

    i++; // saltar ++ o --

    // 4. Debe terminar en $
    if (i >= tokens.size() || tokens.get(i).getTkns() != Tokens.Finalizador) {
        return "Error sintáctico línea " + lineaInstruccion
                + ": se esperaba '$' al final\n";
    }

    i++; // saltar $

    // 5. Aplicar operación
    if (var instanceof Inter) {
        Inter interVar = (Inter) var;

        int nuevoValor = interVar.getValor();

        if (op == Tokens.Incremento) {
            nuevoValor++;
        } else {
            nuevoValor--;
        }

        tablaVariables.put(nombreVar, new Inter(nuevoValor));

        return nombreVar + "=" + nuevoValor + ", sin error semántico\n";
    }

    if (var instanceof Dec) {
        Dec decVar = (Dec) var;

        double nuevoValor = decVar.getValor();

        if (op == Tokens.Incremento) {
            nuevoValor++;
        } else {
            nuevoValor--;
        }

        tablaVariables.put(nombreVar, new Dec(nuevoValor));

        return nombreVar + "=" + nuevoValor + ", sin error semántico\n";
    }

    return "";
}
    
    
    
    // ══════════════════════════════════════════════════════
    //  UTILIDADES
    // ══════════════════════════════════════════════════════
    private boolean esOperador(Tokens tkn) {
        return tkn == Tokens.Suma || tkn == Tokens.Resta
            || tkn == Tokens.Multiplicacion || tkn == Tokens.Division;
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
