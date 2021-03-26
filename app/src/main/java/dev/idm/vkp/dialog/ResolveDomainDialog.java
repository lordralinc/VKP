package dev.idm.vkp.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.dialog.base.AccountDependencyDialogFragment;
import dev.idm.vkp.domain.IUtilsInteractor;
import dev.idm.vkp.domain.InteractorFactory;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.spots.SpotsDialog;
import dev.idm.vkp.util.Optional;
import dev.idm.vkp.util.RxUtils;
import dev.idm.vkp.util.Utils;

public class ResolveDomainDialog extends AccountDependencyDialogFragment {

    private int mAccountId;
    private String url;
    private String domain;
    private IUtilsInteractor mUtilsInteractor;

    public static Bundle buildArgs(int aid, String url, String domain) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, aid);
        args.putString(Extra.URL, url);
        args.putString(Extra.DOMAIN, domain);
        return args;
    }

    public static ResolveDomainDialog newInstance(int aid, String url, String domain) {
        return newInstance(buildArgs(aid, url, domain));
    }

    public static ResolveDomainDialog newInstance(Bundle args) {
        ResolveDomainDialog domainDialog = new ResolveDomainDialog();
        domainDialog.setArguments(args);
        return domainDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountId = getArguments().getInt(Extra.ACCOUNT_ID);
        mUtilsInteractor = InteractorFactory.createUtilsInteractor();
        url = getArguments().getString(Extra.URL);
        domain = getArguments().getString(Extra.DOMAIN);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog progressDialog = new SpotsDialog.Builder().setContext(requireActivity()).setMessage(getString(R.string.please_wait)).setCancelable(true).setCancelListener(this).build();

        request();
        return progressDialog;
    }

    private void request() {
        appendDisposable(mUtilsInteractor.resolveDomain(mAccountId, domain)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onResolveResult, this::onResolveError));
    }

    private void onResolveError(Throwable t) {
        showErrorAlert(Utils.getCauseIfRuntime(t).getMessage());
    }

    private void onResolveResult(Optional<Owner> optionalOwner) {
        if (optionalOwner.isEmpty()) {
            PlaceFactory.getExternalLinkPlace(getAccountId(), url).tryOpenWith(requireActivity());
        } else {
            PlaceFactory.getOwnerWallPlace(mAccountId, optionalOwner.get()).tryOpenWith(requireActivity());
        }

        dismissAllowingStateLoss();
    }

    private void showErrorAlert(String error) {
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.error)
                .setMessage(error).setPositiveButton(R.string.try_again, (dialog, which) -> request())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dismiss())
                .show();
    }
}