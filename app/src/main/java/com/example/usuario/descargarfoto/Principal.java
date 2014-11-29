package com.example.usuario.descargarfoto;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class Principal extends Activity {

    private EditText tvDireccion, tvNombreFoto;
    private RadioButton  rbPrivada;
    private String ruta;
    private String nombre;
    private ImageView iv;

    /***************************************************************/
    /*                                                             */
    /*                         METODOS ON                          */
    /*                                                             */
    /***************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /***************************************************************/
    /*                                                             */
    /*                             Hilo                            */
    /*                                                             */
    /***************************************************************/

    class HiloFacil extends AsyncTask<Object, Integer, String> {

        HiloFacil(String... p) {
            //Lo primero que se ejecuta.
        }

        @Override
        protected void onPreExecute() {
            //1º en ejecutarse con execute, en la hebra UI (pensado para trabajo previo)
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Object[] params) {
            //2º en ejecutarse. En una hebra nueva.
            try {
                //Comprobamos si hemos insertado el nombre de la foto o si escogemos el nombre por defecto
                String direccion = tvDireccion.getText().toString();
                URL url = new URL(direccion);
                if(tvNombreFoto.getText().toString().compareTo("")==0){
                    String[] ruta = direccion.split("/");
                    nombre = ruta[ruta.length-1];
                    nombre = nombre.substring(0,nombre.length()-4);
                }else{
                    nombre = tvNombreFoto.getText().toString();
                }
                // Establecemos conexion y filtramos por el tipo de extensión
                URLConnection conexion = url.openConnection();
                //Cogemos los 3 ultimos caracteres de la direccion
                String tipo = direccion.substring(direccion.length()-3);
                if(tipo.equals("png")||tipo.equals("jpg")||tipo.equals("gif")){
                    InputStream is = conexion.getInputStream();
                    //Dependiendo del radio button guardaremos en la memoria privada o publica
                    if (rbPrivada.isChecked()) {
                        ruta = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() +"/"+ nombre+"."+tipo;
                    } else {
                        ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()
                                +"/"+ nombre+"."+tipo;
                    }
                    FileOutputStream fos = new FileOutputStream(ruta);

                    byte[] b = new byte[2048];
                    int lenght = is.read(b);
                    while (lenght > 0) {
                        fos.write(b, 0, lenght);
                        lenght = is.read(b);
                    }
                    is.close();
                    fos.close();
                }else{
                    tostada(getString(R.string.tipo));
                }
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //3º en ejecutarse de forma intermitente para escribir en la UI
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            //4º en ejecutarse al finalizar la hebra. En la hebra UI.
            File imgFile = new File(ruta);
            //Si se ha guardado la imagen se muestra
            if(imgFile.exists())
            {
                iv.setImageURI(Uri.fromFile(imgFile));
                tostada(getString(R.string.guardado));
            }
            super.onPostExecute(s);
        }

        @Override
        protected void onCancelled() {
            //si cancelo la hebra
            super.onCancelled();
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }
    }

    /***************************************************************/
    /*                                                             */
    /*                         AUXILIARES                          */
    /*                                                             */
    /***************************************************************/


    public void guardar(View v) {
        if(tvDireccion.getText().toString().compareTo("")!=0){
            HiloFacil hf = new HiloFacil();
            hf.execute();
        }else{
            tostada(getString(R.string.ruta));
        }
    }

    //Sacamos mensajes de información
    private void tostada(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void initComponents(){
        tvDireccion = (EditText) findViewById(R.id.etLink);
        tvNombreFoto = (EditText) findViewById(R.id.etNombre);
        rbPrivada = (RadioButton) findViewById(R.id.rbPrivada);
        iv = (ImageView)findViewById(R.id.ivImagen);
    }

}
