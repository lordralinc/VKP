package dev.idm.vkp.fragment.attachments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.ActivityFeatures;
import dev.idm.vkp.model.Post;
import dev.idm.vkp.model.WallEditorAttrs;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.PostEditPresenter;
import dev.idm.vkp.mvp.view.IPostEditView;
import dev.idm.vkp.util.AssertUtils;

public class PostEditFragment extends AbsPostEditFragment<PostEditPresenter, IPostEditView>
        implements IPostEditView {

    public static PostEditFragment newInstance(Bundle args) {
        PostEditFragment fragment = new PostEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static Bundle buildArgs(int accountId, @NonNull Post post, @NonNull WallEditorAttrs attrs) {
        Bundle args = new Bundle();
        args.putParcelable(Extra.POST, post);
        args.putParcelable(Extra.ATTRS, attrs);
        args.putInt(Extra.ACCOUNT_ID, accountId);
        return args;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @NotNull
    @Override
    public IPresenterFactory<PostEditPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            Post post = requireArguments().getParcelable(Extra.POST);
            int accountId = requireArguments().getInt(Extra.ACCOUNT_ID);
            WallEditorAttrs attrs = requireArguments().getParcelable(Extra.ATTRS);

            AssertUtils.requireNonNull(post);
            AssertUtils.requireNonNull(attrs);
            return new PostEditPresenter(accountId, post, attrs, saveInstanceState);
        };
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_attchments, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        new ActivityFeatures.Builder()
                .begin()
                .setHideNavigationMenu(true)
                .setBarsColored(requireActivity(), true)
                .build()
                .apply(requireActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ready:
                getPresenter().fireReadyClick();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void closeAsSuccess() {
        requireActivity().onBackPressed();
    }

    @Override
    public void showConfirmExitDialog() {
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.confirmation)
                .setMessage(R.string.save_changes_question)
                .setPositiveButton(R.string.button_yes, (dialog, which) -> getPresenter().fireExitWithSavingConfirmed())
                .setNegativeButton(R.string.button_no, (dialog, which) -> getPresenter().fireExitWithoutSavingClick())
                .setNeutralButton(R.string.button_cancel, null)
                .show();
    }

    @Override
    public boolean onBackPressed() {
        return getPresenter().onBackPressed();
    }
}