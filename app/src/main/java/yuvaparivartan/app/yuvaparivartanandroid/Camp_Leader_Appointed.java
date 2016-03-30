package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import yuvaparivartan.app.yuvaparivartanandroid.dbhelpers.MDbHelper;

public class Camp_Leader_Appointed extends ActionBarActivity {

    private Spinner camp_leader,camp_leader_induction_provided;
    public TextView camp_leader_date, tempDate;
    private TextView state, district, block;

    private RelativeLayout add_newleader_button;
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


    public void commonInitialization() {
        context = Camp_Leader_Appointed.this;
        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

        camp_status = (TextView) findViewById(R.id.camp_status);
        village = (TextView) findViewById(R.id.village);
        camp_code = (TextView) findViewById(R.id.camp_code);
        camp_leader = (Spinner) findViewById(R.id.camp_leader);
        camp_leader_induction_provided= (Spinner) findViewById(R.id.camp_leader_induction_provided);
        state = (TextView) findViewById(R.id.state);
        district = (TextView) findViewById(R.id.district);
        block = (TextView) findViewById(R.id.block);

        camp_leader_date = (TextView) findViewById(R.id.camp_leader_date);

        add_newleader_button= (RelativeLayout) findViewById(R.id.add_newleadder_button);
        save = (TextView) findViewById(R.id.save);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campleaderappointed);
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
        new CommonFunction().setDependancy1(false, context, camp_leader, "camp_leader_profile", "name", null, null, null, null);
        new CommonFunction().setInformationInFormIfAvailable("camp", context);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.camp_introduction_provided, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        camp_leader_induction_provided.setAdapter(adapter);

        camp_leader_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(context, DatePickerM.class);
                in.putExtra("date", camp_leader_date.getText().toString());
                in.putExtra("min_date", String.valueOf(System.currentTimeMillis()));
                in.putExtra("update_Date", last_selecteddate);
                startActivityForResult(in, 1);
                tempDate = camp_leader_date;

            }
        });

        add_newleader_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommonFunction().whileAddFormRemoveSaveInforFromSharedPreferencce(context, "camp_leader_profile");

                Intent in = new Intent(Camp_Leader_Appointed.this,AddCampLeader.class);
                startActivity(in);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if(new CommonFunction().applyFormRules(rulesValidation(),fieldLabels(),true,context).equals("")==true) {
                        JSONObject saveObject = new JSONObject();
                        saveObject.put("camp_leader", new CommonFunction().getSelectedItemIdFromSpinner(camp_leader, "camp_leader_profile", context));
                        saveObject.put("camp_leader_date", camp_leader_date.getText().toString());
                        saveObject.put("camp_leader_induction_provided", camp_leader_induction_provided.getSelectedItem().toString().trim());

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

    @Override
    protected void onResume() {
        super.onResume();
//        new CommonFunction().setInformationInFormIfAvailable("camp", context);
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

            rules.put("camp_leader_date",new CommonFunction().rule_required);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  rules;
    }

    public JSONObject fieldLabels()
    {
        JSONObject labels = new JSONObject();
        try {

            labels.put("camp_leader_date","Date");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return labels;
    }

}
