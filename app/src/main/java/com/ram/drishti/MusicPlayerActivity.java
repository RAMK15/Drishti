package com.ram.drishti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class MusicPlayerActivity extends AppCompatActivity {
    SharedPreferences musicdata;
    SharedPreferences.Editor musicdataeditor;
    Set<String> mp3path=new HashSet<String>();
    int position;
    boolean isPlaying=false;
    boolean isLooping;
    boolean isShuffling;
    TextView album,genre,artist,songname;
    ImageView album_art;
    String path[];
    public static final String ACTION_STRING = "SONG_COMPLETE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        //linking various UI elements defined in xml page
        album=(TextView)findViewById(R.id.album);
        artist=(TextView)findViewById(R.id.artist);
        genre=(TextView)findViewById(R.id.genre);
        album_art=(ImageView)findViewById(R.id.album_art);
        songname=(TextView)findViewById(R.id.songname);

        //getting list of music files and if not already there searching the device for it
        musicdata=getSharedPreferences("musicdata", Context.MODE_PRIVATE);
        musicdataeditor=musicdata.edit();
        Set<String> temp=new HashSet<String>();
        mp3path=musicdata.getStringSet("mp3path", temp);
        if(mp3path.size()<=0){
            GetPathListByExtension tempObj=new GetPathListByExtension("mp3");
            if(tempObj.getfilecount()<=0){
                //if no music detected
            }
            else {
                mp3path=tempObj.listoffiles;
                musicdataeditor.putStringSet("mp3path",mp3path);
                musicdataeditor.commit();
            }
        }
        path=new String[mp3path.size()];
        mp3path.toArray(path);
        Toast.makeText(MusicPlayerActivity.this,""+mp3path.size(),Toast.LENGTH_SHORT).show();

        //details of last song that was played
        position=musicdata.getInt("position",-1);
        if(position==-1){
            position=0;
            musicdataeditor.putInt("position",0);
            musicdataeditor.commit();
        }

        //current state of music player
        isLooping=musicdata.getBoolean("isLooping",false);
        isShuffling=musicdata.getBoolean("isShuffling",false);
        musicdataeditor.putBoolean("isLooping",isLooping);
        musicdataeditor.putBoolean("isShuffling",isShuffling);
        musicdataeditor.commit();
        setScreen(position);
        album_art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position=musicdata.getInt("position",0);
                int pos=position+1;
                if(pos>=mp3path.size())pos=0;
                musicdataeditor.putInt("position",pos);
                musicdataeditor.commit();
                stopService(new Intent(MusicPlayerActivity.this, MusicService.class));
                startService(new Intent(MusicPlayerActivity.this,MusicService.class));
                setScreen(pos);
            }
        });

    }

    public  void setScreen(int position){
        MediaMetadataRetriever metaRetriver;
        metaRetriver = new MediaMetadataRetriever();
        byte[] art;
        art = metaRetriver.getEmbeddedPicture();
        metaRetriver.setDataSource(path[position]);
        try {
            art = metaRetriver.getEmbeddedPicture();
            Bitmap songImage = BitmapFactory
                    .decodeByteArray(art, 0, art.length);
            //Toast.makeText(VoiceSupport.this,"in setscreen",Toast.LENGTH_SHORT).show();
            Bitmap resized = Bitmap.createScaledBitmap(songImage, 300, 300, true);
            album_art.setImageBitmap(resized);
            album.setText(metaRetriver
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            artist.setText(metaRetriver
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            genre.setText(metaRetriver
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
            songname.setText(metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        } catch (Exception e) {
            Toast.makeText(MusicPlayerActivity.this,"This mp3 file doesn't follow proper standards",Toast.LENGTH_LONG).show();
            album_art.setBackgroundColor(Color.GRAY);
            album.setText("Unknown Album");
            artist.setText("Unknown Artist");
            genre.setText("Unknown Genre");
        }
    }
}
