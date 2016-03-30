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
import org.json.JSONException;
import org.json.JSONObject;


public class AddCampLeader extends ActionBarActivity {

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
    private EditText name;
    private EditText address;
    private TextView save;
    private Spinner course;
    private TextView take_image,title_inpage;
    private Spinner area_manager;
    private Spinner camp_coordinator;
    private EditText age;


    public void commonInitialization()
    {
        context = AddCampLeader.this;
        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

        name = (EditText) findViewById(R.id.name);

        area_manager = (Spinner) findViewById(R.id.area_manager);
        camp_coordinator = (Spinner) findViewById(R.id.camp_coordinator);

        address = (EditText) findViewById(R.id.address);
        age = (EditText) findViewById(R.id.age);

        save = (TextView) findViewById(R.id.save);
        take_image = (TextView) findViewById(R.id.take_image);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_camp_leader);
        commonInitialization();

        new CommonFunction().setDependancy1(false, context, area_manager, "area_manager_profile", "name", null, null, null, null);
        new CommonFunction().setDependancy1(true, context, camp_coordinator, "camp_coordinator_profile", "name", area_manager, "area_manager", "area_manager_profile", null);
        new CommonFunction().setInformationInFormIfAvailable("camp_leader_profile", context);



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    JSONObject saveObject = new JSONObject();
                    saveObject.put("name", name.getText().toString());
                    saveObject.put("area_manager", new CommonFunction().getSelectedItemIdFromSpinner(area_manager, "area_manager_profile", context));
                    saveObject.put("camp_coordinator", new CommonFunction().getSelectedItemIdFromSpinner(camp_coordinator, "camp_coordinator_profile", context));
                    saveObject.put("address", address.getText().toString());
                    saveObject.put("age", age.getText().toString());

                    // new CommonFunction().saveInformation(context, "camp_facilitator", saveObject);

                    new CommonFunction().saveInformation(context, "camp_leader_profile", saveObject);

                    new CommonFunction().showAlertDialog(new CommonFunction().data_saved_successfully, "", context);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        take_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (new CommonFunction().isDetailSavedOnce(context, "camp_facilitator") == true) {

                        sharededitor.putString(new CommonFunction().take_image_table_name, "camp_facilitator");
                        sharededitor.putString(new CommonFunction().take_image_column_name, "photo");
                        sharededitor.putString(new CommonFunction().take_image_row_id, new CommonFunction().getCurrentTableInformationIfAvailable(context, "camp_facilitator").getString("id"));
                        sharededitor.commit();

                        Intent in = new Intent(AddCampLeader.this, TakeOrPickImage.class);
                        startActivity(in);
                    } else {
                        new CommonFunction().showAlertDialog("Please save information first", "", context);
                    }
                } catch (Exception e) {
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
//            if(data != null && data.getExtras() != null && data.getExtras().containsKey("date")==true) {
//                tempDate.setText(data.getExtras().getString("date"));
//            }
        }
    }

    public JSONObject rulesValidation()
    {
        JSONObject rules = new JSONObject();
        try {

            rules.put("name",new CommonFunction().rule_required);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  rules;
    }

    public JSONObject fieldLabels()
    {
        JSONObject labels = new JSONObject();
        try {

            labels.put("name","Name");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return labels;
    }

}
