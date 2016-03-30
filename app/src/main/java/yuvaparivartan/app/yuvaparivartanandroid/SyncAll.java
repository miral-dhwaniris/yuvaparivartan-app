package yuvaparivartan.app.yuvaparivartanandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yuvaparivartan.app.yuvaparivartanandroid.app.AppController;
import yuvaparivartan.app.yuvaparivartanandroid.dbhelpers.MDbHelper;

public class SyncAll {


    public String formName = "";

    public boolean reload = false;

    private String response_string = "";
    private JSONArray responseArray = new JSONArray();
    private JSONObject responseObject = new JSONObject() ;



    Context context;
    public ProgressDialog pDialog;
    String from = "";
    private JSONObject DetailObjects;
    private JSONArray question_answerArrayDb;
    private JSONArray farmerArrayDb;
    private JSONArray riskProfillingArrayDb;
    private JSONObject responseObjectFrom;


    public void syncInBackground(final Context context,ProgressDialog pDialog, final String from)
    {
        this.context = context;
        this.pDialog = pDialog;
        this.from = from;

//        syncFarmers();

        syncTables();


    }

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor sharededitor;

    public void commonInitialization(Context context)
    {
        sharedpreferences = context.getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();
    }

    public JSONObject appendWhereCondition(Context context)
    {
        JSONObject whereObject = new JSONObject();
        JSONObject whereArray = new JSONObject();


        JSONObject getVariables = new JSONObject();
        try {
            commonInitialization(context);

            JSONObject loginInfo =  new JSONObject(sharedpreferences.getString("login_info",""));

            whereObject = new JSONObject();
            whereObject.put("camp_coordinator",loginInfo.getJSONObject("data").getString("camp_coordinator"));
            getVariables.put("agenda", whereObject);

            whereObject = new JSONObject();
            whereObject.put("camp_coordinator",loginInfo.getJSONObject("data").getString("camp_coordinator"));
            getVariables.put("camp", whereObject);

            whereObject = new JSONObject();
            whereObject.put("camp_coordinator",loginInfo.getJSONObject("data").getString("camp_coordinator"));
            getVariables.put("meet_key_person", whereObject);

            whereObject = new JSONObject();
            whereObject.put("camp_coordinator",loginInfo.getJSONObject("data").getString("camp_coordinator"));
            getVariables.put("door_to_door_meeting", whereObject);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return getVariables;
    }

    public ArrayList<String> getTableList()
    {
        ArrayList<String> tableList = new ArrayList<String>();
        tableList.add("agenda");
        tableList.add("camp");
        tableList.add("activities");
        tableList.add("state");
        tableList.add("district");
        tableList.add("block");
        tableList.add("camp_facilitator");
        tableList.add("courses");
        tableList.add("area_manager_profile");
        tableList.add("camp_leader_profile");
        tableList.add("camp_coordinator_profile");
        tableList.add("meet_key_person");
        tableList.add("community_meeting");
        tableList.add("door_to_door_meeting");


        return tableList;
    }





    ArrayList<String> tableList = getTableList();
    int tableListCunter = 0;
    ArrayList<String> responseErrorTables = new ArrayList<String>();

    public void syncTables()
    {

        final String tableName = tableList.get(tableListCunter);

        try {
            String tag_string_req = "string_req";

            String timeStampString = "";
            String extrAttributeString = "";

            if(new MDbHelper().getTimestampOfTable(tableName, context).equals("") == false)
            {
                timeStampString = timeStampString + "&timestamp="+new MDbHelper().getTimestampOfTable(tableName,context);
            }

            String controllerName = tableName.replace("_","-");
            String url = context.getString(R.string.bas_url)+"site/datalist&table_name="+tableName+""+timeStampString+extrAttributeString;


            if(pDialog==null) {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            final ProgressDialog finalPDialog = pDialog;



            //-----

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {



                    try {

                        response = response.replace("\n","");




//                        JsonFactory jsonFactory = new JsonFactory();
//                        JsonParser jsonParser = f.createJsonParser(your_json_string);
//                        jsonParser.nextToken();
//                        List<Ad> adList = new ArrayList<Ad>();
//                        while (jp.nextToken() == JsonToken.START_OBJECT)) {
//                            Ad ad = mapper.readValue(jsonParser, Ad.class);
//                            adList.add(ad);
//                        }



                        responseObject = new JSONObject(response);



                        JSONArray datammm1 = new MDbHelper().getAll(tableName,"",context);

                        new MDbHelper().insertData(context, responseObject);


                        JSONArray datammm = new MDbHelper().getAll(tableName,"",context);

                        if(tableListCunter<tableList.size()-1)
                        {
                            tableListCunter++;
                            syncTables();
                        }
                        else
                        {
                            pDialog.cancel();
                            if(from.equals("login")==true)
                            {
                                Intent in = new Intent(context,HomePageActivity.class);
                                in.putExtra("fromlogin",true);
                                context.startActivity(in);
                                ((Activity)context).finish();

                            }
                            else
                            {
                                new CommonFunction().showAlertDialog("Successfully sync.","",context);
                            }
                        }


                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.cancel();
                    new CommonFunction().showAlertDialog("there is some problem with pull please try again.", "", context);

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();


                    if(appendWhereCondition(context).has(tableName)==true)
                    {
                        try {
                            params.put("search_params", appendWhereCondition(context).get(tableName).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //                params.put("password", "Ghogha@123");

                    return params;
                }

            };



            strReq.setRetryPolicy(new DefaultRetryPolicy(2 * 60 * 1000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(strReq, "1");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void syncFarmers()
    {
        try {
            String tag_string_req = "string_req";

            String timeStampString = "";

            if(new MDbHelper().getTimestampOfTable("farmer",context).equals("") == false)
            {
                timeStampString = timeStampString + "&timestamp="+new MDbHelper().getTimestampOfTable("farmer",context);
            }


            String url = context.getString(R.string.bas_url)+"farmer/datalist"+timeStampString;


            if(pDialog==null) {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            final ProgressDialog finalPDialog = pDialog;
            StringRequest strReq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {



                    try {

                        response = response.replace("\n","");
                        responseObject = new JSONObject(response);





                        new MDbHelper().insertData(context, responseObject);



//                        if(from.equals("push") ==true)
//                        {
//                            pDialog.cancel();
////                            JSONArray farmerArrayDb1 = new MDbHelper().getAll("farmer", "", context);
////                            for(int jm = 0;jm<farmerArrayDb.length();jm++)
////                            {
////                                try {
////                                    JSONObject idmSingle = farmerArrayDb1.getJSONObject(jm);
////                                    updateUploadStatus(idmSingle.getString("id"), context);
////                                } catch (JSONException e) {
////                                    e.printStackTrace();
////                                }
////                            }
////
////                            finalPDialog.cancel();
////
//                            new CommonFunction().showAlertDialog("Successfully sync.","",context);
//
//                        }
//                        else {
//                            syncQuestionStructure();
//                        }


                        syncRiskprofillingList();

//                        syncQuestionStructure();


//                        if(from.equals("login")==true)
//                        {
//                            Intent in = new Intent(context,Dashboard.class);
//                            context.startActivity(in);
//                            ((Activity)context).finish();
//                        }



//                        JSONArray allData = new MDbHelper().getAll("farmer","",context);
//                        JSONArray timestampArray =  new MDbHelper().getAll("tmstamp", "", context);
//                        Log.d("allData",allData.toString());




                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    VolleyLog.d("errorM", "Error: " + error.getMessage());
                    finalPDialog.cancel();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //                params.put("email", "miralbhalani@gmail.com");
                    //                params.put("password", "Ghogha@123");

                    return params;
                }

            };

            AppController.getInstance().addToRequestQueue(strReq, "1");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    public void syncRiskprofillingList()
    {
        try {
            String tag_string_req = "string_req";

            String timeStampString = "";

            if(new MDbHelper().getTimestampOfTable("risk_profilling",context).equals("") == false)
            {
                timeStampString = timeStampString + "&timestamp="+new MDbHelper().getTimestampOfTable("risk_profilling",context);
            }


            String url = context.getString(R.string.bas_url)+"risk-profilling/datalist"+timeStampString;


            if(pDialog==null) {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            final ProgressDialog finalPDialog = pDialog;
            StringRequest strReq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {



                    try {

                        response = response.replace("\n","");
                        responseObject = new JSONObject(response);





                        new MDbHelper().insertData(context, responseObject);

                        syncQuestionAnswersList();



//                        if(from.equals("push") ==true)
//                        {
//                            pDialog.cancel();
////                            JSONArray farmerArrayDb1 = new MDbHelper().getAll("farmer", "", context);
////                            for(int jm = 0;jm<farmerArrayDb.length();jm++)
////                            {
////                                try {
////                                    JSONObject idmSingle = farmerArrayDb1.getJSONObject(jm);
////                                    updateUploadStatus(idmSingle.getString("id"), context);
////                                } catch (JSONException e) {
////                                    e.printStackTrace();
////                                }
////                            }
////
////                            finalPDialog.cancel();
////
//                            new CommonFunction().showAlertDialog("Successfully sync.","",context);
//
//                        }
//                        else {
//                            syncQuestionStructure();
//                        }


//                        if(from.equals("login")==true)
//                        {
//                            Intent in = new Intent(context,Dashboard.class);
//                            context.startActivity(in);
//                            ((Activity)context).finish();
//                        }



                        JSONArray allData = new MDbHelper().getAll("risk_profilling","",context);
//                        JSONArray timestampArray =  new MDbHelper().getAll("tmstamp", "", context);
                        Log.d("allData", allData.toString());




                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("errorM", "Error: " + error.getMessage());
                    finalPDialog.cancel();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //                params.put("email", "miralbhalani@gmail.com");
                    //                params.put("password", "Ghogha@123");

                    return params;
                }

            };

            AppController.getInstance().addToRequestQueue(strReq, "1");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public void syncQuestionAnswersList()
    {
        try {
            String tag_string_req = "string_req";

            String timeStampString = "";

            if(new MDbHelper().getTimestampOfTable("question_answers",context).equals("") == false)
            {
                timeStampString = timeStampString + "&timestamp="+new MDbHelper().getTimestampOfTable("question_answers",context);
            }


            String url = context.getString(R.string.bas_url)+"question-answers/datalist"+timeStampString;


            if(pDialog==null) {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            final ProgressDialog finalPDialog = pDialog;
            StringRequest strReq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {



                    try {

                        response = response.replace("\n","");
                        responseObject = new JSONObject(response);





                        new MDbHelper().insertData(context, responseObject);



                        if(from.equals("push") ==true)
                        {
                            pDialog.cancel();
//                            JSONArray farmerArrayDb1 = new MDbHelper().getAll("farmer", "", context);
//                            for(int jm = 0;jm<farmerArrayDb.length();jm++)
//                            {
//                                try {
//                                    JSONObject idmSingle = farmerArrayDb1.getJSONObject(jm);
//                                    updateUploadStatus(idmSingle.getString("id"), context);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            finalPDialog.cancel();
//
                            new CommonFunction().showAlertDialog("Successfully sync.","",context);

                        }
                        else {
                            syncQuestionStructure();
                        }


//                        if(from.equals("login")==true)
//                        {
//                            Intent in = new Intent(context,Dashboard.class);
//                            context.startActivity(in);
//                            ((Activity)context).finish();
//                        }



                        JSONArray allData = new MDbHelper().getAll("question_answers","",context);
//                        JSONArray timestampArray =  new MDbHelper().getAll("tmstamp", "", context);
                        Log.d("allData", allData.toString());




                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("errorM", "Error: " + error.getMessage());
                    finalPDialog.cancel();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //                params.put("email", "miralbhalani@gmail.com");
                    //                params.put("password", "Ghogha@123");

                    return params;
                }

            };

            AppController.getInstance().addToRequestQueue(strReq, "1");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void syncQuestionStructure()
    {
        try {
            String tag_string_req = "string_req";




            String url = context.getString(R.string.bas_url)+"question-structure/datalist";


            if(pDialog==null) {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            final ProgressDialog finalPDialog = pDialog;
            StringRequest strReq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {



                    try {

                        response = response.replace("\n","");
                        responseObject = new JSONObject(response);





                        new MDbHelper().insertData(context, responseObject);



                        syncOptions();



                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("errorM", "Error: " + error.getMessage());
                    finalPDialog.cancel();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //                params.put("email", "miralbhalani@gmail.com");
                    //                params.put("password", "Ghogha@123");

                    return params;
                }

            };

            AppController.getInstance().addToRequestQueue(strReq, "1");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public void syncOptions()
    {
        try {
            String tag_string_req = "string_req";




            String url = context.getString(R.string.bas_url)+"options/datalist";


            if(pDialog==null) {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            final ProgressDialog finalPDialog = pDialog;
            StringRequest strReq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        response = response.replace("\n","");
                        responseObject = new JSONObject(response);

                        new MDbHelper().insertData(context, responseObject);
                        JSONArray farmers = new MDbHelper().getAll("farmer","",context);
                        JSONArray question_structure = new MDbHelper().getAll("question_structure","",context);
                        JSONArray options = new MDbHelper().getAll("options","",context);

                        pDialog.cancel();
                        if(from.equals("login")==true)
                        {
                            Intent in = new Intent(context,HomePageActivity.class);
                            in.putExtra("fromlogin",true);
                            context.startActivity(in);
                            ((Activity)context).finish();

                        }
                        else
                        {
                            new CommonFunction().showAlertDialog("Successfully sync.","",context);
                        }


//                        else if(from.equals("push") ==true)
//                        {
//                            JSONArray farmerArrayDb1 = new MDbHelper().getAll("farmer", "", context);
//                            for(int jm = 0;jm<farmerArrayDb.length();jm++)
//                            {
//                                try {
//                                    JSONObject idmSingle = farmerArrayDb1.getJSONObject(jm);
//                                    updateUploadStatus(idmSingle.getString("id"), context);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }



//                        JSONArray allData = new MDbHelper().getAll("farmer","",context);
//                        JSONArray timestampArray =  new MDbHelper().getAll("tmstamp", "", context);
//                        Log.d("allData",allData.toString());




                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("errorM", "Error: " + error.getMessage());
                    finalPDialog.cancel();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //                params.put("email", "miralbhalani@gmail.com");
                    //                params.put("password", "Ghogha@123");

                    return params;
                }

            };

            AppController.getInstance().addToRequestQueue(strReq, "1");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void push(final Context context)
    {
        try {
            String tag_string_req = "string_req";


            DetailObjects = new JSONObject();

//            JSONArray farmerArray = new JSONArray();
//            JSONArray FarmerArrayFromDb = new MDbHelper().getAll("farmer", "", context);
//            for (int i=0;i<FarmerArrayFromDb.length();i++)
//            {
//                JSONObject farmerObject = new JSONObject();
//                JSONObject farmerObject1 = new JSONObject();
//                farmerObject1 = FarmerArrayFromDb.getJSONObject(i);
//                farmerObject.put("Farmer",farmerObject1);
//                farmerArray.put(farmerObject);
//            }



            farmerArrayDb =  new CommonFunction().addAllDetails(context, "farmer","");

            DetailObjects.put("Farmer", farmerArrayDb);

            question_answerArrayDb =  new CommonFunction().addAllDetails(context, "question_answers","");

            DetailObjects.put("QuestionAnswers", question_answerArrayDb);


            String url = context.getString(R.string.bas_url)+"farmer/submit";


            if(pDialog==null) {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            final ProgressDialog finalPDialog = pDialog;
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    finalPDialog.cancel();
                    response = response.replace("\n", "");





                    new MDbHelper().deleteAll("question_answers", "", context);
                    new MDbHelper().deleteAll("farmer", " WHERE id LIKE '%mid%'", context);
                    syncInBackground(context, null, "push");


                    Toast.makeText(context, "Push Successfully", Toast.LENGTH_LONG);


//                    if(response.equals("1")==true)
//                    {
//                        new CommonFunction().showAlertDialog("Push Successfully.","",context);
//                    }


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("errorM", "Error: " + error.getMessage());

                    new CommonFunction().showAlertDialog("Push failed. please try again with push data on dashboard", "", context);

                    finalPDialog.cancel();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("body", DetailObjects.toString());

                    return params;
                }

            };

            strReq.setRetryPolicy(new DefaultRetryPolicy( 2 * 60 * 1000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(strReq, "1");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }










    public JSONObject getPushTableList()
    {
        JSONObject mainArray = new JSONObject();
        try {
            JSONObject jobjSingle = new JSONObject();
            JSONArray jarrayM = new JSONArray();



            jarrayM = new JSONArray();

            //--SingleModuleOfSubOftable
            jobjSingle = new JSONObject();
            jobjSingle.put("table_name","camp");
            jobjSingle.put("column_name","camp_facilitator");
            jarrayM.put(jobjSingle);

            mainArray.put("camp_facilitator", jarrayM);


            //--SingleModuleOfSubOftable
            jobjSingle = new JSONObject();
            jobjSingle.put("table_name","camp");
            jobjSingle.put("column_name","camp_leader");
            jarrayM.put(jobjSingle);

            mainArray.put("camp_leader_profile", jarrayM);

            mainArray.put("camp", new JSONArray());
            mainArray.put("meet_key_person", new JSONArray());
            mainArray.put("community_meeting", new JSONArray());
            mainArray.put("door_to_door_meeting", new JSONArray());
            mainArray.put("agenda", new JSONArray());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return mainArray;
    }


    //llrru


    final JSONObject pushTableList = getPushTableList();
    JSONArray pushTableListArray = pushTableList.names();
    int pi = 0;

//    JSONObject responseObjectFromFarmers = new JSONObject();
    JSONObject responseObjectM = new JSONObject();
    public void pushTables(final Context context)
    {
        try {


            JSONArray mArrayDb = new JSONArray();

            final String tableName = pushTableListArray.getString(pi);
            String controllerName = tableName.replace("_","-");
            String capitalTableName = "";

            String[] SplitedM  = tableName.split("_");
            for (String splitMM : SplitedM) {
                String upperString = splitMM.substring(0,1).toUpperCase() + splitMM.substring(1);
                capitalTableName = capitalTableName + upperString;
            }




            //miralmmmmg
            try {
                String tag_string_req = "string_req";


                DetailObjects = new JSONObject();
                mArrayDb =  new CommonFunction().addAllDetails(context, tableName, " where upload_status=''");

                DetailObjects.put("table_name",tableName);
                DetailObjects.put("model_name", new CommonFunction().modelNameFromTableName(tableName));
                DetailObjects.put(capitalTableName, mArrayDb);

                String url = context.getString(R.string.bas_url)+"site/submit";


                if(pDialog==null) {
                    pDialog = new ProgressDialog(context);
                    pDialog.setMessage("Loading...");
                    pDialog.setCancelable(false);
                    pDialog.show();
                }

                final ProgressDialog finalPDialog = pDialog;
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        response = response.replace("\n", "");



                        try {

                            responseObjectM = new JSONObject();
                            try {
                                responseObjectM = new JSONObject(response);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                responseObjectM = new JSONObject();
                            }



                            //-----Update Farmer Datails
                            JSONArray jarray = new JSONArray();
                            JSONArray namesm = responseObjectM.names();
                            if(namesm == null)
                            {
                                namesm = new JSONArray();
                            }
                            for(int i=0;i<namesm.length();i++)
                            {
                                String key = namesm.getString(i);

                                JSONObject jobj = new JSONObject();
                                if(key.contains("mid")==true)
                                {
                                    jobj.put("mid",key.replace("mid", ""));
                                }
                                jobj.put("id",responseObjectM.getString(key));
                                jobj.put("upload_status","1");
                                jarray.put(jobj);


                            }
                            new MDbHelper().updateSomeOne(tableName, jarray, context);



                            JSONArray subtableArray = pushTableList.getJSONArray(tableName);



                            for(int pis=0;pis<subtableArray.length();pis++)
                            {
                                JSONObject subtableArraySingle = subtableArray.getJSONObject(pis);
                                //--curram
                                String dependantTableName = subtableArraySingle.getString("table_name");
                                String dependantTableColumnName = subtableArraySingle.getString("column_name");;
                                //-----Update risk_profilling
                                for(int i=0;i<namesm.length();i++)
                                {
                                    String key = namesm.getString(i);

                                    if(key.contains("mid")==true)
                                    {
                                        JSONObject jobj = new JSONObject();
                                        jobj.put(dependantTableColumnName, responseObjectM.getString(key));
                                        jobj.put("upload_status", "");
                                        new MDbHelper().updateSomeOneOnly(dependantTableName, jobj, context, ""+dependantTableColumnName+"='" + key + "'");
                                    }
                                }

                            }



//                            pushQuestionAnswers(context);

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();

                        }

                        if(pi<pushTableListArray.length()-1) {
                            pi++;
                            pushTables(context);
                        }
                        else
                        {

                            finalPDialog.cancel();
                            new SyncAll().syncInBackground(context, null, "SyncALl");
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("errorM", "Error: " + error.getMessage());

                        finalPDialog.cancel();

                        new CommonFunction().showAlertDialog("Push failed. please try again with push data on dashboard", "", context);

                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("body", DetailObjects.toString());

                        return params;
                    }

                };

                strReq.setRetryPolicy(new DefaultRetryPolicy(2 * 60 * 1000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                AppController.getInstance().addToRequestQueue(strReq, "1");

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    JSONObject responseObjectFromFarmers = new JSONObject();
    public void pushFarmers(final Context context)
    {
        try {
            String tag_string_req = "string_req";


            DetailObjects = new JSONObject();

//            JSONArray farmerArray = new JSONArray();
//            JSONArray FarmerArrayFromDb = new MDbHelper().getAll("farmer", "", context);
//            for (int i=0;i<FarmerArrayFromDb.length();i++)
//            {
//                JSONObject farmerObject = new JSONObject();
//                JSONObject farmerObject1 = new JSONObject();
//                farmerObject1 = FarmerArrayFromDb.getJSONObject(i);
//                farmerObject.put("Farmer",farmerObject1);
//                farmerArray.put(farmerObject);
//            }



            farmerArrayDb =  new CommonFunction().addAllDetails(context, "farmer", " where upload_status=''");

            DetailObjects.put("Farmer", farmerArrayDb);

//            question_answerArrayDb =  new CommonFunction().addAllDetails(context, "question_answers");
//
//            DetailObjects.put("QuestionAnswers", question_answerArrayDb);


            String url = context.getString(R.string.bas_url)+"farmer/submit-farmers";


            if(pDialog==null) {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            final ProgressDialog finalPDialog = pDialog;
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    response = response.replace("\n", "");


                    try {
                            responseObjectFromFarmers = new JSONObject(response);


                            //-----Update Farmer Datails
                            JSONArray jarray = new JSONArray();
                            JSONArray namesm = responseObjectFromFarmers.names();
                            for(int i=0;i<namesm.length();i++)
                            {
                                String key = namesm.getString(i);

                                if(key.contains("mid")==true)
                                {

                                    JSONObject jobj = new JSONObject();
                                    jobj.put("mid",key.replace("mid", ""));
                                    jobj.put("id",responseObjectFromFarmers.getString(key));
                                    jobj.put("upload_status","1");
                                    jarray.put(jobj);

                                }
                            }
                            new MDbHelper().updateSomeOne("farmer",jarray,context);




                            //-----Update risk_profilling
                            for(int i=0;i<namesm.length();i++)
                            {
                                String key = namesm.getString(i);

                                if(key.contains("mid")==true)
                                {
                                    JSONObject jobj = new JSONObject();
                                    jobj.put("farmer_id", responseObjectFromFarmers.getString(key));
                                    new MDbHelper().updateSomeOneOnly("risk_profilling", jobj, context, "farmer_id='" + key + "'");
                                }
                            }


                            //-----Update QuestionAnswers
                            for(int i=0;i<namesm.length();i++)
                            {
                                String key = namesm.getString(i);

                                if(key.contains("mid")==true)
                                {
                                    JSONObject jobj = new JSONObject();
                                    jobj.put("farmer_id", responseObjectFromFarmers.getString(key));
                                    new MDbHelper().updateSomeOneOnly("question_answers",jobj, context,"farmer_id='"+key+"'");
                                }
                            }


                        pushRiskProfilling(context);
//                            pushQuestionAnswers(context);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                          pushRiskProfilling(context);

//                        pushQuestionAnswers(context);
                    }



//                    new MDbHelper().deleteAll("question_answers", "", context);
//                    new MDbHelper().deleteAll("farmer", " WHERE id LIKE '%mid%'", context);


//                    syncInBackground(context, null, "push");


//                    Toast.makeText(context,"Push Successfully",Toast.LENGTH_LONG);


//                    if(response.equals("1")==true)
//                    {
//                        new CommonFunction().showAlertDialog("Push Successfully.","",context);
//                    }


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("errorM", "Error: " + error.getMessage());

                    finalPDialog.cancel();

                    new CommonFunction().showAlertDialog("Push failed. please try again with push data on dashboard", "", context);

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("body", DetailObjects.toString());

                    return params;
                }

            };

            strReq.setRetryPolicy(new DefaultRetryPolicy( 2 * 60 * 1000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(strReq, "1");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }







    JSONObject responseObjectFromRiskProfilling = new JSONObject();
    public void pushRiskProfilling(final Context context)
    {
        try {
            String tag_string_req = "string_req";


            DetailObjects = new JSONObject();

//            JSONArray farmerArray = new JSONArray();
//            JSONArray FarmerArrayFromDb = new MDbHelper().getAll("farmer", "", context);
//            for (int i=0;i<FarmerArrayFromDb.length();i++)
//            {
//                JSONObject farmerObject = new JSONObject();
//                JSONObject farmerObject1 = new JSONObject();
//                farmerObject1 = FarmerArrayFromDb.getJSONObject(i);
//                farmerObject.put("Farmer",farmerObject1);
//                farmerArray.put(farmerObject);
//            }



            //miralmmm
            riskProfillingArrayDb =  new CommonFunction().addAllDetails(context, "risk_profilling", " where upload_status='' and risk_profiling_status='1'");

            DetailObjects.put("RiskProfilling", riskProfillingArrayDb);

//            question_answerArrayDb =  new CommonFunction().addAllDetails(context, "question_answers");
//
//            DetailObjects.put("QuestionAnswers", question_answerArrayDb);


            String url = context.getString(R.string.bas_url)+"farmer/submit-risk-profilling";


            if(pDialog==null) {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            final ProgressDialog finalPDialog = pDialog;
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    response = response.replace("\n", "");


                    try {
                        responseObjectFrom = new JSONObject(response);


                        //-----Update RiskProfilling Datails
                        JSONArray jarray = new JSONArray();
                        JSONArray namesm = responseObjectFrom.names();
                        for(int i=0;i<namesm.length();i++)
                        {
                            String key = namesm.getString(i);

                            if(key.contains("mid")==true)
                            {

                                JSONObject jobj = new JSONObject();
                                jobj.put("mid",key.replace("mid", ""));
                                jobj.put("id",responseObjectFrom.getString(key));
                                jobj.put("upload_status","1");
                                jarray.put(jobj);

                            }
                        }
                        new MDbHelper().updateSomeOne("risk_profilling",jarray,context);

                        //-----Update QuestionAnswers
                        for(int i=0;i<namesm.length();i++)
                        {
                            String key = namesm.getString(i);

                            if(key.contains("mid")==true)
                            {
                                JSONObject jobj = new JSONObject();
                                jobj.put("risk_profilling_id", responseObjectFrom.getString(key));
                                new MDbHelper().updateSomeOneOnly("question_answers",jobj, context,"risk_profilling_id='"+key+"'");
                            }
                        }


                        pushQuestionAnswers(context);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        pushQuestionAnswers(context);
                    }



//                    new MDbHelper().deleteAll("question_answers", "", context);
//                    new MDbHelper().deleteAll("farmer", " WHERE id LIKE '%mid%'", context);


//                    syncInBackground(context, null, "push");


//                    Toast.makeText(context,"Push Successfully",Toast.LENGTH_LONG);


//                    if(response.equals("1")==true)
//                    {
//                        new CommonFunction().showAlertDialog("Push Successfully.","",context);
//                    }


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("errorM", "Error: " + error.getMessage());

                    finalPDialog.cancel();

                    new CommonFunction().showAlertDialog("Push failed. please try again with push data on dashboard","",context);

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("body", DetailObjects.toString());

                    return params;
                }

            };

            strReq.setRetryPolicy(new DefaultRetryPolicy( 2 * 60 * 1000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(strReq, "1");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    public void pushQuestionAnswers(final Context context)
    {
        try {
            String tag_string_req = "string_req";


            DetailObjects = new JSONObject();

//            JSONArray farmerArray = new JSONArray();
//            JSONArray FarmerArrayFromDb = new MDbHelper().getAll("farmer", "", context);
//            for (int i=0;i<FarmerArrayFromDb.length();i++)
//            {
//                JSONObject farmerObject = new JSONObject();
//                JSONObject farmerObject1 = new JSONObject();
//                farmerObject1 = FarmerArrayFromDb.getJSONObject(i);
//                farmerObject.put("Farmer",farmerObject1);
//                farmerArray.put(farmerObject);
//            }



            question_answerArrayDb =  new CommonFunction().addAllDetails(context, "question_answers"," where upload_status=''");

            DetailObjects.put("QuestionAnswers", question_answerArrayDb);


            String url = context.getString(R.string.bas_url)+"farmer/submit-question-answers";


            if(pDialog==null) {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            final ProgressDialog finalPDialog = pDialog;
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    finalPDialog.cancel();
                    response = response.replace("\n", "");

                    try {

                        responseObject = new JSONObject(response);


                        //-----Update QuestionAnswers
                        JSONArray jarray = new JSONArray();
                        JSONArray namesm = responseObject.names();
                        for(int i=0;i<namesm.length();i++)
                        {
                            String key = namesm.getString(i);

                            JSONObject jobj = new JSONObject();
                            jobj.put("mid",key);
                            jobj.put("id",responseObject.getString(key));
                            jobj.put("upload_status","1");
                            jarray.put(jobj);

                        }
                        new MDbHelper().updateSomeOne("question_answers",jarray, context);


                        new SyncAll().syncInBackground(context, null, "push");

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("errorM", "Error: " + error.getMessage());

                    new CommonFunction().showAlertDialog("Push failed. please try again with push data on dashboard","",context);

                    finalPDialog.cancel();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("body", DetailObjects.toString());

                    return params;
                }

            };

            strReq.setRetryPolicy(new DefaultRetryPolicy( 2 * 60 * 1000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            AppController.getInstance().addToRequestQueue(strReq, "1");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public void updateUploadStatus(String farmerId,Context context)
    {
        try {
            JSONObject mObj = new JSONObject();
            mObj.put("table_name","farmer");
            mObj.put("timestamp","");


            JSONObject labels = new JSONObject();
            labels.put("upload_status","upload_status");
            mObj.put("labels",labels);


            JSONArray data = new JSONArray();
            JSONObject singleData = new JSONObject();


            //SingleData
            singleData = new JSONObject();

            singleData.put("upload_status","1");
            singleData.put("id", farmerId);



            data.put(singleData);

            mObj.put("data", data);


            new MDbHelper().insertData(context, mObj);


//            refreshUI(farmerInformation.getString("id"));



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }








}
