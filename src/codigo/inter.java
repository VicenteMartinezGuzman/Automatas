
package codigo;
/**
 *
 * @author sandr
 */
public class inter extends Validar {
    private int valor;
    
    public inter(int valor) {
        this.valor = valor;
    }
    @Override
    public Object evaluar() {
        return valor;
    }
}
