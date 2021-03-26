package dev.idm.vkp.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import dev.idm.vkp.R;
import dev.idm.vkp.listener.BackPressCallback;
import dev.idm.vkp.settings.CurrentTheme;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.Utils;
import dev.idm.vkp.view.zoomhelper.ZoomHelper;

public abstract class NoMainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private final FragmentManager.OnBackStackChangedListener mBackStackListener = this::resolveToolbarNavigationIcon;
    private boolean isZoomPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(Settings.get().ui().getMainTheme());
        Utils.prepareDensity(this);
        super.onCreate(savedInstanceState);
        isZoomPhoto = Settings.get().other().isDo_zoom_photo();
        setContentView(R.layout.activity_no_main);

        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.setStatusBarColor(CurrentTheme.getStatusBarColor(this));
        w.setNavigationBarColor(CurrentTheme.getNavigationBarColor(this));

        getSupportFragmentManager().addOnBackStackChangedListener(mBackStackListener);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utils.updateActivityContext(newBase));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isZoomPhoto) {
            return super.dispatchTouchEvent(ev);
        }
        return ZoomHelper.Companion.getInstance().dispatchTouchEvent(ev, this) || super.dispatchTouchEvent(ev);
    }

    @IdRes
    protected int getMainContainerViewId() {
        return R.id.fragment;
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        mToolbar = toolbar;
        resolveToolbarNavigationIcon();
    }

    private void resolveToolbarNavigationIcon() {
        if (Objects.isNull(mToolbar)) return;

        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 1) {
            mToolbar.setNavigationIcon(R.drawable.arrow_left);
        } else {
            mToolbar.setNavigationIcon(R.drawable.close);
        }

        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();

        Fragment front = getSupportFragmentManager().findFragmentById(getMainContainerViewId());
        if (front instanceof BackPressCallback) {
            if (!(((BackPressCallback) front).onBackPressed())) {
                return;
            }
        }

        if (fm.getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            supportFinishAfterTransition();
        }
    }

    @Override
    protected void onDestroy() {
        getSupportFragmentManager().removeOnBackStackChangedListener(mBackStackListener);
        super.onDestroy();
    }
}
