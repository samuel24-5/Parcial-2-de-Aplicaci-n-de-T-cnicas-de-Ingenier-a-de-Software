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
public abstract class Reserva implements Validable {
    protected static int contadorId = 1;
    
    protected int id;
    protected Aula aula;
    protected LocalDate fecha;
    protected LocalTime horaInicio;
    protected LocalTime horaFin;
    protected String responsable;
    protected String descripcion;
    protected EstadoReserva estado;
    
    public Reserva(Aula aula, LocalDate fecha, LocalTime horaInicio, 
                   LocalTime horaFin, String responsable, String descripcion) {
        this.id = contadorId++;
        this.aula = aula;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.responsable = responsable;
        this.descripcion = descripcion;
        this.estado = EstadoReserva.ACTIVA;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public Aula getAula() { return aula; }
    public void setAula(Aula aula) { this.aula = aula; }
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    
    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
    
    // Método abstracto para obtener tipo específico
    public abstract String getTipoReserva();
    
    @Override
    public boolean validar() throws ReservaExcepcion {
        if (fecha == null || horaInicio == null || horaFin == null) {
            throw new ReservaExcepcion("Fecha y horas son obligatorias");
        }
        
        if (fecha.isBefore(LocalDate.now())) {
            throw new ReservaExcepcion("No se pueden hacer reservas en fechas pasadas");
        }
        
        if (!horaFin.isAfter(horaInicio)) {
            throw new ReservaExcepcion("La hora de fin debe ser posterior a la hora de inicio");
        }
        
        if (responsable == null || responsable.trim().isEmpty()) {
            throw new ReservaExcepcion("El responsable es obligatorio");
        }
        
        return true;
    }
    
    public long getDuracionHoras() {
        return java.time.Duration.between(horaInicio, horaFin).toHours();
    }
    
    @Override
    public String toString() {
        return String.format("Reserva #%d - %s [%s] - Aula: %s - %s %s-%s - %s", 
                id, getTipoReserva(), estado, aula.getCodigo(), fecha, 
                horaInicio, horaFin, responsable);
    }
}