package edu.umich.autobrowser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import android.util.Log;

public class Util {
  public static String sanitizedURL(String url) {
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
      url = "http://" + url;
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
}
