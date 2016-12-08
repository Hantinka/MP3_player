package com.example.irina.mp3;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Irina on 07.12.2016.
 */
public class OpenFileDialog extends AlertDialog.Builder {

    private String currentPath = Environment.getExternalStorageDirectory().getPath();
    private List<File> files = new ArrayList<File>();


    private class FileAdapter extends ArrayAdapter <File> {
        public FileAdapter (Context context, List <File> files){
            super(context, android.R.layout.simple_list_item_1, files);
        }
        @Override
        public View getView (int position, View convertView, ViewGroup parent){
            TextView view = (TextView) super.getView(position, convertView, parent);
            File file = getItem(position);
            view.setText(file.getName());
            return view;
        }
    }


    public OpenFileDialog (Context context){
        super(context);
        LinearLayout linearLayout = createMainLayout(context);
        files.addAll(getFiles(currentPath));
        ListView listView = createListView(context);
        listView.setAdapter(new FileAdapter(context, files));
        linearLayout.addView(listView);
        setView(linearLayout)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null);
    }

    private static Display getDefaultDisplay (Context context){
        return ((WindowManager) context.getSystemService (context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    private static Point getScreenSize (Context context){
        Point screenSize = new Point();
        getDefaultDisplay(context).getSize(screenSize);
        return screenSize;
    }

    private static int getLinearLayoutMinHeight (Context context){
        return getScreenSize(context).y;
    }

    private LinearLayout createMainLayout (Context context){
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setMinimumHeight(getLinearLayoutMinHeight(context));
        return linearLayout;
    }

    private void rebuildFiles (ArrayAdapter<File> adapter){
        files.clear();
        files.addAll(getFiles(currentPath));
        adapter.notifyDataSetChanged();
    }

    private ListView createListView (Context context){
        ListView listView = new ListView(context);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                final ArrayAdapter<File> adapter = (FileAdapter) adapterView.getAdapter();
                File file = adapter.getItem(index);
                if (file.isDirectory()){
                    currentPath = file.getPath();
                    rebuildFiles(adapter);
                }
            }
        });
        return listView;
    }

    private List<File> getFiles (String directoryPath){
        File directory = new File(directoryPath);
        List<File> fileList = Arrays.asList(directory.listFiles());
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File file, File file2) {
                if (file.isDirectory() && file2.isFile())
                return -1;
                else if (file.isFile() && file2.isDirectory())
                    return 1;
                else
                    return file.getPath().compareTo(file2.getPath());
            }
        });
        return fileList;
    }
}
