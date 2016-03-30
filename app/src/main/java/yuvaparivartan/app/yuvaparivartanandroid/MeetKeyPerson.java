package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;


public class MeetKeyPerson extends ActionBarActivity {


    private Spinner state, district, block;
    public TextView meeting_date,tempDate;
    SpAdapter adapter;
    private Context context;
    private String last_selecteddate;
    private ArrayAdapter<String> spinnerAdapter;
    private JSONArray stateListArray;
    private JSONArray districtListArray;
    private Spinner role;
    private TextView save;
    private EditText village;


    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor sharededitor;
    private Spinner camp_coordinator;
    private EditText name_of_person;
    private EditText phone_number;

    public void commonInitialization()
    {
        context = MeetKeyPerson.this;


        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

        state = (Spinner) findViewById(R.id.state);
        district = (Spinner) findViewById(R.id.district);
        block = (Spinner) findViewById(R.id.block);
        village = (EditText) findViewById(R.id.village);
        camp_coordinator = (Spinner) findViewById(R.id.camp_coordinator);
        meeting_date = (TextView) findViewById(R.id.meeting_date);
        name_of_person = (EditText) findViewById(R.id.name_of_person);
        phone_number = (EditText) findViewById(R.id.phone_number);
        role = (Spinner) findViewById(R.id.role);

        save = (TextView) findViewById(R.id.save);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meet_key_person);
        commonInitialization();

        //--SpinnerInitialization and dependancy specification
        new CommonFunction().setDependancy1(false,context, state,"state","state",null,null,null,null);
        new CommonFunction().setDependancy1(true, context, district, "district", "district", state, "state", "state", block);
        new CommonFunction().setDependancy1(true, context, block, "block", "block", district, "district", "district", null);


        new CommonFunction().setDependancy1(false, context, camp_coordinator, "camp_coordinator_profile", "name", null, null, null, null);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.meet_key_person_role, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role.setAdapter(adapter);


        meeting_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(context, DatePickerM.class);
                in.putExtra("date", meeting_date.getText().toString());
                in.putExtra("min_date", String.valueOf(System.currentTimeMillis()));
                in.putExtra("update_Date", last_selecteddate);
                startActivityForResult(in, 1);
                tempDate = meeting_date;

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
                        saveObject.put("meeting_date", meeting_date.getText().toString());
                        saveObject.put("name_of_person", name_of_person.getText().toString());
                        saveObject.put("role", role.getSelectedItem().toString());
                        saveObject.put("phone_number", phone_number.getText().toString());

                        new CommonFunction().saveInformation(context,"meet_key_person",saveObject);


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

            rules.put("meeting_date",new CommonFunction().rule_required);
            rules.put("name_of_person",new CommonFunction().rule_required);
            rules.put("phone_number",new CommonFunction().rule_required);

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
