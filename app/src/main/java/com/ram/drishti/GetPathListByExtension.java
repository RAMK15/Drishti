package com.ram.drishti;

import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.HashSet;

/**
 * Created by RAM KUMAR on 8/18/2017.
 */
public class GetPathListByExtension {
    public HashSet<String> listoffiles=new HashSet<String>();
    public GetPathListByExtension(String extension){
        String externalstoragedirectory= Environment.getExternalStorageDirectory()+"";
        String datadirectory=Environment.getDataDirectory()+"";
        String downloadcachedirectory=Environment.getDownloadCacheDirectory()+"";
        String rootdirectory=Environment.getRootDirectory()+"";
        String externalstoragepublicmusicdirectory=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+"";
        String externalstragepublicringtonedirectory=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES)+"";
        String samsunginternalmemory="/storage/extSdCard";
        String samsungexternalmemory="/storage/sdcard0";
        String[] expected_locations={externalstoragedirectory,datadirectory,downloadcachedirectory,rootdirectory,externalstoragepublicmusicdirectory,externalstragepublicringtonedirectory,samsungexternalmemory,samsunginternalmemory};
        for(int i=0;i<expected_locations.length;i++){
            File file=new File(expected_locations[i]);
            Search_Dir(file,extension);
        }
    }
    public int getfilecount(){
        return listoffiles.size();
    }
    public void Search_Dir(File dir,String extension){
        try {
            File FileList[]=dir.listFiles();
            if(FileList!=null){
                for (int i = 0; i < FileList.length; i++) {
                    if(FileList[i].isDirectory()){
                        Search_Dir(FileList[i],extension);
                    }
                    else{
                        if(FileList[i].getName().endsWith(extension)){
                            listoffiles.add(FileList[i].getAbsolutePath());
                        }
                    }
                }
            }
        }catch (Exception e){
        }
    }
}
