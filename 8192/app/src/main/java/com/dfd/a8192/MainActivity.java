package com.dfd.a8192;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by a61-201405-2055 on 16/06/24.
 */
public class MainActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        Button oneShotBtn = (Button)findViewById(R.id.oneShot);
        Button omenBtn = (Button)findViewById(R.id.omen);
        oneShotBtn.setOnClickListener(this);
        omenBtn.setOnClickListener(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.oneShot) {
            Intent intent = new Intent(getApplicationContext(), OneShotActivity.class);
            startActivity(intent);
        }
    }
}
