package dev.idm.vkp.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import dev.idm.vkp.R;
import dev.idm.vkp.fragment.EnterPinFragment;
import dev.idm.vkp.util.Utils;

public class EnterPinActivity extends NoMainActivity {

    public static Class<?> getClass(Context context) {
        return Utils.is600dp(context) ? EnterPinActivity.class : EnterPinActivityPortraitOnly.class;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, EnterPinFragment.newInstance())
                    .commit();
        }
    }
}
