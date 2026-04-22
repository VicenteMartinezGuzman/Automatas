/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codigo;

import java.io.IOException;

/**
 *
 * @author vicen
 */
public abstract class Operacion {
    public abstract Number sumar(Number a, Number b) throws Exception;
    public abstract Number restar(Number a, Number b) throws Exception;
    public abstract Number multiplicar(Number a, Number b) throws Exception;
    public abstract Number dividir(Number a, Number b) throws Exception;
}
