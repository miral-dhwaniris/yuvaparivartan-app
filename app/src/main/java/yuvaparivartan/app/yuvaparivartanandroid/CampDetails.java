package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import yuvaparivartan.app.yuvaparivartanandroid.dbhelpers.MDbHelper;


public class CampDetails extends ActionBarActivity {


    private Spinner state, district, block;
    public TextView camp_start_date, tentative_end_date, actual_end_date, tempDate;
    SpAdapter adapter;
    private Context context;
    private String last_selecteddate;
    private ArrayAdapter<String> spinnerAdapter;
    private JSONArray stateListArray;
    private JSONArray districtListArray;
    private Spinner camp_status;
    private TextView save;
    private EditText village;
    private EditText camp_code;
    private EditText no_of_enrollment;


    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor sharededitor;
    private Spinner camp_coordinator;

    public void commonInitialization()
    {
        context = CampDetails.this;


        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

        state = (Spinner) findViewById(R.id.state);
        district = (Spinner) findViewById(R.id.district);
        block = (Spinner) findViewById(R.id.block);
        camp_coordinator = (Spinner) findViewById(R.id.camp_coordinator);

        village = (EditText) findViewById(R.id.village);
        camp_code = (EditText) findViewById(R.id.camp_code);


        camp_status = (Spinner) findViewById(R.id.camp_status);

        camp_start_date = (TextView) findViewById(R.id.camp_start_date);
        tentative_end_date = (TextView) findViewById(R.id.tentative_end_date);
        actual_end_date = (TextView) findViewById(R.id.actual_end_date);
        no_of_enrollment = (EditText) findViewById(R.id.no_of_enrollment);

        save = (TextView) findViewById(R.id.save);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campdetails);
        commonInitialization();


        sharededitor.remove(new CommonFunction().getSheredPreferanceDetailsKeyForTable("camp"));
        sharededitor.commit();


        //--SpinnerInitialization and dependancy specification
        new CommonFunction().setDependancy1(false,context, state,"state","state",null,null,null,null);
        new CommonFunction().setDependancy1(true, context, district, "district", "district", state, "state", "state", block);
        new CommonFunction().setDependancy1(true,context, block,"block","block", district,"district","district",null);


        new CommonFunction().setDependancy1(false,context, camp_coordinator,"camp_coordinator_profile","name",null,null,null,null);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.camp_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        camp_status.setAdapter(adapter);


        camp_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(context, DatePickerM.class);
                in.putExtra("date", camp_start_date.getText().toString());
                in.putExtra("min_date", String.valueOf(System.currentTimeMillis()));
                in.putExtra("update_Date", last_selecteddate);
                startActivityForResult(in, 1);
                tempDate = camp_start_date;

            }
        });


        tentative_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(context, DatePickerM.class);
                in.putExtra("date", tentative_end_date.getText().toString());
                in.putExtra("min_date", String.valueOf(System.currentTimeMillis()));
                in.putExtra("update_Date", last_selecteddate);
                startActivityForResult(in, 1);
                tempDate = tentative_end_date;
            }
        });


        actual_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(context, DatePickerM.class);
                in.putExtra("date", tentative_end_date.getText().toString());
                in.putExtra("min_date", String.valueOf(System.currentTimeMillis()));
                in.putExtra("update_Date", last_selecteddate);
                startActivityForResult(in, 1);
                tempDate = actual_end_date;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {

                    if(new CommonFunction().applyFormRules(rulesValidation(),fieldLabels(),true,context).equals("")==true) {
                        JSONObject saveObject = new JSONObject();
                        saveObject.put("state", new CommonFunction().getSelectedItemIdFromSpinner(state, "state", context));
                        saveObject.put("district", new CommonFunction().getSelectedItemIdFromSpinner(district, "district", context));
                        saveObject.put("block", new CommonFunction().getSelectedItemIdFromSpinner(block, "block", context));
                        saveObject.put("camp_coordinator", new CommonFunction().getSelectedItemIdFromSpinner(camp_coordinator, "camp_coordinator_profile", context));
                        saveObject.put("village", village.getText().toString());
                        saveObject.put("camp_code", camp_code.getText().toString());
                        saveObject.put("camp_start_date", camp_start_date.getText().toString());
                        saveObject.put("tentative_end_date", tentative_end_date.getText().toString());
                        saveObject.put("actual_end_date", actual_end_date.getText().toString());
                        saveObject.put("camp_status", camp_status.getSelectedItem().toString());
                        saveObject.put("no_of_enrollment", no_of_enrollment.getText().toString());

                        new CommonFunction().saveInformation(context,"camp",saveObject);


                        new CommonFunction().setAgendaDone(context);

                        new CommonFunction().showAlertDialog("Data Saved successfully", "", context);
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
            rules.put("camp_code",new CommonFunction().rule_required);
            rules.put("camp_start_date",new CommonFunction().rule_required);
            rules.put("tentative_end_date",new CommonFunction().rule_required);
            rules.put("actual_end_date",new CommonFunction().rule_required);
            rules.put("camp_status",new CommonFunction().rule_required);
            rules.put("no_of_enrollment",new CommonFunction().rule_required);

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
