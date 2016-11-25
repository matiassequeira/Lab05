package dam.isi.frsf.utn.edu.ar.lab05;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GestionarProyectoActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnNuevo;
    Button btnModificar;
    Button btnEliminar;
    ListView lvProyectos;
    List<String> listaProyectos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_proyecto);

        traerProyectos();

        btnEliminar = (Button) findViewById(R.id.buttonEliminarProyecto);
        btnModificar=(Button) findViewById(R.id.buttonModificarProyecto);
        btnNuevo=(Button) findViewById(R.id.buttonNuevoProyecto);
        lvProyectos = (ListView) findViewById(R.id.listViewProyectos);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_single_choice,listaProyectos);
        lvProyectos.setAdapter(adapter);

        btnEliminar.setOnClickListener(this);
        btnModificar.setOnClickListener(this);
        btnNuevo.setOnClickListener(this);
    }

    private void traerProyectos() {
        listaProyectos=new ArrayList<>();
        HttpURLConnection urlConnection=null;
        try {
            URL url= new URL("http://10.0.2.2:4000/proyectos/");
            urlConnection= (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            InputStreamReader isw= new InputStreamReader(in);
            StringBuilder sb= new StringBuilder();
            int data = isw.read();
            while(data != -1) {
                char current= (char) data;
                sb.append(current);
                data = isw.read();
            }
            Log.d("TEST-ARR",sb.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if(urlConnection!=null)
                urlConnection.disconnect();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonEliminarProyecto:
                break;
            case R.id.buttonModificarProyecto:
                break;
            case R.id.buttonNuevoProyecto:
                break;
        }
    }


}

