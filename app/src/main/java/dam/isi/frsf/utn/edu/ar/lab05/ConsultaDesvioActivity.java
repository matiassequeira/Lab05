package dam.isi.frsf.utn.edu.ar.lab05;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.modelo.Tarea;

public class ConsultaDesvioActivity extends AppCompatActivity implements View.OnClickListener, BusquedaFinalizadaListener<Tarea>{

    EditText minutosDesviados;
    CheckBox tareasTerminadas;
    Button btnconsultar;
    TextView tareasDesviadas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_desvio);

        minutosDesviados = (EditText) findViewById(R.id.editTxtMinutosDesviados);
        tareasTerminadas = (CheckBox) findViewById(R.id.checkBoxTareasTerminadas);
        btnconsultar = (Button) findViewById(R.id.btnConsultar);
        btnconsultar.setOnClickListener(this);
        tareasDesviadas = (TextView) findViewById(R.id.textViewTareasDesviadas);
    }

    @Override
    public void onClick(View v) {
        FormBusqueda fb = new FormBusqueda(Integer.parseInt(minutosDesviados.getText().toString()), tareasTerminadas.isChecked() );
        new BuscarTareasTask(ConsultaDesvioActivity.this).execute(fb);
    }

    @Override
    public void busquedaFinalizada(List<Tarea> tareasDesviadas) {
        String
        for(Tarea t: tareasDesviadas){
            String
        }

    }

    @Override
    public void busquedaActualizada(String mensaje) {

    }
}
