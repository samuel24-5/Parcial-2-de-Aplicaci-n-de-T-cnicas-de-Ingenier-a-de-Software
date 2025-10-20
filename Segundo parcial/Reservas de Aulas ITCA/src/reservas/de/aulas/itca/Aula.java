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
public class Aula {
    private String codigo;
    private String nombre;
    private int capacidad;
    private TipoAula tipo;
    private boolean disponible;
    
    public Aula(String codigo, String nombre, int capacidad, TipoAula tipo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.tipo = tipo;
        this.disponible = true;
    }
    
    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    
    public TipoAula getTipo() { return tipo; }
    public void setTipo(TipoAula tipo) { this.tipo = tipo; }
    
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    
    @Override
    public String toString() {
        return String.format("Aula [%s] %s - %s - Capacidad: %d - %s", 
                codigo, nombre, tipo, capacidad, disponible ? "Disponible" : "No disponible");
    }
}