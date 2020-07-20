package com.example.aymen.androidchat;


import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import java.util.Date;
import java.text.*;


import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.content.DialogInterface;


import javax.xml.parsers.SAXParser;

import me.dm7.barcodescanner.zxing.ZXingScannerView;



import static android.support.v7.app.AlertDialog.*;

public class ChatBoxActivity extends AppCompatActivity{


    Timer timer;
    TimerTask timerTask;

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();


    public RecyclerView myRecylerView ;
    public List<Message> MessageList ;
    public ChatBoxAdapter chatBoxAdapter;
    public  EditText messagetxt ;
    public  Button send ;
    //declare socket object
    private Socket socket;

    public String Nickname ;

    public boolean estadoconexion;

    public String mensajedeservidor;
    public String temporalstring;

    public String mensajesdespera;

    boolean estadodeconexion, estadoderefresco;

    public int conteodeespera;
   // public int conteolabel;

    public String datotituloconvenio;
    public String datotituloestacionamiento;
    public Integer numeroconvenio;
   // public TextView labelnumero;

    private TextView tituloconvenio;
    TextView tvIsConnected2;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);

        tituloconvenio = (TextView)findViewById(R.id.etTituloConvenio2);
        readSharedPreferences();
        tituloconvenio.setText(datotituloestacionamiento +". Convenio:" + datotituloconvenio);


        messagetxt = findViewById(R.id.message);
        send = findViewById(R.id.send);
        tvIsConnected2 = (TextView) findViewById(R.id.tvIsConnected2);
       // labelnumero = (TextView)findViewById(R.id.etLabel);

        send.setEnabled(false);
        send.setVisibility(View.INVISIBLE);
        estadodeconexion = true;
        estadoderefresco = false;
        conteodeespera = 0;
      //  conteolabel = 0;
        // get the nickame of the user
        temporalstring = getIntent().getExtras().getString(MainActivity.NICKNAME);
        //connect you socket client to the server
        Nickname = "APPVALIDADOR";
        checkNetworkConnection();




        try {

           socket = IO.socket("http://192.168.1.96:8082");
          //  socket = IO.socket("https://websocketpic.azurewebsites.net/");
           // socket = IO.socket("http://accesawork.ddns.net:8083");
            socket.connect();
            socket.emit("join", Nickname);
        }
        catch (URISyntaxException e) {
              e.printStackTrace();
            errordered();
        }

       //setting up recyler
        MessageList = new ArrayList<>();
        myRecylerView = findViewById(R.id.messagelist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());

        // message send action
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieve the nickname and the message content and fire the event messagedetection
                Intent a  = new Intent(ChatBoxActivity.this,MainActivity.class);
              //  estadoconexion = socket.connected();
                if (estadoconexion) {
                        socket.emit("messagedetection", Nickname, temporalstring);
                        mensajede_espera();
                        estadoderefresco = true;

                }
                else
                {
                 errordered();
                 escanear_denuevo();
                 finish();
                 startActivity(a);
                 ChatBoxActivity.this.finish();
                }
            }
        });


        //implementing socket listeners
        socket.on("userjoinedthechat", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];

                        Toast.makeText(ChatBoxActivity.this,data,Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        socket.on("userdisconnect", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];
                        Toast.makeText(ChatBoxActivity.this,data,Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        socket.on("messageapp", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            //extract data from fired event
                            String convenioid = data.getString("idconvenio");
                            mensajedeservidor = data.getString("mensaje_app");
                            if (convenioid.equals(numeroconvenio.toString())) {
                                //mensaje_recivido();

                                if (mensajedeservidor.equals("Registro completo.")){
                                ImageView image = new ImageView(ChatBoxActivity.this);
                                image.setImageResource(R.drawable.palomita);

                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(ChatBoxActivity.this).
                                                setMessage(mensajedeservidor).
                                                setPositiveButton("SI", new OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //dialog.dismiss();
                                                        finish();
                                                        System.exit(0);

                                                    }
                                                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                finish();
                                                System.exit(0);
                                                // startActivity(atf);
                                            }
                                        }).setView(image);
                                builder.create().show();
                            }

                                if (mensajedeservidor.equals("No se puede validar el boleto!. El sistema de caja no está disponible. Intente mas tarde.")){
                                    ImageView image = new ImageView(ChatBoxActivity.this);
                                    image.setImageResource(R.drawable.incorrecto);

                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(ChatBoxActivity.this).
                                                    setMessage(mensajedeservidor).
                                                    setPositiveButton("OK", new OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //dialog.dismiss();
                                                            finish();
                                                            System.exit(0);

                                                        }
                                                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialog) {
                                                    finish();
                                                    System.exit(0);
                                                    // startActivity(atf);
                                                }
                                            }).setView(image);
                                    builder.create().show();
                                }

                                if (mensajedeservidor.equals("El Boleto ya ha sido registrado.")){
                                    ImageView image = new ImageView(ChatBoxActivity.this);
                                    image.setImageResource(R.drawable.incorrecto);

                                    AlertDialog.Builder builder =
                                            new AlertDialog.Builder(ChatBoxActivity.this).
                                                    setMessage(mensajedeservidor).
                                                    setPositiveButton("OK", new OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //dialog.dismiss();
                                                            finish();
                                                            System.exit(0);

                                                        }
                                                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialog) {
                                                    finish();
                                                    System.exit(0);
                                                    // startActivity(atf);
                                                }
                                            }).setView(image);
                                    builder.create().show();
                                }


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });

    }




    @Override
    protected void onResume() {
        super.onResume();
        //onResume we start our timer so it can start when the app comes from the background
        startTimer();
    }


    // check network connection
    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            // show "Connected" & type of network "WIFI or MOBILE"
            tvIsConnected2.setText(" Conectado con "+ networkInfo.getTypeName());
            // change background color to red
            //tvIsConnected.setBackgroundColor(0xFF7CCC26);//FF1AFC9A
            tvIsConnected2.setBackgroundColor(0xFF1AFC9A);//


        } else {
            // show "Not Connected"
            tvIsConnected2.setText(" No Conectado");
            // change background color to green
            tvIsConnected2.setBackgroundColor(0xFFFF0000);
        }

        return isConnected;
    }


    private void readSharedPreferences(){
        SharedPreferences settings = getSharedPreferences("identificadores",0);
        datotituloconvenio = settings.getString("Convenio","No asignado");
        datotituloestacionamiento = settings.getString("Estacionamiento","No asignado");
        numeroconvenio = settings.getInt("IdConvenio",0);
    }


    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 2000ms the TimerTask will run every 3000ms
        timer.schedule(timerTask, 2000, 3000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }



    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                     //   conteolabel ++;
//                        labelnumero.setText(String.valueOf(conteolabel)); //int i = 5;
                        //String strI = String.valueOf(i);
                        if (estadodeconexion){
                            conteodeespera++;

                            estadoconexion = socket.connected();
                            if (estadoconexion == false) {
                                mensaje_conexion();
                                send.setEnabled(false);
                                send.setVisibility(View.INVISIBLE);
                                mensajesdespera = "en espera!";
                            } else {

                                stoptimertask();
                                mensajesdespera = "realizado.!";
                                estadodeconexion = false;
                                conteodeespera = 0;
                                socket.emit("messagedetection", Nickname, temporalstring);
                                mensajede_espera();
                                estadoderefresco = true;
                               // finish();
                                //System.exit(0);
                                //send.setEnabled(true);
                                //send.setVisibility(View.VISIBLE);
                            }

                            if (conteodeespera>=3) {
                                stoptimertask();
                                tiempoFueraConexion();
                                conteodeespera = 0;

                                Globals g4 = Globals.getInstance();
                                //g3.setData(100);
                                int data = g4.getData();
                                if (data == 200) {

                                Intent atf = new Intent(ChatBoxActivity.this, MainActivity.class);
                                finish();
                                startActivity(atf);
                                //onResume();
                                ChatBoxActivity.this.finish();
                                }
                            }

                            //show the toast
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getApplicationContext(), "Estado de la conexion : " + mensajesdespera, duration);
                            toast.show();
                    }

                    if (estadoderefresco) {
                            estadoderefresco = false;
                            stoptimertask();
                       // Intent at  = new Intent(ChatBoxActivity.this,MainActivity.class);
                        //finish();
                       // startActivity(at);
                        //ChatBoxActivity.this.finish();
                    }




                    }
                });
            }
        };
    }


    public void tiempoFueraConexion(){
        Toast.makeText(this,"No se pudo conectar con servidor. Revise su conexion de internet!",Toast.LENGTH_SHORT).show();
    }

    public void mensaje_conexion(){
        Toast.makeText(this,"Esperando conexion con servidor.",Toast.LENGTH_SHORT).show();
    }

    public void mensaje_recivido(){
        Toast.makeText(this,mensajedeservidor,Toast.LENGTH_SHORT).show();
    }

    public void mensajede_espera(){
       // send.setEnabled(false);

        Toast.makeText(this,"Esperando a servidor para validar",Toast.LENGTH_SHORT).show();
    }

    public void escanear_denuevo(){
        Toast.makeText(this,"Escanee nuevo boleto por favor.!!!",Toast.LENGTH_SHORT).show();
        send.setEnabled(false);
    }

    public void errordered(){
        Toast.makeText(this,"No se encontro el servidor.!!!",Toast.LENGTH_SHORT).show();
    }

    public void redenlazada(){

        Toast.makeText(this,"Conectado con el servidor.!!!",Toast.LENGTH_SHORT).show();
    }
    public void espereporfavor(){

        Toast.makeText(this,"Espere por favor...",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.disconnect();
  }



  }
