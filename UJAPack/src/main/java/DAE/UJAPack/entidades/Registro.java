/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAE.UJAPack.entidades;

import java.time.LocalDateTime;



public class Registro {
    
    /** Fecha y Hora del registro*/
    private LocalDateTime fecha;
    /** Entrada del registro*/
    private boolean entrada;
    
    public Registro(LocalDateTime fecha, boolean entrada) {
        
        this.fecha = fecha;
        this.entrada = entrada;
        
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public boolean getEntrada() {
        return entrada;
    }
    
}
