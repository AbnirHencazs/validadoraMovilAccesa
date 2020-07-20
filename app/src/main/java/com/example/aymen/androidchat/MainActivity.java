package com.example.aymen.androidchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.zxing.Result;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import java.text.*;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.content.Context;


public class MainActivity extends AppCompatActivity {

    private ZXingScannerView vistaescaner;
    private TextView tituloconvenio;

    private String codigodebarras;
    private String mensajeleido;
    private String jsonmensaje;

    public  static  final String PREFS_NAME = "MiNombre";

    private Button btn;
    private Button escanearbutton;
    private EditText nickname;
   // private EditText jsonmensaje;

    public String datotituloconvenio;
    public String datotituloestacionamiento;
    public Integer numeroconvenio;
    public Integer numeroEstacionamiento;
    TextView tvIsConnected;

    public static final String NICKNAME = "usernickname";
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //call UI component  by id
        tituloconvenio = (TextView)findViewById(R.id.etNombreConvenio) ;
        btn = (Button) findViewById(R.id.enterchat) ;
        escanearbutton = (Button)findViewById(R.id.button);
        nickname = (EditText) findViewById(R.id.nickname);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        checkNetworkConnection();
        readSharedPreferences();
        tituloconvenio.setText(datotituloestacionamiento + ". Convenio: " + datotituloconvenio);
        btn.setVisibility(View.INVISIBLE);
        //Globals g = Globals.getInstance();
       // g.setData(100);



    }

    @Override
    protected void onStart() {
        super.onStart();
       // Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
      //  Escanear(vistaescaner);
        Globals g2 = Globals.getInstance();
        int data=g2.getData();
        if (data == 100){
            g2.setData(200);
         //   Escanear(vistaescaner);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
       // Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }



    // check network connection
    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            // show "Connected" & type of network "WIFI or MOBILE"
            tvIsConnected.setText(" Conectado con "+networkInfo.getTypeName());
            // change background color to red
            //tvIsConnected.setBackgroundColor(0xFF7CCC26);//FF1AFC9A
            tvIsConnected.setBackgroundColor(0xFF1AFC9A);//


        } else {
            // show "Not Connected"
            tvIsConnected.setText(" No Conectado");
            // change background color to green
            tvIsConnected.setBackgroundColor(0xFFFF0000);
        }

        return isConnected;
    }


    private void readSharedPreferences(){
        SharedPreferences settings = getSharedPreferences("identificadores",0);
        datotituloconvenio = settings.getString("Convenio","No asignado");
        datotituloestacionamiento = settings.getString("Estacionamiento", "No asignado");
        numeroconvenio = settings.getInt("IdConvenio",0);
        numeroEstacionamiento = settings.getInt("IdEsacion", 0);

    }




    public void Conectar(View view){
             if (nickname.getText().toString().contains("Estacionamiento")){
            Intent i  = new Intent(MainActivity.this,ChatBoxActivity.class);
            //retreive nickname from textview and add it to intent extra
            i.putExtra(NICKNAME,nickname.getText().toString());
            btn.setEnabled(false);
            startActivity(i);
           // MainActivity.this.finish();
        }
        else {
                escanerprimero();
             }
    }


    public void escanerprimero(){
        Toast.makeText(this,"Escanee primero su boleto por favor.!!!",Toast.LENGTH_SHORT).show();
    }

    public void BoletoInvalido(){
        Toast.makeText(this,"Boleto no válido.!!!",Toast.LENGTH_SHORT).show();
    }


    public void  Escanear(View view){
        vistaescaner = new ZXingScannerView(this);
        vistaescaner.setResultHandler(new zxingscanner());
        setContentView(vistaescaner);
        vistaescaner.startCamera();
        escanearbutton.setEnabled(false);
        escanearbutton.setVisibility(View.INVISIBLE);
    }

    class zxingscanner implements ZXingScannerView.ResultHandler{

        @Override
        public void handleResult(Result result) {
            String dato = result.getText();
            codigodebarras = result.getText();
            setContentView(R.layout.activity_main);
            vistaescaner.stopCamera();
            nickname = (EditText) findViewById(R.id.nickname);
           // if (codigodebarras.length()==16) {
                if(codigodebarras.length()==23 || codigodebarras.length()==16){
                try {
                    // get JSONObject from JSON file
                    JSONObject obj = new JSONObject();
                    //SACAMOS LA FECHA COMPLETA
                    Date d = new Date();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    //int valorid = 13;

                    obj.accumulate("Estacionamiento", numeroEstacionamiento);
                    obj.accumulate("Boleto", codigodebarras);
                    obj.accumulate("Convenio", numeroconvenio);
                    obj.accumulate("Insertado", dateFormat.format(d));
                    JSONObject actualizado = obj.accumulate("Actualizado", dateFormat.format(d));

                    jsonmensaje = obj.toString();
                    nickname.setText(jsonmensaje);
                    checkNetworkConnection();
                    // btn.setVisibility(View.VISIBLE);
                    // escanearbutton.setVisibility(View.INVISIBLE);
                    // btn.setEnabled(true);
                    // btn.setTextColor(Color.parseColor("#ffffff"));
                    // escanearbutton.setEnabled(false);

                  //  Globals g3 = Globals.getInstance();
                    //g3.setData(100);
                  //  int data = g3.getData();
                 //   if (data == 200) {
                    ///////////////////////
                    if (nickname.getText().toString().contains("Estacionamiento")) {
                        Intent i = new Intent(MainActivity.this, ChatBoxActivity.class);
                        //retreive nickname from textview and add it to intent extra
                        i.putExtra(NICKNAME, nickname.getText().toString());
                        // btn.setEnabled(false);
                        startActivity(i);
                        MainActivity.this.finish();
                    } else {
                        escanerprimero();
                    }
              //  }
                    ///////////////////////

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                btn.setEnabled(false);
                BoletoInvalido();
            }

        }
    }


}
