package org.smartregister.tbr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.smartregister.tbr.R;
import org.smartregister.tbr.application.TbrApplication;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

/**
 * Created by ndegwamartin on 09/10/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_toolbar_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_language) {
            Utils.showToast(this, "Changing Languages");
            return true;
        } else if (id == R.id.action_logout) {
            logOutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOutUser() {

        Intent i = new Intent(TbrApplication.getInstance().getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }
}
