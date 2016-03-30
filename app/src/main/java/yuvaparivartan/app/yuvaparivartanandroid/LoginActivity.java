package yuvaparivartan.app.yuvaparivartanandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends ActionBarActivity {

    private Button login;
    private ProgressDialog pDialog;
    private EditText username_et,pasword_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        SharedPreferences sp = getApplicationContext().getSharedPreferences("main", 0);
        String logedIn = sp.getString("loggedIn", "loggedIn");

        if(logedIn.equals("yes")){
            Intent in = new Intent(LoginActivity.this,HomePageActivity.class);
            startActivity(in);
        }else{

        }


        username_et = (EditText) findViewById(R.id.username_et);
        pasword_et = (EditText) findViewById(R.id.password_et);

        login = (Button) findViewById(R.id.login_button);
        pDialog = new ProgressDialog(LoginActivity.this);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkValidations()){
                    Intent in = new Intent(LoginActivity.this,HomePageActivity.class);

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("main", 0);
                    SharedPreferences.Editor e = sp.edit();
                    e.putString("loggedIn", "yes");
                    e.commit();

                    finish();
                    startActivity(in);
                }

            }

        });

    }

    private boolean checkValidations() {
        if (!username_et.getText().toString().trim().equals("")) {
        } else {
            username_et.setError("Enter UserName");
            username_et.requestFocus();
            return false;
        }

        if (!pasword_et.getText().toString().trim().equals("")) {
        } else {
            pasword_et.setError("Enter Password");
            pasword_et.requestFocus();
            return false;
        }

        if(username_et.getText().toString().trim().equals("admin") && pasword_et.getText().toString().trim().equals("admin")){

        }else{
            new CommonFunction().showAlertDialog("Please Check Username & Password.", "Invalid Username & Password.", LoginActivity.this);
            return false;
        }

        return true;
    }


}
