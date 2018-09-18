package com.mcuadrada.proyectointegrador.Classes;

public class User {

    private String username;
    private String email;
    private String session_token;
    private String first_time;
    private String last_time;

    public User() {
    }

    public User(String username, String email, String session_token, String first_time,
                String last_time) {
        this.username = username;
        this.email = email;
        this.session_token = session_token;
        this.first_time = first_time;
        this.last_time = last_time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSession_token() {
        return session_token;
    }

    public void setSession_token(String session_token) {
        this.session_token = session_token;
    }

    public String getFirst_time() {
        return first_time;
    }

    public void setFirst_time(String first_time) {
        this.first_time = first_time;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }
}
