/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAE.UJAPack.entidades;

import java.util.ArrayList;
import java.util.List;



public class Envio {
    
    /** Identificador del envio*/
    private int identificador;
    /** Importe del envio*/
    private float importe;
    /** Lista de registros de ruta*/
    /**ArrayList reg = new ArrayList();*/
    List<Registro> registros;
    /** Peso del envio*/
    private float peso;
    /** Remitente del envio*/
    private String remitente;
    /** Destinatario del envio*/
    private String destinatario;
    /** Dimensiones del envio*/
    private float dimensiones;
    /** Enumerador del registro de la ruta*/
    enum registro_ruta {
        
    }
    /** Enumerador del estado*/
    enum estado {
        
    }
    
    public Envio(int identificador, float importe, float peso, String remitente, String destinatario, float dimensiones) {
        this.identificador = identificador;
        this.importe = importe;
        this.peso = peso;
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.dimensiones = dimensiones;
        
        registros = new ArrayList<>();
        
    }
    
    public int getIdentificador() {
        return identificador;
    }
    
    public float getImporte() {
        return importe;
    }
    
    public float getPeso() {
        return peso;
    }
    
    public String getRemitente() {
        return remitente;
    }
    
    public String getDestinatario() {
        return destinatario;
    }
    
    public float getDimensiones() {
        return dimensiones;
    }
    
}
