package edu.umich.autobrowser;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends Activity {
  private String DEL = "\t";
  private WebView mWebView;
  
  private int curURLindex = 0;
  private int curTimerIndex = 0;
  private HashMap<String, String> extraHeader;
  private ArrayList<String> urls;
  private ArrayList<Double> timers;
  private int remainingIterations;
  private boolean isNextRepeat;

  // add progress in notification center
  private NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
  private NotificationManager mNotificationManager;
  private int mNotificationId = 9999;
  
  // switch between different mode
  enum Expr {
    PAIR, COMPREHENSIVE, TRIO
  }
  
  //Some things we only have to set the first time.
  private boolean firstTime = true;

  private void updateNotification(String message) {
    if (firstTime) {
      mBuilder.setSmallIcon(R.drawable.ic_launcher)
      .setContentTitle("AutoBrowser Notification")
      .setOnlyAlertOnce(true);
      firstTime = false;
    }
    mBuilder.setContentText(message);

    mNotificationManager.notify(mNotificationId, mBuilder.build());
  }
  
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.webview);
    findAllViewsById();
    
    // load the static variables
    urls = Util.getListOfURLs();
    timers = Util.generateTimer(Constant.start_timer_s,
                                Constant.end_timer_s,
                                Constant.timer_interval_s);
    extraHeader = new HashMap<String, String>();
    
    Expr mode = Expr.COMPREHENSIVE;
    
    // create notification
    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    
    // apply webview settings
    WebSettings webSettings = mWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setGeolocationEnabled(false);
    webSettings.setLoadsImagesAutomatically(true);
    updateNotification("Start experiment");
    remainingIterations = Constant.number_of_repeat;
    
    // delete the previous messages 
    Util.deleteFile(Constant.syncFile, Constant.FileFolder);
    
    if (mode == Expr.PAIR) {
      mWebView.setWebViewClient(new pairWebViewClient());
      isNextRepeat = true;
      mWebView.loadUrl(urls.get(curURLindex));
    } else if (mode == Expr.COMPREHENSIVE) {
      int privTimer = ((curTimerIndex - 1) < 0) ? timers.size() - 1 : (curTimerIndex - 1);
      extraHeader.put("Timer", String.valueOf(timers.get(privTimer)));
      mWebView.setWebViewClient(new timerTuneViewClient());
      String curTime = String.valueOf(System.currentTimeMillis());
      String content = curTime.substring(0, curTime.length() - 3) + "." + 
                       curTime.substring(curTime.length() - 3) + DEL +
                       Util.stripHTTPPrefix(urls.get(curURLindex)) + DEL + 
                       String.valueOf(timers.get(privTimer));
      Util.writeResultToFile(Constant.syncFile, Constant.FileFolder, content);
      mWebView.loadUrl(urls.get(curURLindex), extraHeader);
    } else if (mode == Expr.TRIO) {
      int privTimer = ((curTimerIndex - 1) < 0) ? (Constant.trio_interval_ms.length - 1) : 
                      (curTimerIndex - 1);
      extraHeader.put("Timer", String.valueOf(Constant.trio_interval_ms[privTimer]));
      mWebView.setWebViewClient(new trioWebViewClient());
      mWebView.loadUrl(urls.get(curURLindex), extraHeader);
    }
  }

  // bind all the widgets
  private void findAllViewsById() {
    mWebView = (WebView) findViewById(R.id.webview);
  }
  
  // define the automatic webview client for multiple URLs
  private class pairWebViewClient extends WebViewClient {
    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      int totalRemainingRequests = (urls.size() - curURLindex) + (remainingIterations - 1) * urls.size() - 1;
      updateNotification("Remaining requests # is " + totalRemainingRequests);
      // clear cache
      view.clearCache(true);
      
      try {
        if (isNextRepeat == true) {
          Thread.sleep(Constant.intra_packet_interval_ms);
        } else {
          //String msg = "Remaining iterations is " + remainingIterations;
          //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
          Thread.sleep(Constant.inter_packet_interval_ms);
          curURLindex++;
        }
      } catch (InterruptedException e) {
        Log.e(Constant.logTag, "ERROR: thread is interrupted");
      }
      
      String DEL = "\t";
      Log.i(Constant.logTag, isNextRepeat + DEL + curURLindex + DEL + remainingIterations);
      
      if (isNextRepeat == false && curURLindex >= urls.size() && remainingIterations > 0) {
        curURLindex = 0;
        remainingIterations--;
      }
      
      isNextRepeat = !isNextRepeat;
      
      if (curURLindex < urls.size() && remainingIterations > 0) {
        view.loadUrl(urls.get(curURLindex));
      }
    }
    
    @Override
    // overwrite the redirect behaviors
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Log.i(Constant.logTag, "shouldOverrideUrlLoading called for " + url);
      view.stopLoading();
      return true;
    }
  }
  
  // Control experiment for tuning the inter-request timer
  private class timerTuneViewClient extends WebViewClient {
    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      int totalRemainingRequests = (timers.size() - curTimerIndex) + 
                                   (urls.size() - curURLindex - 1) * timers.size() + 
                                   (remainingIterations - 1) * urls.size() * timers.size() - 1;
      updateNotification("Remaining requests # is " + totalRemainingRequests);
      
      try {
        Thread.sleep((long)(timers.get(curTimerIndex) * 1000));
      } catch (InterruptedException e) {
        Log.e(Constant.logTag, "ERROR: thread is interrupted");
      }
      curTimerIndex++;
      if (curTimerIndex >= timers.size()) {
        // Another 
        curTimerIndex = 0;
        curURLindex++;
        if (curURLindex >= urls.size()) {
          curURLindex = 0;
          remainingIterations--;
        }
      }
      
      String DEL = "\t";
      Log.i(Constant.logTag, url + DEL + curTimerIndex + DEL + curURLindex + DEL + 
                             remainingIterations + DEL + totalRemainingRequests);
      
      if (curURLindex < urls.size() && remainingIterations > 0) {
        int privTimer = ((curTimerIndex - 1) < 0) ? timers.size() - 1 : (curTimerIndex - 1);
        extraHeader.put("Timer", String.valueOf(timers.get(privTimer)));
        String curTime = String.valueOf(System.currentTimeMillis());
        String content = curTime.substring(0, curTime.length() - 3) + "." + 
                         curTime.substring(curTime.length() - 3) + DEL +
                         Util.stripHTTPPrefix(urls.get(curURLindex)) + DEL + 
                         String.valueOf(timers.get(privTimer));
        Util.writeResultToFile(Constant.syncFile, Constant.FileFolder, content);
        view.loadUrl(urls.get(curURLindex), extraHeader);
      }
    }
    
    @Override
    // overwrite the redirect behaviors
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Log.i(Constant.logTag, "shouldOverrideUrlLoading called for " + url);
      view.stopLoading();
      return true;
    }
  }
  
  // Control experiment for comparing interfered and non-interfered packet difference
  private class trioWebViewClient extends WebViewClient {
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
      super.onReceivedError(view, errorCode, description, failingUrl);
      Log.i(Constant.logTag, "ERROR: fail to load a page " + failingUrl);
      trioURLrequest(view, failingUrl);
    }
    
    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      trioURLrequest(view, url);
    }
    
    @Override
    // overwrite the redirect behaviors
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Log.i(Constant.logTag, "shouldOverrideUrlLoading called for " + url);
      view.stopLoading();
      return true;
    }
  }
  
  // loading URL request function for trio test
  public void trioURLrequest(WebView view, String url) {
    int totalRemainingRequests = (Constant.trio_interval_ms.length - curTimerIndex) + 
        (urls.size() - curURLindex - 1) * Constant.trio_interval_ms.length + 
        (remainingIterations - 1) * urls.size() * Constant.trio_interval_ms.length - 1;
    updateNotification("Remaining requests # is " + totalRemainingRequests);
    
    try {
      Log.i(Constant.logTag, "Timeout for " + Constant.trio_interval_ms[curTimerIndex] + " ms");
      Thread.sleep(Constant.trio_interval_ms[curTimerIndex]);
    } catch (InterruptedException e) {
      Log.e(Constant.logTag, "ERROR: thread is interrupted");
    }
    curTimerIndex++;
    if (curTimerIndex >= Constant.trio_interval_ms.length) {
      // Another 
      curTimerIndex = 0;
      curURLindex++;
      if (curURLindex >= urls.size()) {
        curURLindex = 0;
        remainingIterations--;
      }
    }
    
    String DEL = "\t";
    Log.i(Constant.logTag, url + DEL + curTimerIndex + DEL + curURLindex + DEL + 
      remainingIterations + DEL + totalRemainingRequests);
    
    if (curURLindex < urls.size() && remainingIterations > 0) {
      int privTimer = ((curTimerIndex - 1) < 0) ? (Constant.trio_interval_ms.length - 1)
                      : (curTimerIndex - 1);
      extraHeader.put("Timer", String.valueOf(Constant.trio_interval_ms[privTimer]));
      view.loadUrl(urls.get(curURLindex), extraHeader);
    }
  }
}
