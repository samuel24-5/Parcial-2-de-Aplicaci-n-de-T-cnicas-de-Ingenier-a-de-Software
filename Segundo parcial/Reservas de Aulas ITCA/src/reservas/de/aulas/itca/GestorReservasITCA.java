/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservas.de.aulas.itca;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author ottop
 */
public class GestorReservasITCA {
    private List<Aula> aulas;
    private List<Reserva> reservas;
    private Scanner scanner;
    private static final String ARCHIVO_AULAS = "aulas.csv";
    private static final String ARCHIVO_RESERVAS = "reservas.csv";
    private static final String ARCHIVO_REPORTES = "reportes.txt";
    
    public GestorReservasITCA() {
        this.aulas = new ArrayList<>();
        this.reservas = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        cargarDatos();
    }
    
    // Métodos de persistencia
    private void cargarDatos() {
        cargarAulas();
        cargarReservas();
    }
    
    private void cargarAulas() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_AULAS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 4) {
                    String codigo = datos[0];
                    String nombre = datos[1];
                    int capacidad = Integer.parseInt(datos[2]);
                    TipoAula tipo = TipoAula.valueOf(datos[3]);
                    aulas.add(new Aula(codigo, nombre, capacidad, tipo));
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar el archivo de aulas. Se iniciará con datos vacíos.");
        }
    }
    
    private void cargarReservas() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_RESERVAS))) {
            String linea;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 8) {
                    String tipo = datos[0];
                    int id = Integer.parseInt(datos[1]);
                    String codigoAula = datos[2];
                    LocalDate fecha = LocalDate.parse(datos[3], dateFormatter);
                    LocalTime horaInicio = LocalTime.parse(datos[4], timeFormatter);
                    LocalTime horaFin = LocalTime.parse(datos[5], timeFormatter);
                    String responsable = datos[6];
                    String descripcion = datos[7];
                    EstadoReserva estado = EstadoReserva.valueOf(datos[8]);
                    
                    Aula aula = buscarAulaPorCodigo(codigoAula);
                    if (aula != null) {
                        Reserva reserva = null;
                        
                        switch (tipo) {
                            case "CLASE":
                                reserva = new ReservaClase(aula, fecha, horaInicio, horaFin, 
                                        responsable, descripcion, datos[9], datos[10]);
                                break;
                            case "PRACTICA":
                                reserva = new ReservaPractica(aula, fecha, horaInicio, horaFin,
                                        responsable, descripcion, datos[9], Integer.parseInt(datos[10]));
                                break;
                            case "EVENTO":
                                reserva = new ReservaEvento(aula, fecha, horaInicio, horaFin,
                                        responsable, descripcion, TipoEvento.valueOf(datos[9]), 
                                        Integer.parseInt(datos[10]));
                                break;
                        }
                        
                        if (reserva != null) {
                            reserva.setEstado(estado);
                            reservas.add(reserva);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar el archivo de reservas. Se iniciará con datos vacíos.");
        }
    }
    
    private void guardarDatos() {
        guardarAulas();
        guardarReservas();
    }
    
    private void guardarAulas() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_AULAS))) {
            for (Aula aula : aulas) {
                pw.println(String.format("%s,%s,%d,%s",
                        aula.getCodigo(), aula.getNombre(), aula.getCapacidad(), aula.getTipo()));
            }
        } catch (IOException e) {
            System.out.println("Error al guardar aulas: " + e.getMessage());
        }
    }
    
    private void guardarReservas() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_RESERVAS))) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            
            for (Reserva reserva : reservas) {
                String lineaBase = String.format("%s,%d,%s,%s,%s,%s,%s,%s",
                        reserva.getTipoReserva(),
                        reserva.getId(),
                        reserva.getAula().getCodigo(),
                        reserva.getFecha().format(dateFormatter),
                        reserva.getHoraInicio().format(timeFormatter),
                        reserva.getHoraFin().format(timeFormatter),
                        reserva.getResponsable(),
                        reserva.getDescripcion(),
                        reserva.getEstado());
                
                String datosEspecificos = "";
                if (reserva instanceof ReservaClase) {
                    ReservaClase rc = (ReservaClase) reserva;
                    datosEspecificos = String.format(",%s,%s", rc.getMateria(), rc.getGrupo());
                } else if (reserva instanceof ReservaPractica) {
                    ReservaPractica rp = (ReservaPractica) reserva;
                    datosEspecificos = String.format(",%s,%d", rp.getEquipoNecesario(), rp.getCantidadEstudiantes());
                } else if (reserva instanceof ReservaEvento) {
                    ReservaEvento re = (ReservaEvento) reserva;
                    datosEspecificos = String.format(",%s,%d", re.getTipoEvento(), re.getParticipantesEsperados());
                }
                
                pw.println(lineaBase + datosEspecificos);
            }
        } catch (IOException e) {
            System.out.println("Error al guardar reservas: " + e.getMessage());
        }
    }
    
    // Métodos de gestión de aulas
    private void gestionarAulas() {
        while (true) {
            System.out.println("\n=== GESTIÓN DE AULAS ===");
            System.out.println("1. Registrar aula");
            System.out.println("2. Listar aulas");
            System.out.println("3. Modificar aula");
            System.out.println("4. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            
            int opcion = leerEntero();
            
            switch (opcion) {
                case 1: registrarAula(); break;
                case 2: listarAulas(); break;
                case 3: modificarAula(); break;
                case 4: return;
                default: System.out.println("Opción inválida");
            }
        }
    }
    
    private void registrarAula() {
        System.out.println("\n--- REGISTRAR AULA ---");

        scanner.nextLine();

        System.out.print("Código del aula: ");
        String codigo = scanner.nextLine().trim();

        // Validar que el código no esté vacío
        if (codigo.isEmpty()) {
            System.out.println("Error: El código del aula no puede estar vacío");
            return;
        }

        // Validar si el código ya existe
        if (buscarAulaPorCodigo(codigo) != null) {
            System.out.println("Error: Ya existe un aula con el código '" + codigo + "'");
            return;
        }

        System.out.print("Nombre del aula: ");
        String nombre = scanner.nextLine().trim();

        if (nombre.isEmpty()) {
            System.out.println("Error: El nombre del aula no puede estar vacío");
            return;
        }

        System.out.print("Capacidad: ");
        int capacidad = leerEntero();

        // Validar capacidad positiva
        if (capacidad <= 0) {
            System.out.println("Error: La capacidad debe ser un número positivo");
            return;
        }

        System.out.println("Tipos de aula disponibles:");
        for (int i = 0; i < TipoAula.values().length; i++) {
            System.out.println((i + 1) + ". " + TipoAula.values()[i]);
        }
        System.out.print("Seleccione el tipo de aula: ");
        int tipoIndex = leerEntero() - 1;

        if (tipoIndex < 0 || tipoIndex >= TipoAula.values().length) {
            System.out.println("Error: Tipo de aula inválido");
            return;
        }

        TipoAula tipo = TipoAula.values()[tipoIndex];

        try {
            Aula nuevaAula = new Aula(codigo, nombre, capacidad, tipo);
            aulas.add(nuevaAula);
            guardarDatos();
            System.out.println("Aula registrada exitosamente: " + nuevaAula);
        } catch (Exception e) {
            System.out.println("Error al registrar el aula: " + e.getMessage());
        }
    }
    
    private void listarAulas() {
        System.out.println("\n--- LISTA DE AULAS ---");
        if (aulas.isEmpty()) {
            System.out.println("No hay aulas registradas");
            return;
        }
        
        aulas.forEach(System.out::println);
    }
    
    private void modificarAula() {
        System.out.println("\n--- MODIFICAR AULA ---");
        System.out.print("Código del aula a modificar: ");
        String codigo = scanner.nextLine();
        
        Aula aula = buscarAulaPorCodigo(codigo);
        if (aula == null) {
            System.out.println("No se encontró el aula");
            return;
        }
        
        System.out.println("Aula actual: " + aula);
        System.out.print("Nuevo nombre (actual: " + aula.getNombre() + "): ");
        String nombre = scanner.nextLine();
        if (!nombre.trim().isEmpty()) {
            aula.setNombre(nombre);
        }
        
        System.out.print("Nueva capacidad (actual: " + aula.getCapacidad() + "): ");
        String capacidadStr = scanner.nextLine();
        if (!capacidadStr.trim().isEmpty()) {
            try {
                aula.setCapacidad(Integer.parseInt(capacidadStr));
            } catch (NumberFormatException e) {
                System.out.println("Capacidad inválida, se mantiene la actual");
            }
        }
        
        System.out.println("¿Cambiar disponibilidad? (s/n): ");
        String respuesta = scanner.nextLine();
        if (respuesta.equalsIgnoreCase("s")) {
            aula.setDisponible(!aula.isDisponible());
        }
        
        System.out.println("Aula modificada exitosamente");
        guardarDatos();
    }
    
    // Métodos de gestión de reservas
    private void gestionarReservas() {
        while (true) {
            System.out.println("\n=== GESTIÓN DE RESERVAS ===");
            System.out.println("1. Registrar reserva");
            System.out.println("2. Buscar reservas");
            System.out.println("3. Modificar reserva");
            System.out.println("4. Cancelar reserva");
            System.out.println("5. Listar todas las reservas");
            System.out.println("6. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            
            int opcion = leerEntero();
            
            switch (opcion) {
                case 1: registrarReserva(); break;
                case 2: buscarReservas(); break;
                case 3: modificarReserva(); break;
                case 4: cancelarReserva(); break;
                case 5: listarReservas(); break;
                case 6: return;
                default: System.out.println("Opción inválida");
            }
        }
    }
    
    private void registrarReserva() {
        System.out.println("\n--- REGISTRAR RESERVA ---");
        System.out.println("Tipo de reserva:");
        System.out.println("1. Clase");
        System.out.println("2. Práctica");
        System.out.println("3. Evento");
        System.out.print("Seleccione tipo: ");
        
        int tipo = leerEntero();
        if (tipo < 1 || tipo > 3) {
            System.out.println("Tipo inválido");
            return;
        }
        
        // Seleccionar aula
        listarAulas();
        System.out.print("Código del aula: ");
        String codigoAula = scanner.nextLine();
        Aula aula = buscarAulaPorCodigo(codigoAula);
        
        if (aula == null || !aula.isDisponible()) {
            System.out.println("Aula no disponible o no encontrada");
            return;
        }
        
        // Datos comunes
        System.out.print("Fecha (yyyy-mm-dd): ");
        LocalDate fecha = leerFecha();
        
        System.out.print("Hora inicio (HH:mm): ");
        LocalTime horaInicio = leerHora();
        
        System.out.print("Hora fin (HH:mm): ");
        LocalTime horaFin = leerHora();
        
        scanner.nextLine(); // Limpiar buffer
        System.out.print("Responsable: ");
        String responsable = scanner.nextLine();
        
        System.out.print("Descripción: ");
        String descripcion = scanner.nextLine();
        
        // Validar conflicto de horarios
        if (existeConflictoHorario(aula, fecha, horaInicio, horaFin, -1)) {
            System.out.println("Conflicto de horario con otra reserva");
            return;
        }
        
        try {
            Reserva reserva = null;
            
            switch (tipo) {
                case 1: // Clase
                    System.out.print("Materia: ");
                    String materia = scanner.nextLine();
                    System.out.print("Grupo: ");
                    String grupo = scanner.nextLine();
                    reserva = new ReservaClase(aula, fecha, horaInicio, horaFin, 
                            responsable, descripcion, materia, grupo);
                    break;
                    
                case 2: // Práctica
                    System.out.print("Equipo necesario: ");
                    String equipo = scanner.nextLine();
                    System.out.print("Cantidad de estudiantes: ");
                    int estudiantes = leerEntero();
                    reserva = new ReservaPractica(aula, fecha, horaInicio, horaFin,
                            responsable, descripcion, equipo, estudiantes);
                    break;
                    
                case 3: // Evento
                    System.out.println("Tipo de evento:");
                    for (int i = 0; i < TipoEvento.values().length; i++) {
                        System.out.println((i + 1) + ". " + TipoEvento.values()[i]);
                    }
                    System.out.print("Seleccione tipo: ");
                    int tipoEventoIndex = leerEntero() - 1;
                    TipoEvento tipoEvento = TipoEvento.values()[tipoEventoIndex];
                    
                    System.out.print("Participantes esperados: ");
                    int participantes = leerEntero();
                    reserva = new ReservaEvento(aula, fecha, horaInicio, horaFin,
                            responsable, descripcion, tipoEvento, participantes);
                    break;
            }
            
            if (reserva != null && reserva.validar()) {
                reservas.add(reserva);
                System.out.println("Reserva registrada exitosamente");
                guardarDatos();
            }
            
        } catch (ReservaExcepcion e) {
            System.out.println("Error al validar reserva: " + e.getMessage());
        }
    }
    
    private void buscarReservas() {
        System.out.println("\n--- BUSCAR RESERVAS ---");
        System.out.println("1. Por responsable");
        System.out.println("2. Por aula");
        System.out.println("3. Por fecha");
        System.out.println("4. Por tipo de reserva");
        System.out.print("Seleccione criterio: ");
        
        int criterio = leerEntero();
        List<Reserva> resultados = new ArrayList<>();
        
        switch (criterio) {
            case 1:
                System.out.print("Nombre del responsable: ");
                scanner.nextLine();
                String responsable = scanner.nextLine();
                resultados = reservas.stream()
                    .filter(r -> r.getResponsable().toLowerCase().contains(responsable.toLowerCase()))
                    .collect(Collectors.toList());
                break;
                
            case 2:
                listarAulas();
                System.out.print("Código del aula: ");
                scanner.nextLine();
                String codigoAula = scanner.nextLine();
                resultados = reservas.stream()
                    .filter(r -> r.getAula().getCodigo().equals(codigoAula))
                    .collect(Collectors.toList());
                break;
                
            case 3:
                System.out.print("Fecha (yyyy-mm-dd): ");
                LocalDate fecha = leerFecha();
                resultados = reservas.stream()
                    .filter(r -> r.getFecha().equals(fecha))
                    .collect(Collectors.toList());
                break;
                
            case 4:
                System.out.println("Tipo de reserva:");
                System.out.println("1. Clase");
                System.out.println("2. Práctica");
                System.out.println("3. Evento");
                System.out.print("Seleccione tipo: ");
                int tipo = leerEntero();
                String tipoStr = "";
                switch (tipo) {
                    case 1: tipoStr = "CLASE"; break;
                    case 2: tipoStr = "PRACTICA"; break;
                    case 3: tipoStr = "EVENTO"; break;
                }
                final String tipoFinal = tipoStr;
                resultados = reservas.stream()
                    .filter(r -> r.getTipoReserva().equals(tipoFinal))
                    .collect(Collectors.toList());
                break;
                
            default:
                System.out.println("Criterio inválido");
                return;
        }
        
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron reservas");
        } else {
            System.out.println("\n--- RESULTADOS DE BÚSQUEDA ---");
            resultados.forEach(System.out::println);
        }
    }
    
    private void modificarReserva() {
        System.out.println("\n--- MODIFICAR RESERVA ---");
        System.out.print("ID de la reserva a modificar: ");
        int id = leerEntero();
        
        Reserva reserva = buscarReservaPorId(id);
        if (reserva == null) {
            System.out.println("No se encontró la reserva");
            return;
        }
        
        if (reserva.getEstado() != EstadoReserva.ACTIVA) {
            System.out.println("Solo se pueden modificar reservas activas");
            return;
        }
        
        System.out.println("Reserva actual: " + reserva);
        System.out.println("¿Qué desea modificar?");
        System.out.println("1. Fecha");
        System.out.println("2. Hora inicio");
        System.out.println("3. Hora fin");
        System.out.println("4. Descripción");
        System.out.print("Seleccione opción: ");
        
        int opcion = leerEntero();
        
        try {
            switch (opcion) {
                case 1:
                    System.out.print("Nueva fecha (yyyy-mm-dd): ");
                    LocalDate nuevaFecha = leerFecha();
                    if (existeConflictoHorario(reserva.getAula(), nuevaFecha, 
                            reserva.getHoraInicio(), reserva.getHoraFin(), reserva.getId())) {
                        System.out.println("Conflicto de horario con otra reserva");
                        return;
                    }
                    reserva.setFecha(nuevaFecha);
                    break;
                    
                case 2:
                    System.out.print("Nueva hora inicio (HH:mm): ");
                    LocalTime nuevaHoraInicio = leerHora();
                    if (existeConflictoHorario(reserva.getAula(), reserva.getFecha(), 
                            nuevaHoraInicio, reserva.getHoraFin(), reserva.getId())) {
                        System.out.println("Conflicto de horario con otra reserva");
                        return;
                    }
                    reserva.setHoraInicio(nuevaHoraInicio);
                    break;
                    
                case 3:
                    System.out.print("Nueva hora fin (HH:mm): ");
                    LocalTime nuevaHoraFin = leerHora();
                    if (existeConflictoHorario(reserva.getAula(), reserva.getFecha(), 
                            reserva.getHoraInicio(), nuevaHoraFin, reserva.getId())) {
                        System.out.println("Conflicto de horario con otra reserva");
                        return;
                    }
                    reserva.setHoraFin(nuevaHoraFin);
                    break;
                    
                case 4:
                    System.out.print("Nueva descripción: ");
                    scanner.nextLine();
                    String nuevaDescripcion = scanner.nextLine();
                    reserva.setDescripcion(nuevaDescripcion);
                    break;
                    
                default:
                    System.out.println("Opción inválida");
                    return;
            }
            
            if (reserva.validar()) {
                System.out.println("Reserva modificada exitosamente");
                guardarDatos();
            }
            
        } catch (ReservaExcepcion e) {
            System.out.println("Error al validar reserva: " + e.getMessage());
        }
    }
    
    private void cancelarReserva() {
        System.out.println("\n--- CANCELAR RESERVA ---");
        System.out.print("ID de la reserva a cancelar: ");
        int id = leerEntero();
        
        Reserva reserva = buscarReservaPorId(id);
        if (reserva == null) {
            System.out.println("No se encontró la reserva");
            return;
        }
        
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            System.out.println("La reserva ya está cancelada");
            return;
        }
        
        reserva.setEstado(EstadoReserva.CANCELADA);
        System.out.println("Reserva cancelada exitosamente");
        guardarDatos();
    }
    
    private void listarReservas() {
        System.out.println("\n--- TODAS LAS RESERVAS ---");
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas registradas");
            return;
        }
        
        // Ordenamiento configurable
        System.out.println("Ordenar por:");
        System.out.println("1. Fecha");
        System.out.println("2. Aula");
        System.out.println("3. Responsable");
        System.out.print("Seleccione criterio: ");
        
        int criterio = leerEntero();
        List<Reserva> reservasOrdenadas = new ArrayList<>(reservas);
        
        switch (criterio) {
            case 1:
                reservasOrdenadas.sort(Comparator.comparing(Reserva::getFecha)
                        .thenComparing(Reserva::getHoraInicio));
                break;
            case 2:
                reservasOrdenadas.sort(Comparator.comparing(r -> r.getAula().getCodigo()));
                break;
            case 3:
                reservasOrdenadas.sort(Comparator.comparing(Reserva::getResponsable));
                break;
            default:
                System.out.println("Criterio inválido, se mostrarán sin orden específico");
        }
        
        reservasOrdenadas.forEach(System.out::println);
    }
    
    // Métodos de reportes
    private void generarReportes() {
        while (true) {
            System.out.println("\n=== REPORTES ===");
            System.out.println("1. Top 3 aulas con más horas reservadas");
            System.out.println("2. Ocupación por tipo de aula");
            System.out.println("3. Distribución por tipo de reserva");
            System.out.println("4. Exportar reportes a archivo");
            System.out.println("5. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            
            int opcion = leerEntero();
            
            switch (opcion) {
                case 1: generarTopAulas(); break;
                case 2: generarOcupacionPorTipo(); break;
                case 3: generarDistribucionPorTipo(); break;
                case 4: exportarReportes(); break;
                case 5: return;
                default: System.out.println("Opción inválida");
            }
        }
    }
    
    private void generarTopAulas() {
        System.out.println("\n--- TOP 3 AULAS CON MÁS HORAS RESERVADAS ---");
        
        Map<Aula, Long> horasPorAula = reservas.stream()
            .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
            .collect(Collectors.groupingBy(
                Reserva::getAula,
                Collectors.summingLong(Reserva::getDuracionHoras)
            ));
        
        horasPorAula.entrySet().stream()
            .sorted(Map.Entry.<Aula, Long>comparingByValue().reversed())
            .limit(3)
            .forEach(entry -> 
                System.out.println(entry.getKey().getCodigo() + " - " + 
                                 entry.getKey().getNombre() + ": " + 
                                 entry.getValue() + " horas")
            );
    }
    
    private void generarOcupacionPorTipo() {
        System.out.println("\n--- OCUPACIÓN POR TIPO DE AULA ---");
        
        Map<TipoAula, Long> ocupacionPorTipo = reservas.stream()
            .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
            .collect(Collectors.groupingBy(
                r -> r.getAula().getTipo(),
                Collectors.summingLong(Reserva::getDuracionHoras)
            ));
        
        ocupacionPorTipo.forEach((tipo, horas) -> 
            System.out.println(tipo + ": " + horas + " horas")
        );
    }
    
    private void generarDistribucionPorTipo() {
        System.out.println("\n--- DISTRIBUCIÓN POR TIPO DE RESERVA ---");
        
        Map<String, Long> distribucion = reservas.stream()
            .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
            .collect(Collectors.groupingBy(
                Reserva::getTipoReserva,
                Collectors.counting()
            ));
        
        long total = distribucion.values().stream().mapToLong(Long::longValue).sum();
        
        distribucion.forEach((tipo, cantidad) -> {
            double porcentaje = total > 0 ? (cantidad * 100.0 / total) : 0;
            System.out.printf("%s: %d reservas (%.1f%%)%n", tipo, cantidad, porcentaje);
        });
    }
    
    private void exportarReportes() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_REPORTES))) {
            pw.println("=== REPORTE DE RESERVAS ITCA ===");
            pw.println("Generado: " + LocalDate.now());
            pw.println();
            
            // Top 3 aulas
            pw.println("TOP 3 AULAS CON MÁS HORAS RESERVADAS:");
            Map<Aula, Long> horasPorAula = reservas.stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
                .collect(Collectors.groupingBy(
                    Reserva::getAula,
                    Collectors.summingLong(Reserva::getDuracionHoras)
                ));
            
            horasPorAula.entrySet().stream()
                .sorted(Map.Entry.<Aula, Long>comparingByValue().reversed())
                .limit(3)
                .forEach(entry -> 
                    pw.println("  " + entry.getKey().getCodigo() + " - " + 
                             entry.getKey().getNombre() + ": " + 
                             entry.getValue() + " horas")
                );
            
            pw.println();
            
            // Ocupación por tipo
            pw.println("OCUPACIÓN POR TIPO DE AULA:");
            Map<TipoAula, Long> ocupacionPorTipo = reservas.stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
                .collect(Collectors.groupingBy(
                    r -> r.getAula().getTipo(),
                    Collectors.summingLong(Reserva::getDuracionHoras)
                ));
            
            ocupacionPorTipo.forEach((tipo, horas) -> 
                pw.println("  " + tipo + ": " + horas + " horas")
            );
            
            pw.println();
            
            // Distribución por tipo de reserva
            pw.println("DISTRIBUCIÓN POR TIPO DE RESERVA:");
            Map<String, Long> distribucion = reservas.stream()
                .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
                .collect(Collectors.groupingBy(
                    Reserva::getTipoReserva,
                    Collectors.counting()
                ));
            
            long total = distribucion.values().stream().mapToLong(Long::longValue).sum();
            
            distribucion.forEach((tipo, cantidad) -> {
                double porcentaje = total > 0 ? (cantidad * 100.0 / total) : 0;
                pw.printf("  %s: %d reservas (%.1f%%)%n", tipo, cantidad, porcentaje);
            });
            
            System.out.println("Reporte exportado exitosamente a " + ARCHIVO_REPORTES);
            
        } catch (IOException e) {
            System.out.println("Error al exportar reporte: " + e.getMessage());
        }
    }
    
    // Métodos auxiliares
    private Aula buscarAulaPorCodigo(String codigo) {
        return aulas.stream()
            .filter(a -> a.getCodigo().equals(codigo))
            .findFirst()
            .orElse(null);
    }
    
    private Reserva buscarReservaPorId(int id) {
        return reservas.stream()
            .filter(r -> r.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    private boolean existeConflictoHorario(Aula aula, LocalDate fecha, 
                                         LocalTime horaInicio, LocalTime horaFin, 
                                         int idExcluir) {
        return reservas.stream()
            .filter(r -> r.getId() != idExcluir)
            .filter(r -> r.getEstado() == EstadoReserva.ACTIVA)
            .filter(r -> r.getAula().equals(aula))
            .filter(r -> r.getFecha().equals(fecha))
            .anyMatch(r -> {
                // Verificar superposición de horarios
                return (horaInicio.isBefore(r.getHoraFin()) && horaFin.isAfter(r.getHoraInicio()));
            });
    }
    
    private int leerEntero() {
        while (true) {
            try {
                int numero = scanner.nextInt();
                scanner.nextLine(); // CONSUMIR EL SALTO DE LÍNEA
                return numero;
            } catch (InputMismatchException e) {
                System.out.print("Por favor, ingrese un número válido: ");
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }
    
    private LocalDate leerFecha() {
        while (true) {
            try {
                String fechaStr = scanner.next();
                return LocalDate.parse(fechaStr);
            } catch (DateTimeParseException e) {
                System.out.print("Formato de fecha inválido (use yyyy-mm-dd): ");
            }
        }
    }
    
    private LocalTime leerHora() {
        while (true) {
            try {
                String horaStr = scanner.next();
                return LocalTime.parse(horaStr);
            } catch (DateTimeParseException e) {
                System.out.print("Formato de hora inválido (use HH:mm): ");
            }
        }
    }
    
    // Método principal
    public void ejecutar() {
        System.out.println("=== GESTOR DE RESERVAS DE AULAS ITCA ===");
        
        while (true) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Gestión de Aulas");
            System.out.println("2. Gestión de Reservas");
            System.out.println("3. Reportes");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            
            int opcion = leerEntero();
            
            switch (opcion) {
                case 1: gestionarAulas(); break;
                case 2: gestionarReservas(); break;
                case 3: generarReportes(); break;
                case 4: 
                    guardarDatos();
                    System.out.println("Hasta luego");
                    return;
                default: 
                    System.out.println("Opción inválida");
            }
        }
    }
    
    public static void main(String[] args) {
        GestorReservasITCA gestor = new GestorReservasITCA();
        gestor.ejecutar();
    }
}