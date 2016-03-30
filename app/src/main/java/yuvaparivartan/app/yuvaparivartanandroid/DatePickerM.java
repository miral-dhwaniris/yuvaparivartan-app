package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class DatePickerM extends AppCompatActivity {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */

    String dateM = "";

    private CharSequence mTitle;
    private Bundle extras;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.date_pickerm);

        datePicker = (DatePicker) findViewById(R.id.datepickermm);

        datePicker.setCalendarViewShown(false);

        try {

            extras = getIntent().getExtras();
            if(extras!=null)
            {
                dateM = extras.getString("date");

               /* if(dateM.equals("") == false) {

                    String[] dateArray = dateM.split("-");
                    datePicker.updateDate(Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[1]) - 1, Integer.parseInt(dateArray[0]));

                }*/
//                datePicker.setMinDate(System.currentTimeMillis());


                long mm = System.currentTimeMillis();
                if(extras.containsKey("min_date") == true)
                {
                    datePicker.setMinDate(Long.parseLong(extras.getString("min_date")));
                }
                if(extras.containsKey("max_date") == true)
                {
                    datePicker.setMaxDate(Long.parseLong(extras.getString("max_date")));
                }
                if(extras.containsKey("update_Date") == true)
                {
                    String getlastdate = extras.getString("update_Date");
                    if(getlastdate != null ){

                        if(!(getlastdate.equals(""))){

                            SimpleDateFormat parser = new SimpleDateFormat("dd-MM-yyyy");
                            Date yourDate = parser.parse(getlastdate);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(yourDate);
                            calendar.get(Calendar.DAY_OF_MONTH); //Day of the month :)
                            String year = Integer.toString(calendar.get(Calendar.YEAR));
                            String month = Integer.toString(calendar.get(Calendar.MONTH));
                            String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

                            datePicker.updateDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

                        }

                    }

                }
            }

        }
        catch (Exception e)
        {

            e.printStackTrace();
            Log.d("adas", "Adada");

        }


        TextView set = (TextView) findViewById(R.id.set);
        TextView cancel = (TextView) findViewById(R.id.cancel);
        TextView remove = (TextView) findViewById(R.id.remove);


        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int day = datePicker.getDayOfMonth();
                String day1 = "";
                if(day<10)
                {
                    day1 = "0"+day;
                }
                else
                {
                    day1 = ""+day;
                }
                int month = datePicker.getMonth()+1;
                String month1 = "";
                if(month<10)
                {
                    month1 = "0"+month;
                }
                else
                {
                    month1 = ""+month;
                }
                int year = datePicker.getYear();

                dateM = ""+day1+"-"+month1+"-"+year;

                goOut();


            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateM = "";
                goOut();

            }
        });


        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dateM = "";
                goOut();

            }
        });

    }


    public void goOut()
    {
        Intent i = getIntent();
        i.putExtra("date", dateM);
        setResult(1, i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }



}
