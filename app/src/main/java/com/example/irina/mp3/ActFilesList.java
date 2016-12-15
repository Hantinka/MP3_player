package com.example.irina.mp3;


import android.content.Context;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;

public class ActFilesList extends AppCompatActivity {


    private ListView lvFilesList;
    private ImageView ivIcon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_files_list);
        lvFilesList = (ListView)findViewById(R.id.lvOpenFileList);
        ivIcon = (ImageView)findViewById(R.id.ivIcon);
        createList();
    }

    public void createList (){
        String directoryPath = Environment.getExternalStorageDirectory().getPath();
        final File file = new File(directoryPath);
        final File [] files = file.listFiles();
        //Utils.DBG(directoryPath);
        final ArrayAdapter<File> adapter = new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, files);
        Utils.DBG("File 0 => "+ files[0].toString());
        lvFilesList.setAdapter(adapter);




        lvFilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.DBG("Нажат пункт меню");
                adapter.getItem(position);
                if (file.isDirectory()){
                Utils.DBG("Это папка");

                }
            }
        });
    }


}
