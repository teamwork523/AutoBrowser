package edu.umich.autobrowser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import android.util.Log;

public class Util {
  public static String sanitizedURL(String url) {
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
      url = "http://" + url;
    }
    return url;
  }
  
  public static String stripHTTPPrefix(String url) {
    if (url.subSequence(0, 7).equals("http://")) {
      return url.substring(7);
    } else if (url.subSequence(0, 8).equals("https://")) {
      return url.substring(8);
    }
    return url;
  }
  
  public static ArrayList<String> getListOfURLs() {
    ArrayList<String> urls = new ArrayList<String>();
    File file = new File(Constant.urlFilePath);
    try {
      Scanner in = new Scanner(file);
      while (in.hasNextLine()) {
        urls.add(Util.sanitizedURL(in.nextLine()));
      }
      Log.i(Constant.logTag, urls.toString());
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      Log.e(Constant.logTag, "ERROR: no file existed. " + e.getMessage());
    }
    return urls;
  }
  
  public static HashMap<String, Double> getURLsAndTimerPairs() {
    HashMap<String, Double> urlTimerPairs = new HashMap<String, Double>();
    File file = new File(Constant.urlFilePath);
    try {
      Scanner in = new Scanner(file);
      while (in.hasNextLine()) {
        String[] curLine = in.nextLine().split(" ");
        String url = Util.sanitizedURL(curLine[0]);
        Double timer = Double.valueOf(curLine[1]);
        urlTimerPairs.put(url, timer);
      }
      Log.i(Constant.logTag, urlTimerPairs.toString());
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      Log.e(Constant.logTag, "ERROR: no file existed. " + e.getMessage());
    }
    return urlTimerPairs;
  }
  
  public static ArrayList<Double> generateTimer(double start, double end, double interval) {
    ArrayList<Double> timers = new ArrayList<Double>();
    double curTimer = start;
    while (curTimer <= end) {
      timers.add(curTimer);
      curTimer += interval;
    }
    return timers;
  }
  
 //Wrote a line to the designated file
 public static void writeResultToFile(String filename, String realFolderPath, String line) {
   // append ".txt" to the filename
   String dstFilePath = realFolderPath + "/" + filename;
   // automatic append a newline to the line 
   String content = line + "\n";
   File d = new File(realFolderPath);
   File f = new File(dstFilePath);
   
   // check if directory exist
   if (!d.exists()) {
     if (!d.mkdirs()) {
       Log.e(Constant.logTag, "ERROR: fail to create directory " + realFolderPath);
       return;
     }
   }
   
   // check file existence
   if (!f.exists()) {
     try {
       f.createNewFile();
       // set file to be readable
     } catch (IOException e) {
       e.printStackTrace();
       Log.e(Constant.logTag, "ERROR: fail to create file " + dstFilePath);
     }
   }
   
   // append to file 
   try {
     // prevent multiple threads write to the same file
     @SuppressWarnings("resource")
     FileChannel channel = new RandomAccessFile(f, "rw").getChannel(); // Use the file channel to create a lock on the file.
     FileLock lock = null;
     
     do {
       // try to acquire a lock
       lock = channel.tryLock();
     } while (lock == null);
   
     FileOutputStream out = new FileOutputStream(f, true);
     out.write(content.getBytes(), 0, content.length());
     out.close();
     
     // release the lock
     lock.release();
     channel.close();
   } catch (IOException e) {
     e.printStackTrace();
     Log.e(Constant.logTag, "ERROR: cannot write to file.\n" + e.toString());
   }
  }
 
   // delete a file 
   public static void deleteFile(String filename, String fileFolderPath) {
     String realFilePath = fileFolderPath + "/" + filename;
     try{
       File file = new File(realFilePath);
       if (file.delete()){
         Log.e(Constant.logTag, file.getName() + " is deleted!");
       }else{
         Log.e(Constant.logTag, "Delete operation is failed.");
       }
     } catch(Exception e){
       Log.e(Constant.logTag, "ERROR: fail to delete a file. " + e.getMessage());
     }
   }
}
