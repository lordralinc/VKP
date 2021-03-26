package dev.idm.vkp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.idm.vkp.R;
import dev.idm.vkp.adapter.RecyclerMenuAdapter;
import dev.idm.vkp.domain.IOwnersRepository;
import dev.idm.vkp.domain.Repository;
import dev.idm.vkp.model.Icon;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.Text;
import dev.idm.vkp.model.WallEditorAttrs;
import dev.idm.vkp.model.menu.AdvancedItem;
import dev.idm.vkp.settings.ISettings;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.Objects;
import dev.idm.vkp.util.RxUtils;
import dev.idm.vkp.util.Utils;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static dev.idm.vkp.util.Utils.firstNonEmptyString;

public class PostPublishPrepareActivity extends AppCompatActivity implements RecyclerMenuAdapter.ActionListener {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private RecyclerMenuAdapter adapter;
    private RecyclerView recyclerView;
    private View proressView;

    private ActivityUtils.StreamData streams;
    private String links;
    private String mime;
    private int accountId;
    private boolean loading;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utils.updateActivityContext(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(Settings.get().ui().getMainTheme());
        Utils.prepareDensity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_publish_prepare);

        adapter = new RecyclerMenuAdapter(R.layout.item_advanced_menu_alternative, Collections.emptyList());
        adapter.setActionListener(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        proressView = findViewById(R.id.progress_view);

        if (Objects.isNull(savedInstanceState)) {
            accountId = Settings.get().accounts().getCurrent();

            if (accountId == ISettings.IAccountsSettings.INVALID_ID) {
                Toast.makeText(this, R.string.error_post_creation_no_auth, Toast.LENGTH_LONG).show();
                finish();
            }

            streams = ActivityUtils.checkLocalStreams(this);
            mime = streams == null ? null : streams.mime;
            links = ActivityUtils.checkLinks(this);

            setLoading(true);
            IOwnersRepository interactor = Repository.INSTANCE.getOwners();
            compositeDisposable.add(interactor.getCommunitiesWhereAdmin(accountId, true, true, false)
                    .zipWith(interactor.getBaseOwnerInfo(accountId, accountId, IOwnersRepository.MODE_NET), (owners, owner) -> {
                        List<Owner> result = new ArrayList<>();
                        result.add(owner);
                        result.addAll(owners);
                        return result;
                    })
                    .compose(RxUtils.applySingleIOToMainSchedulers())
                    .subscribe(this::onOwnersReceived, this::onOwnersGetError));
        }

        updateViews();
    }

    private void onOwnersGetError(Throwable throwable) {
        setLoading(false);
        Toast.makeText(this, firstNonEmptyString(throwable.getMessage(), throwable.toString()), Toast.LENGTH_LONG).show();

        finish();
    }

    private void onOwnersReceived(List<Owner> owners) {
        setLoading(false);

        if (owners.isEmpty()) {
            finish(); // wtf???
            return;
        }

        Owner iam = owners.get(0);

        List<AdvancedItem> items = new ArrayList<>();

        for (Owner owner : owners) {
            WallEditorAttrs attrs = new WallEditorAttrs(owner, iam);

            items.add(new AdvancedItem(owner.getOwnerId(), new Text(owner.getFullName()))
                    .setIcon(Icon.fromUrl(owner.get100photoOrSmaller()))
                    .setSubtitle(new Text("@" + owner.getDomain()))
                    .setTag(attrs));
        }

        adapter.setItems(items);
    }

    private void setLoading(boolean loading) {
        this.loading = loading;
        updateViews();
    }

    private void updateViews() {
        recyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
        proressView.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onClick(AdvancedItem item) {
        WallEditorAttrs attrs = (WallEditorAttrs) item.getTag();

        Intent intent = PostCreateActivity.newIntent(this, accountId, attrs, streams == null ? null : streams.uris, links, mime);
        startActivity(intent);

        finish();
    }

    @Override
    public void onLongClick(AdvancedItem item) {

    }
}
