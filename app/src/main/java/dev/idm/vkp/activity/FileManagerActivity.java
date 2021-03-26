package dev.idm.vkp.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import dev.idm.vkp.R;
import dev.idm.vkp.fragment.FileManagerFragment;
import dev.idm.vkp.listener.BackPressCallback;
import dev.idm.vkp.settings.CurrentTheme;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.Utils;

public class FileManagerActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utils.updateActivityContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Settings.get().ui().getMainTheme());
        Utils.prepareDensity(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_no_main);
        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.setStatusBarColor(CurrentTheme.getStatusBarColor(this));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.close);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        if (savedInstanceState == null) {
            attachFragment();
        }
    }

    private void attachFragment() {
        FileManagerFragment ignoredFragment = new FileManagerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, ignoredFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (fragment instanceof BackPressCallback) {
            if (((BackPressCallback) fragment).onBackPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}
