package com.mcuadrada.proyectointegrador.Fragments;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mcuadrada.proyectointegrador.Patterns.SingletonVolley;
import com.mcuadrada.proyectointegrador.R;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends DialogFragment {

    private TextInputEditText tietEmail, tietPass1, tietPass2, tietUsername;
    private TextInputLayout tilEmail, tilPass1, tilPass2, tilUsername;
    private Button btnSignUp;

    private static final String TAG = SignUpFragment.class.getSimpleName();

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        //Se crea la vista del fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container,
                false);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_DayNight_NoActionBar);
        //Las variables se relacionan con los objetos en el layout
        tietUsername = (TextInputEditText) view.findViewById(R.id.tietSignUpUsername);
        tietEmail = (TextInputEditText) view.findViewById(R.id.tietSignUpEmail);
        tietPass1 = (TextInputEditText) view.findViewById(R.id.tietSignUpPass1);
        tietPass2 = (TextInputEditText) view.findViewById(R.id.tietSignUpPass2);
        tilUsername = (TextInputLayout) view.findViewById(R.id.tilSignUpUsername);
        tilEmail = (TextInputLayout) view.findViewById(R.id.tilSignUpEmail);
        tilPass1 = (TextInputLayout) view.findViewById(R.id.tilSignUpPass1);
        tilPass2 = (TextInputLayout) view.findViewById(R.id.tilSignUpPass2);
        btnSignUp = (Button) view.findViewById(R.id.btnSignUp);
        //Escuchador cuando se oprime el botón
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                if (validateEmptyFields())
                    if (validatePasswords())
                        signUp();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    /**
     * Esta función verifica si los campos de registro están completos.
     *
     * @return true si están completos los campos, false si está uno vacío.
     */
    private boolean validateEmptyFields() {
        Log.d(TAG, "validateEmptyFields: ");
        String email, pass1, pass2, username;
        //Asignacion de variables con los datos del layout
        username = tietUsername.getText().toString().trim();
        email = tietEmail.getText().toString().trim();
        pass1 = tietPass1.getText().toString().trim();
        pass2 = tietPass2.getText().toString().trim();
        //Deshabilitacion de errores mostrados
        tilUsername.setErrorEnabled(false);
        tilEmail.setErrorEnabled(false);
        tilPass1.setErrorEnabled(false);
        tilPass2.setErrorEnabled(false);

        if (email.isEmpty()) {
            tilEmail.setError(getResources().getString(R.string.signup_emptyemail));
        } else if (pass1.isEmpty()) {
            tilPass1.setError(getResources().getString(R.string.signup_emptypass));
        } else if (pass2.isEmpty()) {
            tilPass2.setError(getResources().getString(R.string.signup_emptypass));
        } else if (username.isEmpty()) {
            tilUsername.setError(getResources().getString(R.string.signup_usernamehint));
        } else {
            return true;
        }
        return false;
    }

    private boolean validatePasswords() {
        Log.d(TAG, "validatePasswords: ");
        String pass1, pass2;
        //Asignacion de variables con valores del layout
        pass1 = tietPass1.getText().toString().trim();
        pass2 = tietPass2.getText().toString().trim();
        //Deshabilitacion de errores
        tilPass2.setErrorEnabled(false);
        //Contraseñas iguales
        if (pass1.equals(pass2))
            return true;

        tilPass2.setError(getResources().getString(R.string.signup_notequalspass));
        return false;
    }

    private void signUp() {
        Log.d(TAG, "signUp: ");
        String email, pass, username, device;
        //Asignacion de variables con valores del layout
        username = tietUsername.getText().toString().trim();
        email = tietEmail.getText().toString().trim();
        pass = tietPass1.getText().toString().trim();
        device = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            if (getActivity().getApplicationContext().
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                device = (telephonyManager.getDeviceId());
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        101);
            }
        }
        String url = "http://mcuadrada.com/pi_pm2/webservices/signup.php?email=" + email
                + "&pass=" + pass + "&username=" + username + "&device=" + device;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("email")) {
                            Toast.makeText(getActivity(),
                                    "Este email ya existe, prueba con otro",
                                    Toast.LENGTH_LONG).show();
                            tietEmail.setFocusable(true);
                        } else if (response.contains("username")) {
                            Toast.makeText(getActivity(),
                                    "Este nombre de usuario ya existe, prueba con otro",
                                    Toast.LENGTH_SHORT).show();
                            tietUsername.setFocusable(true);
                        } else {
                            Log.d(TAG, "onResponse: " + response);
                            Toast.makeText(getActivity(),
                                    response,
                                    Toast.LENGTH_SHORT).show();
                            SignUpFragment signUpFragment = new SignUpFragment();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .remove(signUpFragment).commit();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
            }
        });
        RequestQueue requestQueue = SingletonVolley.getInstance(getActivity()).getRequestQueue();
        SingletonVolley.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        101);
            } else {
                signUp();
            }
        }
    }
}
