package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Objects;

import yuvaparivartan.app.yuvaparivartanandroid.dbhelpers.MDbHelper;


public class CampFacilitatorM extends ActionBarActivity {


    private TextView state, district, block;



    public TextView camp_start_date, tentative_end_date, actual_end_date, tempDate;
    SpAdapter adapter;
    private Context context;
    private String last_selecteddate;
    private ArrayAdapter<String> spinnerAdapter;
    private JSONArray stateListArray;
    private JSONArray districtListArray;
    private TextView camp_status;
    private TextView save;
    private TextView village;


    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor sharededitor;
    private TextView camp_facilitator_date;
    private Spinner camp_facilitator;
    private JSONObject agendaInfo;
    private TextView add_new_facilitator;

    public void commonInitialization()
    {
        context = CampFacilitatorM.this;


        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();



        state = (TextView) findViewById(R.id.state);
        district = (TextView) findViewById(R.id.district);
        block = (TextView) findViewById(R.id.block);
        village = (TextView) findViewById(R.id.village);
        camp_status = (TextView) findViewById(R.id.camp_status);
        camp_facilitator_date = (TextView) findViewById(R.id.camp_facilitator_date);
        camp_facilitator = (Spinner) findViewById(R.id.camp_facilitator);


        add_new_facilitator = (TextView) findViewById(R.id.add_new_facilitator);

        save = (TextView) findViewById(R.id.save);

    }


    @Override
    protected void onResume() {
        super.onResume();
//        new CommonFunction().setDependancy1(false, context, camp_facilitator, "camp_facilitator", "name", null, null, null, null);
//        new CommonFunction().setInformationInFormIfAvailable("camp", context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campfacilitator);
        commonInitialization();

        //--get camp information from agenda camp id and set it on sharedpreferance
        try {
//            agendaInfo = new JSONObject(sharedpreferences.getString(new CommonFunction().getSheredPreferanceDetailsKeyForTable("agenda"), ""));
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

        new CommonFunction().setDependancy1(false, context, camp_facilitator, "camp_facilitator", "name", null, null, null, null);
        new CommonFunction().setInformationInFormIfAvailable("camp", context);

        add_new_facilitator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommonFunction().whileAddFormRemoveSaveInforFromSharedPreferencce(context,"camp_facilitator");
                Intent in = new Intent(CampFacilitatorM.this,AddCampFacilitator.class);
                startActivityForResult(in, 2);
            }
        });

        camp_facilitator_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(context, DatePickerM.class);
                in.putExtra("date", camp_facilitator_date.getText().toString());
                in.putExtra("min_date", String.valueOf(System.currentTimeMillis()));
                in.putExtra("update_Date", last_selecteddate);
                tempDate = camp_facilitator_date;

                startActivityForResult(in, 1);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if(new CommonFunction().applyFormRules(rulesValidation(),fieldLabels(),true,context).equals("")==true) {
                        JSONObject saveObject = new JSONObject();
                        saveObject.put("camp_facilitator", new CommonFunction().getSelectedItemIdFromSpinner(camp_facilitator, "camp_facilitator", context));
                        saveObject.put("camp_facilitator_date", camp_facilitator_date.getText().toString());

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == 1)
        {
            if(data != null && data.getExtras() != null && data.getExtras().containsKey("date")==true) {
                tempDate.setText(data.getExtras().getString("date"));
            }
        }
        if(requestCode == 2)
        {
            new CommonFunction().setDependancy1(false, context, camp_facilitator, "camp_facilitator", "name", null, null, null, null);
        }
    }


    public JSONObject rulesValidation()
    {
        JSONObject rules = new JSONObject();
        try {

            rules.put("camp_facilitator_date",new CommonFunction().rule_required);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  rules;
    }

    public JSONObject fieldLabels()
    {
        JSONObject labels = new JSONObject();
        try {

            labels.put("camp_facilitator_date","Camp Facilitator Date");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return labels;
    }


}
