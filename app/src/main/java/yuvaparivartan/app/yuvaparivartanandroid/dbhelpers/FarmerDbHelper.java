package yuvaparivartan.app.yuvaparivartanandroid.dbhelpers;

import android.content.Context;

import org.json.JSONArray;

import java.util.HashMap;

public class FarmerDbHelper {

    public static final String DATABASE_NAME = "mifos.db";
    public static final String table_name = "center";




    public static final String id = "id";
    public static final String center_id = "center_id";
    public static final String accountNo = "accountNo";
    public static final String name = "name";
    public static final String externalId = "externalId";
    public static final String officeId = "officeId";
    public static final String staffId = "staffId";
    public static final String staffName = "staffName";
    public static final String hierarchy = "hierarchy";

    public static final String calendar_id = "calendar_id";
    public static final String calendar_calendarInstanceId = "calendar_calendarInstanceId";
    public static final String calendar_entityId = "calendar_entityId";
    public static final String calendar_title = "calendar_title";
    public static final String calendar_description = "calendar_description";
    public static final String calendar_location = "calendar_location";
    public static final String collectionTotalDue = "collectionTotalDue";

    public static final String denomination_status = "denomination_status";

    public static final String interval = "interval";

    /*
     * status == null --- denomination pending
     * status == 1 --- denomination taken
     */


    JSONArray fieldsArray = new JSONArray();
    {
        fieldsArray.put(center_id);
        fieldsArray.put(accountNo);
        fieldsArray.put(name);
        fieldsArray.put(externalId);
        fieldsArray.put(officeId);
        fieldsArray.put(staffId);
        fieldsArray.put(staffName);
        fieldsArray.put(hierarchy);

        fieldsArray.put(calendar_id);
        fieldsArray.put(calendar_calendarInstanceId);
        fieldsArray.put(calendar_entityId);
        fieldsArray.put(calendar_title);
        fieldsArray.put(calendar_description);
        fieldsArray.put(calendar_location);
        fieldsArray.put(collectionTotalDue);
        fieldsArray.put(denomination_status);
        fieldsArray.put(interval);
    }


    private HashMap hp;


    CommonDbHelper commonDbHelper;
    Context context;

    public FarmerDbHelper(Context context)
    {
        this.context = context;
    }

    public CommonDbHelper getCommonDbHelper()
    {
        commonDbHelper = new CommonDbHelper(context,fieldsArray,table_name);
        return  commonDbHelper;
    }

}