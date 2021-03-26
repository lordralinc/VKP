package dev.idm.vkp.db.model;

import dev.idm.vkp.db.model.entity.ArticleEntity;
import dev.idm.vkp.db.model.entity.AudioArtistEntity;
import dev.idm.vkp.db.model.entity.AudioEntity;
import dev.idm.vkp.db.model.entity.AudioMessageEntity;
import dev.idm.vkp.db.model.entity.AudioPlaylistEntity;
import dev.idm.vkp.db.model.entity.CallEntity;
import dev.idm.vkp.db.model.entity.DocumentEntity;
import dev.idm.vkp.db.model.entity.Entity;
import dev.idm.vkp.db.model.entity.EventEntity;
import dev.idm.vkp.db.model.entity.GiftItemEntity;
import dev.idm.vkp.db.model.entity.GraffitiEntity;
import dev.idm.vkp.db.model.entity.LinkEntity;
import dev.idm.vkp.db.model.entity.MarketAlbumEntity;
import dev.idm.vkp.db.model.entity.MarketEntity;
import dev.idm.vkp.db.model.entity.NotSupportedEntity;
import dev.idm.vkp.db.model.entity.PageEntity;
import dev.idm.vkp.db.model.entity.PhotoAlbumEntity;
import dev.idm.vkp.db.model.entity.PhotoEntity;
import dev.idm.vkp.db.model.entity.PollEntity;
import dev.idm.vkp.db.model.entity.PostEntity;
import dev.idm.vkp.db.model.entity.StickerEntity;
import dev.idm.vkp.db.model.entity.StoryEntity;
import dev.idm.vkp.db.model.entity.TopicEntity;
import dev.idm.vkp.db.model.entity.VideoEntity;
import dev.idm.vkp.db.model.entity.WallReplyEntity;


public final class AttachmentsTypes {
    public static final int PHOTO = 1;
    public static final int VIDEO = 2;
    public static final int AUDIO = 4;
    public static final int DOC = 8;
    public static final int POST = 16;
    public static final int ARTICLE = 32;
    public static final int LINK = 64;
    public static final int STORY = 128;
    public static final int CALL = 256;
    public static final int POLL = 512;
    public static final int PAGE = 1024;
    public static final int AUDIO_PLAYLIST = 2048;
    public static final int STICKER = 4096;
    public static final int TOPIC = 8192;
    public static final int AUDIO_MESSAGE = 16384;
    public static final int GIFT = 32768;
    public static final int GRAFFITY = 65536;
    public static final int ALBUM = 131072;
    public static final int NOT_SUPPORTED = 262144;
    public static final int WALL_REPLY = 524288;
    public static final int EVENT = 1048576;
    public static final int MARKET = 2097152;
    public static final int MARKET_ALBUM = 4194304;
    public static final int ARTIST = 8388608;

    private AttachmentsTypes() {
    }

    public static int typeForInstance(Entity entity) {
        if (entity instanceof PhotoEntity) {
            return PHOTO;
        } else if (entity instanceof VideoEntity) {
            return VIDEO;
        } else if (entity instanceof PostEntity) {
            return POST;
        } else if (entity instanceof DocumentEntity) {
            return DOC;
        } else if (entity instanceof PollEntity) {
            return POLL;
        } else if (entity instanceof AudioEntity) {
            return AUDIO;
        } else if (entity instanceof LinkEntity) {
            return LINK;
        } else if (entity instanceof StickerEntity) {
            return STICKER;
        } else if (entity instanceof PageEntity) {
            return PAGE;
        } else if (entity instanceof TopicEntity) {
            return TOPIC;
        } else if (entity instanceof AudioMessageEntity) {
            return AUDIO_MESSAGE;
        } else if (entity instanceof GiftItemEntity) {
            return GIFT;
        } else if (entity instanceof ArticleEntity) {
            return ARTICLE;
        } else if (entity instanceof StoryEntity) {
            return STORY;
        } else if (entity instanceof CallEntity) {
            return CALL;
        } else if (entity instanceof AudioArtistEntity) {
            return ARTIST;
        } else if (entity instanceof AudioPlaylistEntity) {
            return AUDIO_PLAYLIST;
        } else if (entity instanceof GraffitiEntity) {
            return GRAFFITY;
        } else if (entity instanceof PhotoAlbumEntity) {
            return ALBUM;
        } else if (entity instanceof NotSupportedEntity) {
            return NOT_SUPPORTED;
        } else if (entity instanceof WallReplyEntity) {
            return WALL_REPLY;
        } else if (entity instanceof EventEntity) {
            return EVENT;
        } else if (entity instanceof MarketEntity) {
            return MARKET;
        } else if (entity instanceof MarketAlbumEntity) {
            return MARKET_ALBUM;
        }

        throw new UnsupportedOperationException("Unsupported type: " + entity.getClass());
    }

    public static Class<? extends Entity> classForType(int type) {
        switch (type) {
            case PHOTO:
                return PhotoEntity.class;
            case VIDEO:
                return VideoEntity.class;
            case POST:
                return PostEntity.class;
            case DOC:
                return DocumentEntity.class;
            case POLL:
                return PollEntity.class;
            case AUDIO:
                return AudioEntity.class;
            case LINK:
                return LinkEntity.class;
            case STICKER:
                return StickerEntity.class;
            case PAGE:
                return PageEntity.class;
            case TOPIC:
                return TopicEntity.class;
            case AUDIO_MESSAGE:
                return AudioMessageEntity.class;
            case GIFT:
                return GiftItemEntity.class;
            case ARTICLE:
                return ArticleEntity.class;
            case STORY:
                return StoryEntity.class;
            case CALL:
                return CallEntity.class;
            case ARTIST:
                return AudioArtistEntity.class;
            case AUDIO_PLAYLIST:
                return AudioPlaylistEntity.class;
            case GRAFFITY:
                return GraffitiEntity.class;
            case ALBUM:
                return PhotoAlbumEntity.class;
            case NOT_SUPPORTED:
                return NotSupportedEntity.class;
            case WALL_REPLY:
                return WallReplyEntity.class;
            case EVENT:
                return EventEntity.class;
            case MARKET:
                return MarketEntity.class;
            case MARKET_ALBUM:
                return MarketAlbumEntity.class;
            default:
                throw new UnsupportedOperationException("Unsupported type: " + type);
        }
    }
}
