package dam.isi.frsf.utn.edu.ar.lab05;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.os.Looper;
import android.os.Message;

import java.util.Calendar;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;

/**
 * Created by mdominguez on 06/10/16.
 */
public class TareaCursorAdapter extends CursorAdapter {
    private LayoutInflater inflador;
    private ProyectoDAO myDao;
    private Context contexto;
    Calendar fin;
    Calendar inicio;

    public TareaCursorAdapter (Context contexto, Cursor c, ProyectoDAO dao) {
        super(contexto, c, false);
        myDao= dao;
        this.contexto = contexto;
    }

    @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        inflador = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vista = inflador.inflate(R.layout.fila_tarea,viewGroup,false);
        return vista;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        //obtener la posicion de la fila actual y asignarla a los botones y checkboxes
        int pos = cursor.getPosition();

        // Referencias UI.
        TextView nombre= (TextView) view.findViewById(R.id.tareaTitulo);
        TextView tiempoAsignado= (TextView) view.findViewById(R.id.tareaMinutosAsignados);
        TextView tiempoTrabajado= (TextView) view.findViewById(R.id.tareaMinutosTrabajados);
        TextView prioridad= (TextView) view.findViewById(R.id.tareaPrioridad);
        TextView responsable= (TextView) view.findViewById(R.id.tareaResponsable);
        CheckBox finalizada = (CheckBox)  view.findViewById(R.id.tareaFinalizada);

        final Button btnFinalizar = (Button)   view.findViewById(R.id.tareaBtnFinalizada);
        final Button btnEditar = (Button)   view.findViewById(R.id.tareaBtnEditarDatos);
        final ToggleButton btnEstado = (ToggleButton) view.findViewById(R.id.tareaBtnTrabajando);
        final Button btnEliminar = (Button) view.findViewById(R.id.buttonEliminar);

        nombre.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.TAREA)));
        Integer horasAsigandas = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS));
        tiempoAsignado.setText(horasAsigandas*60 + " minutos");

        Integer minutosAsigandos = cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS));
        tiempoTrabajado.setText(minutosAsigandos+ " minutos");

        String p = cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS));
        prioridad.setText(p);
        responsable.setText(cursor.getString(cursor.getColumnIndex(ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO_ALIAS)));
        finalizada.setChecked(cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA))==1);
        finalizada.setTextIsSelectable(false);

        btnEditar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer idTarea= (Integer) view.getTag();
                Intent intEditarAct = new Intent(contexto,AltaTareaActivity.class);
                intEditarAct.putExtra("UPDATE",true);
                intEditarAct.putExtra("ID_TAREA", idTarea);
                context.startActivity(intEditarAct);

            }
        });

        btnFinalizar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer idTarea= (Integer) view.getTag();
                Thread backGroundUpdate = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("LAB05-MAIN","finalizar tarea : --- "+idTarea);
                        myDao.finalizar(idTarea);
                        handlerRefresh.sendEmptyMessage(1);
                    }
                });
                backGroundUpdate.start();
            }
        });

        btnEliminar.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(contexto);
                // Setting Dialog Title
                alertDialog.setTitle("Eliminar Tarea");
                // Setting Dialog Message
                alertDialog.setMessage("Estas seguro que deseas eliminar la tarea?");
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {

                                final Integer idTarea= (Integer) view.getTag();
                                myDao.borrarTarea(idTarea);
                                handlerRefresh.sendEmptyMessage(1);
                            }
                        });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                dialog.cancel();
                            }
                        });
                // Showing Alert Message
                alertDialog.show();
            }
        });

        ObjetoComplejo oc = new ObjetoComplejo(cursor.getInt(cursor.getColumnIndex("_id")), cursor.getInt(cursor.getColumnIndex(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS)));

        btnEstado.setTag(oc);
        btnEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(btnEstado.isChecked()){
                    inicio=Calendar.getInstance();
                }
                else{
                    fin=Calendar.getInstance();
                    long tiempoTotalEnMS = fin.getTimeInMillis()-inicio.getTimeInMillis();
                    double minutos = (float) (tiempoTotalEnMS/5000.0);
                    final ObjetoComplejo objetocomplejo = (ObjetoComplejo)view.getTag();
                    objetocomplejo.tiempoTrabajado+=minutos;


                    Thread backGroundUpdate = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("LAB05-MAIN","Actualizar tiempo trabajado : --- "+objetocomplejo.id+"   Tiempo trabajado:"+objetocomplejo.tiempoTrabajado);
                            myDao.actualizarTiempoTrabajo(objetocomplejo.id, objetocomplejo.tiempoTrabajado);
                            handlerRefresh.sendEmptyMessage(1);
                        }
                    });

                    backGroundUpdate.start();

                }
            }
        });


        }

    Handler handlerRefresh = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            TareaCursorAdapter.this.changeCursor(myDao.listaTareas(1));
        }
    };


    public class ObjetoComplejo{
        Integer id;
        double tiempoTrabajado;


        ObjetoComplejo(Integer entero, double tiempoTrabajad){
            id=entero;
            tiempoTrabajado=tiempoTrabajad;
        }

    }


}

