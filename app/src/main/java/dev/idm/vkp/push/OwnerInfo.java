package dev.idm.vkp.push;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import dev.idm.vkp.R;
import dev.idm.vkp.domain.IOwnersRepository;
import dev.idm.vkp.domain.Repository;
import dev.idm.vkp.model.Community;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.User;
import dev.idm.vkp.util.Optional;
import io.reactivex.rxjava3.core.Single;

public class OwnerInfo {

    private final Owner owner;
    private final Bitmap avatar;

    private OwnerInfo(@NonNull Owner owner, Bitmap avatar) {
        this.owner = owner;
        this.avatar = avatar;
    }

    public static Single<OwnerInfo> getRx(@NonNull Context context, int accountId, int ownerId) {
        Context app = context.getApplicationContext();
        IOwnersRepository interactor = Repository.INSTANCE.getOwners();

        return interactor.getBaseOwnerInfo(accountId, ownerId, IOwnersRepository.MODE_ANY)
                .flatMap(owner -> Single.fromCallable(() -> NotificationUtils.loadRoundedImage(app, owner.get100photoOrSmaller(), R.drawable.ic_avatar_unknown))
                        .map(Optional::wrap)
                        .onErrorReturnItem(Optional.empty())
                        .map(optional -> new OwnerInfo(owner, optional.get())));
    }

    @NonNull
    public User getUser() {
        return (User) owner;
    }

    @NonNull
    public Owner getOwner() {
        return owner;
    }

    @NonNull
    public Community getCommunity() {
        return (Community) owner;
    }

    public Bitmap getAvatar() {
        return avatar;
    }
}