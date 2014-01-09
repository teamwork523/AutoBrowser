package edu.umich.autobrowser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AutoBrowserActivity extends Activity {
  private Button startButton;
  private Context context;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = this;
    setContentView(R.layout.activity_auto_browser);
    findAllViewsById();
    startButton.setOnClickListener(OnClickStartListener);
    
  }
  
  //bind all the widgets
  private void findAllViewsById() {
    startButton = (Button) findViewById(R.id.startButton);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.activity_auto_browser, menu);
    return true;
  }
  
  // define start button listener
  private OnClickListener OnClickStartListener = new OnClickListener() {
  
    public void onClick(View v) {
      //String msg = "Start button clicked";
      //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
      Intent intent = new Intent(context, WebViewActivity.class);
      startActivity(intent);
    }
  };
  
}
