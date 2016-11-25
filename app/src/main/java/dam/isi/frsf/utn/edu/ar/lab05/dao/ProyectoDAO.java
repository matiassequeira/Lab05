package dam.isi.frsf.utn.edu.ar.lab05.dao;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;
/**
 * Created by mdominguez on 06/10/16.
 */
public class ProyectoDAO {
    private static final String _SQL_TAREAS_X_PROYECTO = "SELECT "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata._ID+" as "+ProyectoDBMetadata.TablaTareasMetadata._ID+
            ", "+ProyectoDBMetadata.TablaTareasMetadata.TAREA +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA +
            ", "+ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD +
            ", "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+"."+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD +" as "+ProyectoDBMetadata.TablaPrioridadMetadata.PRIORIDAD_ALIAS+
            ", "+ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE +
            ", "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+"."+ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO +" as "+ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO_ALIAS+
            " FROM "+ProyectoDBMetadata.TABLA_PROYECTO + " "+ProyectoDBMetadata.TABLA_PROYECTO_ALIAS+", "+
            ProyectoDBMetadata.TABLA_USUARIOS + " "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+", "+
            ProyectoDBMetadata.TABLA_PRIORIDAD + " "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+", "+
            ProyectoDBMetadata.TABLA_TAREAS + " "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+
            " WHERE "+ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PROYECTO+" = "+ProyectoDBMetadata.TABLA_PROYECTO_ALIAS+"."+ProyectoDBMetadata.TablaProyectoMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE+" = "+ProyectoDBMetadata.TABLA_USUARIOS_ALIAS+"."+ProyectoDBMetadata.TablaUsuariosMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD+" = "+ProyectoDBMetadata.TABLA_PRIORIDAD_ALIAS+"."+ProyectoDBMetadata.TablaPrioridadMetadata._ID +" AND "+
            ProyectoDBMetadata.TABLA_TAREAS_ALIAS+"."+ProyectoDBMetadata.TablaTareasMetadata.PROYECTO+" = ?";
    private ProyectoOpenHelper dbHelper;
    private SQLiteDatabase db;
    public ProyectoDAO(Context c){
        this.dbHelper = new ProyectoOpenHelper(c);
    }
    public void open(){
        this.open(false);
    }
    public void open(Boolean toWrite){
        if(toWrite) {
            db = dbHelper.getWritableDatabase();
        }
        else{
            db = dbHelper.getReadableDatabase();
        }
    }
    public void close(){
        db = dbHelper.getReadableDatabase();
    }

    public Proyecto buscarProyecto(){
        db = dbHelper.getReadableDatabase();
        Proyecto proyecto = new Proyecto();

        Cursor cursor = db.rawQuery("SELECT * FROM "+ProyectoDBMetadata.TABLA_PROYECTO,null);
        cursor.moveToFirst();
        proyecto.setId(cursor.getInt(0));
        proyecto.setNombre(cursor.getString(1));

        return proyecto;
    }

    public Usuario buscarUsuario(int id){
        db = dbHelper.getReadableDatabase();
        Usuario usr = new Usuario();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ProyectoDBMetadata.TABLA_USUARIOS+" WHERE "+ProyectoDBMetadata.TablaUsuariosMetadata._ID+"= "+id,null);
        if (cursor.moveToFirst()) {
            usr.setId(cursor.getInt(0));
            usr.setNombre(cursor.getString(1));
            usr.setCorreoElectronico(cursor.getString(2));
            return usr;
        }
        else return null;
    }

    public Cursor listaTareas(Integer idProyecto){
        db = dbHelper.getReadableDatabase();
        Cursor cursorPry = db.rawQuery("SELECT "+ProyectoDBMetadata.TablaProyectoMetadata._ID+ " FROM "+ProyectoDBMetadata.TABLA_PROYECTO,null);
        Integer idPry= 0;
        if(cursorPry.moveToFirst()){
            idPry=cursorPry.getInt(0);
        }
        cursorPry.close();
        Cursor cursor = null;
        Log.d("LAB05-MAIN","PROYECTO : _"+idPry.toString()+" - "+ _SQL_TAREAS_X_PROYECTO);
        cursor = db.rawQuery(_SQL_TAREAS_X_PROYECTO,new String[]{idPry.toString()});
        return cursor;
    }
    public Cursor listaUsuarios(){
        db= dbHelper.getReadableDatabase();
        Cursor cursorUsuarios = db.rawQuery("SELECT rowid _id, NOMBRE FROM USUARIOS", null);
        return cursorUsuarios;
    }

    public void guardarUsuario(Usuario u){
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO, u.getNombre());
        cv.put(ProyectoDBMetadata.TablaUsuariosMetadata.MAIL,u.getCorreoElectronico());
        db.insert(ProyectoDBMetadata.TABLA_USUARIOS, ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO,cv);

    }
    public void nuevaTarea(Tarea t){
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA,t.getDescripcion());
        cv.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,t.getHorasEstimadas());
        cv.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS, t.getMinutosTrabajados());
        cv.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD, t.getPrioridad().getId());
        cv.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE, t.getResponsable().getId());
        cv.put(ProyectoDBMetadata.TablaTareasMetadata.PROYECTO, t.getProyecto().getId());

        db.insert(ProyectoDBMetadata.TABLA_TAREAS,ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS,cv);
    }

    public void actualizarTarea(Tarea t){
        db = dbHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ProyectoDBMetadata.TablaTareasMetadata.TAREA,t.getDescripcion());
        cv.put(ProyectoDBMetadata.TablaTareasMetadata.HORAS_PLANIFICADAS,t.getHorasEstimadas());
        cv.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS, t.getMinutosTrabajados());
        cv.put(ProyectoDBMetadata.TablaTareasMetadata.PRIORIDAD, t.getPrioridad().getId());
        cv.put(ProyectoDBMetadata.TablaTareasMetadata.RESPONSABLE, t.getResponsable().getId());
        cv.put(ProyectoDBMetadata.TablaTareasMetadata.PROYECTO, t.getProyecto().getId());

        db.update(ProyectoDBMetadata.TABLA_TAREAS, cv, "_ID = "+t.getId(),null);
    }

    public void borrarTarea(int idTarea){
        db = dbHelper.getWritableDatabase();
        db.delete(ProyectoDBMetadata.TABLA_TAREAS,"_ID = "+idTarea,null);
    }
    public List<Prioridad> listarPrioridades(){
        return null;
    }
    public List<Usuario> listarUsuarios(){
        return null;
    }
    public void finalizar(Integer idTarea){
        //Establecemos los campos-valores a actualizar
        ContentValues valores = new ContentValues();
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.FINALIZADA,1);
        SQLiteDatabase mydb =dbHelper.getWritableDatabase();
        mydb.update(ProyectoDBMetadata.TABLA_TAREAS, valores, "_id=?", new String[]{idTarea.toString()});
    }
    public List<Tarea> listarDesviosPlanificacion(Boolean soloTerminadas,Integer desvioMaximoMinutos){
        // retorna una lista de todas las tareas que tardaron más (en exceso) o menos (por defecto)
        // que el tiempo planificado.
        // si la bandera soloTerminadas es true, se busca en las tareas terminadas, sino en todas.
        List<Tarea> resultado = new ArrayList<>();
        List<Tarea> resultadoIntermedio = new ArrayList<>();

        Cursor cursor = this.listaTareas(1);

        cursor.moveToFirst();

        do {
            boolean finalizada;
            if(cursor.getInt(7)==1){
                finalizada=true;
            }
            else finalizada=false;

            if(soloTerminadas && finalizada){
                if (desvioMaximoMinutos==null && (cursor.getInt(2)*60 > cursor.getInt(3))){
                    Proyecto proyecto = this.buscarProyecto();
                    Usuario usuario = this.buscarUsuario(cursor.getInt(5));
                    Prioridad prioridad = new Prioridad(cursor.getInt(4), String.valueOf(cursor.getInt(4)));
                    Tarea tarea = new Tarea(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), finalizada , proyecto, prioridad, usuario );
                    resultado.add(tarea);
                }
                else if (desvioMaximoMinutos!= null && desvioMaximoMinutos > (cursor.getInt(3) - cursor.getInt(2)*60)){
                    Proyecto proyecto = this.buscarProyecto();
                    Usuario usuario = this.buscarUsuario(cursor.getInt(5));
                    Prioridad prioridad = new Prioridad(cursor.getInt(4), String.valueOf(cursor.getInt(4)));
                    Tarea tarea = new Tarea(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), finalizada , proyecto, prioridad, usuario );
                    resultado.add(tarea);
                }
            }
            else if (!soloTerminadas){
                if (desvioMaximoMinutos==null && (cursor.getInt(2)*60 > cursor.getInt(3))){
                    Proyecto proyecto = this.buscarProyecto();
                    Usuario usuario = this.buscarUsuario(cursor.getInt(5));
                    Prioridad prioridad = new Prioridad(cursor.getInt(4), String.valueOf(cursor.getInt(4)));
                    Tarea tarea = new Tarea(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), finalizada , proyecto, prioridad, usuario );
                    resultado.add(tarea);
                }
                else if (desvioMaximoMinutos!= null && desvioMaximoMinutos > (cursor.getInt(3) - cursor.getInt(2)*60)){
                    Proyecto proyecto = this.buscarProyecto();
                    Usuario usuario = this.buscarUsuario(cursor.getInt(5));
                    Prioridad prioridad = new Prioridad(cursor.getInt(4), String.valueOf(cursor.getInt(4)));
                    Tarea tarea = new Tarea(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), finalizada , proyecto, prioridad, usuario );
                    resultado.add(tarea);
                }
            }
        }while (cursor.moveToNext());

        return resultado;
    }
    public void actualizarTiempoTrabajo(Integer idTarea, Double tiempoTrabajado) {

        ContentValues valores = new ContentValues();
        valores.put(ProyectoDBMetadata.TablaTareasMetadata.MINUTOS_TRABAJADOS, tiempoTrabajado.intValue());
        db =dbHelper.getWritableDatabase();
        db.update(ProyectoDBMetadata.TABLA_TAREAS, valores, "_ID="+idTarea, null);
    }

    public int getIDUsuario(String nombre) {
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT rowid _id FROM "+ProyectoDBMetadata.TABLA_USUARIOS +" WHERE " +
                ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO +" = ?", new String[]{nombre});
        cursor.moveToFirst();

        return cursor.getInt(0);
    }
}