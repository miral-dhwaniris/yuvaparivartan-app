package yuvaparivartan.app.yuvaparivartanandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class HomePageActivity extends ActionBarActivity {

    private TextView history, camp_bt, all_activities_bt, calendar_bt, push_bt,pull_bt,logout_bt;
    private Bundle extras;
    boolean doubleBackToExitPressedOnce = false;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor sharededitor;
    private Context context;
    private TextView today_activities;
    private TextView create_your_agenda;

    public void commonInitialization()
    {
        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

        context = HomePageActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        //getSupportActionBar().hide();
        commonInitialization();
        history = (TextView)findViewById(R.id.history_button);
        camp_bt = (TextView)findViewById(R.id.camp_button);
        all_activities_bt = (TextView)findViewById(R.id.all_activities_button);
        calendar_bt = (TextView)findViewById(R.id.calendar_button);
        push_bt = (TextView)findViewById(R.id.push_button);
        pull_bt = (TextView)findViewById(R.id.pull_button);
        logout_bt = (TextView)findViewById(R.id.logout);
        today_activities = (TextView)findViewById(R.id.today_activities);

        create_your_agenda = (TextView)findViewById(R.id.create_your_agenda);

        extras = getIntent().getExtras();

        if(extras!=null)
        {
            if(extras.containsKey("SuccessToWeb"))
            {
                if(extras.getString("SuccessToWeb").equals("SuccessToWeb")==true)
                {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomePageActivity.this);
                    alertDialogBuilder.setTitle("Success");
                    alertDialogBuilder.setMessage("Form being sent.");
                    alertDialogBuilder.setIcon(R.drawable.success);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


                }else if(extras.getString("SuccessToWeb").equals("SuccessToLocal")==true) {
                    new CommonFunction().showAlertDialog("Form being saved offline.", "", HomePageActivity.this);

                }else if(extras.getString("SuccessToWeb").equals("No call associated with the provided phone number")==true) {
                    new CommonFunction().showAlertDialog("No call associated with the provided phone number", "Please check Your Phone Number", HomePageActivity.this);
                }
            }

            if(extras.containsKey("saveToLocal"))
            {
                if(extras.getString("saveToLocal").equals("saveToLocal")==true)
                {
                    new CommonFunction().showAlertDialog("Form being saved offline.", "Internet not Available.", HomePageActivity.this);
                }
            }
        }

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Intent in = new Intent(HomePageActivity.this, ClusterLevelActivity.class);
                startActivity(in);*/

            }
        });


        create_your_agenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new CommonFunction().whileAddFormRemoveSaveInforFromSharedPreferencce(context,"agenda");

                Intent in = new Intent(HomePageActivity.this, CreateAgenda.class);
                startActivity(in);

            }
        });

        push_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SyncAll().pushTables(context);

            }
        });

        pull_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SyncAll().syncInBackground(HomePageActivity.this, null, "HomePage");

            }
        });

        camp_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent in = new Intent(HomePageActivity.this, HomePageActivity.class);
//                startActivity(in);

            }
        });

        all_activities_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(HomePageActivity.this, AllactivitiesPageActivity.class);
                startActivity(in);

            }
        });

        logout_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedpreferences.edit().remove("login_info").commit();
                sharedpreferences.edit().remove("from_camp_leader").commit();


                Intent in = new Intent(HomePageActivity.this, Login.class);
                startActivity(in);

                finish();

            }
        });

        calendar_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent in = new Intent(HomePageActivity.this, LoginActivity.class);
//                startActivity(in);
//
//                SharedPreferences sp =getSharedPreferences("MyPref", 0);
//
//                sp.edit().remove("login_info").commit();
                //sp.edit().remove("LastSyncTIme").commit();


                finish();
            }
        });

        today_activities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(context,AllactivitiesPageActivity.class);
                in.putExtra("form_name","Todays Activity");
                in.putExtra("where_clause"," where agenda_date='"+new CommonFunction().getCurrentDateInFormat()+"'");
                startActivity(in);

            }
        });


    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
