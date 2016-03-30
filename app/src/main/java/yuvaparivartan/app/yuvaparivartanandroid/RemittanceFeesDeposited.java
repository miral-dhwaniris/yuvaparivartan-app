package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import yuvaparivartan.app.yuvaparivartanandroid.dbhelpers.MDbHelper;


public class RemittanceFeesDeposited extends ActionBarActivity {

    private EditText amount_of_fees_deposited;
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
    private TextView name_of_person_holding_the_amount;
    private TextView name_of_person_holding_the_amount_phone_number;


    public void commonInitialization() {
        context = RemittanceFeesDeposited.this;
        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

        camp_status = (TextView) findViewById(R.id.camp_status);
        village = (TextView) findViewById(R.id.village);
        camp_code = (TextView) findViewById(R.id.camp_code);
        amount_of_fees_deposited = (EditText) findViewById(R.id.amount_of_fees_deposited);

        state = (TextView) findViewById(R.id.state);
        district = (TextView) findViewById(R.id.district);
        block = (TextView) findViewById(R.id.block);

        camp_start_date = (TextView) findViewById(R.id.camp_start_date);
        tentative_end_date = (TextView) findViewById(R.id.tentative_end_date);
        actual_end_date = (TextView) findViewById(R.id.actual_end_date);


        name_of_person_holding_the_amount = (TextView) findViewById(R.id.name_of_person_holding_the_amount);
        name_of_person_holding_the_amount_phone_number = (TextView) findViewById(R.id.name_of_person_holding_the_amount_phone_number);

        save = (TextView) findViewById(R.id.save);



        current_fees_collection = (TextView) findViewById(R.id.current_fees_collection);
        total_of_fees_collected = (TextView) findViewById(R.id.total_of_fees_collected);



    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remittance_fees_deposited);
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

        } catch (JSONException e) {
            e.printStackTrace();
        }


        //--Text change listner of ammount of fees collected for total in total of fees collected
        amount_of_fees_deposited.addTextChangedListener(new TextWatcher() {
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

                double amountOfFeesCollected = Double.parseDouble(currentFeesCollectedString)-Double.parseDouble(amount_of_fees_deposited.getText().toString());

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
                        saveObject.put("amount_of_fees_deposited", amount_of_fees_deposited.getText().toString());
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

            rules.put("amount_of_fees_deposited",new CommonFunction().rule_required);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  rules;
    }

    public JSONObject fieldLabels()
    {
        JSONObject labels = new JSONObject();
        try {

            labels.put("amount_of_fees_deposited","Ammount Of Fees Deposited");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return labels;
    }
}
