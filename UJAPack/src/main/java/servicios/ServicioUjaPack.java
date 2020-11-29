package servicios;

import entidades.Envio;
import entidades.PuntoRuta.PuntoRuta;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

public interface ServicioUjaPack {

    /**
     * Avanza todos los envios que no esten ya en estado de Entregado
     */
    void avanzarEnvios();

    /**
     * Genera un nuevo envio
     *
     * @param remitente           Nombre del lugar de Inicio del envio
     * @param destinatario        Nombre del lugar de Finalizacion del envio
     * @param _datos_remitente    Datos de la persona que realiza el envio
     * @param _datos_destinatario Datos de la persona que recibe el envio
     * @param peso                Peso del paquete
     * @param dimensiones         Dimensiones del paquete
     * @return Nuevo envio creado
     */
     Envio generarEnvio(@NotBlank String remitente, @NotBlank String destinatario, @Positive Float peso, @Positive Float dimensiones, @NotBlank String _datos_remitente, @NotBlank String _datos_destinatario);


    /**
     * Funcion funciona solo a las 00:00:00, inspecciona a los pedidos en transito
     * y si han pasado mas de 7 dias modifica su estado a Extraviado y los añade a otro mapa
     */
     void actualizarEnviosExtraviados(LocalDateTime ahora);

    /**
     * Busca los envios extraviados en un intervalo de tiempo
     *
     * @param desde Fecha desde donde se quiere buscar
     * @param hasta Fecha hasta donde se quiere buscar
     * @return Lista de envios extraviados dentro del intervalo de tiempo
     */
     List<Envio> consultarEnviosExtraviados(LocalDateTime desde, LocalDateTime hasta);

    /**
     * @return Lista de todos los envios extraviados
     */
     List<Envio> consultarEnviosExtraviados();

    /**
     * Calcula el porcentaje de envios extraviados en el ultimo periodo de tiempo seleccionado
     *
     * @param ultimo Opcion seleccionada por el usuario dia/mes/año
     * @return Porcentaje de envios extraviados
     */
     double porcentajeEnviosExtraviados(String ultimo);

    /**
     * Activa la notificacion en un envio
     *
     * @param idenvio ID del envio
     * @param noti    Punto donde se quiere tener una notificacion de su llegada/salida
     */
     void activarNotificacion(long idenvio, String noti);

    /**
     * Devuelve la situacion actual del envio
     *
     * @param idenvio ID del envio
     * @return Cadena de texto con la informacion
     */
     String situacionActualEnvio(long idenvio);


    /**
     * Devuelve toda la informacion sobre la ruta de un pedido(hasta el momento o ya finalizado)
     *
     * @param idenvio ID del envio
     * @return Cadena de texto con la informacion
     */
     List<String> listadoRutaEnvio(long idenvio);

    /**
     * Dado dos Strings, uno del punto de incio del envio
     * y otro del lugar al que se desea mandar devuelve la ruta minima de Puntos de Ruta
     *
     * @param remitente    Nombre del punto de ruta de Inicio
     * @param destinatario Nombre del punto de ruta de Finalizacion
     * @return Lista de Puntos de Ruta, el camino minimo
     */
     List<PuntoRuta> listaRutaMinima(@NotBlank String remitente, @NotBlank String destinatario);

    /**
     * Devuelve un envio
     *
     * @param id del envio
     */
     Envio verEnvio(long id);
}
