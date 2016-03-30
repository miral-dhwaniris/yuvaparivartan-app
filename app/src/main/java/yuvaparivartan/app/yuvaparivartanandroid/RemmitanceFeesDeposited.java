package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class RemmitanceFeesDeposited extends ActionBarActivity {


    private EditText state_et, district_et, block_et, campstartdate_et, tentative_enddate_et, actual_date_et;
    SpAdapter adapter;
    List<String> listServ = new ArrayList<>();
    private Context context;
    private String last_selecteddate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remmitance_fees_deposited);

        listServ.add("Gujarat");
        listServ.add("Maharashtra");
        listServ.add("Karnataka");
        context = RemmitanceFeesDeposited.this;

        state_et = (EditText)findViewById(R.id.state_spinnner);
        district_et = (EditText)findViewById(R.id.district_spinner);
        block_et = (EditText)findViewById(R.id.block_spinner);

        campstartdate_et = (EditText) findViewById(R.id.camp_startdate_Et);
        tentative_enddate_et = (EditText) findViewById(R.id.tentative_enddate_et);
        actual_date_et = (EditText) findViewById(R.id.actual_enddate_et);



        state_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new SpAdapter(RemmitanceFeesDeposited.this, listServ);
                adapter.notifyDataSetChanged();
                new android.support.v7.app.AlertDialog.Builder(RemmitanceFeesDeposited.this)
                        .setTitle("States")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int pos) {

                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });


        district_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new SpAdapter(RemmitanceFeesDeposited.this, listServ);
                adapter.notifyDataSetChanged();
                new android.support.v7.app.AlertDialog.Builder(RemmitanceFeesDeposited.this)
                        .setTitle("Districts")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int pos) {

                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });


        block_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new SpAdapter(RemmitanceFeesDeposited.this, listServ);
                adapter.notifyDataSetChanged();
                new android.support.v7.app.AlertDialog.Builder(RemmitanceFeesDeposited.this)
                        .setTitle("Blocks")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int pos) {

                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });


        campstartdate_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(context, DatePickerM.class);
                in.putExtra("date", campstartdate_et.getText().toString());
                in.putExtra("max_date", String.valueOf(System.currentTimeMillis()));
                in.putExtra("update_Date", last_selecteddate);
                startActivityForResult(in, 100);
            }
        });

        tentative_enddate_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(context, DatePickerM.class);
                in.putExtra("date", tentative_enddate_et.getText().toString());
                in.putExtra("max_date", String.valueOf(System.currentTimeMillis()));
                in.putExtra("update_Date", last_selecteddate);
                startActivityForResult(in, 200);
            }
        });

        actual_date_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(context, DatePickerM.class);
                in.putExtra("date", actual_date_et.getText().toString());
                in.putExtra("max_date", String.valueOf(System.currentTimeMillis()));
                in.putExtra("update_Date", last_selecteddate);
                startActivityForResult(in, 300);
            }
        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            if (requestCode == 100) {

                String date = data.getExtras().getString("date");
                if (!date.equals("0")) {

                    last_selecteddate = date;
                    try {
                        SimpleDateFormat parser = new SimpleDateFormat("dd-MM-yyyy");
                        SimpleDateFormat format_topass = new SimpleDateFormat("yyyy-MM-dd");

                        Date dobdate = null;
                        try {
                            dobdate = parser.parse(date);
                            String dobDateToPass22 = format_topass.format(dobdate);
                            campstartdate_et.setText(dobDateToPass22);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Date yourDate = parser.parse(date);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(yourDate);
                        calendar.get(Calendar.DAY_OF_MONTH); //Day of the month :)
                        String year = Integer.toString(calendar.get(Calendar.YEAR));
                        String month = Integer.toString(calendar.get(Calendar.MONTH));
                        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));


                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {
                    last_selecteddate = "";
                    campstartdate_et.setText("");

                }
            }

            if (requestCode == 200) {
                String date = data.getExtras().getString("date");
                if (!date.equals("0")) {
                    last_selecteddate = date;

                    SimpleDateFormat format_topass = new SimpleDateFormat("yyyy-MM-dd");

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date dobdate = null;
                    try {
                        dobdate = sdf.parse(date);
                        String dobDateToPass22 = format_topass.format(dobdate);
                        tentative_enddate_et.setText(dobDateToPass22);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    last_selecteddate = "";
                    tentative_enddate_et.setText("");
                }
            }

            if (requestCode == 300) {
                String date = data.getExtras().getString("date");
                if (!date.equals("0")) {
                    last_selecteddate = date;

                    SimpleDateFormat format_topass = new SimpleDateFormat("yyyy-MM-dd");

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date dobdate = null;
                    try {
                        dobdate = sdf.parse(date);
                        String dobDateToPass22 = format_topass.format(dobdate);
                        actual_date_et.setText(dobDateToPass22);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    last_selecteddate = "";
                    actual_date_et.setText("");
                }
            }
        }
    }

}
