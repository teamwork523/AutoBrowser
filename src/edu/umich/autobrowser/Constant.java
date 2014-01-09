package edu.umich.autobrowser;

import android.os.Environment;

public class Constant {
  public static long inter_packet_interval_ms = 5000;
  public static long intra_packet_interval_ms = 1000;
  public static final String logTag = "autobrowser";
  
  //output file path
  public static final String urlFilePath = Environment.getExternalStorageDirectory().getPath() + "/AutoBrowser/urls.txt";
}
