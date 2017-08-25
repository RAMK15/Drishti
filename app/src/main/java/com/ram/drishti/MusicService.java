package com.ram.drishti;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    SharedPreferences musicdata;
    SharedPreferences.Editor musicdataeditor;
    Set<String> mp3path=new HashSet<String>();
    String[] path;
    int position;
    boolean isPlaying=false;
    boolean isLooping;
    boolean isShuffling;
    MediaPlayer player;
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicdata=getSharedPreferences("musicdata", Context.MODE_PRIVATE);
        musicdataeditor=musicdata.edit();
        mp3path=musicdata.getStringSet("mp3path",null);
        if(mp3path.size()<=0){
            onDestroy();
        }
        path=new String[mp3path.size()];
        mp3path.toArray(path);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        position=musicdata.getInt("position", 0);
        if(position>=mp3path.size()||position<0){
            position=0;
        }
        player=MediaPlayer.create(this, Uri.parse(path[position]));
        player.setOnCompletionListener(this);
        if (player.isPlaying()){
            player.stop();
        }
        player.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(player.isPlaying())player.stop();
        player.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        isLooping=musicdata.getBoolean("isLooping",false);
        isShuffling=musicdata.getBoolean("isShuffling",false);
        if(!isLooping){
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
        }
        sendBroadcast(new Intent(MusicPlayerActivity.ACTION_STRING));
        player.reset();
        try {
            player.setDataSource(this,Uri.parse(path[position]));
            player.start();
        }catch (Exception e){
            stopService(new Intent(this,MusicService.class));
            startService(new Intent(this,MusicService.class));
        }
    }
}
