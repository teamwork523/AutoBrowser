package edu.umich.autobrowser;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AutoBrowserActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auto_browser);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.activity_auto_browser, menu);
    return true;
  }

}
