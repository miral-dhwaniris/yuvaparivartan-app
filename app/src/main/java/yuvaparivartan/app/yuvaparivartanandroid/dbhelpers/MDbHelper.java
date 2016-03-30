package yuvaparivartan.app.yuvaparivartanandroid.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MDbHelper {


    private SQLiteDatabase mydb;


    public String DatabaseName = "yuvaparivartan";


    public void recordTimeStampOfTable(String table_name,SQLiteDatabase mydb,String timestamp)
    {

        JSONArray mfields = new JSONArray();
        mfields.put("table_name");
        mfields.put("timestamp");

        createAnyTable("tmstamp", mfields, mydb);

        ContentValues contentValues = new ContentValues();
        contentValues.put("table_name",table_name);
        contentValues.put("timestamp", timestamp);

        int updateResult = mydb.update("tmstamp", contentValues, "table_name='" + table_name + "'", null);
        if(updateResult == 0)
        {
            mydb.insert("tmstamp",null,contentValues);
        }


    }



    public String getTimestampOfTable(String table_name,Context context)
    {

      JSONArray jarray = new MDbHelper().getAll("tmstamp", " where table_name='" + table_name + "'", context);

      if(jarray.length()>0)
      {
          try {
              return jarray.getJSONObject(0).getString("timestamp");
          } catch (JSONException e) {
              e.printStackTrace();
          }
      }

        return "";

    }



    public void createAnyTable(String table_name,JSONArray fields, SQLiteDatabase mydb)
    {
        try {
                //Creating table if not exist
                String createTableQueryString = "create table if not exists "
                        + table_name
                        + " (mid integer primary key";
                for (int i=0;i<fields.length();i++)
                {
                    createTableQueryString = createTableQueryString + ","+fields.getString(i)+" text";
                }
                createTableQueryString = createTableQueryString + ");";
                mydb.execSQL(createTableQueryString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    public void alterAnyTable(String table_name,JSONArray fields, SQLiteDatabase mydb)
    {
        try {
            //Alter Table
            Cursor mCursor = mydb.rawQuery("SELECT * FROM " + table_name + " LIMIT 0", null);
            if(mCursor!=null) {
                for (int i = 0; i < fields.length(); i++) {
                    if (mCursor.getColumnIndex(fields.getString(i)) == -1) {
                        //column not exist
                        mydb.execSQL("ALTER TABLE " + table_name + " ADD COLUMN " + fields.getString(i) + " text;");
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    public String[] getColumnNames(String table_name,Context context)
    {

        mydb = context.openOrCreateDatabase(DatabaseName, Context.MODE_PRIVATE, null);
        try {
            Cursor c = mydb.rawQuery("SELECT * FROM " + table_name + " WHERE 0", null);
            String[] columnNames = c.getColumnNames();
            return columnNames;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        mydb.close();

        return  null;
    }


    public void insertData(Context context,JSONObject responseObject)
    {

        mydb = context.openOrCreateDatabase(DatabaseName, Context.MODE_PRIVATE, null);
        try {

            String table_name = responseObject.getString("table_name");
            JSONObject labels = responseObject.getJSONObject("labels");
            JSONArray fields = labels.names();
            JSONArray data = responseObject.getJSONArray("data");


            //Open OR Create Database


            createAnyTable(table_name,fields,mydb);

            alterAnyTable(table_name,fields,mydb);


            //insert Main Table
            for(int i=0;i<data.length();i++)
            {

                JSONObject dataSingle1 = data.getJSONObject(i);

                Cursor mCursor1 = mydb.rawQuery("SELECT * FROM " + table_name + " where id='" + dataSingle1.getString("id") + "'", null);

                ContentValues contentValues = new ContentValues();
                for(int j=0;j<fields.length();j++)
                {
//                    upload_status
//                    {"upload_satatus":"","risk_profiling_status":"1","id":"2"}
                    String key = fields.getString(j);
                    if(dataSingle1.has(key)==true) {
                        contentValues.put(key, dataSingle1.getString(key));
                    }
                }
                int updateResult = mydb.update(table_name,contentValues,"id='"+dataSingle1.getString("id")+"'",null);

                if(updateResult == 0)
                {
                    mydb.insert(table_name,null,contentValues);
                }

            }



            if(responseObject.getString("timestamp").equals("")==false) {
                recordTimeStampOfTable(table_name, mydb, responseObject.getString("timestamp"));
            }


            //----Delete Rows that not exist on server-----
            if(responseObject.has("existing_ids")==true)
            {
                //--NOT IN THINGS
                String whereClause = "id NOT IN ";

                String middleWhereClause = "";
                JSONArray existing_ids = responseObject.getJSONArray("existing_ids");
                for(int ei=0;ei<existing_ids.length();ei++)
                {
                    JSONObject existing_idsSingle = existing_ids.getJSONObject(ei);
                    if(ei == 0) {
                        middleWhereClause = middleWhereClause + "'" + existing_idsSingle.getString("id") + "'";
                    }
                    else
                    {
                        middleWhereClause = middleWhereClause + ",'" + existing_idsSingle.getString("id") + "'";
                    }
                }

                whereClause = whereClause+"("+middleWhereClause+")";
                //------


                //--MID not like things
                whereClause = whereClause + " and id not like '%mid%'";


                mydb.delete(table_name,whereClause,null);
            }



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        mydb.close();
    }





    public JSONObject createBlankEntry(Context context,String table_name1,JSONArray data1)
    {

        mydb = context.openOrCreateDatabase(DatabaseName, Context.MODE_PRIVATE, null);
        try {

            String table_name = table_name1;
            JSONArray fields = new JSONArray("[\"id\",\"risk_profiling_status\",\"upload_status\"]");

            JSONArray data = new JSONArray("[{\"id\":\"\",\"risk_profiling_status\":\"\",\"upload_status\":\"\"}]");
            if(data1.length() != 0)
            {
                data = data1;
            }


            //Open OR Create Database


            createAnyTable(table_name,fields,mydb);

            alterAnyTable(table_name,fields,mydb);


            //insert Main Table
            for(int i=0;i<data.length();i++)
            {

                JSONObject dataSingle1 = data.getJSONObject(i);

                Cursor mCursor1 = mydb.rawQuery("SELECT * FROM " + table_name + " where id='" + dataSingle1.getString("id") + "'", null);

                ContentValues contentValues = new ContentValues();
                for(int j=0;j<fields.length();j++)
                {
//                    upload_status
//                    {"upload_satatus":"","risk_profiling_status":"1","id":"2"}
                    String key = fields.getString(j);
                    if(dataSingle1.has(key)==true) {
                        contentValues.put(key, dataSingle1.getString(key));
                    }
                }

                String searchId = dataSingle1.getString("id").replace("mid","");

//                int updateResult = mydb.update(table_name, contentValues, "id='" + dataSingle1.getString("id") + "'", null);
//                if(updateResult == 0) {
                  int updateResult = mydb.update(table_name, contentValues, "mid='"+searchId+"'", null);
//                }

                if(updateResult == 0)
                {
                    mydb.insert(table_name,null,contentValues);
                }
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        mydb.close();




        JSONObject lastObject = new JSONObject();
        JSONArray lastRow = new MDbHelper().getAll(table_name1," ORDER BY mid DESC LIMIT 1",context);

        if(lastRow.length()!=0)
        {
            try {
                if(lastRow.getJSONObject(0).getString("id").equals("")==true)
                {
                    lastObject = createBlankEntry(context,table_name1,new JSONArray("[{\"id\":\"mid"+lastRow.getJSONObject(0).getString("mid")+"\"}]"));
                }
                else
                {
                    lastObject = lastRow.getJSONObject(0);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return lastObject;

    }


    public void updateSomeOne(String table_name,JSONArray data,Context context)
    {
        mydb = context.openOrCreateDatabase(DatabaseName, Context.MODE_PRIVATE, null);

        try {

            //Open OR Create Database



            for(int i=0;i<data.length();i++)
            {
                JSONObject dataSingle = data.getJSONObject(i);

                JSONArray namesA = data.getJSONObject(i).names();

                ContentValues contentValues = new ContentValues();
                for(int j=0;j<namesA.length();j++)
                {
                    contentValues.put(namesA.getString(j),dataSingle.getString(namesA.getString(j)));
                }


                if(dataSingle.has("mid")==true)
                {
                    int updateResult = mydb.update(table_name, contentValues, "mid=" + dataSingle.getString("mid") + "", null);
                    Log.d("updateResult", String.valueOf(updateResult));
                }
                else
                {
                    int updateResult = mydb.update(table_name, contentValues, "id=" + dataSingle.getString("id") + "", null);
                    Log.d("updateResult", String.valueOf(updateResult));
                }




            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        mydb.close();
    }


    public void updateSomeOneOnly(String table_name,JSONObject data,Context context,String conditionWithoutWhere)
    {


        //Open OR Create Database
        mydb = context.openOrCreateDatabase(DatabaseName, Context.MODE_PRIVATE, null);


        try {


            JSONArray namesA = data.names();

            ContentValues contentValues = new ContentValues();
            for(int j=0;j<namesA.length();j++)
            {
                contentValues.put(namesA.getString(j), data.getString(namesA.getString(j)));
            }


            int updateResult = mydb.update(table_name, contentValues, conditionWithoutWhere , null);


            Log.d("updateResult", String.valueOf(updateResult));


            mydb.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        mydb.close();
    }


    boolean isTableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }


    public JSONArray getAll(String table_name,String query,Context context)
    {
        //Open OR Create Database

        mydb = context.openOrCreateDatabase(DatabaseName, Context.MODE_PRIVATE, null);


        JSONArray data = new JSONArray();

        try {


            if(isTableExists(mydb,table_name) == true) {
            /*retrieve data from database */
                Cursor c = mydb.rawQuery("SELECT * FROM " + table_name + query, null);

                String[] columnNames = c.getColumnNames();




                // Check if our result was valid.
                c.moveToFirst();
                if (c != null) {
                    // Loop through all Results
                    do {


                        if(c.getCount() !=  0) {
                            JSONObject singleData = new JSONObject();

                            for (int j = 0; j < columnNames.length; j++) {
                                try {
                                    singleData.put(columnNames[j], c.getString(c.getColumnIndex(columnNames[j])));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                            data.put(singleData);
                        }

                    } while (c.moveToNext());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        mydb.close();

        return data;
    }


    public JSONObject getAllWithJsonobject(String table_name,String query,Context context)
    {
        //Open OR Create Database

        mydb = context.openOrCreateDatabase(DatabaseName, Context.MODE_PRIVATE, null);


        JSONObject data = new JSONObject();

        try {


            if(isTableExists(mydb,table_name) == true) {
            /*retrieve data from database */
                Cursor c = mydb.rawQuery("SELECT * FROM " + table_name + query, null);

                String[] columnNames = c.getColumnNames();




                // Check if our result was valid.
                c.moveToFirst();
                if (c != null) {
                    // Loop through all Results
                    do {


                        if(c.getCount() !=  0) {
                            JSONObject singleData = new JSONObject();

                            for (int j = 0; j < columnNames.length; j++) {
                                try {
                                    singleData.put(columnNames[j], c.getString(c.getColumnIndex(columnNames[j])));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                            data.put(singleData.getString("id"),singleData);
                        }

                    } while (c.moveToNext());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        mydb.close();

        return data;
    }






    public void deleteAll(String table_name,String query,Context context)
    {
        //Open OR Create Database
        mydb = context.openOrCreateDatabase(DatabaseName, Context.MODE_PRIVATE, null);


        try {
            if(isTableExists(mydb,table_name) == true) {
            /*retrieve data from database */
                mydb.execSQL("DELETE FROM " + table_name + query);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        mydb.close();


    }








}








//------Referemce Code
//                if(mCursor1!=null)
//                {
//                    if(mCursor1.getCount()>0)
//                    {
//                        ContentValues contentValues = new ContentValues();
//                        contentValues.put("");
//                        mydb.update(table_name,)
//                    }
//                }
//
//                String insertString = "INSERT INTO "
//                                    + table_name
//                                    + " " +fieldString
//                                    + " VALUES ";
//
//                //Creating Insert String
//                String insertValuesString = "(";
//                for(int j=0;j<fields.length();j++)
//                {
//                    if(j==0)
//                    {
//                        insertValuesString = insertValuesString + "'"+ dataSingle.getString(fields.getString(j)) +"'";
//                    }
//                    else
//                    {
//                        insertValuesString = insertValuesString + ",'"+ dataSingle.getString(fields.getString(j)) +"'";
//                    }
//                }
//                insertValuesString = insertValuesString + ");";
//
//                insertString = insertString + insertValuesString;
//
//                mydb.execSQL(insertString);
