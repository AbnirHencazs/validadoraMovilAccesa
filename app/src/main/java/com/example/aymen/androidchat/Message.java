package com.example.aymen.androidchat;

/**
 * Created by Aymen on 08/06/2018.
 */

public class Message {

    private String nickname;
    private String message ;

    private String jsonmensaje;

    public  Message(String nickname, String message){

    }
    public Message(String nickname, String message, String jsonmensaje) {
        this.nickname = nickname;
        this.message = message;
        this.jsonmensaje = jsonmensaje;
    }

    public String getJsonmensaje(){
        return jsonmensaje;
    }

    public void setJsonmensaje(String jsonmensaje) {
        this.jsonmensaje = jsonmensaje;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
