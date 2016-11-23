package dam.isi.frsf.utn.edu.ar.lab05;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
    Button btnAgregarUsuario;
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
        btnAgregarUsuario = (Button) findViewById(R.id.btnAgregarUsuario);

        Cursor cursorSpinner = new ProyectoDAO(this).listaUsuarios();
        SpinnerAdapter adapter=new SimpleCursorAdapter(this,android.R.layout.simple_spinner_dropdown_item, cursorSpinner,
                new String[]{"NOMBRE"}, new int[] {android.R.id.text1},0);

        responsable.setAdapter(adapter);

        btnGuardar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        btnAgregarUsuario.setOnClickListener(this);


        Intent intent= this.getIntent();
        actualizacion = intent.getBooleanExtra("UPDATE", false);
        id_tarea = intent.getIntExtra("ID_TAREA", -1);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCancelar:
                finish();
                break;
            case R.id.btnGuardar:
                guardarTarea();
                break;
            case R.id.btnAgregarUsuario:
                abrirBusqueda();
                break;
        }
    }

    private void abrirBusqueda() {
        // Creating alert Dialog with one Button
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Buscar usuario");

        // Setting Dialog Message
        alertDialog.setMessage("Introduzca usuario");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Buscar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                       buscarContacto(input.getText().toString());
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

        // closed

        // Showing Alert Message
        alertDialog.show();
    }

    private void buscarContacto(String nombreBuscado) {
        JSONArray arr = new JSONArray();
        final StringBuilder resultado = new StringBuilder();
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        // consulta ejemplo buscando por nombre visualizado en los contactos agregados
        Cursor c =this.getContentResolver().query(uri, null, ContactsContract.Contacts.DISPLAY_NAME+
                " LIKE '"+nombreBuscado+"%'", null, sortOrder);

        int count = c.getColumnCount();
        int fila = 0;
        String[] columnas= new String[count];
        try {
            while(c.moveToNext()) {
                JSONObject unContacto = new JSONObject();
                for(int i = 0; (i < count );  i++) {
                    if(fila== 0)
                        columnas[i]=c.getColumnName(i);
                    unContacto.put(columnas[i],c.getString(i));
                }
                Log.d("TEST-ARR",unContacto.toString());
                arr.put(fila,unContacto);
                fila++;
                Log.d("TEST-ARR","fila : "+fila);

                // elegir columnas de ejemplo
                //resultado.append(unContacto.get("name_raw_contact_id")
                //        +" - "+unContacto.get("display_name")+ " - "+ unContacto.get("data1"));

                guardarUsuario(new Usuario(0, (String) unContacto.get("display_name"), (String) unContacto.get("data1") ));
                break;
                //String email=buscarEmail((String) unContacto.get("name_raw_contact_id"));
                //Usuario usuario = new Usuario(0, (String) unContacto.get("display_name"),email);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("TEST-ARR",arr.toString());
    }

    private void guardarUsuario(Usuario usuario) {
        ProyectoDAO dao = new ProyectoDAO(this);
        //dao.guardarUsuario(usuario);
        final Usuario usu = usuario;

        Thread backGroundUpdate = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection=null;
                try {
                    JSONObject usr= new JSONObject();


                    usr.put("id", usu.getId());
                    usr.put("nombre", usu.getNombre());
                    usr.put("correoElectronico", usu.getCorreoElectronico());

                    URL url= new URL("http://10.0.2.2:4000/usuarios");
                    urlConnection= (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setChunkedStreamingMode(0);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type","application/json");

                    DataOutputStream printout= new DataOutputStream(urlConnection.getOutputStream());

                    printout.writeBytes(URLEncoder.encode(usr.toString(),"UTF-8"));
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

    private void guardarUsuarioEnServer(Usuario usuario) {
        HttpURLConnection urlConnection=null;
        try {
            JSONObject usr= new JSONObject();


            usr.put("id", usuario.getId());
            usr.put("nombre", usuario.getNombre());
            usr.put("correoElectronico", usuario.getCorreoElectronico());

            URL url= new URL("http://localhost:4000/usuarios");
            urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/json");

            DataOutputStream printout= new DataOutputStream(urlConnection.getOutputStream());

            printout.writeBytes(URLEncoder.encode(usr.toString(),"UTF-8"));
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