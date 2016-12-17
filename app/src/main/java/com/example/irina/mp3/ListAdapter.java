package com.example.irina.mp3;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Irina on 17.12.2016.
 */
public class ListAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Files> objects;

    ListAdapter (Context context, ArrayList<Files> files){
        ctx = context;
        objects = files;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null){
            view = lInflater.inflate(R.layout.files_list_item, parent, false);
        }

        Files f = getFiles(position);

        ((TextView) view.findViewById(R.id.label)).setText(f.directoryPath);

        return view;
    }

    Files getFiles(int position){
        return ((Files) getItem(position));
    }

}
