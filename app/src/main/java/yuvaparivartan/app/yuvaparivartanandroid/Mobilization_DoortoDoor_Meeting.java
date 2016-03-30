package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


public class Mobilization_DoortoDoor_Meeting extends ActionBarActivity {


    private EditText state_et,district_et,block_et;
    SpAdapter adapter;
    List<String> listServ = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobilization_doortodoor_meetings);

        listServ.add("Gujarat");
        listServ.add("Maharashtra");
        listServ.add("Karnataka");

        state_et = (EditText)findViewById(R.id.state_spinnner);
        district_et = (EditText)findViewById(R.id.district_spinner);
        block_et = (EditText)findViewById(R.id.block_spinner);


        state_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new SpAdapter(Mobilization_DoortoDoor_Meeting.this, listServ);
                adapter.notifyDataSetChanged();
                new android.support.v7.app.AlertDialog.Builder(Mobilization_DoortoDoor_Meeting.this)
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
                adapter = new SpAdapter(Mobilization_DoortoDoor_Meeting.this, listServ);
                adapter.notifyDataSetChanged();
                new android.support.v7.app.AlertDialog.Builder(Mobilization_DoortoDoor_Meeting.this)
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
                adapter = new SpAdapter(Mobilization_DoortoDoor_Meeting.this, listServ);
                adapter.notifyDataSetChanged();
                new android.support.v7.app.AlertDialog.Builder(Mobilization_DoortoDoor_Meeting.this)
                        .setTitle("Blocks")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int pos) {

                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });




    }

}
