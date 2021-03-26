package dev.idm.vkp.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.ActivityFeatures;
import dev.idm.vkp.activity.ActivityUtils;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.listener.OnSectionResumeCallback;
import dev.idm.vkp.listener.TextWatcherAdapter;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.RequestExecutePresenter;
import dev.idm.vkp.mvp.view.IRequestExecuteView;
import dev.idm.vkp.util.AppPerms;

public class       RequestExecuteFragment extends BaseMvpFragment<RequestExecutePresenter, IRequestExecuteView> implements IRequestExecuteView {

    private final AppPerms.doRequestPermissions requestWritePermission = AppPerms.requestPermissions(this,
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
            () -> {
                if (isPresenterPrepared()) {
                    getPresenter().fireWritePermissionResolved();
                }
            });
    private TextInputEditText mResposeBody;

    public static RequestExecuteFragment newInstance(int accountId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        RequestExecuteFragment fragment = new RequestExecuteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_request_executor, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        mResposeBody = root.findViewById(R.id.response_body);

        MaterialAutoCompleteTextView methodEditText = root.findViewById(R.id.method);
        methodEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPresenter().fireMethodEdit(s);
            }
        });

        TextInputEditText bodyEditText = root.findViewById(R.id.body);
        bodyEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPresenter().fireBodyEdit(s);
            }
        });

        root.findViewById(R.id.button_copy).setOnClickListener(v -> getPresenter().fireCopyClick());
        root.findViewById(R.id.button_save).setOnClickListener(v -> getPresenter().fireSaveClick());
        root.findViewById(R.id.button_execute).setOnClickListener(v -> getPresenter().fireExecuteClick());
        return root;
    }

    @NotNull
    @Override
    public IPresenterFactory<RequestExecutePresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new RequestExecutePresenter(requireArguments().getInt(Extra.ACCOUNT_ID), saveInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requireActivity() instanceof OnSectionResumeCallback) {
            ((OnSectionResumeCallback) requireActivity()).onClearSelection();
        }

        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setTitle(R.string.request_executor_title);
            actionBar.setSubtitle(null);
        }

        new ActivityFeatures.Builder()
                .begin()
                .setHideNavigationMenu(false)
                .setBarsColored(requireActivity(), true)
                .build()
                .apply(requireActivity());
    }

    @Override
    public void displayBody(String body) {
        safelySetText(mResposeBody, body);
    }

    @Override
    public void hideKeyboard() {
        ActivityUtils.hideSoftKeyboard(requireActivity());
    }

    @Override
    public void requestWriteExternalStoragePermission() {
        requestWritePermission.launch();
    }
}
