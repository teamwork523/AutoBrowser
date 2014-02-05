package edu.umich.autobrowser;

import android.os.Environment;

public class Constant {
  // Pair Experiment Configuration
  public static long inter_packet_interval_ms = 5000;
  public static long intra_packet_interval_ms = 1000;
  public static int number_of_repeat = 1;
  
  // Trio Experiement Configuration
  public static long[] trio_interval_ms = {1000, 7000, 15000};
  
  // Test Experiment Configuration
  public static double start_timer_s = 1;
  public static double end_timer_s = 11;
  public static double timer_interval_s = 0.1;
  
  public static final String logTag = "autobrowser";
  
  // I/O path
  public static final String FileFolder = Environment.getExternalStorageDirectory().getPath() + "/AutoBrowser";
  public static final String urlFilePath = FileFolder + "/urls.txt";
  public static final String urlPairFilePath = FileFolder + "/urls_pair.txt";
  public static final String syncFile = "sync.txt";
}
