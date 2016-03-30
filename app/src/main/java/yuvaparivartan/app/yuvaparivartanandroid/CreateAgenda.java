package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;


public class CreateAgenda extends ActionBarActivity {

    public TextView camp_start_date, tentative_end_date, actual_end_date, tempDate;
    SpAdapter adapter;
    private Context context;
    private String last_selecteddate;
    private ArrayAdapter<String> spinnerAdapter;
    private JSONArray stateListArray;
    private JSONArray districtListArray;

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor sharededitor;
    private JSONObject agendaInfo;
    private TextView save;
    private Spinner activity;
    private Spinner camp_coordinator;
    private Spinner camp;
    private EditText village;
    private TextView agenda_date;


    public void commonInitialization()
    {
        context = CreateAgenda.this;
        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

        activity = (Spinner) findViewById(R.id.activity);
        camp_coordinator = (Spinner) findViewById(R.id.camp_coordinator);
        camp = (Spinner) findViewById(R.id.camp);
        village = (EditText) findViewById(R.id.village);
        agenda_date = (TextView) findViewById(R.id.agenda_date);

        save = (TextView) findViewById(R.id.save);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_agenda);
        commonInitialization();

        new CommonFunction().setDependancy1(false, context, activity, "activities", "name", null, null, null, null);
        new CommonFunction().setDependancy1(false, context, camp_coordinator, "camp_coordinator_profile", "name", null, null, null, null);
        new CommonFunction().setDependancy1(false, context, camp, "camp", "camp_code", null, null, null, null);

        new CommonFunction().setInformationInFormIfAvailable("agenda", context);



        agenda_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(context, DatePickerM.class);
                in.putExtra("date", agenda_date.getText().toString());
                in.putExtra("min_date", String.valueOf(System.currentTimeMillis()));
                in.putExtra("update_Date", last_selecteddate);
                startActivityForResult(in, 1);
                tempDate = agenda_date;

            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {

                    if(new CommonFunction().applyFormRules(rulesValidation(),fieldLabels(),true,context).equals("")==true) {

                        JSONObject saveObject = new JSONObject();
                        saveObject.put("activity", new CommonFunction().getSelectedItemCustomColumnFromSpinner(activity, "activities", context, "key"));
                        saveObject.put("camp_coordinator", new CommonFunction().getSelectedItemIdFromSpinner(camp_coordinator, "camp_coordinator_profile", context));
                        saveObject.put("camp", new CommonFunction().getSelectedItemIdFromSpinner(camp, "camp", context));
                        saveObject.put("village", village.getText().toString());
                        saveObject.put("agenda_date", agenda_date.getText().toString());
                        saveObject.put("created_by_camp_coordinator", "1");

                        new CommonFunction().saveInformation(context, "agenda", saveObject);


                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked


                                        Intent in = new Intent(context, AllactivitiesPageActivity.class);
                                        startActivity(in);

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Agenda created successfully . Do you want to go in activity list?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();


                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == 1)
        {
            if(data != null && data.getExtras() != null && data.getExtras().containsKey("date")==true) {
                tempDate.setText(data.getExtras().getString("date"));
            }
        }
    }


    public JSONObject rulesValidation()
    {
        JSONObject rules = new JSONObject();
        try {

            rules.put("village",new CommonFunction().rule_required);
            rules.put("agenda_date",new CommonFunction().rule_required);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  rules;
    }

    public JSONObject fieldLabels()
    {
        JSONObject labels = new JSONObject();
        try {


        } catch (Exception e) {
            e.printStackTrace();
        }
        return labels;
    }

}
