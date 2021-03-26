package dev.idm.vkp.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.adapter.vkdatabase.ChairsAdapter;
import dev.idm.vkp.dialog.base.AccountDependencyDialogFragment;
import dev.idm.vkp.domain.IDatabaseInteractor;
import dev.idm.vkp.domain.InteractorFactory;
import dev.idm.vkp.model.database.Chair;
import dev.idm.vkp.util.RxUtils;

public class SelectChairsDialog extends AccountDependencyDialogFragment implements ChairsAdapter.Listener {

    public static final String REQUEST_CODE_CHAIRS = "request_chairs";
    private static final int COUNT_PER_REQUEST = 1000;
    private int mAccountId;
    private int facultyId;
    private ArrayList<Chair> mData;
    private RecyclerView mRecyclerView;
    private ChairsAdapter mAdapter;
    private IDatabaseInteractor mDatabaseInteractor;

    public static SelectChairsDialog newInstance(int aid, int facultyId, Bundle additional) {
        Bundle args = additional == null ? new Bundle() : additional;
        args.putInt(Extra.FACULTY_ID, facultyId);
        args.putInt(Extra.ACCOUNT_ID, aid);
        SelectChairsDialog selectCityDialog = new SelectChairsDialog();
        selectCityDialog.setArguments(args);
        return selectCityDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountId = getArguments().getInt(Extra.ACCOUNT_ID);
        mDatabaseInteractor = InteractorFactory.createDatabaseInteractor();
        facultyId = getArguments().getInt(Extra.FACULTY_ID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View root = View.inflate(requireActivity(), R.layout.dialog_simple_recycler_view, null);
        mRecyclerView = root.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));

        boolean firstRun = false;
        if (mData == null) {
            mData = new ArrayList<>();
            firstRun = true;
        }

        mAdapter = new ChairsAdapter(requireActivity(), mData);
        mAdapter.setListener(this);
        mRecyclerView.setAdapter(mAdapter);

        if (firstRun) {
            request(0);
        }

        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.chair)
                .setView(root)
                .setNegativeButton(R.string.button_cancel, null)
                .create();
    }

    private void request(int offset) {
        appendDisposable(mDatabaseInteractor.getChairs(mAccountId, facultyId, COUNT_PER_REQUEST, offset)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(chairs -> onDataReceived(offset, chairs), throwable -> {
                }));
    }

    private void onDataReceived(int offset, List<Chair> chairs) {
        if (offset == 0) {
            mData.clear();
        }

        mData.addAll(chairs);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(Chair chair) {
        Bundle intent = new Bundle();
        intent.putParcelable(Extra.CHAIR, chair);
        intent.putInt(Extra.ID, chair.getId());
        intent.putString(Extra.TITLE, chair.getTitle());

        if (getArguments() != null) {
            intent.putAll(getArguments());
        }
        getParentFragmentManager().setFragmentResult(REQUEST_CODE_CHAIRS, intent);
        dismiss();
    }
}
