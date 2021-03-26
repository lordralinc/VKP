package dev.idm.vkp.listener;

import dev.idm.vkp.model.drawer.SectionMenuItem;

public interface OnSectionResumeCallback {
    void onSectionResume(SectionMenuItem sectionDrawerItem);

    void onChatResume(int accountId, int peerId, String title, String imgUrl);

    void onClearSelection();
}
