package dev.idm.vkp.settings;

import android.content.Context;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dev.idm.vkp.model.drawer.AbsMenuItem;
import dev.idm.vkp.model.drawer.RecentChat;

import static dev.idm.vkp.util.Utils.safeIsEmpty;

class RecentChatsSettings implements ISettings.IRecentChats {

    private final Context app;

    private final Gson gson;

    RecentChatsSettings(Context app) {
        this.app = app.getApplicationContext();
        gson = new Gson();
    }

    private static String recentChatKeyFor(int aid) {
        return "recent" + aid;
    }

    @Override
    public List<RecentChat> get(int accountId) {
        List<RecentChat> recentChats = new ArrayList<>();

        Set<String> stringSet = PreferenceManager.getDefaultSharedPreferences(app)
                .getStringSet(recentChatKeyFor(accountId), null);

        if (!safeIsEmpty(stringSet)) {
            for (String s : stringSet) {
                try {
                    RecentChat recentChat = gson.fromJson(s, RecentChat.class);
                    recentChats.add(recentChat);
                } catch (Exception ignored) {
                }
            }
        }

        return recentChats;
    }

    @Override
    public void store(int accountId, List<RecentChat> chats) {
        Set<String> target = new LinkedHashSet<>();
        for (AbsMenuItem item : chats) {
            if (item instanceof RecentChat) {

                if (((RecentChat) item).getAid() != accountId) continue;

                target.add(gson.toJson(item));
            }
        }

        PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putStringSet(recentChatKeyFor(accountId), target)
                .apply();
    }
}
