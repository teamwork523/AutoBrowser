package edu.umich.autobrowser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.ArrayList;

public class WebViewActivity extends Activity {
  private WebView mWebView;
  private int curURLindex = 0;
  private ArrayList<String> urls;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.webview);
    findAllViewsById();
    
    // load the urls
    urls = Util.getListOfURLs();
    
    // apply webview settings
    WebSettings webSettings = mWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setGeolocationEnabled(false);
    webSettings.setLoadsImagesAutomatically(true);
    
    mWebView.setWebViewClient(new autoWebViewClient());
    runOnUiThread(new Runnable(){
      @Override
      public void run() {
        mWebView.loadUrl(urls.get(curURLindex));
      } 
    });
    
    
    /*mWebView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
      }
    });
    
    String[] testURLs = {"www.expedia.com", 
                         "www.bankofamerica.com",
                         "instagram.com"};
    for (String testURL : testURLs) {
      mWebView.loadUrl(Util.sanitizedURL(testURL));
      Log.i(Constant.logTag, "Request for " + testURL);
      try { 
        Thread.sleep(Constant.inter_packet_interval_ms);
      } catch (InterruptedException e) {
        Log.e(Constant.logTag, "ERROR: thread is interrupted");
      }
    }*/
  }
  
  // bind all the widgets
  private void findAllViewsById() {
    mWebView = (WebView) findViewById(R.id.webview);
  }
  
  // define the automatic webview client
  private class autoWebViewClient extends WebViewClient {
    @Override
    public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      String msg = "Page loading finished " + url;
      //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
      Log.i(Constant.logTag, msg);
      try { 
        Thread.sleep(Constant.inter_packet_interval_ms);
      } catch (InterruptedException e) {
        Log.e(Constant.logTag, "ERROR: thread is interrupted");
      }
      curURLindex++;
      if (curURLindex < urls.size()) {
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
}
