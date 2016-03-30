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

import yuvaparivartan.app.yuvaparivartanandroid.SpAdapter;
import yuvaparivartan.app.yuvaparivartanandroid.dbhelpers.MDbHelper;


public class AddCampFacilitator extends ActionBarActivity {

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


    public void commonInitialization()
    {
        context = AddCampFacilitator.this;
        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

        name = (EditText) findViewById(R.id.name);
        address = (EditText) findViewById(R.id.address);
        course = (Spinner) findViewById(R.id.course);

        save = (TextView) findViewById(R.id.save);
        take_image = (TextView) findViewById(R.id.take_image);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_camp_facilitator);
        commonInitialization();

        new CommonFunction().setDependancy1(false, context, course, "course", "name", null, null, null, null);
        new CommonFunction().setInformationInFormIfAvailable("camp_facilitator", context);



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject saveObject = new JSONObject();
                    saveObject.put("name", name.getText().toString());
                    saveObject.put("address", address.getText().toString());
                    saveObject.put("course", new CommonFunction().getSelectedItemIdFromSpinner(course, "course", context));

                    // new CommonFunction().saveInformation(context, "camp_facilitator", saveObject);

                    new CommonFunction().saveInformation(context, "camp_facilitator", saveObject);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        take_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(new CommonFunction().isDetailSavedOnce(context,"camp_facilitator")==true)
                    {

                        sharededitor.putString(new CommonFunction().take_image_table_name, "camp_facilitator");
                        sharededitor.putString(new CommonFunction().take_image_column_name, "photo");
                        sharededitor.putString(new CommonFunction().take_image_row_id, new CommonFunction().getCurrentTableInformationIfAvailable(context,"camp_facilitator").getString("id"));
                        sharededitor.commit();

                        Intent in = new Intent(AddCampFacilitator.this,TakeOrPickImage.class);
                        startActivity(in);
                    }
                    else
                    {
                        new CommonFunction().showAlertDialog("Please save information first","",context);
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
//            if(data != null && data.getExtras() != null && data.getExtras().containsKey("date")==true) {
//                tempDate.setText(data.getExtras().getString("date"));
//            }
        }
    }


}
