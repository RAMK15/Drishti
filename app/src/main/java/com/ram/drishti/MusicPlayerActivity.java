package com.ram.drishti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MusicPlayerActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener {
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
    private GestureDetectorCompat mDetector;
    private VelocityTracker mVelocityTracker = null;

    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            position=musicdata.getInt("position",0);
            setScreen(position);
        }
    };
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
        musicdataeditor.putBoolean("isShuffling", isShuffling);
        musicdataeditor.commit();
        setScreen(position);
        mDetector=new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(ACTION_STRING));
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Intent i=new Intent(MusicPlayerActivity.this,MusicService.class);
        isPlaying=musicdata.getBoolean("isPlaying",false);
        if(isPlaying){
            stopService(i);
            isPlaying=false;
        }
        else{
            startService(i);
            isPlaying=true;
        }
        musicdataeditor.putBoolean("isPlaying", isPlaying);
        musicdataeditor.commit();
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Intent i = new Intent(MusicPlayerActivity.this, MusicService.class);
        stopService(i);
        finish();
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        int index1 = event1.getActionIndex();
        int pointerId1 = event1.getPointerId(index1);
        int index2 = event2.getActionIndex();
        int pointerId2 = event2.getPointerId(index2);
        double x1=event1.getX(pointerId1);
        double y1=event1.getY(pointerId1);
        double x2=event2.getX(pointerId2);
        double y2=event2.getY(pointerId2);
        double theta=Math.toDegrees(Math.atan2(y1-y2,x2-x1));
        if(x2-x1<0 && (theta>135 && theta<=180) || (theta<-135 && theta>=-180)){
            //left swipe for next song
            playnextsong();
        }
        else if(x2-x1>0 && theta<45 && theta>-45){
            playprevsong();
            //right swipe for prev song
        }
        else if(y2-y1<0 && theta>45 && theta<135){
            //up for shuffling
            isShuffling=musicdata.getBoolean("isShuffling",false);
            musicdataeditor.putBoolean("isShuffling",(!isShuffling));
            musicdataeditor.commit();
        }
        else if(y2-y1>0 && theta<-45 && theta>-135){
            //down for looping
            isLooping=musicdata.getBoolean("isLooping",false);
            musicdataeditor.putBoolean("isLooping",(!isLooping));
            musicdataeditor.commit();
        }
        else{
            //some error
        }
        return true;
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
    public  void playnextsong(){
        Intent i=new Intent(MusicPlayerActivity.this,MusicService.class);
        isLooping=musicdata.getBoolean("isLooping", false);
        isShuffling=musicdata.getBoolean("isShuffling",false);
        if(isShuffling){
            Random random=new Random();
            position=random.nextInt(mp3path.size());
        }
        else{
            if(position==mp3path.size()-1){
                position=0;
            }
            else{
                position=position+1;
            }
        }
        musicdataeditor.putInt("position",position);
        musicdataeditor.commit();
        stopService(i);
        startService(i);
        setScreen(position);

    }
    public  void playprevsong(){
        Intent i=new Intent(MusicPlayerActivity.this,MusicService.class);
        isLooping=musicdata.getBoolean("isLooping",false);
        isShuffling=musicdata.getBoolean("isShuffling",false);
        if(isShuffling){
            Random random=new Random();
            position=random.nextInt(mp3path.size());
        }
        else{
            if(position==0){
                position=mp3path.size()-1;
            }
            else{
                position=position-1;
            }
        }
        musicdataeditor.putInt("position",position);
        musicdataeditor.commit();
        stopService(i);
        startService(i);
        setScreen(position);
    }
}
