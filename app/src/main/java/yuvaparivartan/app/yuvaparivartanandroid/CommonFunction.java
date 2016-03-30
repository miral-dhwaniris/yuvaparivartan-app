package yuvaparivartan.app.yuvaparivartanandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import yuvaparivartan.app.yuvaparivartanandroid.dbhelpers.MDbHelper;

public class CommonFunction {



    public  String data_saved_successfully = "Data saved successfully";

    public String camp_details = "camp_details";
    public String agenda_info = "agenda_info";

    public String take_image_table_name = "take_image_table_name";
    public String take_image_column_name = "take_image_column_name";
    public String take_image_row_id = "take_image_row_id";


    public String _save_info = "_save_info";


    //---ValidationTypes
    public String rule_required = "required";
    public String rule_phone_number = "phone_number";
    public String rule_email = "email";
    public String rule_min_length = "min_length";
    public String rule_max_length = "max_length";
    //---


    public String getCurrentDateInFormat()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public void setAgendaDone(Context context)
    {
        try {
            JSONObject saveObject = new JSONObject();
            saveObject.put("status", "1");
            new CommonFunction().saveInformation(context, "agenda", saveObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String modelNameFromTableName(String tableName)
    {
        String[] tablenames = tableName.split("_");
        String tableNameCapital = "";
        for(int j=0;j<tablenames.length;j++)
        {
            tableNameCapital = tableNameCapital
                    + tablenames[j].substring(0,1).toUpperCase() + tablenames[j].substring(1).toLowerCase();
        }

        return tableNameCapital;
    }
    public void whileAddFormRemoveSaveInforFromSharedPreferencce(Context context,String tableName)
    {
        commonInitialization(context);
        sharededitor.remove(new CommonFunction().getSheredPreferanceDetailsKeyForTable(tableName));
        sharededitor.commit();
    }


    public String applyFormRules(JSONObject rules,JSONObject fieldLabels,boolean withDialog,Context context)
    {
        String validtionString = "";
        JSONArray rulesArray = rules.names();

        for(int i = 0;i<rulesArray.length();i++)
        {
            try {

                String ruleID = rulesArray.getString(i);
                String ruleValue = rules.getString(rulesArray.getString(i));
                String valueInField = "";

                String fieldLabel = "";
                if(fieldLabels.has(ruleID)==true)
                {
                    fieldLabel = fieldLabels.getString(ruleID);
                }
                else
                {
                    String[] ruleArray = ruleID.split("_");
                    for(int ri=0;ri<ruleArray.length;ri++)
                    {
                        String capStrng = ruleArray[ri].substring(0, 1).toUpperCase() + " " + ruleArray[ri].substring(1);
                        fieldLabel = fieldLabel+capStrng;
                    }
                }


                Object obj = context.getResources().getIdentifier(ruleID, "id", context.getPackageName());

                View mView = null;
                if(obj != null) {
                    int resId = (int) obj;
                    mView = (View) ((Activity) context).findViewById(resId);

                    if(mView instanceof EditText)
                    {
                        EditText mViewEdit = (EditText) mView;
                        valueInField = mViewEdit.getText().toString();
                    }
                    if(mView instanceof TextView)
                    {
                        TextView mViewEdit = (TextView) mView;
                        valueInField = mViewEdit.getText().toString();
                    }

                }



                if(ruleValue.equals(new CommonFunction().rule_required) && valueInField.equals("")==true)
                {
                    validtionString = "\n";
                    validtionString = validtionString + fieldLabel + " is required field";
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(validtionString.equals("")==false) {
            new CommonFunction().showAlertDialog(validtionString, "", context);
        }

        return  validtionString;
    }

    public void setInformationInFormIfAvailable(String tableName,Context context)
    {
        commonInitialization(context);
        try {

            String[] columnNames =  new MDbHelper().getColumnNames(tableName,context);

            JSONObject tableDetailsObject = new JSONObject(sharedpreferences.getString(new CommonFunction().getSheredPreferanceDetailsKeyForTable(tableName),""));
            if(tableDetailsObject!=null && tableDetailsObject.has("id")==true) {
                JSONArray tableDetailsObjectArray = new MDbHelper().getAll(tableName, " where id='" + tableDetailsObject.getString("id") + "'", context);
                if(tableDetailsObjectArray.length()>0)
                {
                    tableDetailsObject = tableDetailsObjectArray.getJSONObject(0);
                }
            }

            if(tableDetailsObject!=null && tableDetailsObject.length()>0) {
                for (String columnName : columnNames) {

                    try {
                        Object obj = context.getResources().getIdentifier(columnName, "id", context.getPackageName());

                        if(obj != null)
                        {
                            int resId = (int) obj;
                            View mView = (View) ((Activity) context).findViewById(resId);

                            if(mView!=null)
                            {
                                if ((mView instanceof TextView || mView instanceof EditText)) {

                                    String textValueForSpinner = tableDetailsObject.getString(columnName);
                                    if(mView.getTag()!= null && mView.getTag().toString()!=null && mView.getTag().toString().contains("fromspinner")==true)
                                    {
                                        JSONArray reArray =  new MDbHelper().getAll(mView.getTag().toString().split("-")[1]," where mid='"+tableDetailsObject.getString(columnName)+"'",context);
                                        if(reArray.length()!=0)
                                        {
                                            textValueForSpinner = reArray.getJSONObject(0).getString(mView.getTag().toString().split("-")[2]);
                                        }
                                    }


                                    String key_name = columnName;
                                    if(key_name.contains("date")==true && textValueForSpinner.contains("-")==true) {
                                        textValueForSpinner = convertDateFormat("yyyy-MM-dd","dd-MM-yyyy",textValueForSpinner);
                                    }


                                    ((TextView) mView).setText(textValueForSpinner);
                                }
                                if ((mView instanceof Spinner)) {
                                    String idInSpinner = tableDetailsObject.getString(columnName);


                                    String spinnerTableName = "";
                                    if(((Spinner) mView).getTag()!=null && ((Spinner) mView).getTag().toString()!=null && ((Spinner) mView).getTag().toString().contains("fromspinner")==true)
                                    {
                                        String[] splitedSpinnertagString = ((Spinner) mView).getTag().toString().split("-");
                                        spinnerTableName = splitedSpinnertagString[1];
                                    }
                                    if(idInSpinner.equals("")==false) {
                                        new CommonFunction().setSelectedItemIdToSpinner(((Spinner) mView), spinnerTableName, idInSpinner, context);
                                    }

                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public boolean isDetailSavedOnce(Context context,String tableName)
    {
        commonInitialization(context);
        if(sharedpreferences.contains(getSheredPreferanceDetailsKeyForTable(tableName))==true)
        {
            return true;
        }
        return false;
    }

    public JSONObject getCurrentTableInformationIfAvailable(Context context,String tableName)
    {
        try {
            commonInitialization(context);
            if(sharedpreferences.contains(getSheredPreferanceDetailsKeyForTable(tableName))==true)
            {
                JSONObject jobjM = new JSONObject(sharedpreferences.getString(getSheredPreferanceDetailsKeyForTable(tableName), ""));
                return  jobjM;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }


    public String convertDateFormat(String dateFrom,String dateTo,String dateToConvert)
    {

        try {
            String date = dateToConvert;
            SimpleDateFormat sdf = new SimpleDateFormat(dateFrom);
            Date testDate = null;
            try {
                testDate = sdf.parse(date);
            }catch(Exception ex){
                ex.printStackTrace();
            }
            SimpleDateFormat formatter = new SimpleDateFormat(dateTo);
            String newFormat = formatter.format(testDate);
            return newFormat;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public void saveInformation(Context context,String tableName,JSONObject mobjInsert)
    {
        commonInitialization(context);
        String sharedpreferenceKeyForDetails = getSheredPreferanceDetailsKeyForTable(tableName);

        if(sharedpreferences.contains(sharedpreferenceKeyForDetails)==false) {
            JSONObject jobj = new MDbHelper().createBlankEntry(context, tableName, new JSONArray());
            if (jobj.length() != 0) {
                sharededitor.putString(sharedpreferenceKeyForDetails, jobj.toString());
                sharededitor.commit();
            }
        }


        try {
            JSONObject mObj = new JSONObject();
            mObj.put("table_name", tableName);
            mObj.put("timestamp", "");
            JSONObject labels = new JSONObject();

            //--Labels

            JSONArray mobjInsertNamesArray = mobjInsert.names();

            labels.put("upload_status", "upload_status");
            for(int i=0;i<mobjInsertNamesArray.length();i++)
            {
                labels.put(mobjInsertNamesArray.getString(i), mobjInsertNamesArray.getString(i));
            }

            mObj.put("labels", labels);
            JSONArray data = new JSONArray();
            JSONObject singleData = new JSONObject();
            singleData = new JSONObject();

            //--Datas

            for(int i=0;i<mobjInsertNamesArray.length();i++)
            {
                String key_name = mobjInsertNamesArray.getString(i);
                String valueString = mobjInsert.getString(mobjInsertNamesArray.getString(i));
                if(key_name.contains("date")==true && mobjInsert.getString(mobjInsertNamesArray.getString(i)).contains("-")==true)
                {
                    valueString = convertDateFormat("dd-MM-yyyy","yyyy-MM-dd",mobjInsert.getString(mobjInsertNamesArray.getString(i)));
                }

                singleData.put(mobjInsertNamesArray.getString(i), valueString);
            }

            singleData.put("upload_status", "");

            JSONObject blankentryDetails = new JSONObject(sharedpreferences.getString(sharedpreferenceKeyForDetails,""));
            singleData.put("id", blankentryDetails.getString("id"));

            data.put(singleData);
            mObj.put("data", data);
            Log.d("data", mObj.toString());
            new MDbHelper().insertData(context, mObj);

            JSONArray mm = new MDbHelper().getAll(tableName, "", context);

             Log.d("dfd", mm.toString());




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  String getSheredPreferanceDetailsKeyForTable(String tableName)
    {
        return  tableName+_save_info;
    }


    public void addCommonHeadersPost(HttpURLConnection con)
    {
        try
        {
//            application/x-www-form-urlencoded
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("POST");
            String USER_AGENT = "Mozilla/5.0";
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setDoOutput(true);
        }
        catch (Exception e)
        {

        }
    }

    public String measured_are = "measured_are";

    public String sure_to_logout = "Are you sure to logout?";


    public String opening_camera = "opening_camera";

    public String acuracyMessagebefore = "Multimedia is saved with GPS Accuracy of ";
    public String acuracyMessageafter = ". You can wait for better accuracy and take this Multimedia again.";

    public Boolean testing = false;
    public String selectedColor = "#f2f2f2";

    public String farmer_information = "farmer_information";

    public String cropInformation = "cropInformation";
    public String farmer_information_risk_profilling = "farmer_information_risk_profilling";
    public String farmer_information_temp_question_answer = "farmer_information_temp_question_answer";

    public String media_path = "media_path";

    public String alreadyTaken = "Already taken do you want to edit?";
    public String acuracyLevel = "Your current accuracy is low. Pleae change mode to high and wait for 5 min";
    public String dialogStringdoyouwanttoatakeimage = "Accuracy level is greater than 10 meters. Do you want to take coordinates? If your current accuracy is low, please change mode to high and wait for 5 minutes.";



    public String areyouSureToremoveCoordinate = "Are you sure to remove the coordinate info";


    public String currentAccuracy = "Current GPS accuracy (In Mts.) = ";




    public String getCurrentTimestamp()
    {
        Date date= new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(date.getTime());
        return timeStamp;
    }



    public String getActivityName(String activityKey,Context context)
    {
        String activityName = "";
        try {
            JSONArray ActivityName = new MDbHelper().getAll("activities"," where key='"+activityKey+"'",context);

            activityName = activityKey;
            if(ActivityName.length()>0)
            {
                activityName = ActivityName.getJSONObject(0).getString("name");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return activityName;
    }

    public String getLastKeyIncrementedFromJsonObject(JSONObject objectM)
    {

        JSONArray objectMNames =  objectM.names();

        if(objectMNames != null) {
            try {
                List<String> jsonValues = new ArrayList<String>();
                for (int i = 0; i < objectMNames.length(); i++) {
                    jsonValues.add(objectMNames.getString(i));
                }
                Collections.sort(jsonValues);
                objectMNames = new JSONArray(jsonValues);

                return String.valueOf(Integer.parseInt(objectMNames.getString(objectM.length() - 1)) + 1);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return  "1";
    }

    public String getNewImageNumberForQuestion(JSONObject questionStructure,JSONObject farmerInformationRiskProfilling,Context context)
    {
        JSONObject photo_lat_lngJobj = getCurrentPhotoLatLngInfoOfQuestion(questionStructure,farmerInformationRiskProfilling,context);
        String newKey = new CommonFunction().getLastKeyIncrementedFromJsonObject(photo_lat_lngJobj);
        return newKey;
    }


    public JSONObject getCurrentPhotoLatLngInfoOfQuestion(JSONObject questionStructure,JSONObject farmerInformationRiskProfilling,Context context)
    {
        JSONObject photo_lat_lngJobj = new JSONObject();

        try {
            JSONArray question_answerInformation = new MDbHelper().getAll("question_answers", " where question_id='" + questionStructure.getString("id") + "' and risk_profilling_id='" + farmerInformationRiskProfilling.getString("id") + "'", context);


            if(question_answerInformation.length()>0) {
                JSONObject singlequestion_answerInformation = question_answerInformation.getJSONObject(0);

                if (singlequestion_answerInformation.has("photo_lat_lngs") == true) {
                    photo_lat_lngJobj = new JSONObject(singlequestion_answerInformation.getString("photo_lat_lngs"));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return  photo_lat_lngJobj;
    }

    public JSONObject putIncrementedkeyLatLngtimestampForQuestionAnswers(JSONObject questionStructure,JSONObject farmerInformationRiskProfilling,String lat, String lng,Context context)
    {


        JSONObject photo_lat_lngJobj = new JSONObject();
        try {

            photo_lat_lngJobj = getCurrentPhotoLatLngInfoOfQuestion(questionStructure,farmerInformationRiskProfilling,context);

//            JSONArray question_answerInformation = new MDbHelper().getAll("question_answers", " where question_id='" + questionStructure.getString("id") + "' and risk_profilling_id='" + farmerInformationRiskProfilling.getString("id") + "'", context);
//
//
//            if(question_answerInformation.length()>0) {
//                JSONObject singlequestion_answerInformation = question_answerInformation.getJSONObject(0);
//
//                if (singlequestion_answerInformation.has("photo_lat_lngs") == true) {
//                    photo_lat_lngJobj = new JSONObject(singlequestion_answerInformation.getString("photo_lat_lngs"));
//                }
//            }

            JSONObject latlngs = new JSONObject();
            latlngs.put("lat",lat);
            latlngs.put("lng",lng);
            latlngs.put("timestamp", String.valueOf(System.currentTimeMillis()));


            String newKey = new CommonFunction().getLastKeyIncrementedFromJsonObject(photo_lat_lngJobj);

            photo_lat_lngJobj.put(newKey,latlngs);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return photo_lat_lngJobj;

    }


    public JSONObject putDefinedkeyLatLngtimestampForQuestionAnswers(JSONObject questionStructure,JSONObject farmerInformationRiskProfilling,String lat, String lng,Context context,String definedKey)
    {


        JSONObject photo_lat_lngJobj = new JSONObject();
        try {

            photo_lat_lngJobj = getCurrentPhotoLatLngInfoOfQuestion(questionStructure,farmerInformationRiskProfilling,context);

//            JSONArray question_answerInformation = new MDbHelper().getAll("question_answers", " where question_id='" + questionStructure.getString("id") + "' and risk_profilling_id='" + farmerInformationRiskProfilling.getString("id") + "'", context);
//
//
//            if(question_answerInformation.length()>0) {
//                JSONObject singlequestion_answerInformation = question_answerInformation.getJSONObject(0);
//
//                if (singlequestion_answerInformation.has("photo_lat_lngs") == true) {
//                    photo_lat_lngJobj = new JSONObject(singlequestion_answerInformation.getString("photo_lat_lngs"));
//                }
//            }

            JSONObject latlngs = new JSONObject();
            latlngs.put("lat",lat);
            latlngs.put("lng",lng);
            latlngs.put("timestamp", String.valueOf(System.currentTimeMillis()));

            photo_lat_lngJobj.put(definedKey,latlngs);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return photo_lat_lngJobj;

    }



    public int getphotolatlnglength(JSONObject questionSingleData, JSONObject farmerInformationRiskProfilling, Context context)
    {
        int photo_lat_lngslength = 0;
        JSONArray question_answerInformation = new JSONArray();
        try {
            question_answerInformation = new MDbHelper().getAll("question_answers", " where question_id='" + questionSingleData.getString("id") + "' and risk_profilling_id='" + farmerInformationRiskProfilling.getString("id") + "'", context);

            if(question_answerInformation.length()!=0) {
                photo_lat_lngslength = new JSONObject(question_answerInformation.getJSONObject(0).getString("photo_lat_lngs")).length();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  photo_lat_lngslength;
    }



    public void resizeImage(File mediaFile)
    {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath(), bmOptions);
            bitmap = new CommonFunction().scaleBitmap(bitmap);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);



            try {
                FileOutputStream fo = null;
                fo = new FileOutputStream(mediaFile);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public Bitmap scaleBitmap(Bitmap bm) {


//        int maxWidth = 5000;
//        int maxHeight = 5000;

        int maxWidth = 2592;
        int maxHeight = 2592;

        int width = bm.getWidth();
        int height = bm.getHeight();

        Log.v("Pictures", "Width and height are " + width + "--" + height);

        if (width > height) {
            // landscape
            if(width>maxWidth) {
                double ratio = width / maxWidth;
                width = maxWidth;
                height = (int) (height / ratio);
            }
        } else if (height > width) {
            // portrait
            if(height>maxHeight) {
                double ratio = height / maxHeight;
                height = maxHeight;
                width = (int) (width / ratio);
            }
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }


    public int getGreaterNumberImage(JSONObject farmerInformation,JSONObject questionSingleData,JSONObject farmerInformationRiskProfilling){

        // Check that the SDCard is mounted


        int greaterNumber = 0;

        String farmerFolderName = new CommonFunction().getFarmerFolderName(farmerInformation, farmerInformationRiskProfilling);

        File mediaStorageDir = null;
        try {

            String questionFirstWord = questionSingleData.getString("question").split(" ")[0];
            mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Crop360/"+farmerFolderName+"/QuestionImages/"+questionSingleData.getString("question_number")+"_"+questionFirstWord+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create the storage directory(MyCameraVideo) if it does not exist
        if (mediaStorageDir.exists()){
            for (File f : mediaStorageDir.listFiles()) {
                if (f.isFile()) {
                    String name = f.getName();

                    String[] namesSplit = name.split("_");
                    String Stringm = namesSplit[namesSplit.length - 1];
                    Stringm = Stringm.split("\\.")[0];

                    if(greaterNumber< Integer.parseInt(Stringm))
                    {
                        greaterNumber = Integer.parseInt(Stringm);
                    }
                    Log.d("name", name);
                }
            }
        }


        return greaterNumber;
    }


    public JSONArray getSerizlizedNames(JSONObject adapterVariable)
    {
        JSONArray adapterVariableNames = adapterVariable.names();


        try {
            List<String> jsonValues = new ArrayList<String>();
            for (int i = 0; i < adapterVariableNames.length(); i++)
                jsonValues.add(adapterVariableNames.getString(i));
            Collections.sort(jsonValues);
            adapterVariableNames = new JSONArray(jsonValues);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return  adapterVariableNames;
    }


    public String checkNeedToAddkeyInmMiddleSomewhere(JSONObject adapterVariable)
    {
        try {
            JSONArray adapterVariableNames =  new CommonFunction().getSerizlizedNames(adapterVariable);
            for(int i=0;i<adapterVariableNames.length();i++)
            {
                if(adapterVariableNames.getString(i).equals(String.valueOf((i + 1)))==false)
                {
                    adapterVariable.put(String.valueOf((i + 1)),new JSONObject());
                    return String.valueOf((i + 1));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return  "";
    }



    public int getSelectedItemWithoutResource(Context context,String itemName,String[] ArrayM)
    {
        int selectedItem  = 0;
//        freshFarmerData.getString("variety")
//        R.array.variety
        try {
            String[] dfdf = ArrayM;

            for(int mij=0;mij<dfdf.length;mij++)
            {
                if(dfdf[mij].equals(itemName)==true)
                {
                    selectedItem = mij;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return selectedItem;
    }

    public int getSelectedItem(Context context,String itemName,int resoursename)
    {
        int selectedItem  = 0;
//        freshFarmerData.getString("variety")
//        R.array.variety
        try {
            CharSequence[] dfdf = context.getResources().getTextArray(resoursename);

            for(int mij=0;mij<dfdf.length;mij++)
            {
                if(dfdf[mij].equals(itemName)==true)
                {
                    selectedItem = mij;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return selectedItem;
    }

    public void showGPSDisabledAlertToUser(final Context context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
//        Goto Settings Page To
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                ((Activity) context).startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void checkGps(Context context)
    {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            showGPSDisabledAlertToUser(context);
        }
    }


    public boolean checkGps1(Context context)
    {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return true;
        }else{
            return false;
        }
    }


    public String getFarmerFolderName(JSONObject farmerInformation,JSONObject farmerInformationRiskProfilling)
    {
        String farmerFolderName = "";
        try {
            farmerFolderName =  farmerFolderName + farmerInformation.getString("name");
            farmerFolderName =  farmerFolderName + "_" + farmerInformationRiskProfilling.getString("urn_no");
            farmerFolderName =  farmerFolderName + "_" + farmerInformationRiskProfilling.getString("date_of_survey");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  farmerFolderName;
    }


    public void setDependancy1(final boolean isDependantToSomeone,final Context context,final Spinner dependantSpinner, final String dependantTableName,final String dependantColumnToShow,final Spinner dependantToSpinner,final String columnInDependantForDependantTo,final String dependantToTableName,final Spinner dependantsDependant)
    {
        if(isDependantToSomeone == true)
        {
            dependantToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    new CommonFunction().setSpinnerAdapterWithDependancy(context, dependantSpinner, dependantTableName, dependantColumnToShow, dependantToSpinner, columnInDependantForDependantTo, dependantToTableName, true);
                    if(dependantsDependant != null && dependantSpinner.getSelectedItem() == null)
                    {
                        dependantsDependant.setAdapter(null);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        else
        {
            new CommonFunction().setSpinnerAdapterWithDependancy(context,dependantSpinner,dependantTableName,dependantColumnToShow,null,null,null,false);
        }
    }


    public void setSpinnerAdapterWithDependancy(Context context,Spinner spinnerToSet,String tableForSpinnerToSet,String columnNeedToShowInSpinnerToShow,Spinner SpinnerDependantToSpinner,String SpinnerDependantToColumn, String spinnerDependantToTable,boolean isSpinnerDependantToSomeone)
    {

        try
        {
            commonInitialization(context);
            if(isSpinnerDependantToSomeone == false) {
                JSONArray ListArray = new MDbHelper().getAll(tableForSpinnerToSet, "", context);
                spinnerToSet.setAdapter(new CommonFunction().getAdapterArray(ListArray, context, columnNeedToShowInSpinnerToShow));


                sharededitor.putString(tableForSpinnerToSet + "_spinner", ListArray.toString());
                sharededitor.commit();
            }
            else
            {
                JSONArray dependantArray = new JSONArray(sharedpreferences.getString(spinnerDependantToTable+"_spinner",""));
                String identification = dependantArray.getJSONObject(SpinnerDependantToSpinner.getSelectedItemPosition()).getString("id");
                JSONArray ListArray = new MDbHelper().getAll(tableForSpinnerToSet, " where " + SpinnerDependantToColumn + "='" + identification + "'", context);
                spinnerToSet.setAdapter(new CommonFunction().getAdapterArray(ListArray, context, columnNeedToShowInSpinnerToShow));


                sharededitor.putString(tableForSpinnerToSet + "_spinner", ListArray.toString());
                sharededitor.commit();
            }
            //---
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }


    public void setSelectedItemIdToSpinner(Spinner SpinnerM ,String mTable,String idOfTable,Context context)
    {
        commonInitialization(context);
        try {
            JSONArray dependantArray = new JSONArray(sharedpreferences.getString(mTable+"_spinner",""));

            for(int i=0;i<dependantArray.length();i++)
            {
                if(dependantArray.getJSONObject(i).has("id")==true && dependantArray.getJSONObject(i).getString("id").equals(idOfTable)==true)
                {
                    SpinnerM.setSelection(i);
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getSelectedItemIdFromSpinner(Spinner SpinnerDependantToSpinner ,String spinnerDependantToTable,Context context)
    {
        commonInitialization(context);
        try {
            JSONArray dependantArray = new JSONArray(sharedpreferences.getString(spinnerDependantToTable+"_spinner",""));
            String identification = dependantArray.getJSONObject(SpinnerDependantToSpinner.getSelectedItemPosition()).getString("id");
            return identification;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public String getSelectedItemCustomColumnFromSpinner(Spinner SpinnerDependantToSpinner ,String spinnerDependantToTable,Context context,String columnName)
    {
        commonInitialization(context);
        try {
            JSONArray dependantArray = new JSONArray(sharedpreferences.getString(spinnerDependantToTable+"_spinner",""));
            String identification = dependantArray.getJSONObject(SpinnerDependantToSpinner.getSelectedItemPosition()).getString(columnName);
            return identification;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }


    private JSONArray concatArray(JSONArray... arrs)
            throws JSONException {
        JSONArray result = new JSONArray();
        for (JSONArray arr : arrs) {
            for (int i = 0; i < arr.length(); i++) {
                result.put(arr.get(i));
            }
        }
        return result;
    }


    public ArrayAdapter<String> getAdapterArray(JSONArray marray,Context context ,String labelName)
    {
        String[] stateList = new CommonFunction().getStringArrayFromJsonArray(marray, context,labelName);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, stateList);

        return spinnerAdapter;
    }

    public String[] getStringArrayFromJsonArray(JSONArray JArray,Context context,String labelName)
    {
        String[] mArray = new String[0];
        try {

            mArray = new String[JArray.length()];
            for(int i=0;i<JArray.length();i++)
            {
                JSONObject JArraySingle = JArray.getJSONObject(i);
                mArray[i] = JArraySingle.getString(labelName);
            }
            return mArray;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return mArray;
    }

    public String[] getTableArrayArrayWithoutFirstIndexNull(String tableName,Context context,String whereClausse)
    {
        String[] mArray = new String[0];
        try {

            JSONArray JArray = new MDbHelper().getAll(tableName, whereClausse, context);

            mArray = new String[JArray.length()];
            for(int i=0;i<JArray.length();i++)
            {
                JSONObject JArraySingle = JArray.getJSONObject(i);
                mArray[i] = JArraySingle.getString("name");
            }
            return mArray;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return mArray;
    }

    public String[] getTableArrayArray(String tableName,Context context,String whereClausse)
    {
        String[] mArray = new String[0];
        try {

            JSONArray JArray = new MDbHelper().getAll(tableName, whereClausse, context);

            mArray = new String[JArray.length()+1];
            mArray[0] = "";
            for(int i=0;i<JArray.length();i++)
            {
                JSONObject JArraySingle = JArray.getJSONObject(i);
                mArray[i+1] = JArraySingle.getString("name");
            }
            return mArray;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return mArray;
    }


    public JSONArray addAllDetails(Context context,String tableName,String whereCondition)
    {

        JSONArray MArray = new JSONArray();
        try {

            String[] tablenames = tableName.split("_");
            String tableNameCapital = "";
            for(int j=0;j<tablenames.length;j++)
            {
                tableNameCapital = tableNameCapital
                        + tablenames[j].substring(0,1).toUpperCase() + tablenames[j].substring(1).toLowerCase();
            }


            JSONArray FarmerArrayFromDb = new MDbHelper().getAll(tableName, whereCondition , context);
            for (int i=0;i<FarmerArrayFromDb.length();i++)
            {
                JSONObject farmerObject = new JSONObject();
                JSONObject farmerObject1 = new JSONObject();
                farmerObject1 = FarmerArrayFromDb.getJSONObject(i);
                farmerObject.put(tableNameCapital,farmerObject1);
                MArray.put(farmerObject);
            }

            return MArray;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return MArray;
    }



    public String changeDateFormat(String dateM,String inputFormatM,String outputForamtM) {
        String inputPattern = inputFormatM;
        String outputPattern = outputForamtM;
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(dateM);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }



    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor sharededitor;
    public void commonInitialization(Context context)
    {
        sharedpreferences = context.getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();
    }

    public String alertMessage = "Alert";
    public String please_syncMessage = "Please Upload offline data";

    public void showAlertDialog(String Message,String title,Context context)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(Message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
//            .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public int getselectedIndex(List<String> list,String selected)
    {
        for(int i = 0;i<list.size();i++)
        {
            if(selected.equals(list.get(i))==true)
            {
                return i;
            }
        }
        return 0;
    }

    JSONArray jarray = new JSONArray();






    public String request_params = "request_params";
    public String murl = "murl";
    public String method = "method";

    String userCredentials = "appuser:appuser@15";
//    String userCredentials = "prasoon:prasoon";

    public String currentDateWithPlus()
    {
        String CurrentDatewithPlus = "" ;

        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();

        DateFormat date = new SimpleDateFormat("dd+MMMM+yyyy");
        CurrentDatewithPlus = date.format(currentLocalTime);

        Log.d("CurrentDatewithPlus", CurrentDatewithPlus);

        return CurrentDatewithPlus;
    }

    public String currentDateWithSpace()
    {

        String CurrentDatewithSpace = "" ;

        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();

        DateFormat date = new SimpleDateFormat("dd MMMM yyyy");
        CurrentDatewithSpace = date.format(currentLocalTime);

        Log.d("CurrentDatewithSpace", CurrentDatewithSpace);


        return  CurrentDatewithSpace;
    }

    public void addCommonHeadersPost(HttpsURLConnection con)
    {
        try
        {
            byte[] encode = userCredentials.getBytes();
            String basicAuth = "Basic " + new String(Base64.encode(encode, Base64.DEFAULT));
            con.setRequestProperty("Authorization", basicAuth);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("POST");
            String USER_AGENT = "Mozilla/5.0";
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setDoOutput(true);
        }
        catch (Exception e)
        {

        }
    }

    public void addCommonHeadersPostforImageUpload(HttpURLConnection con)
    {
        try
        {
            byte[] encode = userCredentials.getBytes();
            String basicAuth = "Basic " + new String(Base64.encode(encode, Base64.DEFAULT));
            con.setRequestProperty("Authorization", basicAuth);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestMethod("POST");
            String USER_AGENT = "Mozilla/5.0";
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setDoOutput(true);
        }
        catch (Exception e)
        {

        }
    }

    public void addCommonHeadersGet(HttpURLConnection con)
    {
        try
        {

            byte[] encode = userCredentials.getBytes();
            String basicAuth = "Basic " + new String(Base64.encode(encode, Base64.DEFAULT));
            con.setRequestProperty ("Authorization", basicAuth);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("GET");
            String USER_AGENT = "Mozilla/5.0";
            con.setRequestProperty("User-Agent", USER_AGENT);
        }
        catch (Exception e)
        {

        }
    }








    Context context = null;
    String versionNumberWeb = "";

    public void syncVersion(Context context)
    {
        this.context = context;
        new SyncVersion().execute();
    }


    class SyncVersion extends AsyncTask<Void,Void,Void>
    {

        public String problemString = "";

        private ProgressDialog barProgressDialog;
        private String status;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            barProgressDialog = new ProgressDialog(context);
//            barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            barProgressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {







            try
            {
                /*
                 * Calling Login api --- POST METHOD
                 * ssolanki1
                 * ssolanki
                 */
                //URL defination

                String url = "http://188.166.242.137/AppVersions/web/index.php?r=versions/get-version&app_id=crop360";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                //add common headers
//                new CommonFunction().addCommonHeadersPost(con);

                con.setRequestMethod("GET");
                String USER_AGENT = "Mozilla/5.0";
                con.setRequestProperty("User-Agent", USER_AGENT);
                con.setDoOutput(true);

                //add BODY parameters
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                String urlParameters = "";
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                //geting response code
                int responseCode = con.getResponseCode();

                if(responseCode == 200)
                {
                    // read response in bufferReader -- getting in response_string
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    String response_string = response.toString();
                    versionNumberWeb = response_string;

                    Log.d("versionNumber", versionNumberWeb);

                }


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                String version = pInfo.versionName;

                if(Float.parseFloat(version)< Float.parseFloat(versionNumberWeb))
                {
                    new AlertDialog.Builder(context)
                            .setTitle("Please download letest version of app")
                            .setMessage("")
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete

                                    String url = "https://play.google.com/store/apps/details?id=com.app.crop360&hl=en";
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    ((Activity) context).startActivity(i);
                                    dialog.cancel();

                                }
                            })
//            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }


}