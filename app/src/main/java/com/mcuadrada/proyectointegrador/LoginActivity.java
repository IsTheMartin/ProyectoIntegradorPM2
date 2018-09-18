package com.mcuadrada.proyectointegrador;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.mcuadrada.proyectointegrador.Classes.User;
import com.mcuadrada.proyectointegrador.Fragments.SignUpFragment;
import com.mcuadrada.proyectointegrador.Patterns.SingletonPreferences;
import com.mcuadrada.proyectointegrador.Patterns.SingletonVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private TextInputEditText tietLoginUsername;
    private TextInputLayout tilLoginUsername;
    private TextInputEditText tietLoginPassword;
    private TextInputLayout tilLoginPassword;
    private Button btnLoginSignIn;
    private TextView tvLoginSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Inicializamos las variables tomando la relación con el layout activity_login
        tvLoginSignUp = (TextView) findViewById(R.id.txvLoginSignUp);
        btnLoginSignIn = (Button) findViewById(R.id.btnLoginSignIn);
        tilLoginPassword = (TextInputLayout) findViewById(R.id.tilLoginPassword);
        tietLoginPassword = (TextInputEditText) findViewById(R.id.tietLoginPassword);
        tilLoginUsername = (TextInputLayout) findViewById(R.id.tilLoginUsername);
        tietLoginUsername = (TextInputEditText) findViewById(R.id.tietLoginUsername);
        //Se crean listeners para el botón y el textview, implementando la interfaz OnClickListener
        tvLoginSignUp.setOnClickListener(this);
        btnLoginSignIn.setOnClickListener(this);
        //Esta parte sirve para autologuearse, en caso de no haber cerrado sesión
        //Obtenemos los datos de la shared preference
        User user = SingletonPreferences.getInstance(LoginActivity.this).getLoginData();
        //Nos vamos a loguear solo con el token de sesión
        String sessionToken = user.getSession_token();
        //Si no está vacío el token, se hace el login rápido
        if (!sessionToken.isEmpty())
            quickLogin(sessionToken);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        tietLoginUsername.setText("");
        tietLoginPassword.setText("");
        tietLoginUsername.requestFocus();
    }

    /**
        Esta función es obligatoriamente implementada por la interfaz OnClickListener
        Aquí se capturan y procesan todos los clicks que reciben las vistas (objetos) en el layout
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txvLoginSignUp:
                Log.d(TAG, "onClick interface: sign up text view clicked");
                startSignUpDialogFragment();
                break;
            case R.id.btnLoginSignIn:
                Log.d(TAG, "onClick: interface sign in button clicked");
                if (validateInputText()) {
                    Log.d(TAG, "onClick: all input fields are correct.");
                    normalLogin();
                }
                break;
        }
    }

    /**
     * Esta función valida que los campos del login (usuario y contraseña) estén completos
     * @return true si están completos, false si están vacíos
     */
    private boolean validateInputText() {
        String email = tietLoginUsername.getText().toString().trim();
        String pass = tietLoginPassword.getText().toString().trim();

        tilLoginUsername.setErrorEnabled(false);
        tilLoginPassword.setErrorEnabled(false);

        if (email.isEmpty() || pass.isEmpty()) {
            tilLoginUsername.setError(getResources().getString(R.string.login_empty));
            tilLoginPassword.setError(getResources().getString(R.string.login_empty));
            return false;
        }
        return true;
    }

    /**
     * Esta función realiza una petición hacia el servidor remoto mediante el uso de volley
     * con los datos de usuario y contraseña, si la petición es correcta (si el usuario existe en
     * el sistema) entonces volley traerá los datos del usuario en un formato JSON, si no devolverá
     * un JSON con el error encontrado en la petición
     */
    private void normalLogin() {
        Log.d(TAG, "normalLogin: ");
        String username, pass;
        username = tietLoginUsername.getText().toString().trim();
        pass = tietLoginPassword.getText().toString().trim();

        //URL para conectarse al webservice del servidor remoto
        String url = "http://mcuadrada.com/pi_pm2/webservices/login.php?username=" + username
                + "&pass=" + pass;
        //Se crea un arreglo de JSON (puede que haya más de un registro), se le pasa el método GET,
        //la URL del webservice con los datos del login, se establece un listener para obtener un
        //resultado exitoso y un listener en caso de un error
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            //Listener cuando la respuesta es exitosa
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "onResponse: " + response);
                try {
                    /*
                        JSON
                        username: cajero
                        email: cajero@gmail.com
                        session_token: kjsdfbdsjfhbajslhfbasd
                        Sin JSON
                        cajerocajero@gmail.comkjsdfbdsjfhbajslhfbasd
                    */
                    //Se procesa la respuesta del JSON y se asigna a una variable para manipularla
                    JSONArray jsonArray = response;
                    //La respuesta puede tener más de un usuario, se crea un bucle para recorrer
                    //cada registro
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //cada registro se convierte a un objeto json
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        //Preguntamos si alguna llave (key) tiene por nombre "error"
                        //Así sabemos si la respuesta tiene error por parte de la petición
                        //No por que los tipos de datos son incorrectos, o que no haya internet,
                        // etc...
                        if (jsonObject.has("error")) {
                            //Obtenemos la descripción del error como un String
                            String error = jsonObject.getString("desc");
                            //Mostramos el error en un Toast
                            Toast.makeText(LoginActivity.this,
                                    error,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //En caso de tener los datos del usuario
                            //Obtenemos el nombre de usuario
                            String username = jsonObject.getString("username");
                            User user = new User(
                                    username,
                                    jsonObject.getString("email"),
                                    jsonObject.getString("session_token"),
                                    jsonObject.getString("first_time"),
                                    jsonObject.getString("last_time")
                            );
                            SingletonPreferences.getInstance(LoginActivity.this).setLoginData(user);
                            //Lo mostramos en un Toast
                            Toast.makeText(LoginActivity.this,
                                    "Bienvenido " + username, Toast.LENGTH_SHORT).show();
                            //Y lanzamos la actividad principal
                            startActivity(new Intent(LoginActivity.this,
                                    MainActivity.class));
                        }
                    }
                } catch (JSONException e) {
                    //Algunas veces los tipos de datos en el JSON producen error, se tienen que
                    //atrapar en un catch y procesarlos
                    Log.e(TAG, "onResponse: ", e);
                }
            }
        }, new Response.ErrorListener() {
            //En caso de que haya un error como cuando el servidor está incorrecto o caído,
            //es decir, errores ajenos a la aplicación
            @Override
            public void onErrorResponse(VolleyError error) {
                //Generalmente no se muestran al usuario, estos errores deben de guardarse en un
                //logger
                Log.e(TAG, "onErrorResponse: ", error);
            }
        });

        //RequestQueue requestQueue = SingletonVolley.getInstance(LoginActivity.this)
        //        .getRequestQueue();
        //Añadimos el arrayJSON anterior, a una cola de peticiones que necesita ejecutarse en
        //volley e instantáneamente volley manda la información al webservice
        SingletonVolley.getInstance(LoginActivity.this).addToRequestQueue(jsonArrayRequest);
    }

    private void quickLogin(String sessionToken){
        Log.d(TAG, "quickLogin: ");
        //URL para conectarse al webservice del servidor remoto
        String url = "http://mcuadrada.com/pi_pm2/webservices/quick_login.php?session_token="
                + sessionToken;
        //Se crea un arreglo de JSON (puede que haya más de un registro), se le pasa el método GET,
        //la URL del webservice con los datos del login, se establece un listener para obtener un
        //resultado exitoso y un listener en caso de un error
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            //Listener cuando la respuesta es exitosa
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "onResponse: " + response);
                try {
                    //Se procesa la respuesta del JSON y se asigna a una variable para manipularla
                    JSONArray jsonArray = response;
                    //La respuesta puede tener más de un usuario, se crea un bucle para recorrer
                    //cada registro
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //cada registro se convierte a un objeto json
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        //Preguntamos si alguna llave (key) tiene por nombre "error"
                        //Así sabemos si la respuesta tiene error por parte de la petición
                        //No por que los tipos de datos son incorrectos, o que no haya internet,
                        // etc...
                        if (jsonObject.has("error")) {
                            //Obtenemos la descripción del error como un String
                            String error = jsonObject.getString("desc");
                            //Mostramos el error en un Toast
                            Toast.makeText(LoginActivity.this,
                                    error,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //En caso de tener los datos del usuario
                            //Obtenemos el nombre de usuario
                            String username = jsonObject.getString("username");
                            User user = new User(
                                    username,
                                    jsonObject.getString("email"),
                                    jsonObject.getString("session_token"),
                                    jsonObject.getString("first_time"),
                                    jsonObject.getString("last_time")
                            );
                            SingletonPreferences.getInstance(LoginActivity.this).setLoginData(user);
                            //Lo mostramos en un Toast
                            Toast.makeText(LoginActivity.this,
                                    "Bienvenido " + username, Toast.LENGTH_SHORT).show();
                            //Y lanzamos la actividad principal
                            startActivity(new Intent(LoginActivity.this,
                                    MainActivity.class));
                        }
                    }
                } catch (JSONException e) {
                    //Algunas veces los tipos de datos en el JSON producen error, se tienen que
                    //atrapar en un catch y procesarlos
                    Log.e(TAG, "onResponse: ", e);
                }
            }
        }, new Response.ErrorListener() {
            //En caso de que haya un error como cuando el servidor está incorrecto o caído,
            //es decir, errores ajenos a la aplicación
            @Override
            public void onErrorResponse(VolleyError error) {
                //Generalmente no se muestran al usuario, estos errores deben de guardarse en un
                //logger
                Log.e(TAG, "onErrorResponse: ", error);
            }
        });

        //RequestQueue requestQueue = SingletonVolley.getInstance(LoginActivity.this)
        //        .getRequestQueue();
        //Añadimos el arrayJSON anterior, a una cola de peticiones que necesita ejecutarse en
        //volley e instantáneamente volley manda la información al webservice
        SingletonVolley.getInstance(LoginActivity.this).addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Esta función abre en un dialogo con información para que un nuevo usuario se registre en el
     * sistema, se utiliza un fragmento para mostrar el diseño
     */
    private void startSignUpDialogFragment() {
        //Crea una variable de la clase SignUpFragment
        SignUpFragment signUpFragment = new SignUpFragment();
        //El administrador de fragmentos obtiene todos los fragmentos de la app
        FragmentManager fm = getSupportFragmentManager();
        //Luego crea e inicia una transacción
        FragmentTransaction ft = fm.beginTransaction();
        //finalmente muestra el fragmento de registro indicando que el administrador de fragmentos
        //es el que lo va estar gestionando y se le pasa una cadena para identificación del
        //desarrollo.
        signUpFragment.show(fm,TAG);
    }
}
