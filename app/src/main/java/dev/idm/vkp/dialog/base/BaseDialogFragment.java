package dev.idm.vkp.dialog.base;

import androidx.fragment.app.DialogFragment;

import dev.idm.vkp.util.ViewUtils;

public abstract class BaseDialogFragment extends DialogFragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        ViewUtils.keyboardHide(requireActivity());
    }
}