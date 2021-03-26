package dev.idm.vkp.view.emoji;

import dev.idm.vkp.model.StickerSet;

public class StickerSection extends AbsSection {

    public final StickerSet stickerSet;

    public StickerSection(StickerSet set) {
        super(TYPE_STICKER);
        stickerSet = set;
    }
}
