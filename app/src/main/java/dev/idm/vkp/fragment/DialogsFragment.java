package dev.idm.vkp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dev.idm.vkp.CheckDonate;
import dev.idm.vkp.Extra;
import dev.idm.vkp.HelperSimple;
import dev.idm.vkp.R;
import dev.idm.vkp.activity.ActivityFeatures;
import dev.idm.vkp.activity.ActivityUtils;
import dev.idm.vkp.activity.EnterPinActivity;
import dev.idm.vkp.activity.MainActivity;
import dev.idm.vkp.activity.SelectProfilesActivity;
import dev.idm.vkp.adapter.DialogsAdapter;
import dev.idm.vkp.api.Apis;
import dev.idm.vkp.api.interfaces.IAccountApis;
import dev.idm.vkp.dialog.DialogNotifOptionsDialog;
import dev.idm.vkp.fragment.base.BaseMvpFragment;
import dev.idm.vkp.fragment.search.SearchContentType;
import dev.idm.vkp.fragment.search.criteria.DialogsSearchCriteria;
import dev.idm.vkp.fragment.search.criteria.MessageSeachCriteria;
import dev.idm.vkp.listener.EndlessRecyclerOnScrollListener;
import dev.idm.vkp.listener.OnSectionResumeCallback;
import dev.idm.vkp.listener.PicassoPauseOnScrollListener;
import dev.idm.vkp.model.Dialog;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.Peer;
import dev.idm.vkp.model.User;
import dev.idm.vkp.mvp.core.IPresenterFactory;
import dev.idm.vkp.mvp.presenter.DialogsPresenter;
import dev.idm.vkp.mvp.view.IDialogsView;
import dev.idm.vkp.place.Place;
import dev.idm.vkp.place.PlaceFactory;
import dev.idm.vkp.settings.ISettings;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.AssertUtils;
import dev.idm.vkp.util.CustomToast;
import dev.idm.vkp.util.InputTextDialog;
import dev.idm.vkp.util.Optional;
import dev.idm.vkp.util.Utils;
import dev.idm.vkp.util.ViewUtils;

import static dev.idm.vkp.util.Objects.nonNull;

public class DialogsFragment extends BaseMvpFragment<DialogsPresenter, IDialogsView>
        implements IDialogsView, DialogsAdapter.ClickListener {

    private final ActivityResultLauncher<Intent> requestSelectProfile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ArrayList<Owner> users = result.getData().getParcelableArrayListExtra(Extra.OWNERS);
                    AssertUtils.requireNonNull(users);

                    getPresenter().fireUsersForChatSelected(users);
                }
            });
    private RecyclerView mRecyclerView;
    private DialogsAdapter mAdapter;
    private final ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NotNull RecyclerView recyclerView,
                              @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
            viewHolder.itemView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
            Dialog dialog = mAdapter.getByPosition(viewHolder.getAdapterPosition());
            if (isPresenterPrepared()) {
                getPresenter().fireRepost(dialog);
            }
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }
    };
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isCreateChat = true;
    private FloatingActionButton mFab;
    private final RecyclerView.OnScrollListener mFabScrollListener = new RecyclerView.OnScrollListener() {
        int scrollMinOffset;

        @Override
        public void onScrolled(@NotNull RecyclerView view, int dx, int dy) {
            if (scrollMinOffset == 0) {
                // one-time-init
                scrollMinOffset = (int) Utils.dpToPx(2, view.getContext());
            }

            if (dy > scrollMinOffset && mFab.isShown()) {
                mFab.hide();
            }

            if (dy < -scrollMinOffset && !mFab.isShown()) {
                mFab.show();
                if (view.getLayoutManager() instanceof LinearLayoutManager) {
                    LinearLayoutManager myLayoutManager = (LinearLayoutManager) view.getLayoutManager();
                    ToggleFab(myLayoutManager.findFirstVisibleItemPosition() > 20);
                }
            }
        }
    };
    private final ActivityResultLauncher<Intent> requestEnterPin = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Settings.get().security().setShowHiddenDialogs(true);
                    ReconfigureOptionsHide(true);
                    notifyDataSetChanged();
                }
            });

    public static DialogsFragment newInstance(int accountId, int dialogsOwnerId, @Nullable String subtitle) {
        DialogsFragment fragment = new DialogsFragment();
        Bundle args = new Bundle();
        args.putString(Extra.SUBTITLE, subtitle);
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.OWNER_ID, dialogsOwnerId);
        fragment.setArguments(args);
        return fragment;
    }

    private void ToggleFab(boolean isUp) {
        if (isCreateChat == isUp) {
            isCreateChat = !isUp;
            mFab.setImageResource(isCreateChat ? R.drawable.pencil : R.drawable.ic_outline_keyboard_arrow_up);
        }
    }

    private void onSecurityClick() {
        if (Settings.get().security().isUsePinForSecurity()) {
            requestEnterPin.launch(new Intent(requireActivity(), EnterPinActivity.class));
        } else {
            Settings.get().security().setShowHiddenDialogs(true);
            ReconfigureOptionsHide(true);
            notifyDataSetChanged();
        }
    }

    private void ReconfigureOptionsHide(boolean isShowHidden) {
        mAdapter.updateShowHidden(isShowHidden);
        if (Settings.get().security().getSetSize("hidden_dialogs") <= 0) {
            mFab.setImageResource(R.drawable.pencil);
            Settings.get().security().setShowHiddenDialogs(false);
            return;
        }

        mFab.setImageResource(isShowHidden ? R.drawable.offline : R.drawable.pencil);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
     }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_dialogs, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        OptionView optionView = new OptionView();
        getPresenter().fireOptionViewCreated(optionView);
        menu.findItem(R.id.action_search).setVisible(optionView.canSearch);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                getPresenter().fireSearchClick();
                break;
            case R.id.action_star:
                getPresenter().fireImportantClick();
                break;
        }
        return true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dialogs, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        mFab = root.findViewById(R.id.fab);
        mFab.setOnClickListener(v -> {
            if (Settings.get().security().getShowHiddenDialogs()) {
                Settings.get().security().setShowHiddenDialogs(false);
                ReconfigureOptionsHide(false);
                notifyDataSetChanged();
            } else {
                if (isCreateChat) {
                    createGroupChat();
                } else {
                    mRecyclerView.smoothScrollToPosition(0);
                    ToggleFab(false);
                }
            }
        });

        mFab.setOnLongClickListener(v -> {
            if (!Settings.get().security().getShowHiddenDialogs() && Settings.get().security().getSetSize("hidden_dialogs") > 0) {
                onSecurityClick();
            }
            return true;
        });

        mRecyclerView = root.findViewById(R.id.recycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mRecyclerView.addOnScrollListener(new PicassoPauseOnScrollListener(DialogsAdapter.PICASSO_TAG));
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });

        mSwipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());

        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout);

        mAdapter = new DialogsAdapter(requireActivity(), Collections.emptyList());
        mAdapter.setClickListener(this);

        mRecyclerView.setAdapter(mAdapter);
        ReconfigureOptionsHide(Settings.get().security().getShowHiddenDialogs());
        return root;
    }

    @Override
    public void setCreateGroupChatButtonVisible(boolean visible) {
        if (nonNull(mFab) && nonNull(mRecyclerView)) {
            mFab.setVisibility(visible ? View.VISIBLE : View.GONE);
            if (visible) {
                mRecyclerView.addOnScrollListener(mFabScrollListener);
            } else {
                mRecyclerView.removeOnScrollListener(mFabScrollListener);
            }
        }
    }

    @Override
    public void notifyHasAttachments(boolean has) {
        if (has) {
            new ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(mRecyclerView);
            if (HelperSimple.INSTANCE.needHelp(HelperSimple.DIALOG_SEND_HELPER, 3)) {
                showSnackbar(R.string.dialog_send_helper, true);
            }
        }
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        getPresenter().fireDialogClick(dialog);
    }

    @Override
    public boolean onDialogLongClick(Dialog dialog) {
        List<String> options = new ArrayList<>();

        ContextView contextView = new ContextView();
        getPresenter().fireContextViewCreated(contextView, dialog);

        String delete = getString(R.string.delete);
        String addToHomeScreen = getString(R.string.add_to_home_screen);
        String notificationSettings = getString(R.string.peer_notification_settings);
        String addToShortcuts = getString(R.string.add_to_launcer_shortcuts);

        String setHide = getString(R.string.hide_dialog);
        String setShow = getString(R.string.set_no_hide_dialog);
        String setRead = getString(R.string.read);

        if (contextView.canDelete) {
            options.add(delete);
        }

        if (contextView.canAddToHomescreen) {
            options.add(addToHomeScreen);
        }

        if (contextView.canConfigNotifications) {
            options.add(notificationSettings);
        }

        if (contextView.canAddToShortcuts && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            options.add(addToShortcuts);
        }

        if (!contextView.isHidden) {
            options.add(setHide);
        }

        if (contextView.isHidden && Settings.get().security().getShowHiddenDialogs()) {
            options.add(setShow);
        }

        if (contextView.canRead) {
            options.add(setRead);
        }

        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(contextView.isHidden && !Settings.get().security().getShowHiddenDialogs() ? getString(R.string.dialogs) : dialog.getDisplayTitle(requireActivity()))
                .setItems(options.toArray(new String[0]), (dialogInterface, which) -> {
                    String selected = options.get(which);
                    if (selected.equals(delete)) {
                        Utils.ThemedSnack(requireView(), R.string.delete_chat_do, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.button_yes,
                                v1 -> getPresenter().fireRemoveDialogClick(dialog)).show();
                    } else if (selected.equals(addToHomeScreen)) {
                        getPresenter().fireCreateShortcutClick(dialog);
                    } else if (selected.equals(notificationSettings)) {
                        getPresenter().fireNotificationsSettingsClick(dialog);
                    } else if (selected.equals(addToShortcuts)) {
                        getPresenter().fireAddToLauncherShortcuts(dialog);
                    } else if (selected.equals(setRead)) {
                        getPresenter().fireRead(dialog);
                    } else if (selected.equals(setHide)) {
                        if (!CheckDonate.isFullVersion(requireActivity())) {
                            return;
                        }
                        if (!Settings.get().security().isUsePinForSecurity()) {
                            CustomToast.CreateCustomToast(requireActivity()).showToastError(R.string.not_supported_hide);
                            PlaceFactory.getSecuritySettingsPlace().tryOpenWith(requireActivity());
                        } else {
                            Settings.get().security().AddValueToSet(dialog.getId(), "hidden_dialogs");
                            ReconfigureOptionsHide(Settings.get().security().getShowHiddenDialogs());
                            notifyDataSetChanged();
                        }
                    } else if (selected.equals(setShow)) {
                        Settings.get().security().RemoveValueFromSet(dialog.getId(), "hidden_dialogs");
                        ReconfigureOptionsHide(Settings.get().security().getShowHiddenDialogs());
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.button_cancel, null)
                .show();

        return !options.isEmpty();
    }

    @Override
    public void askToReload() {
        View view = getView();
        if (nonNull(view)) {
            Snackbar.make(view, R.string.update_dialogs, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.button_yes, v -> {
                getPresenter().fireRefresh();
            }).show();
        }
    }

    @Override
    public void onAvatarClick(Dialog dialog) {
        getPresenter().fireDialogAvatarClick(dialog);
    }

    private void createGroupChat() {
        requestSelectProfile.launch(SelectProfilesActivity.startFriendsSelection(requireActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.get().ui().notifyPlaceResumed(Place.DIALOGS);

        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setTitle(R.string.dialogs);
            actionBar.setSubtitle(requireArguments().getString(Extra.SUBTITLE));
        }

        if (requireActivity() instanceof OnSectionResumeCallback) {
            ((OnSectionResumeCallback) requireActivity()).onSectionResume(AdditionalNavigationFragment.SECTION_ITEM_DIALOGS);
        }

        new ActivityFeatures.Builder()
                .begin()
                .setHideNavigationMenu(false)
                .setBarsColored(requireActivity(), true)
                .build()
                .apply(requireActivity());
    }

    /*
    @Override
    public void onDestroyView() {
        if (nonNull(mAdapter)) {
            mAdapter.cleanup();
        }

        if (nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }

        super.onDestroyView();
    }

     */

    @Override
    public void displayData(List<Dialog> data) {
        if (nonNull(mAdapter)) {
            mAdapter.setData(data);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (nonNull(mAdapter)) {
            mAdapter.updateHidden(Settings.get().security().loadSet("hidden_dialogs"));
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataAdded(int position, int count) {
        if (nonNull(mAdapter)) {
            mAdapter.updateHidden(Settings.get().security().loadSet("hidden_dialogs"));
            mAdapter.notifyItemRangeInserted(position, count);
        }
    }


    private void updateSubscribe(Optional<String> stringOptional) {
        String text = stringOptional.get();
        Log.d("API", text);
        try {
            JSONObject response = new JSONObject(text).getJSONObject("response");
            JSONObject conversations = response.getJSONObject("conversations");
            JSONArray items = conversations.getJSONArray("items");
            int count = conversations.getInt("count");

            for (int i = 0; i < count; i++){
                JSONObject item = items.getJSONObject(i);
                int vk_mask = item.getInt("sound");
                int current_mask = Settings.get().notifications().getNotifPref(
                        Settings.get().accounts().getCurrent(),
                        item.getInt("peer_id")
                );

                if (current_mask > vk_mask && vk_mask == 0){
                    Settings
                        .get()
                        .notifications()
                        .setNotifPref(
                            Settings.get().accounts().getCurrent(),
                            item.getInt("peer_id"),
                            vk_mask
                        );
                }

                if (current_mask == 0 && vk_mask != 0){
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                    int value = sharedPreferences.getBoolean("high_notif_priority", false) ? ISettings.INotificationSettings.FLAG_HIGH_PRIORITY : 0;
                    if (sharedPreferences.getBoolean("new_dialog_message_notif_enable", true)) {
                        value += ISettings.INotificationSettings.FLAG_SHOW_NOTIF;
                    }

                    if (sharedPreferences.getBoolean("new_dialog_message_notif_sound", true)) {
                        value += ISettings.INotificationSettings.FLAG_SOUND;
                    }

                    if (sharedPreferences.getBoolean("new_dialog_message_notif_vibration", true)) {
                        value += ISettings.INotificationSettings.FLAG_VIBRO;
                    }

                    if (sharedPreferences.getBoolean("new_dialog_message_notif_led", true)) {
                        value += ISettings.INotificationSettings.FLAG_LED;
                    }

                    if (sharedPreferences.getBoolean("new_dialog_message_notif_push", true)) {
                        value += ISettings.INotificationSettings.FLAG_PUSH;
                    }


                    Settings
                        .get()
                        .notifications()
                        .setNotifPref(
                            Settings.get().accounts().getCurrent(),
                            item.getInt("peer_id"),
                            value
                        );
                }
            }
        } catch (Exception e) {
            Log.e("API", e.getMessage());
        }

    }

    @Override
    public void showRefreshing(boolean refreshing) {
        int account_id = Settings.get().accounts().getCurrent();
        String access_token = Settings.get().accounts().getAccessToken(account_id);

        IAccountApis api = Apis.get().vkManual(account_id, access_token);
        Map<String, String> params = new HashMap<>();

        if (nonNull(mSwipeRefreshLayout)) {
            api
                .other()
                .rawRequest("account.getPushSettings", params)
                .subscribe(this::updateSubscribe);
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }



    @Override
    public void goToChat(int accountId, int messagesOwnerId, int peerId, String title, String ava_url) {
        PlaceFactory.getChatPlace(accountId, messagesOwnerId, new Peer(peerId).setTitle(title).setAvaUrl(ava_url)).tryOpenWith(requireActivity());
    }

    @Override
    public void goToSearch(int accountId) {
        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.info)
                .setCancelable(true)
                .setMessage(R.string.what_search)
                .setNegativeButton(R.string.search_dialogs, (dialog, which) -> {
                    DialogsSearchCriteria criteria = new DialogsSearchCriteria("");

                    PlaceFactory.getSingleTabSearchPlace(accountId, SearchContentType.DIALOGS, criteria)
                            .tryOpenWith(requireActivity());
                })
                .setPositiveButton(R.string.search_messages, (dialog, which) -> {
                    MessageSeachCriteria criteria = new MessageSeachCriteria("");

                    PlaceFactory.getSingleTabSearchPlace(accountId, SearchContentType.MESSAGES, criteria)
                            .tryOpenWith(requireActivity());
                })
                .show();
    }

    @Override
    public void goToImportant(int accountId) {
        PlaceFactory.getImportantMessages(accountId).tryOpenWith(requireActivity());
    }


    @Override
    public void showSnackbar(@StringRes int res, boolean isLong) {
        View view = getView();
        if (nonNull(view)) {
            Snackbar.make(view, res, isLong ? BaseTransientBottomBar.LENGTH_LONG : BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showEnterNewGroupChatTitle(List<User> users) {
        new InputTextDialog.Builder(requireActivity())
                .setTitleRes(R.string.set_groupchat_title)
                .setAllowEmpty(true)
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .setCallback(newValue -> getPresenter().fireNewGroupChatTitleEntered(users, newValue))
                .show();
    }

    @Override
    public void showNotificationSettings(int accountId, int peerId) {
        DialogNotifOptionsDialog dialog = DialogNotifOptionsDialog.newInstance(accountId, peerId);
        dialog.show(getParentFragmentManager(), "dialog-notif-options");
    }

    @Override
    public void goToOwnerWall(int accountId, int ownerId, @Nullable Owner owner) {
        PlaceFactory.getOwnerWallPlace(accountId, ownerId, owner).tryOpenWith(requireActivity());
    }

    @NotNull
    @Override
    public IPresenterFactory<DialogsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new DialogsPresenter(
                requireArguments().getInt(Extra.ACCOUNT_ID),
                requireArguments().getInt(Extra.OWNER_ID),
                requireActivity().getIntent().getParcelableExtra(MainActivity.EXTRA_INPUT_ATTACHMENTS),
                saveInstanceState
        );
    }

    private static final class OptionView implements IOptionView {

        boolean canSearch;

        @Override
        public void setCanSearch(boolean can) {
            canSearch = can;
        }
    }

    private static final class ContextView implements IContextView {

        boolean canDelete;
        boolean canAddToHomescreen;
        boolean canConfigNotifications;
        boolean canAddToShortcuts;
        boolean canRead;
        boolean isHidden;

        @Override
        public void setCanDelete(boolean can) {
            canDelete = can;
        }

        @Override
        public void setCanAddToHomescreen(boolean can) {
            canAddToHomescreen = can;
        }

        @Override
        public void setCanConfigNotifications(boolean can) {
            canConfigNotifications = can;
        }

        @Override
        public void setCanAddToShortcuts(boolean can) {
            canAddToShortcuts = can;
        }

        @Override
        public void setIsHidden(boolean can) {
            isHidden = can;
        }

        @Override
        public void setCanRead(boolean can) {
            canRead = can;
        }
    }
}
