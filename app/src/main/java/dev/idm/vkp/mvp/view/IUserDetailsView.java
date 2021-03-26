package dev.idm.vkp.mvp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.Peer;
import dev.idm.vkp.model.Photo;
import dev.idm.vkp.model.User;
import dev.idm.vkp.model.menu.AdvancedItem;
import dev.idm.vkp.mvp.core.IMvpView;
import dev.idm.vkp.mvp.view.base.IAccountDependencyView;

public interface IUserDetailsView extends IMvpView, IAccountDependencyView, IErrorView {
    void displayData(@NonNull List<AdvancedItem> items);

    void displayToolbarTitle(User user);

    void openOwnerProfile(int accountId, int ownerId, @Nullable Owner owner);

    void onPhotosLoaded(Photo photo);

    void openPhotoAlbum(int accountId, int ownerId, int albumId, ArrayList<Photo> photos, int position);

    void openChatWith(int accountId, int messagesOwnerId, @NonNull Peer peer);

    void openPhotoUser(User user);
}
