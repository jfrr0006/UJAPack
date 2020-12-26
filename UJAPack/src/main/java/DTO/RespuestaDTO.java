package DTO;

public class RespuestaDTO {
    //Su proposito es solo Auxiliar para que el RestTemplate de los Test no tenga problema al hacer la conversion a JSON


    /* Mensaje */
    private String mensaje;

    public RespuestaDTO(String _mensaje) {
        this.mensaje = _mensaje;
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
