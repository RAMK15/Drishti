package com.ram.drishti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashSet;

public class MusicPlayerActivity extends AppCompatActivity {
    TextView t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        t=(TextView)findViewById(R.id.textView);
        GetPathListByExtension tempObj=new GetPathListByExtension("mp3");
        t.setText("number of files" + tempObj.getfilecount());
        String[] mp3path=new String[tempObj.getfilecount()];
        (tempObj.listoffiles).toArray(mp3path);
        for(int i=0;i<mp3path.length;i++){
            t.append("\n"+mp3path[i]);
        }
    }
}
