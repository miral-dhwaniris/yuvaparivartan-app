package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shivraj on 2/17/2016.
 */
public class SpAdapter extends BaseAdapter {

    Context context;
    List<String> list = new ArrayList<>();

    public SpAdapter(FragmentActivity activity, List<String> listLoc) {
        this.context = activity;
        this.list = listLoc;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        TextView txt = (TextView)view.findViewById(R.id.txt_spinner);
        txt.setText(list.get(position));
        return view;
    }
}
