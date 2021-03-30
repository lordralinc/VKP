# VKP
Форк ВК клиента Fenrir, с поддержкой музыки и множество другого функционала.

<b>Языки: Русский, английский</b>

<b>Фичи:</b>
- Интеграция с IDM
- Изменение внешнего вида
- AMOLED-тема
- Средства для работы с аккаунтами (мультиаккаунтинг, импорты/экспорты/бэкапы)
- Смена иконок
- Установка пин-кодов
- Шифрование переписок
- Загрузка диалогов в JSON/HTML

<b>Инструкция по сборке:</b>
Требуется:
1. Android Studio 4.2 Beta 5 или выше. Kotlin 1.4.*
2. Android SDK 29,30
3. Android NDK 22.0.7026061
  
Если не работает музыка в VKP, обновите kate_receipt_gms_token в app.build_config.
Взять токен можно из Kate Mobile Extra Mod
  
<b>Компиляция:</b>

1. Для релизных сборок вам нужен сертификат.
    <code>keytool -genkey -v -keystore ExampleKey.keystore -alias fenrir -storetype PKCS12 -keyalg RSA -keysize 2048 -validity 10000</code>
2. Выберите тип сборки (vkp_kate_full) Debug или Release и соберите apk :)

Локальный медиа сервер https://github.com/umerov1999/FenrirMediaServer/releases
