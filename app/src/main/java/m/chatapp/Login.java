package m.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    TextView register;
    EditText username, password;
    Button loginButton;
    String user, pass;
    String usern = null;
    String passn = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences("NEW", MODE_PRIVATE);
        usern = prefs.getString("username",null);
        passn = prefs.getString("password",null);

        register = (TextView)findViewById(R.id.register);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.loginButton);

        if (usern!=null){
            UserDetails.username = usern;
            UserDetails.password = passn;
            Intent i = new Intent(getApplicationContext() , Users.class);
            startActivity(i);
        }





            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Login.this, Register.class));
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    login();

                }
            });










    }

    private void login(){

        if (usern!=null){
            user = usern;
            pass = passn;
        }
        else {

            user = username.getText().toString();
            pass = password.getText().toString();

        }



        if(user.equals("")){
            username.setError("can't be blank");
        }
        else if(pass.equals("")){
            password.setError("can't be blank");
        }
        else{
            String url = "https://chatapp-ef926.firebaseio.com/users.json";
            final ProgressDialog pd = new ProgressDialog(Login.this);
            pd.setMessage("Loading...");
            pd.show();

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {
                    if(s.equals("null")){
                        Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                    }
                    else{
                        try {
                            JSONObject obj = new JSONObject(s);

                            if(!obj.has(user)){
                                Toast.makeText(Login.this, "user not found", Toast.LENGTH_LONG).show();
                            }
                            else if(obj.getJSONObject(user).getString("password").equals(pass)){
                                UserDetails.username = user;
                                UserDetails.password = pass;
                                // Toast.makeText(Login.this, "user found", Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = getSharedPreferences("NEW", MODE_PRIVATE).edit();
                                editor.putString("username",user);
                                editor.putString("password",pass);
                                editor.apply();

                                startActivity(new Intent(Login.this, Users.class));
                            }
                            else {
                                Toast.makeText(Login.this, "incorrect password", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    pd.dismiss();
                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("" + volleyError);
                    pd.dismiss();
                }
            });

            RequestQueue rQueue = Volley.newRequestQueue(Login.this);
            rQueue.add(request);
        }


    }
}
