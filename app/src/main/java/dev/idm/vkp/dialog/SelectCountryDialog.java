package dev.idm.vkp.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import dev.idm.vkp.Extra;
import dev.idm.vkp.R;
import dev.idm.vkp.adapter.vkdatabase.CountriesAdapter;
import dev.idm.vkp.fragment.base.BaseMvpDialogFragment;
import dev.idm.vkp.listener.TextWatcherAdapter;
import dev.idm.vkp.model.database.Country;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.CountriesPresenter;
import dev.idm.vkp.mvp.view.ICountriesView;

import static dev.idm.vkp.util.Objects.nonNull;

public class SelectCountryDialog extends BaseMvpDialogFragment<CountriesPresenter, ICountriesView>
        implements CountriesAdapter.Listener, ICountriesView {

    public static final String REQUEST_CODE_COUNTRY = "request_country";
    private CountriesAdapter mAdapter;
    private View mLoadingView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = View.inflate(requireActivity(), R.layout.dialog_countries, null);

        Dialog dialog = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.countries_title)
                .setView(view)
                .setNegativeButton(R.string.button_cancel, null)
                .create();

        TextInputEditText filterView = view.findViewById(R.id.input);
        filterView.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPresenter().fireFilterEdit(s);
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mAdapter = new CountriesAdapter(requireActivity(), Collections.emptyList());
        mAdapter.setListener(this);

        recyclerView.setAdapter(mAdapter);

        mLoadingView = view.findViewById(R.id.progress_root);

        fireViewCreated();
        return dialog;
    }

    @Override
    public void onClick(Country country) {
        getPresenter().fireCountryClick(country);
    }

    @Override
    public void displayData(List<Country> countries) {
        if (nonNull(mAdapter)) {
            mAdapter.setData(countries);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void displayLoading(boolean loading) {
        if (nonNull(mLoadingView)) {
            mLoadingView.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void returnSelection(Country country) {
        Bundle intent = new Bundle();
        intent.putParcelable(Extra.COUNTRY, country);
        intent.putInt(Extra.ID, country.getId());
        intent.putString(Extra.TITLE, country.getTitle());

        if (getArguments() != null) {
            intent.putAll(getArguments());
        }
        getParentFragmentManager().setFragmentResult(REQUEST_CODE_COUNTRY, intent);
        dismiss();
    }

    @NotNull
    @Override
    public IPresenterFactory<CountriesPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new CountriesPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                saveInstanceState
        );
    }
}
