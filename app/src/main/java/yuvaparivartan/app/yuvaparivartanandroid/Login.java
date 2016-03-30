package yuvaparivartan.app.yuvaparivartanandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import yuvaparivartan.app.yuvaparivartanandroid.app.AppController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {

    private Animation zoomin;
    private TextView login;
    private EditText user_namee;
    private EditText passworde;
    private Context context;
    private String response_string;
    JSONArray responseArray;
    private String problemString = "";
    private JSONObject responseObject;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor sharededitor;
    private ProgressDialog pDialog;
    private SQLiteDatabase mydb;

    public void commonInitialization()
    {
        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

        context = Login.this;

        login = (TextView) findViewById(R.id.login_button);
        user_namee = (EditText) findViewById(R.id.username_et);
        passworde = (EditText) findViewById(R.id.password_et);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        commonInitialization();


        String logedIn = sharedpreferences.getString("login_info", "loggedIn");
        if(!logedIn.equals("loggedIn")){

            Intent in = new Intent(context,HomePageActivity.class);
           // in.putExtra("fromlogin",true);
            context.startActivity(in);
            ((Activity)context).finish();

        }
        Log.d("Asd","AS");

//        new CommonFunction().syncVersion(Login.this);


        if(new CommonFunction().testing == false)
        {
            user_namee.setText("");
            passworde.setText("");
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String tag_string_req = "string_req";

                    String url = context.getString(R.string.bas_url) + "users/mobile-login&user_name="+user_namee.getText().toString()+"&password="+passworde.getText().toString()+"";

                    pDialog = new ProgressDialog(context);
                    pDialog.setCancelable(false);
                    pDialog.setMessage("Loading...");
                    pDialog.show();

                    StringRequest strReq = new StringRequest(Request.Method.GET,
                            url, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {



                            response_string = response;

                            response_string = response_string.replace("\n","");


                            try {

                                if(new JSONObject(response_string).has("status")==true && new JSONObject(response_string).getString("status").equals("true")==true) {

                                    sharededitor.putString("login_info", response_string);
                                    sharededitor.commit();

                                    new SyncAll().syncInBackground(Login.this, pDialog, "login");

                                }
                                else
                                {
                                    pDialog.cancel();
                                    new CommonFunction().showAlertDialog("Please enter valid username and password","",context);
                                }

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                pDialog.cancel();
                                new CommonFunction().showAlertDialog("Please enter valid username and password", "", context);
                            }

//                            pDialog.cancel();

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("errorM", "Error: " + error.getMessage());
                            pDialog.cancel();

                            new CommonFunction().showAlertDialog("Not able to login please check your network connection","",context);

                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            //                params.put("email", "miralbhalani@gmail.com");
                            //                params.put("password", "Ghogha@123");

                            return params;
                        }

                    };

                    strReq.setRetryPolicy(new DefaultRetryPolicy(10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    AppController.getInstance().addToRequestQueue(strReq, "1");

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });





    }

//    class AsyncLogin extends AsyncTask<Void,Void,Void>
//    {
//        public ProgressDialog barProgressDialog;
//        String status = "";
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            barProgressDialog = new ProgressDialog(context);
//            barProgressDialog.setCancelable(false);
////            barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            barProgressDialog.show();
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//                try {
//
//                     /*
//                      * Login API  --- GET METHOD
//                      */
//
//
//                    //getting  officeId and staffId from
//
//
//                    //URL defination
//                    String url = context.getString(R.string.bas_url) + "field-agent/login&user_name="+user_namee.getText().toString()+"&password="+passworde.getText().toString()+"";
//                    URL obj = new URL(url);
//                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//                    //add common headers
//                    new CommonFunction().addCommonHeadersGet(con);
//
//                    //geting response code -- getting in response_string
//                    int responseCode = con.getResponseCode();
//
//                    // read response in bufferReader -- getting in response_string
//                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//                    String inputLine;
//                    StringBuffer response = new StringBuffer();
//                    while ((inputLine = in.readLine()) != null) {
//                        response.append(inputLine);
//                    }
//                    in.close();
//                    response_string = response.toString();
//
//                    sharededitor.putString("login_info", response_string);
//                    sharededitor.commit();
//
//
//                    //adding responseString in json
//                    responseObject = new JSONObject(response_string);
//
//
//                } catch (Exception e) {
//                    Log.d("dd", e.toString());
//                    problemString = "Network Error";
//                }
//
//                return null;
//            }
//
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                barProgressDialog.cancel();
//
//
//                new SyncAll().syncInBackground(Login.this);
//
//
//            }
//        }

}
