package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yuvaparivartan.app.yuvaparivartanandroid.dbhelpers.MDbHelper;


public class AllactivitiesPageActivity extends ActionBarActivity {

    private JSONArray all_activity_list;
    private Context context;
    JSONObject mainobject ;
    private ListView activitiesList;
    private AdapterCenters center_adapter;



    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor sharededitor;


    public void commonInitialization()
    {
        context = AllactivitiesPageActivity.this;


        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

    }



    String[] values = new String[] {"Village Networking - Meet key person",
                                    "Mobilization of students - Community Meeting",
                                    "Mobilization of students - Door to Door Meeting",
                                    "Mobilization of students - Enquiries Generated",
                                    "Arrange Infrastructure for camp", "Camp Details",
                                    "Identify Camp Facilitator",
                                    "Camp Leader Appointed",
                                    "Camp Details - Forms Filled",
                                    "Camp Details - Fees Collected",
                                    "Documentation - Certificate Requisition Sent",
                                    "Documentation - Voucher Submitted",
                                    "Camp Visit - Fees Collected",
                                    "Camp Visit - Attendance",
                                    "Camp Leader Training",
                                    "Remmittance - Fees Collected",
                                    "Remmittance - Fees Deposited",
                                    "Livelihood Documentation",
                                    "Livelihood Opportunnity Search & Tieup" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_activities_page);
        commonInitialization();
        context = AllactivitiesPageActivity.this;
        activitiesList = (ListView) findViewById(R.id.all_activities_list);


        TextView form_name = (TextView) findViewById(R.id.form_name);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("form_name") == true)
        {
            form_name.setText(getIntent().getExtras().getString("form_name"));
        }


       /* all_activity_list = new JSONArray();
        for (int i = 0; i < values.length; ++i) {
            try {
                mainobject =  new JSONObject();
                mainobject.put(Integer.toString(i), values[i]);
                all_activity_list.put(mainobject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/

        String whereclause = "";

        if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey("where_clause")==true)
        {
            whereclause = getIntent().getExtras().getString("where_clause");
        }
        else
        {
            whereclause = " where agenda_date>='"+new CommonFunction().getCurrentDateInFormat()+"'";
        }

        all_activity_list = new MDbHelper().getAll("agenda", whereclause, context);


        for(int i=0;i<all_activity_list.length();i++)
        {
            try {
                JSONObject allActivitiesListSingle =  all_activity_list.getJSONObject(i);
                all_activity_list.getJSONObject(i).put("label", new CommonFunction().getActivityName(allActivitiesListSingle.getString("activity"),context));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        if (all_activity_list.length() != 0) {
            center_adapter= new AdapterCenters();
            activitiesList.setAdapter(center_adapter);
        } else {
            new CommonFunction().showAlertDialog("No CreateAgenda(s) Available", "", context);
        }

        activitiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                String agendaStatus = "";
                boolean agendaDone = false;
                try {
                    if(all_activity_list.getJSONObject(position).has("status")==true && all_activity_list.getJSONObject(position).getString("status").equals("1")==true)
                    {
                        agendaDone = true;
                        agendaStatus = all_activity_list.getJSONObject(position).getString("status");
                    }
                    if(all_activity_list.getJSONObject(position).has("status")==true)
                    {
                        agendaStatus = agendaStatus = all_activity_list.getJSONObject(position).getString("status");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if(agendaDone == false && agendaStatus.equals("2")==false) {
                    try {
                        sharededitor.putString(new CommonFunction().getSheredPreferanceDetailsKeyForTable("agenda"), all_activity_list.getJSONObject(position).toString());
                        sharededitor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    JSONObject jobj = all_activity_list.optJSONObject(position);
                    String activity_name = jobj.optString("activity");


                    if (activity_name.equals("camp_start_details")) {
                        Intent in = new Intent(AllactivitiesPageActivity.this, CampDetails.class);
                        startActivity(in);
                    }

                    if (activity_name.equals("identify_camp_facilitator")) {
                        Intent in = new Intent(AllactivitiesPageActivity.this, CampFacilitatorM.class);
                        startActivity(in);
                    }

                    if (activity_name.equals("camp_leader_appointment")) {
                        Intent in = new Intent(AllactivitiesPageActivity.this, Camp_Leader_Appointed.class);
                        startActivity(in);
                    }

                    if (activity_name.equals("camp_details-number_of_form_filled")) {
                        Intent in = new Intent(AllactivitiesPageActivity.this, CampDetailsFormFilled.class);
                        startActivity(in);
                    }

                    if (activity_name.equals("camp_details-fees_collected")) {
                        Intent in = new Intent(AllactivitiesPageActivity.this, CampDetailsFeesCollected.class);
                        startActivity(in);
                    }

                    if (activity_name.equals("documentation-certification_requesition_sent")) {
                        Intent in = new Intent(AllactivitiesPageActivity.this, DocsCertiSent.class);
                        startActivity(in);
                    }

                    if (activity_name.equals("documentation-voucher_submited")) {
                        Intent in = new Intent(AllactivitiesPageActivity.this, DocsVoucherSubmitted.class);
                        startActivity(in);
                    }

                    if (activity_name.equals("camp_visit-fees_collected")) {
                        Intent in = new Intent(AllactivitiesPageActivity.this, CampDetailsFeesCollected.class);
                        startActivity(in);
                    }

                    if (activity_name.equals("remittance-ammount_of_fees_collected")) {
                        Intent in = new Intent(AllactivitiesPageActivity.this, CampDetailsFeesCollected.class);
                        startActivity(in);
                    }

                    if (activity_name.equals("remittance-fees_deposited")) {
                        Intent in = new Intent(AllactivitiesPageActivity.this, RemittanceFeesDeposited.class);
                        startActivity(in);
                    }

                    if (activity_name.equals("arrange_infrastructure_for_camp")) {
                        Intent in = new Intent(AllactivitiesPageActivity.this, ArrangeInfrastructureForCamp.class);
                        startActivity(in);
                    }

                    //---Non Camp Activity
                    if (activity_name.equals("meet_key_person")) {

                        sharededitor.remove(new CommonFunction().getSheredPreferanceDetailsKeyForTable("meet_key_person"));
                        sharededitor.commit();

                        Intent in = new Intent(AllactivitiesPageActivity.this, MeetKeyPerson.class);
                        startActivity(in);
                    }

                    if (activity_name.equals("community_meeting")) {

                        sharededitor.remove(new CommonFunction().getSheredPreferanceDetailsKeyForTable("community_meeting"));
                        sharededitor.commit();

                        Intent in = new Intent(AllactivitiesPageActivity.this, CommunityMeeting.class);
                        startActivity(in);
                    }
                    if (activity_name.equals("door_to_door_meeting")) {

                        sharededitor.remove(new CommonFunction().getSheredPreferanceDetailsKeyForTable("door_to_door_meeting"));
                        sharededitor.commit();

                        Intent in = new Intent(AllactivitiesPageActivity.this, DoorToDoorMeeting.class);
                        startActivity(in);
                    }
                }
                else
                {
                    if(agendaStatus == "1") {
                        new CommonFunction().showAlertDialog("Agenda is already completed", "", context);
                    }
                }


            }
        });


        activitiesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new CommonFunction().showAlertDialog("CreateAgenda is comleted", "", context);


                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                try {
                                    sharededitor.putString(new CommonFunction().getSheredPreferanceDetailsKeyForTable("agenda"), all_activity_list.getJSONObject(position).toString());
                                    sharededitor.commit();


                                    boolean editable = true;
                                    if(new JSONObject(sharedpreferences.getString(new CommonFunction().getSheredPreferanceDetailsKeyForTable("agenda"),"")).has("status")==true)
                                    {
                                        if(new JSONObject(sharedpreferences.getString(new CommonFunction().getSheredPreferanceDetailsKeyForTable("agenda"),"")).getString("status").equals("2")==true)
                                        {
                                            editable = false;
                                        }
                                    }


                                    if(editable == true) {
                                        JSONObject saveObject = new JSONObject();
                                        saveObject.put("status", "2");

                                        new CommonFunction().saveInformation(context, "agenda", saveObject);

                                        dialog.cancel();

                                        new CommonFunction().showAlertDialog("Agenda canceled successfully", "", context);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you realy want to cancel this agenda?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                return false;
            }
        });



    }

    class AdapterCenters extends BaseAdapter {

        private final LayoutInflater inflater;

        public AdapterCenters() {
            // TODO Auto-generated constructor stub
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = inflater.inflate(R.layout.existing_single_list, null);
            try {

                final JSONObject tempJObj = all_activity_list.getJSONObject(position);
                TextView sr_no = (TextView) convertView.findViewById(R.id.sr_no);
                TextView name = (TextView) convertView.findViewById(R.id.name_benificiary);
                TextView agenda_date = (TextView) convertView.findViewById(R.id.agenda_date);


                sr_no.setText(String.valueOf((position+1)));

                ImageView agenda_done = (ImageView) convertView.findViewById(R.id.agenda_done);
                if(tempJObj.optString("status").equals("1")==true)
                {
                    agenda_done.setVisibility(View.VISIBLE);
                }
                agenda_date.setText(tempJObj.optString("agenda_date"));
                name.setText(tempJObj.getString("label"));


                if(tempJObj.optString("status").equals("2")==true)
                {
                    LinearLayout mainback = (LinearLayout) convertView.findViewById(R.id.mainback);
                    mainback.setBackgroundColor(Color.parseColor("#cccccc"));
                }

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            return convertView;
        }

        @Override
        public int getCount() {
            return all_activity_list.length();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

}
