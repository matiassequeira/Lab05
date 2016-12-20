package dam.isi.frsf.utn.edu.ar.lab05;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.lab05.dao.ProyectoDAO;
import dam.isi.frsf.utn.edu.ar.lab05.modelo.Proyecto;

public class GestionarProyectoActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnNuevo;
    Button btnModificar;
    Button btnEliminar;
    ListView lvProyectos;
    List<String> listaProyectos;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_proyecto);

        btnEliminar = (Button) findViewById(R.id.buttonEliminarProyecto);
        btnEliminar.setOnClickListener(this);
        btnModificar=(Button) findViewById(R.id.buttonModificarProyecto);
        btnModificar.setOnClickListener(this);
        btnNuevo=(Button) findViewById(R.id.buttonNuevoProyecto);
        btnNuevo.setOnClickListener(this);

        lvProyectos = (ListView) findViewById(R.id.listViewProyectos);

        listaProyectos = new ArrayList<String>();
        traerProyectos();

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,listaProyectos);
        lvProyectos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvProyectos.setAdapter(adapter);
    }

    private void traerProyectos() {

        Thread backGroundUpdate = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://10.0.2.2:4000/proyectos/");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    InputStreamReader isw = new InputStreamReader(in);
                    StringBuilder sb = new StringBuilder();
                    int data = isw.read();
                    while (data != -1) {
                        char current = (char) data;
                        sb.append(current);
                        data = isw.read();
                    }
                    Log.d("TEST-ARR", sb.toString());

                    JSONArray JSONproyectos = new JSONArray(sb.toString());
                    Log.d("JSONobject", JSONproyectos.toString());

                    for(int i=0; i<JSONproyectos.length(); i++) {
                        String proyecto = JSONproyectos.getJSONObject(i).getString("nombre");
                        listaProyectos.add(proyecto);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Throwable t){
                    Log.e("JSONobject", "Could not parse malformed JSON");
                }finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
        });

        backGroundUpdate.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.buttonEliminarProyecto:
                final String proyectoSelec = (String)lvProyectos.getAdapter().getItem(lvProyectos.getCheckedItemPosition());
                if(proyectoSelec==null){
                    Toast toast = Toast.makeText(this, "Debe seleccionar un proyecto a eliminar", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{//eliminar proyecto de la BD y del Servidor
                    android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this);

                    alertDialog.setTitle("Eliminar " + proyectoSelec);
                    alertDialog.setMessage("Estas seguro que deseas eliminar el proyecto?");
                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {
                                    //eliminar proyecto de la BD y del Servidor
                                    eliminarProyecto(proyectoSelec);
                                    listaProyectos.remove(proyectoSelec);
                                    adapter.notifyDataSetChanged();
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
                break;

            case R.id.buttonModificarProyecto:
                break;

            case R.id.buttonNuevoProyecto:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Nuevo Proyecto");
                alertDialog.setMessage("Introduzca el nombre de su proyecto:");

                final EditText input = new EditText(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("Crear",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                crearProyecto(input.getText().toString());
                                listaProyectos.add(input.getText().toString());
                                adapter.notifyDataSetChanged();
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

                alertDialog.show();
                break;
        }
    }

    private void crearProyecto(final String nombre){
        Thread backGroundUpdate = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection=null;
                try {
                    Proyecto proyecto = new Proyecto();
                    proyecto.setNombre(nombre);
                    ProyectoDAO dao = new ProyectoDAO(GestionarProyectoActivity.this);
                    dao.guardarProyecto(proyecto);
                    proyecto.setId(dao.getIDProyecto(nombre));

                    JSONObject proyectoJSON = new JSONObject();

                    proyectoJSON.put("id", proyecto.getId());
                    proyectoJSON.put("nombre", proyecto.getNombre());

                    byte[] data = proyectoJSON.toString().getBytes("UTF-8");

                    URL url= new URL("http://10.0.2.2:4000/proyectos/");
                    urlConnection= (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setFixedLengthStreamingMode(data.length);
                    urlConnection.setRequestProperty("Content-Type","application/json");

                    DataOutputStream printout= new DataOutputStream(urlConnection.getOutputStream());

                    printout.write(data);
                    printout.flush();
                    printout.close();

                }catch (JSONException e2) {
                    e2.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }finally{
                    if(urlConnection!= null) urlConnection.disconnect();
                }
            }
        });

        backGroundUpdate.start();
    }

    private void eliminarProyecto(final String nomProyecto){

        Thread backGroundUpdate = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection=null;
                try {
                    ProyectoDAO dao = new ProyectoDAO(GestionarProyectoActivity.this);
                    dao.borrarProyecto(nomProyecto);

                    int id = dao.getIDProyecto(nomProyecto);
                    URL url= new URL("http://10.0.2.2:4000/proyectos/"+id);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty(
                            "Content-Type", "application/x-www-form-urlencoded" );
                    urlConnection.setRequestMethod("DELETE");
                    urlConnection.connect();
                    int responseCode = urlConnection.getResponseCode();

                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally{
                    if(urlConnection!= null) urlConnection.disconnect();
                }
            }
        });

        backGroundUpdate.start();
    }
}

