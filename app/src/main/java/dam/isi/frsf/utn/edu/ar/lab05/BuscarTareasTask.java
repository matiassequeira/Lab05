package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

/**
 * Created by Matias on 11/11/2016.
 */
public class BuscarTareasTask extends AsyncTask<FormBusqueda,Integer,List<Tarea>> {

    private BusquedaFinalizadaListener<Tarea> listener;

    public BuscarTareasTask(BusquedaFinalizadaListener<Tarea> dListener){
        this.listener = dListener;
    }

    @Override
    protected List<Tarea> doInBackground(FormBusqueda... params) {
        ProyectoDAO dao = new ProyectoDAO((Context) listener);

        return dao.listarDesviosPlanificacion(params[0].isTareaTerminada(), params[0].getMaxDesvio());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<Tarea> tareas) {
        listener.busquedaFinalizada(tareas);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {



    }
}
