package com.example.irina.mp3;


import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;


public class ActFilesList extends AppCompatActivity {

    ArrayList<Files> files = new ArrayList<Files>();

    ListAdapter listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_files_list);

        //создаём адаптер
        fillData();
        listAdapter = new ListAdapter(this,files);

        //настраиваем список
        ListView lvOpenFileList = (ListView) findViewById(R.id.lvOpenFileList);
        lvOpenFileList.setAdapter(listAdapter);

    }

    //генерируем данные для адаптера
    void fillData (){
        String directoryPath = Environment.getExternalStorageDirectory().getPath();
        //final File file = new File(directoryPath);
        //final File [] files1 = file.listFiles();
        files.add(new Files(directoryPath));
    }

    

}
