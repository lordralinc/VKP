package dev.idm.vkp.domain

import dev.idm.vkp.Injection
import dev.idm.vkp.db.Stores
import dev.idm.vkp.domain.impl.MessagesRepository
import dev.idm.vkp.domain.impl.OwnersRepository
import dev.idm.vkp.domain.impl.WallsRepository
import dev.idm.vkp.settings.Settings

object Repository {
    val owners: IOwnersRepository by lazy {
        OwnersRepository(Injection.provideNetworkInterfaces(), Stores.getInstance().owners())
    }

    val walls: IWallsRepository by lazy {
        WallsRepository(Injection.provideNetworkInterfaces(), Stores.getInstance(), owners)
    }

    val messages: IMessagesRepository by lazy {
        MessagesRepository(
            Settings.get().accounts(),
            Injection.provideNetworkInterfaces(),
            owners,
            Injection.provideStores(),
            Injection.provideUploadManager()
        )
    }
}