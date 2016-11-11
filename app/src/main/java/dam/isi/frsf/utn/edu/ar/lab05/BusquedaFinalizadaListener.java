package dam.isi.frsf.utn.edu.ar.lab05;

import java.util.List;

/**
 * Created by Matias on 11/11/2016.
 */
public interface BusquedaFinalizadaListener<T> {
    public void busquedaFinalizada(List<T> lRes);
    public void busquedaActualizada(String mensaje);
}
