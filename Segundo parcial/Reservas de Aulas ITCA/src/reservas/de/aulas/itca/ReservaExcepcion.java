/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservas.de.aulas.itca;

/**
 *
 * @author ottop
 */
public class ReservaExcepcion extends Exception {
    public ReservaExcepcion(String mensaje) {
        super(mensaje);
    }
    
    public ReservaExcepcion(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}