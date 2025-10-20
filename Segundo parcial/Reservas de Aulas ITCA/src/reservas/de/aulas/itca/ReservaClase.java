/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservas.de.aulas.itca;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author ottop
 */
public class ReservaClase extends Reserva {
    private String materia;
    private String grupo;
    
    public ReservaClase(Aula aula, LocalDate fecha, LocalTime horaInicio, 
                       LocalTime horaFin, String responsable, String descripcion,
                       String materia, String grupo) {
        super(aula, fecha, horaInicio, horaFin, responsable, descripcion);
        this.materia = materia;
        this.grupo = grupo;
    }
    
    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }
    
    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }
    
    @Override
    public String getTipoReserva() {
        return "CLASE";
    }
    
    @Override
    public boolean validar() throws ReservaExcepcion {
        super.validar();
        
        if (materia == null || materia.trim().isEmpty()) {
            throw new ReservaExcepcion("La materia es obligatoria para reservas de clase");
        }
        
        if (grupo == null || grupo.trim().isEmpty()) {
            throw new ReservaExcepcion("El grupo es obligatorio para reservas de clase");
        }
        
        // Validar que las clases sean en horario laboral (8:00 - 18:00)
        if (horaInicio.isBefore(LocalTime.of(8, 0)) || 
            horaFin.isAfter(LocalTime.of(18, 0))) {
            throw new ReservaExcepcion("Las clases deben estar entre 8:00 y 18:00");
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        return super.toString() + String.format(" - Materia: %s - Grupo: %s", materia, grupo);
    }
}
