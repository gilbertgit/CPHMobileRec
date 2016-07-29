package com.cphandheld.cphmobilerec;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by titan on 5/21/16.
 */
public class MainActivity extends Activity {

    private Button buttonPhysical;
    private Button buttonRescan;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>CPH Reconciliation</font>"));
        actionBar.show();

        buttonPhysical = (Button)findViewById(R.id.buttonPhysical);
        buttonRescan = (Button)findViewById(R.id.buttonRescan);

        buttonPhysical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PhysicalActivity.class);
                startActivity(i);
            }
        });

        buttonRescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RescanActivity.class);
                startActivity(i);
            }
        });

    }

    public void onBackPressed()
    {
        Toast toast = Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 75);
        toast.show();

        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
