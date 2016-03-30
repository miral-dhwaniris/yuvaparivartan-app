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


public class DoorToDoorMeeting extends ActionBarActivity {


    private Spinner state, district, block;
    public TextView meeting_date,tempDate;
    SpAdapter adapter;
    private Context context;
    private String last_selecteddate;
    private ArrayAdapter<String> spinnerAdapter;
    private JSONArray stateListArray;
    private JSONArray districtListArray;
    private TextView save;
    private EditText village;


    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor sharededitor;
    private Spinner camp_coordinator;
    private EditText number_of_households_visited;
    private EditText phone_number;
    private EditText number_of_target_groups_met;

    public void commonInitialization()
    {
        context = DoorToDoorMeeting.this;


        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

        state = (Spinner) findViewById(R.id.state);
        district = (Spinner) findViewById(R.id.district);
        block = (Spinner) findViewById(R.id.block);
        village = (EditText) findViewById(R.id.village);
        camp_coordinator = (Spinner) findViewById(R.id.camp_coordinator);
        meeting_date = (TextView) findViewById(R.id.meeting_date);
        number_of_households_visited = (EditText) findViewById(R.id.number_of_households_visited);
        number_of_target_groups_met = (EditText) findViewById(R.id.number_of_target_groups_met);

        save = (TextView) findViewById(R.id.save);

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.door_to_door_meeting);
        commonInitialization();

        //--SpinnerInitialization and dependancy specification
        new CommonFunction().setDependancy1(false,context, state,"state","state",null,null,null,null);
        new CommonFunction().setDependancy1(true, context, district, "district", "district", state, "state", "state", block);
        new CommonFunction().setDependancy1(true, context, block, "block", "block", district, "district", "district", null);


        new CommonFunction().setDependancy1(false, context, camp_coordinator, "camp_coordinator_profile", "name", null, null, null, null);


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
                        saveObject.put("number_of_households_visited", number_of_households_visited.getText().toString());
                        saveObject.put("number_of_target_groups_met", number_of_target_groups_met.getText().toString());

                        new CommonFunction().saveInformation(context,"door_to_door_meeting",saveObject);


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
            rules.put("number_of_households_visited",new CommonFunction().rule_required);
            rules.put("number_of_target_groups_met",new CommonFunction().rule_required);

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
