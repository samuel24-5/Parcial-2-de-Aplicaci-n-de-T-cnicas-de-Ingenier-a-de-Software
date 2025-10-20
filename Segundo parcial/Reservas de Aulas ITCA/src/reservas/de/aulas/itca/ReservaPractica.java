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
public class ReservaPractica extends Reserva {
    private String equipoNecesario;
    private int cantidadEstudiantes;
    
    public ReservaPractica(Aula aula, LocalDate fecha, LocalTime horaInicio,
                          LocalTime horaFin, String responsable, String descripcion,
                          String equipoNecesario, int cantidadEstudiantes) {
        super(aula, fecha, horaInicio, horaFin, responsable, descripcion);
        this.equipoNecesario = equipoNecesario;
        this.cantidadEstudiantes = cantidadEstudiantes;
    }
    
    public String getEquipoNecesario() { return equipoNecesario; }
    public void setEquipoNecesario(String equipoNecesario) { this.equipoNecesario = equipoNecesario; }
    
    public int getCantidadEstudiantes() { return cantidadEstudiantes; }
    public void setCantidadEstudiantes(int cantidadEstudiantes) { this.cantidadEstudiantes = cantidadEstudiantes; }
    
    @Override
    public String getTipoReserva() {
        return "PRACTICA";
    }
    
    @Override
    public boolean validar() throws ReservaExcepcion {
        super.validar();
        
        if (cantidadEstudiantes <= 0) {
            throw new ReservaExcepcion("La cantidad de estudiantes debe ser mayor a 0");
        }
        
        if (cantidadEstudiantes > aula.getCapacidad()) {
            throw new ReservaExcepcion("La cantidad de estudiantes excede la capacidad del aula");
        }
        
        // Validar que el aula sea de tipo LABORATORIO para prácticas
        if (aula.getTipo() != TipoAula.LABORATORIO) {
            throw new ReservaExcepcion("Las prácticas deben realizarse en laboratorios");
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        return super.toString() + String.format(" - Estudiantes: %d - Equipo: %s", 
                cantidadEstudiantes, equipoNecesario);
    }
}