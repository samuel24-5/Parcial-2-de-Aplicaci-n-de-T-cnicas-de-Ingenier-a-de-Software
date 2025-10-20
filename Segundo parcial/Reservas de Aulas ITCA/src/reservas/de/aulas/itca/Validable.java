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
public interface Validable {
    boolean validar() throws ReservaExcepcion;
}