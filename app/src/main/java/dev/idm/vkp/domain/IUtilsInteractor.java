package dev.idm.vkp.domain;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

import dev.idm.vkp.api.model.VKApiCheckedLink;
import dev.idm.vkp.api.model.response.VkApiChatResponse;
import dev.idm.vkp.api.model.response.VkApiLinkResponse;
import dev.idm.vkp.model.Owner;
import dev.idm.vkp.model.Privacy;
import dev.idm.vkp.model.ShortLink;
import dev.idm.vkp.model.SimplePrivacy;
import dev.idm.vkp.util.Optional;
import io.reactivex.rxjava3.core.Single;

public interface IUtilsInteractor {
    Single<Map<Integer, Privacy>> createFullPrivacies(int accountId, @NonNull Map<Integer, SimplePrivacy> orig);

    Single<Optional<Owner>> resolveDomain(int accountId, String domain);

    Single<ShortLink> getShortLink(int accountId, String url, Integer t_private);

    Single<List<ShortLink>> getLastShortenedLinks(int accountId, Integer count, Integer offset);

    Single<Integer> deleteFromLastShortened(int accountId, String key);

    Single<VKApiCheckedLink> checkLink(int accountId, String url);

    Single<VkApiChatResponse> joinChatByInviteLink(int accountId, String link);

    Single<VkApiLinkResponse> getInviteLink(int accountId, Integer peer_id, Integer reset);
}
