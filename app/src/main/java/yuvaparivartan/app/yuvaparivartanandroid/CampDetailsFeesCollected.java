package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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


public class CampDetailsFeesCollected extends ActionBarActivity {

    private EditText amount_of_fees_collected;
    public TextView camp_start_date, tentative_end_date, actual_end_date, tempDate;
    private TextView state, district, block;

    private TextView save;

    SpAdapter adapter;
    List<String> listServ = new ArrayList<>();
    private Context context;
    private String last_selecteddate;
    private TextView camp_status;
    private TextView village;
    private TextView camp_code;
    private JSONObject agendaInfo;

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor sharededitor;
    private TextView current_fees_collection;
    private TextView total_of_fees_collected;
    private EditText name_of_person_holding_the_amount;
    private EditText name_of_person_holding_the_amount_phone_number;


    public void commonInitialization() {
        context = CampDetailsFeesCollected.this;
        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

        camp_status = (TextView) findViewById(R.id.camp_status);
        village = (TextView) findViewById(R.id.village);
        camp_code = (TextView) findViewById(R.id.camp_code);
        amount_of_fees_collected = (EditText) findViewById(R.id.amount_of_fees_collected);

        state = (TextView) findViewById(R.id.state);
        district = (TextView) findViewById(R.id.district);
        block = (TextView) findViewById(R.id.block);

        camp_start_date = (TextView) findViewById(R.id.camp_start_date);
        tentative_end_date = (TextView) findViewById(R.id.tentative_end_date);
        actual_end_date = (TextView) findViewById(R.id.actual_end_date);


        name_of_person_holding_the_amount = (EditText) findViewById(R.id.name_of_person_holding_the_amount);
        name_of_person_holding_the_amount_phone_number = (EditText) findViewById(R.id.name_of_person_holding_the_amount_phone_number);

        save = (TextView) findViewById(R.id.save);



        current_fees_collection = (TextView) findViewById(R.id.current_fees_collection);
        total_of_fees_collected = (TextView) findViewById(R.id.total_of_fees_collected);



    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campdetails_feescollected);
        commonInitialization();

        try {
            agendaInfo = new JSONObject(sharedpreferences.getString(new CommonFunction().getSheredPreferanceDetailsKeyForTable("agenda"), ""));
            String campId = agendaInfo.getString("camp");
            JSONArray campArray1 =  new MDbHelper().getAll("camp", "", context);
            JSONArray campArray =  new MDbHelper().getAll("camp", " where id='" + campId + "'", context);
            if(campArray.length()!=0)
            {
                JSONObject campObject = campArray.getJSONObject(0);
                sharededitor.putString(new CommonFunction().getSheredPreferanceDetailsKeyForTable("camp"),campObject.toString());
                sharededitor.commit();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



        new CommonFunction().setInformationInFormIfAvailable("camp", context);
        //--ExtraSetInformation
        try {
            current_fees_collection.setText(new JSONObject(sharedpreferences.getString(new CommonFunction().getSheredPreferanceDetailsKeyForTable("camp"),"")).optString("amount_of_fees_collected"));
            if(current_fees_collection.getText().toString().equals("")==true)
            {
                current_fees_collection.setText("0");
            }

            amount_of_fees_collected.setText("");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //--Text change listner of ammount of fees collected for total in total of fees collected
        amount_of_fees_collected.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String currentFeesCollectedString = current_fees_collection.getText().toString();

                if(currentFeesCollectedString.equals("")==true)
                {
                    currentFeesCollectedString = "0";
                }

                double amountOfFeesCollected = Double.parseDouble(currentFeesCollectedString)+Double.parseDouble(amount_of_fees_collected.getText().toString());

                total_of_fees_collected.setText(String.valueOf(amountOfFeesCollected));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if(new CommonFunction().applyFormRules(rulesValidation(),fieldLabels(),true,context).equals("")==true) {
                        JSONObject saveObject = new JSONObject();

                        String currentFeesCollectedString = new JSONObject(sharedpreferences.getString(new CommonFunction().getSheredPreferanceDetailsKeyForTable("camp"),"")).optString("amount_of_fees_collected");

                        if(currentFeesCollectedString.equals("")==true)
                        {
                            currentFeesCollectedString = "0";
                        }

                        double amountOfFeesCollected = Double.parseDouble(currentFeesCollectedString)+Double.parseDouble(amount_of_fees_collected.getText().toString());
                        saveObject.put("amount_of_fees_collected", String.valueOf(amountOfFeesCollected));

                        saveObject.put("name_of_person_holding_the_amount", name_of_person_holding_the_amount.getText().toString());
                        saveObject.put("name_of_person_holding_the_amount_phone_number", name_of_person_holding_the_amount_phone_number.getText().toString());

                        new CommonFunction().saveInformation(context, "camp", saveObject);


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


    public JSONObject rulesValidation()
    {
        JSONObject rules = new JSONObject();
        try {

            rules.put("amount_of_fees_collected",new CommonFunction().rule_required);
            rules.put("name_of_person_holding_the_amount",new CommonFunction().rule_required);
            rules.put("name_of_person_holding_the_amount_phone_number",new CommonFunction().rule_required);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  rules;
    }

    public JSONObject fieldLabels()
    {
        JSONObject labels = new JSONObject();
        try {

            labels.put("amount_of_fees_collected","Ammount Of Fees Collected");
            labels.put("name_of_person_holding_the_amount_phone_number","Contact number of person");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return labels;
    }
}
