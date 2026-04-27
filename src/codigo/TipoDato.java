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
public abstract class TipoDato {
    public abstract TipoDato sumar(TipoDato otro) throws Exception;
    public abstract TipoDato restar(TipoDato otro) throws Exception;
    public abstract TipoDato multiplicar(TipoDato otro) throws Exception;
    public abstract TipoDato dividir(TipoDato otro) throws Exception;
}
