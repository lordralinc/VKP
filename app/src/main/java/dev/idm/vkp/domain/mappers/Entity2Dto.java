package dev.idm.vkp.domain.mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dev.idm.vkp.api.model.AttachmentsTokenCreator;
import dev.idm.vkp.api.model.IAttachmentToken;
import dev.idm.vkp.db.model.entity.ArticleEntity;
import dev.idm.vkp.db.model.entity.AudioArtistEntity;
import dev.idm.vkp.db.model.entity.AudioEntity;
import dev.idm.vkp.db.model.entity.AudioPlaylistEntity;
import dev.idm.vkp.db.model.entity.CallEntity;
import dev.idm.vkp.db.model.entity.DocumentEntity;
import dev.idm.vkp.db.model.entity.Entity;
import dev.idm.vkp.db.model.entity.EventEntity;
import dev.idm.vkp.db.model.entity.GraffitiEntity;
import dev.idm.vkp.db.model.entity.LinkEntity;
import dev.idm.vkp.db.model.entity.MarketAlbumEntity;
import dev.idm.vkp.db.model.entity.MarketEntity;
import dev.idm.vkp.db.model.entity.NotSupportedEntity;
import dev.idm.vkp.db.model.entity.PhotoAlbumEntity;
import dev.idm.vkp.db.model.entity.PhotoEntity;
import dev.idm.vkp.db.model.entity.PollEntity;
import dev.idm.vkp.db.model.entity.PostEntity;
import dev.idm.vkp.db.model.entity.StoryEntity;
import dev.idm.vkp.db.model.entity.VideoEntity;
import dev.idm.vkp.db.model.entity.WallReplyEntity;
import dev.idm.vkp.util.Utils;

import static dev.idm.vkp.util.Objects.nonNull;
import static dev.idm.vkp.util.Utils.safeCountOf;


public class Entity2Dto {

    public static List<IAttachmentToken> createTokens(Collection<? extends Entity> dbos) {
        List<IAttachmentToken> tokens = new ArrayList<>(safeCountOf(dbos));

        if (nonNull(dbos)) {
            for (Entity entity : dbos) {
                tokens.add(createToken(entity));
            }
        }

        return tokens;
    }

    public static IAttachmentToken createToken(Entity entity) {
        if (entity instanceof DocumentEntity) {
            DocumentEntity document = (DocumentEntity) entity;
            return AttachmentsTokenCreator.ofDocument(document.getId(), document.getOwnerId(), document.getAccessKey());
        }

        if (entity instanceof AudioEntity) {
            AudioEntity audio = (AudioEntity) entity;
            return AttachmentsTokenCreator.ofAudio(audio.getId(), audio.getOwnerId(), audio.getAccessKey());
        }

        if (entity instanceof LinkEntity) {
            return AttachmentsTokenCreator.ofLink(((LinkEntity) entity).getUrl());
        }

        if (entity instanceof ArticleEntity) {
            ArticleEntity article = (ArticleEntity) entity;
            return AttachmentsTokenCreator.ofArticle(article.getId(), article.getOwnerId(), article.getAccessKey());
        }

        if (entity instanceof StoryEntity) {
            StoryEntity story = (StoryEntity) entity;
            return AttachmentsTokenCreator.ofStory(story.getId(), story.getOwnerId(), story.getAccessKey());
        }

        if (entity instanceof PhotoAlbumEntity) {
            PhotoAlbumEntity album = (PhotoAlbumEntity) entity;
            return AttachmentsTokenCreator.ofPhotoAlbum(album.getId(), album.getOwnerId());
        }

        if (entity instanceof GraffitiEntity) {
            GraffitiEntity graffity = (GraffitiEntity) entity;
            return AttachmentsTokenCreator.ofGraffity(graffity.getId(), graffity.getOwner_id(), graffity.getAccess_key());
        }

        if (entity instanceof CallEntity) {
            CallEntity call = (CallEntity) entity;
            return AttachmentsTokenCreator.ofCall(call.getInitiator_id(), call.getReceiver_id(), call.getState(), call.getTime());
        }

        if (entity instanceof AudioArtistEntity) {
            AudioArtistEntity artist = (AudioArtistEntity) entity;
            return AttachmentsTokenCreator.ofArtist(artist.getId());
        }

        if (entity instanceof WallReplyEntity) {
            WallReplyEntity wall_reply = (WallReplyEntity) entity;
            return AttachmentsTokenCreator.ofWallReply(wall_reply.getId(), wall_reply.getOwnerId());
        }

        if (entity instanceof NotSupportedEntity) {
            NotSupportedEntity error = (NotSupportedEntity) entity;
            return AttachmentsTokenCreator.ofError(error.getType(), error.getBody());
        }

        if (entity instanceof EventEntity) {
            EventEntity event = (EventEntity) entity;
            return AttachmentsTokenCreator.ofEvent(event.getId(), event.getButton_text());
        }

        if (entity instanceof MarketEntity) {
            MarketEntity market = (MarketEntity) entity;
            return AttachmentsTokenCreator.ofMarket(market.getId(), market.getOwner_id(), market.getAccess_key());
        }

        if (entity instanceof MarketAlbumEntity) {
            MarketAlbumEntity market_album = (MarketAlbumEntity) entity;
            return AttachmentsTokenCreator.ofMarketAlbum(market_album.getId(), market_album.getOwner_id(), market_album.getAccess_key());
        }

        if (entity instanceof AudioPlaylistEntity) {
            AudioPlaylistEntity playlist = (AudioPlaylistEntity) entity;
            if (Utils.isEmpty(playlist.getOriginal_access_key()) || playlist.getOriginal_id() == 0 || playlist.getOriginal_owner_id() == 0) {
                return AttachmentsTokenCreator.ofAudioPlaylist(playlist.getId(), playlist.getOwnerId(), playlist.getAccess_key());
            } else {
                return AttachmentsTokenCreator.ofAudioPlaylist(playlist.getOriginal_id(), playlist.getOriginal_owner_id(), playlist.getOriginal_access_key());
            }
        }

        if (entity instanceof PhotoEntity) {
            PhotoEntity photo = (PhotoEntity) entity;
            return AttachmentsTokenCreator.ofPhoto(photo.getId(), photo.getOwnerId(), photo.getAccessKey());
        }

        if (entity instanceof PollEntity) {
            PollEntity poll = (PollEntity) entity;
            return AttachmentsTokenCreator.ofPoll(poll.getId(), poll.getOwnerId());
        }

        if (entity instanceof PostEntity) {
            PostEntity post = (PostEntity) entity;
            return AttachmentsTokenCreator.ofPost(post.getId(), post.getOwnerId());
        }

        if (entity instanceof VideoEntity) {
            VideoEntity video = (VideoEntity) entity;
            return AttachmentsTokenCreator.ofVideo(video.getId(), video.getOwnerId(), video.getAccessKey());
        }

        throw new UnsupportedOperationException("Token for class " + entity.getClass() + " not supported");
    }
}
