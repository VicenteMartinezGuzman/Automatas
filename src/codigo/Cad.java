/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codigo;

/**
 * Clase Cadena - Tipo 'cad' del lenguaje CEROUNO
 * Detecta errores semanticos en declaraciones de cadena.
 *
 * Ejemplo valido:
 *   cad nombre || "Hola" ;
 *
 * Errores semanticos que detecta:
 *   cad nombre || 42 ;      → Error: no puedes poner entero en cad
 *   cad nombre || 3.14 ;    → Error: no puedes poner decimal en cad
 *   cad nombre || true ;    → Error: no puedes poner booleano en cad
 */
public class Cad extends TipoDato {

    private String nombre;
    private String valor;

    // ── Constructores ──────────────────────────────────────

    public Cad() {
        this.nombre = "";
        this.valor  = "";
    }

    public Cad(String nombre, String valor) {
        this.nombre = nombre;
        this.valor  = valor;
    }

    // ── Getters y Setters ──────────────────────────────────

    public String getNombre() { return nombre; }
    public void   setNombre(String nombre) { this.nombre = nombre; }
    public String getValor()  { return valor; }
    public void   setValor(String valor)   { this.valor = valor; }

    // ── Polimorfismo: Operacion ────────────────────────────
    // Cadena NO puede hacer operaciones matematicas → error semantico

    @Override
    public Number sumar(Number a, Number b) throws Exception {
        throw new Exception(
            ">> Error semantico: No se puede SUMAR variables de tipo 'cad'."
        );
    }

    @Override
    public Number restar(Number a, Number b) throws Exception {
        throw new Exception(
            ">> Error semantico: No se puede RESTAR variables de tipo 'cad'."
        );
    }

    @Override
    public Number multiplicar(Number a, Number b) throws Exception {
        throw new Exception(
            ">> Error semantico: No se puede MULTIPLICAR variables de tipo 'cad'."
        );
    }

    @Override
    public Number dividir(Number a, Number b) throws Exception {
        throw new Exception(
            ">> Error semantico: No se puede DIVIDIR variables de tipo 'cad'."
        );
    }

    // ── VALIDACION SEMANTICA ───────────────────────────────
    /**
     * Revisa si el valor asignado a 'cad' es compatible.
     * Solo acepta texto entre comillas.
     *
     * @param valor       lo que viene despues del || o =
     * @param numeroLinea para el mensaje de error
     */
    public void validarSemantica(String valor, int numeroLinea) throws Exception {
        valor = valor.trim();

        // Quitamos el ; si viene incluido
        valor = valor.replace(";", "").trim();

        // Si el valor NO empieza con comilla, es sospechoso
        if (!valor.startsWith("\"")) {

            // ¿Es un numero entero?
            if (valor.matches("^-?\\d+$")) {
                throw new Exception(
                    ">> Error semantico | Linea " + numeroLinea + ":\n" +
                    "   No es posible asignar el valor entero [" + valor + "] " +
                    "a una variable de tipo 'cad'.\n" +
                    "   Los enteros se declaran con 'integer'.\n" +
                    "   Linea " + numeroLinea
                );
            }

            // ¿Es un numero decimal?
            if (valor.matches("^-?\\d+\\.\\d+$")) {
                throw new Exception(
                    ">> Error semantico | Linea " + numeroLinea + ":\n" +
                    "   No es posible asignar el valor decimal [" + valor + "] " +
                    "a una variable de tipo 'cad'.\n" +
                    "   Los decimales se declaran con 'dec'.\n" +
                    "   Linea " + numeroLinea
                );
            }

            // ¿Es un booleano?
            if (valor.equals("true") || valor.equals("false")) {
                throw new Exception(
                    ">> Error semantico | Linea " + numeroLinea + ":\n" +
                    "   No es posible asignar el booleano [" + valor + "] " +
                    "a una variable de tipo 'cad'.\n" +
                    "   Linea " + numeroLinea
                );
            }
        }

        // Si llega aqui: el valor es texto entre comillas → OK
        this.valor = valor.replace("\"", "").trim();
    }

    // ── INTERPRETAR ────────────────────────────────────────
    /**
     * Procesa una linea completa CEROUNO de tipo cadena.
     * Extrae nombre y valor, luego valida semantica.
     *
     * para linea        ej: cad nombre || "Hola" ;
     * para numeroLinea  numero de linea para errores
     */
    public void interpretar(String linea, int numeroLinea) throws Exception {
        linea = linea.trim();

    // Verificar que tenga || antes de dividir
        if (!linea.contains("||")) {
        throw new Exception(
            ">> Error semantico | Linea " + numeroLinea + ":\n" +
            "   Falta el operador de asignacion '||'.\n" +
            "   Linea " + numeroLinea
        );
    }

        // Ahora sí dividimos, ya sabemos que || existe
        String[] partes = linea.split("\\|\\|", 2);

        // Extraer nombre
        this.nombre = partes[0].replace("cad", "").trim();

          // Extraer valor
         String valorBruto = partes[1].replace(";", "").trim();

          // Validar semantica
        validarSemantica(valorBruto, numeroLinea);
}

    @Override
    public String toString() {
        return "cad " + nombre + " || \"" + valor + "\" ;";
    }

    // MAIN PARA PROBAR LA CLASE SOLA 
    
    public static void main(String[] args) {

        System.out.println("PRUEBA CLASE Cadena CEROUNO\n");

        Cad c = new Cad();

        // Casos de prueba: {linea, descripcion}
        String[][] casos = {
            // Validos
            {"cad nombre || \"Hola Mundo\" ;",     "VALIDO PORQUE ES texto normal"},
            {"cad vacia || \"\" ;",                 "VALIDO PORQUE ES UNA cadena vacia"},
            // Errores semanticos
            {"cad numero || 42 ;",                  "ERROR PORQUE ES UN entero en cad"},
            {"cad precio || 9.5 ;",                 "ERROR PORQUE ES UN decimal en cad"},
            {"cad activo || true ;",                "ERROR PORQUE ES UN booleano en cad"},
            // Polimorfismo
        };

        for (int i = 0; i < casos.length; i++) {
            System.out.println("Linea " + (i+1) + ": " + casos[i][0]);
            System.out.println("Caso:   " + casos[i][1]);
            try {
                c.interpretar(casos[i][0], i + 1);
                System.out.println("Resultado: OK PORQUE -> nombre='" 
                    + c.getNombre() + "' valor='" + c.getValor() + "'");
            } catch (Exception e) {
                System.out.println("Resultado: " + e.getMessage());
            }
            System.out.println();
        }

        // Polimorfismo
        System.out.println("POLIMORFISMO operaciones sobre cadena\n");
        Cad c2 = new Cad("x", "Hola");
        try { c2.sumar(1,2); }       catch(Exception e){ System.out.println(e.getMessage()); }
        try { c2.restar(1,2); }      catch(Exception e){ System.out.println(e.getMessage()); }
        try { c2.multiplicar(1,2); } catch(Exception e){ System.out.println(e.getMessage()); }
        try { c2.dividir(1,2); }     catch(Exception e){ System.out.println(e.getMessage()); }
    }
    }