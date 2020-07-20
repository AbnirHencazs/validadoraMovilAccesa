package com.example.aymen.androidchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private EditText Usuario;
    private EditText Password;
    private EditText Estacionamiento;
    private EditText IdEst;
    private EditText Convenio;
    private EditText IdConv;
    private TextView Avisos;
    private Button   Guardar;
    //public  static  final String PREFS_NAME = "MiNombre";
    public boolean activarapp;
    public boolean camposdenombresllenos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Usuario         = (EditText)findViewById(R.id.etUsuario2);
        Password        = (EditText)findViewById(R.id.etDatoclave) ;
        Estacionamiento = (EditText)findViewById(R.id.etEstacionamiento2);
        IdEst           = (EditText)findViewById(R.id.etIdEstacion2);
        Convenio        = (EditText)findViewById(R.id.etConvenio2);
        IdConv          = (EditText)findViewById(R.id.etIdConvenio2);
        Avisos          = (TextView)findViewById(R.id.etAvisos2);
        Guardar         = (Button)findViewById(R.id.etGuardar2);

/*
        Usuario         = (EditText)findViewById(R.id.etUsuario);
        Password        = (EditText)findViewById(R.id.etPassword) ;
        Estacionamiento = (EditText)findViewById(R.id.etEstacionamiento);
        IdEst           = (EditText)findViewById(R.id.etIdEstacion);
        Convenio        = (EditText)findViewById(R.id.etConvenio);
        IdConv          = (EditText)findViewById(R.id.etIdConvenio);
        Avisos          = (TextView)findViewById(R.id.etAvisos);
        Guardar         = (Button)findViewById(R.id.etGuardar);
*/
        if (readSharePreferencesStatusApp()){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }

    }

    public void guaradardatos(View view){
        Validar(Usuario.getText().toString(),Password.getText().toString());
        readSharePreferencesStatusApp();
    }


    private void createSharedPreferences(String NombreEstacion, String NombreConvenio, int IdEstacion, int IdConvenio, boolean permisoapp ){
        SharedPreferences settings = getSharedPreferences("identificadores",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Estacionamiento",NombreEstacion);
        editor.putString("Convenio",NombreConvenio);
        editor.putInt("IdEsacion",IdEstacion);
        editor.putInt("IdConvenio",IdConvenio);
        editor.putBoolean("EstadoApp",permisoapp);
        editor.commit();

    }


    private boolean readSharePreferencesStatusApp(){
        SharedPreferences settings = getSharedPreferences("identificadores", Context.MODE_PRIVATE);
        int estadoidestacion = settings.getInt("IdEsacion",1);
        int estadoidconvenio = settings.getInt("IdConvenio",0);
        boolean statusapp = settings.getBoolean("EstadoApp",false);
        return statusapp;
    }

    private void Validar(String Username, String userPassword){
        if (Username.equals("admin") && userPassword.equals("accesa01")){
            GuardarNombres(Estacionamiento.getText().toString(),Convenio.getText().toString(),IdEst.getText().toString(), IdConv.getText().toString() );
            if (camposdenombresllenos) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        }
        else {
            Avisos.setText("Password o Usuario incorrectos.");
        }

    }

    private void  GuardarNombres(String NomEstacion, String NomConvenio, String IdEstacion, String IdConven){
        if (NomEstacion.equals("")&&NomConvenio.equals(""))
        {
            Avisos.setText("No se han escrito nombres...");
            return;
        }
        else {
            int IDestacion = 0;
            int IDconvenio = 0;

            try {
                IDestacion = Integer.parseInt(IdEstacion);
                IDconvenio = Integer.parseInt(IdConven);

            } catch(NumberFormatException nfe) {
                // Handle parse error.
            }
            createSharedPreferences(NomEstacion,NomConvenio,IDestacion,IDconvenio,true);
            camposdenombresllenos = true;
        }
    }

}
