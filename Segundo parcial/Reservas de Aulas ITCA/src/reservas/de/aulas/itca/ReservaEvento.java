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
public class ReservaEvento extends Reserva {
    private TipoEvento tipoEvento;
    private int participantesEsperados;
    
    public ReservaEvento(Aula aula, LocalDate fecha, LocalTime horaInicio,
                        LocalTime horaFin, String responsable, String descripcion,
                        TipoEvento tipoEvento, int participantesEsperados) {
        super(aula, fecha, horaInicio, horaFin, responsable, descripcion);
        this.tipoEvento = tipoEvento;
        this.participantesEsperados = participantesEsperados;
    }
    
    public TipoEvento getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(TipoEvento tipoEvento) { this.tipoEvento = tipoEvento; }
    
    public int getParticipantesEsperados() { return participantesEsperados; }
    public void setParticipantesEsperados(int participantesEsperados) { 
        this.participantesEsperados = participantesEsperados; 
    }
    
    @Override
    public String getTipoReserva() {
        return "EVENTO";
    }
    
    @Override
    public boolean validar() throws ReservaExcepcion {
        super.validar();
        
        if (participantesEsperados <= 0) {
            throw new ReservaExcepcion("La cantidad de participantes debe ser mayor a 0");
        }
        
        if (participantesEsperados > aula.getCapacidad()) {
            throw new ReservaExcepcion("La cantidad de participantes excede la capacidad del aula");
        }
        
        // Validar tipo de aula según tipo de evento
        switch (tipoEvento) {
            case CONFERENCIA:
                if (aula.getTipo() != TipoAula.AUDITORIO && aula.getTipo() != TipoAula.TEORICA) {
                    throw new ReservaExcepcion("Las conferencias requieren auditorio o aula teórica");
                }
                break;
            case TALLER:
                if (aula.getTipo() != TipoAula.LABORATORIO) {
                    throw new ReservaExcepcion("Los talleres requieren laboratorio");
                }
                break;
            case REUNION:
                // Las reuniones pueden ser en cualquier tipo de aula
                break;
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        return super.toString() + String.format(" - Evento: %s - Participantes: %d", 
                tipoEvento, participantesEsperados);
    }
}
