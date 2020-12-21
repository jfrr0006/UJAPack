package DTO;

public class RespuestaDTO {


    /* Mensaje */
    private String mensaje;
    /* Valores */
    private Double valor;

    public RespuestaDTO(String _mensaje) {
        this.mensaje = _mensaje;
    }


    public RespuestaDTO(Double _valor) {
        this.valor = _valor;
    }

    public RespuestaDTO() {
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
