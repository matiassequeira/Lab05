package dam.isi.frsf.utn.edu.ar.lab05;

import java.io.Serializable;

/**
 * Created by Matias on 11/11/2016.
 */
public class FormBusqueda implements Serializable {
    int maxDesvio;
    boolean tareaTerminada;

    public FormBusqueda (int desvio, boolean terminada){
        this.maxDesvio=desvio;
        this.tareaTerminada=terminada;
    }

    public int getMaxDesvio() {
        return maxDesvio;
    }

    public void setMaxDesvio(int maxDesvio) {
        this.maxDesvio = maxDesvio;
    }

    public boolean isTareaTerminada() {
        return tareaTerminada;
    }

    public void setTareaTerminada(boolean tareaTerminada) {
        this.tareaTerminada = tareaTerminada;
    }
}
