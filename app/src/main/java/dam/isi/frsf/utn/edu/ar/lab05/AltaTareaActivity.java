package dam.isi.frsf.utn.edu.ar.lab05;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDBMetadata;

public class AltaTareaActivity extends AppCompatActivity implements View.OnClickListener {

    EditText descripcion;
    EditText horasEstimadas;
    SeekBar prioridad;
    Spinner responsable;
    Button btnGuardar;
    Button btnCancelar;

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
                new String[]{ProyectoDBMetadata.TablaUsuariosMetadata.USUARIO}, new int[] {R.id.spinner});
        
        btnGuardar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCancelar:
                break;
            case R.id.btnGuardar:
                break;
        }
    }
}
