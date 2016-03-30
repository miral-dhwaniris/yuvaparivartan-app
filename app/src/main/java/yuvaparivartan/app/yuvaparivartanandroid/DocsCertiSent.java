package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yuvaparivartan.app.yuvaparivartanandroid.dbhelpers.MDbHelper;


public class DocsCertiSent extends ActionBarActivity {

    private EditText certificate_requisition;
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


    public void commonInitialization() {
        context = DocsCertiSent.this;
        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

        camp_status = (TextView) findViewById(R.id.camp_status);
        village = (TextView) findViewById(R.id.village);
        camp_code = (TextView) findViewById(R.id.camp_code);
        certificate_requisition = (EditText) findViewById(R.id.certificate_requisition);

        state = (TextView) findViewById(R.id.state);
        district = (TextView) findViewById(R.id.district);
        block = (TextView) findViewById(R.id.block);

        camp_start_date = (TextView) findViewById(R.id.camp_start_date);
        tentative_end_date = (TextView) findViewById(R.id.tentative_end_date);
        actual_end_date = (TextView) findViewById(R.id.actual_end_date);

        save = (TextView) findViewById(R.id.save);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.docs_certi_sent);
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if(new CommonFunction().applyFormRules(rulesValidation(),fieldLabels(),true,context).equals("")==true) {
                        JSONObject saveObject = new JSONObject();
                        saveObject.put("certificate_requisition", certificate_requisition.getText().toString().trim());
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

            rules.put("certificate_requisition",new CommonFunction().rule_required);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  rules;
    }

    public JSONObject fieldLabels()
    {
        JSONObject labels = new JSONObject();
        try {

            labels.put("certificate_requisition","Email");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return labels;
    }

}
