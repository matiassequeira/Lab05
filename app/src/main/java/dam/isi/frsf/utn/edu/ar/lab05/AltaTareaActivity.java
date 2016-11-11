package dam.isi.frsf.utn.edu.ar.lab05;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Prioridad;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Usuario;

public class AltaTareaActivity extends AppCompatActivity implements View.OnClickListener {
    EditText descripcion;
    EditText horasEstimadas;
    SeekBar prioridad;
    Spinner responsable;
    Button btnGuardar;
    Button btnCancelar;
    ProyectoDAO proyectoDAO;
    boolean actualizacion;
    int id_tarea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_tarea);
        descripcion = (EditText) findViewById(R.id.editText);
        horasEstimadas = (EditText) findViewById(R.id.editText2);
        prioridad= (SeekBar) findViewById(R.id.seekBar);
        responsable=(Spinner) findViewById(R.id.spinner);
        btnGuardar=(Button) findViewById(R.id.btnGuardar);
        btnCancelar=(Button) findViewById(R.id.btnCancelar);

        Cursor cursorSpinner = new ProyectoDAO(this).listaUsuarios();
        SpinnerAdapter adapter=new SimpleCursorAdapter(this,android.R.layout.simple_spinner_dropdown_item, cursorSpinner,
                new String[]{"NOMBRE"}, new int[] {android.R.id.text1},0);

        responsable.setAdapter(adapter);

        btnGuardar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);


        Intent intent= this.getIntent();
        actualizacion = intent.getBooleanExtra("UPDATE", false);
        id_tarea = intent.getIntExtra("ID_TAREA", -1);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCancelar:
                break;
            case R.id.btnGuardar:
                guardarTarea();
                break;
        }
    }

    private void guardarTarea() {
        //no hay nada validado
        String descrip=descripcion.getText().toString();
        Integer horasEst = Integer.parseInt(horasEstimadas.getText().toString());
        Prioridad prio = new Prioridad();
        Integer p=prioridad.getProgress();
        prio.setId(p);
        prio.setPrioridad(p.toString());

        proyectoDAO = new ProyectoDAO(this);
        //traer el proyecto y setearlo
        Proyecto proyecto = proyectoDAO.buscarProyecto();

        //traer usuario
        Cursor cursorUsuario = (Cursor) responsable.getSelectedItem();
        Usuario responsable = proyectoDAO.buscarUsuario(cursorUsuario.getInt(0));

        Tarea tarea = new Tarea(id_tarea, descrip, horasEst, 0, false, proyecto, prio, responsable);

        if(actualizacion){
            proyectoDAO.actualizarTarea(tarea);
        }else  proyectoDAO.nuevaTarea(tarea);

        finish();
    }


}