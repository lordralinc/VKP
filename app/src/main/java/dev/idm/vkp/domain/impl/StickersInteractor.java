package dev.idm.vkp.domain.impl;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.idm.vkp.api.interfaces.INetworker;
import dev.idm.vkp.api.model.VKApiSticker;
import dev.idm.vkp.api.model.VKApiStickerSet;
import dev.idm.vkp.api.model.VkApiStickersKeywords;
import dev.idm.vkp.db.interfaces.IStickersStorage;
import dev.idm.vkp.db.model.entity.StickerSetEntity;
import dev.idm.vkp.db.model.entity.StickersKeywordsEntity;
import dev.idm.vkp.domain.IStickersInteractor;
import dev.idm.vkp.domain.mappers.Dto2Entity;
import dev.idm.vkp.domain.mappers.Entity2Model;
import dev.idm.vkp.model.Sticker;
import dev.idm.vkp.model.StickerSet;
import dev.idm.vkp.settings.Settings;
import dev.idm.vkp.util.AppPerms;
import dev.idm.vkp.util.Utils;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import static dev.idm.vkp.domain.mappers.MapUtil.mapAll;
import static dev.idm.vkp.util.Utils.listEmptyIfNull;


public class StickersInteractor implements IStickersInteractor {

    private final INetworker networker;
    private final IStickersStorage storage;

    public StickersInteractor(INetworker networker, IStickersStorage storage) {
        this.networker = networker;
        this.storage = storage;
    }

    @Override
    public Completable getAndStore(int accountId) {
        Completable stickerSet = networker.vkDefault(accountId)
                .store()
                .getStickers()
                .flatMapCompletable(items -> {
                    List<VKApiStickerSet.Product> list = listEmptyIfNull(items.sticker_pack.items);

                    if (Settings.get().ui().isStickers_by_new()) {
                        Collections.reverse(list);
                    }

                    StickerSetEntity temp = new StickerSetEntity(-1).setTitle("recent")
                            .setStickers(mapAll(listEmptyIfNull(listEmptyIfNull(items.recent.items)), Dto2Entity::mapSticker)).setActive(true).setPurchased(true);
                    List<StickerSetEntity> ret = mapAll(list, Dto2Entity::mapStikerSet);
                    ret.add(temp);
                    return storage.store(accountId, ret);
                });
        if (Settings.get().other().isHint_stickers()) {
            return stickerSet.andThen(networker.vkDefault(accountId)
                    .store()
                    .getStickerKeywords()
                    .flatMapCompletable(hint -> getStickersKeywordsAndStore(accountId, hint)));
        }
        return stickerSet;
    }

    private List<StickersKeywordsEntity> generateKeywords(@NonNull List<List<VKApiSticker>> s, @NonNull List<List<String>> w) {
        List<StickersKeywordsEntity> ret = new ArrayList<>(w.size());
        for (int i = 0; i < w.size(); i++) {
            if (Utils.isEmpty(w.get(i))) {
                continue;
            }
            ret.add(new StickersKeywordsEntity(w.get(i), mapAll(listEmptyIfNull(s.get(i)), Dto2Entity::mapSticker)));
        }
        return ret;
    }

    private Completable getStickersKeywordsAndStore(int accountId, VkApiStickersKeywords items) {
        List<List<VKApiSticker>> s = listEmptyIfNull(items.words_stickers);
        List<List<String>> w = listEmptyIfNull(items.keywords);
        List<StickersKeywordsEntity> temp = new ArrayList<>();
        if (Utils.isEmpty(w) || Utils.isEmpty(s) || w.size() != s.size()) {
            return storage.storeKeyWords(accountId, temp);
        }

        temp.addAll(generateKeywords(s, w));
        return storage.storeKeyWords(accountId, temp);
    }

    @Override
    public Single<List<StickerSet>> getStickers(int accountId) {
        return storage.getPurchasedAndActive(accountId)
                .map(entities -> mapAll(entities, Entity2Model::map));
    }

    @Override
    public Single<List<Sticker>> getKeywordsStickers(int accountId, String s) {
        return storage.getKeywordsStickers(accountId, s)
                .map(entities -> mapAll(entities, Entity2Model::buildStickerFromDbo));
    }

    @Override
    public Completable PlaceToStickerCache(Context context) {
        if (!AppPerms.hasReadWriteStoragePermission(context))
            return Completable.complete();
        return Completable.create(t -> {
            File temp = new File(Settings.get().other().getStickerDir());
            if (!temp.exists()) {
                t.onComplete();
                return;
            }
            File[] file_list = temp.listFiles();
            if (file_list == null || file_list.length <= 0) {
                t.onComplete();
                return;
            }
            Utils.getCachedMyStickers().clear();
            for (File u : file_list) {
                if (u.isFile() && (u.getName().contains(".png") || u.getName().contains(".webp"))) {
                    Utils.getCachedMyStickers().add(new Sticker.LocalSticker(u.getAbsolutePath(), false));
                } else if (u.isFile() && u.getName().contains(".json")) {
                    Utils.getCachedMyStickers().add(new Sticker.LocalSticker(u.getAbsolutePath(), true));
                }
            }
        });
    }
}
