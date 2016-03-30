package yuvaparivartan.app.yuvaparivartanandroid.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class CommonDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mifos.db";
    public String table_name = "";




    public static final String id = "id";


    JSONArray fieldsArray = new JSONArray();



    private HashMap hp;



    Context context;
    private String createTableString;
    private JSONArray fieldsArray1;




    public CommonDbHelper(Context context)
    {
        //versionm
        super(context, DATABASE_NAME, null, 2045);
        this.context = context;
    }


    public CommonDbHelper(Context context,JSONArray fieldsArray,String table_name)
    {
        //versionm
        super(context, DATABASE_NAME, null, 2045);
        this.context = context;
        this.fieldsArray = fieldsArray;
        this.table_name = table_name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub


        FarmerDbHelper farmerDbHelper = new FarmerDbHelper(context);


        fieldsArray1 = farmerDbHelper.fieldsArray;
        createTableString = "create table " + farmerDbHelper.table_name + " (";
        try
        {
            createTableString = createTableString + id + " integer primary key,";
            for(int i=0;i<fieldsArray1.length();i++)
            {
                if(i != fieldsArray1.length()-1) {
                    createTableString = createTableString + fieldsArray1.getString(i) + " text, ";
                }
                else
                {
                    createTableString = createTableString + fieldsArray1.getString(i) + " text ";
                }
            }

            createTableString = createTableString + ")";

        } catch (Exception e) {

        }
        db.execSQL(createTableString);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        FarmerDbHelper farmerDbHelper = new FarmerDbHelper(context);

        db.execSQL("DROP TABLE IF EXISTS " + farmerDbHelper.table_name + "");


        onCreate(db);
    }




    public void deleteAllTables()
    {
        FarmerDbHelper farmerDbHelper = new FarmerDbHelper(context);

        deleteAllM(farmerDbHelper.table_name);

    }


    public boolean insert  (JSONObject jobj)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        JSONArray names = jobj.names();

        try {
            for(int i=0;i<names.length();i++)
            {
                String dd = names.getString(i);
                contentValues.put(names.getString(i), jobj.getString(names.getString(i)));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            long iim = db.insertOrThrow(table_name, null, contentValues);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    public JSONObject getData(int idM){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + table_name + " where id=" + idM + "", null);


        JSONObject jobj = new JSONObject();
        try
        {
            while(res.isAfterLast() == false){

                jobj.put(id, res.getString(res.getColumnIndex(id)));
                for(int i=0;i<fieldsArray.length();i++) {
                    jobj.put(fieldsArray.getString(i), res.getString(res.getColumnIndex(fieldsArray.getString(i))));
                }

                res.moveToNext();
            }
        }
        catch (Exception e)
        {

        }

        return jobj;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, table_name);
        return numRows;
    }

    public boolean update (Integer id, JSONObject jobj)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        JSONArray names = jobj.names();

        try {
            for(int i=0;i<names.length();i++)
            {
                String dd = names.getString(i);
                contentValues.put(names.getString(i), jobj.getString(names.getString(i)));
            }
        }
        catch (Exception e)
        {}

        db.update(table_name, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteOne (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table_name,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer deleteOneWhere (String where)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table_name,
                where,null);
    }

    public JSONArray getAll(String condition)
    {
        JSONArray jarray = new JSONArray();
        try {
            //hp = new HashMap();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from "+table_name+" "+condition+"", null );
            res.moveToFirst();

            JSONObject jobjSingle = new JSONObject();


            while(res.isAfterLast() == false){

                jobjSingle = new JSONObject();
                jobjSingle.put(id, res.getString(res.getColumnIndex(id)));
                for(int i=0;i<fieldsArray.length();i++) {
                    String sss = fieldsArray.getString(i);
                    String ss = res.getString(res.getColumnIndex(fieldsArray.getString(i)));
                    jobjSingle.put(fieldsArray.getString(i), res.getString(res.getColumnIndex(fieldsArray.getString(i))));
                }

                jarray.put(jobjSingle);

                res.moveToNext();
            }
        }
        catch (Exception e)
        {
            Log.d("e", e.toString());
        }

        return jarray;
    }

    public JSONArray getFirstRecord()
    {
        JSONArray jarray = new JSONArray();
        try {
            //hp = new HashMap();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from "+table_name+" "+"ORDER BY id ASC LIMIT 1"+"", null );
            res.moveToFirst();

            JSONObject jobjSingle = new JSONObject();


            while(res.isAfterLast() == false){

                jobjSingle.put(id, res.getString(res.getColumnIndex(id)));
                for(int i=0;i<fieldsArray.length();i++) {
                    jobjSingle.put(fieldsArray.getString(i), res.getString(res.getColumnIndex(fieldsArray.getString(i))));
                }
                jarray.put(jobjSingle);

                res.moveToNext();
            }
        }
        catch (Exception e)
        {

        }

        return jarray;
    }

    public void deleteAll()
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from "+ table_name);
        }
        catch (Exception e)
        {

        }
    }

    public void deleteAllM(String table_namem)
    {
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from "+ table_namem);
        }
        catch (Exception e)
        {

        }
    }
}