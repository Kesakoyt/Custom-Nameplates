/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.api;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.boostedyaml.utils.format.NodeRole;
import net.momirealms.customnameplates.api.feature.CharacterArranger;
import net.momirealms.customnameplates.api.feature.bossbar.BossBar;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.common.locale.TranslationManager;
import net.momirealms.customnameplates.common.plugin.CustomNameplatesProperties;
import net.momirealms.customnameplates.common.plugin.config.ConfigLoader;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public abstract class ConfigManager implements ConfigLoader, Reloadable {

    private static YamlDocument MAIN_CONFIG;

    public static YamlDocument getMainConfig() {
        if (MAIN_CONFIG == null) {
            throw new IllegalStateException("Main config not loaded");
        }
        return MAIN_CONFIG;
    }

    private static ConfigManager instance;

    protected final CustomNameplates plugin;

    protected boolean debug;
    protected boolean checkUpdate;
    protected boolean metrics;
    protected boolean actionbarModule;
    protected boolean imageModule;
    protected boolean bossBarModule;
    protected boolean bubbleModule;
    protected boolean nameplateModule;
    protected boolean backgroundModule;
    protected int defaultRefreshInterval;
    protected int delaySend;
    protected boolean catchOtherActionBar;

    protected String namespace;
    protected String font;

    protected boolean generateOnStart;

    protected char initialChar;

    protected String imagePath;
    protected String nameplatePath;
    protected String bubblePath;
    protected String spaceSplitPath;
    protected String backgroundPath;

    protected boolean enableShader;
    protected boolean hideScoreBoardNumber;
    protected boolean animatedText;
    protected boolean itemsAdderEffect;

    protected BossBar.Color removedBarColor;
    protected boolean bossBar1_20_2;
    protected boolean bossBar1_17;

    protected boolean legacyUnicodes;

    protected boolean packItemsAdder;
    protected boolean packItemsAdderLegacy;
    protected boolean packOraxen;

    protected boolean chatUnsafe;
    protected boolean chatTR;
    protected boolean chatVenture;
    protected boolean chatHusk;
    protected boolean chatCarbon;
    protected boolean chatAdvanced;
    protected boolean chatEss;

    public ConfigManager(CustomNameplates plugin) {
        this.plugin = plugin;
        instance = this;
    }

    @Override
    public void load() {
        String configVersion = CustomNameplatesProperties.getValue("config");
        try (InputStream inputStream = new FileInputStream(resolveConfig("config.yml").toFile())) {
            MAIN_CONFIG = YamlDocument.create(
                    inputStream,
                    plugin.getResourceStream("config.yml"),
                    GeneralSettings.builder()
                            .setRouteSeparator('.')
                            .setUseDefaults(false)
                            .build(),
                    LoaderSettings
                            .builder()
                            .setAutoUpdate(true)
                            .build(),
                    DumperSettings.builder()
                            .setEscapeUnprintable(false)
                            .setScalarFormatter((tag, value, role, def) -> {
                                if (role == NodeRole.KEY) {
                                    return ScalarStyle.PLAIN;
                                } else {
                                    return tag == Tag.STR ? ScalarStyle.DOUBLE_QUOTED : ScalarStyle.PLAIN;
                                }
                            })
                            .build(),
                    UpdaterSettings
                            .builder()
                            .setVersioning(new BasicVersioning("config-version"))
                            .addIgnoredRoute(configVersion, "other-settings.placeholder-refresh-interval", '.')
                            .addIgnoredRoute(configVersion, "other-settings.font-templates", '.')
                            .addIgnoredRoute(configVersion, "other-settings.shift-fonts", '.')
                            .build()
            );
            MAIN_CONFIG.save(resolveConfig("config.yml").toFile());
            loadSettings();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadSettings() {
        YamlDocument config = MAIN_CONFIG;
        AdventureHelper.legacySupport = config.getBoolean("other-settings.legacy-color-code-support", true);
        TranslationManager.forceLocale(TranslationManager.parseLocale(config.getString("force-locale", "")));

        // Basics
        debug = config.getBoolean("debug", false);
        metrics = config.getBoolean("metrics", false);
        checkUpdate = config.getBoolean("update-checker", false);

        // Modules
        actionbarModule = config.getBoolean("modules.actionbars", true);
        imageModule = config.getBoolean("modules.images", true);
        bossBarModule = config.getBoolean("modules.bossbars", true);
        bubbleModule = config.getBoolean("modules.bubbles", true);
        backgroundModule = config.getBoolean("modules.backgrounds", true);
        nameplateModule = config.getBoolean("modules.nameplates", true);

        // Integrations
        packItemsAdder = config.getBoolean("integrations.resource-pack.ItemsAdder", false);
        packItemsAdderLegacy = config.getBoolean("integrations.resource-pack.ItemsAdder-old-method", false);
        if (packItemsAdder) packItemsAdderLegacy = false;
        packOraxen = config.getBoolean("integrations.resource-pack.Oraxen", false);

        chatUnsafe = config.getBoolean("other-settings.unsafe-chat-event", false);
        chatEss = config.getBoolean("integrations.chat.Essentials", false);
        chatCarbon = config.getBoolean("integrations.chat.CarbonChat", false);
        chatHusk = config.getBoolean("integrations.chat.HuskChat", false);
        chatAdvanced = config.getBoolean("integrations.chat.AdvancedChat", false);
        chatTR = config.getBoolean("integrations.chat.TrChat", false);
        chatVenture = config.getBoolean("integrations.chat.VentureChat", false);

        // Packs
        generateOnStart = !config.getBoolean("resource-pack.disable-generation-on-start", false);
        namespace = config.getString("resource-pack.namespace", "nameplates");
        font = config.getString("resource-pack.font", "default");
        initialChar = config.getChar("resource-pack.initial-char", '뀁');
        CharacterArranger.reset(initialChar);

        nameplatePath = config.getString("resource-pack.image-path.nameplates", "font/nameplates/");
        backgroundPath = config.getString("resource-pack.image-path.backgrounds", "font/backgrounds/");
        imagePath = config.getString("resource-pack.image-path.images", "font/images/");
        bubblePath = config.getString("resource-pack.image-path.bubbles", "font/bubbles/");
        spaceSplitPath = config.getString("resource-pack.image-path.space-split", "font/base/");

        enableShader = config.getBoolean("resource-pack.shader.enable", true);
        hideScoreBoardNumber = config.getBoolean("resource-pack.shader.hide-scoreboard-number", false);
        animatedText = config.getBoolean("resource-pack.shader.animated-text", false);
        itemsAdderEffect = config.getBoolean("resource-pack.shader.ItemsAdder-text-effects", false);

        removedBarColor = BossBar.Color.valueOf(config.getString("resource-pack.transparent-bossbar.color", "YELLOW").toUpperCase(Locale.ENGLISH));
        bossBar1_20_2 = config.getBoolean("resource-pack.transparent-bossbar.1_20_2+", true);
        bossBar1_17 = config.getBoolean("resource-pack.transparent-bossbar.1_17-1_20_1", true);

        legacyUnicodes = config.getBoolean("resource-pack.legacy-unicodes", true);

        // Other settings
        delaySend = config.getInt("other-settings.send-delay", 0);
        defaultRefreshInterval = config.getInt("other-settings.default-placeholder-refresh-interval", 10);
        catchOtherActionBar = config.getBoolean("other-settings.catch-other-plugin-actionbar", true);
    }

    @Override
    public void unload() {
        Reloadable.super.unload();
    }

    public static int delaySend() {
        return instance.delaySend;
    }

    public static String spaceSplitPath() {
        return instance.spaceSplitPath;
    }

    public static String bubblePath() {
        return instance.bubblePath;
    }

    public static String imagePath() {
        return instance.imagePath;
    }

    public static String backgroundPath() {
        return instance.backgroundPath;
    }

    public static String nameplatePath() {
        return instance.nameplatePath;
    }

    public static boolean generateOnStart() {
        return instance.generateOnStart;
    }

    public static String namespace() {
        return instance.namespace;
    }

    public static String font() {
        return instance.font;
    }

    public static char initialChar() {
        return instance.initialChar;
    }

    public static boolean actionbarModule() {
        return instance.actionbarModule;
    }

    public static boolean catchOtherActionBar() {
        return instance.catchOtherActionBar;
    }

    public static boolean packItemsAdder() {
        return instance.packItemsAdder;
    }

    public static boolean packItemsAdderLegacy() {
        return instance.packItemsAdderLegacy;
    }

    public static boolean packOraxen() {
        return instance.packOraxen;
    }

    public static boolean hideScoreBoardNumber() {
        return instance.hideScoreBoardNumber;
    }

    public static boolean animatedText() {
        return instance.animatedText;
    }

    public static boolean itemsAdderEffect() {
        return instance.itemsAdderEffect;
    }

    public static BossBar.Color removedBarColor() {
        return instance.removedBarColor;
    }

    public static boolean imageModule() {
        return instance.imageModule;
    }

    public static boolean bossbarModule() {
        return instance.bossBarModule;
    }

    public static boolean bubbleModule() {
        return instance.bubbleModule;
    }

    public static boolean backgroundModule() {
        return instance.backgroundModule;
    }

    public static boolean nameplateModule() {
        return instance.nameplateModule;
    }

    public static boolean debug() {
        return instance.debug;
    }

    public static boolean checkUpdate() {
        return instance.checkUpdate;
    }

    public static boolean metrics() {
        return instance.metrics;
    }

    public static int defaultRefreshInterval() {
        return instance.defaultRefreshInterval;
    }

    public static boolean enableShader() {
        return instance.enableShader;
    }

    public static boolean legacyUnicodes() {
        return instance.legacyUnicodes;
    }

    public static boolean bossBar1_20_2() {
        return instance.bossBar1_20_2;
    }

    public static boolean bossBar1_17() {
        return instance.bossBar1_17;
    }

    public static boolean chatUnsafe() {
        return instance.chatUnsafe;
    }

    public static boolean chatTrChat() {
        return instance.chatTR;
    }

    public static boolean chatVenture() {
        return instance.chatVenture;
    }

    public static boolean chatHusk() {
        return instance.chatHusk;
    }

    public static boolean chatEss() {
        return instance.chatEss;
    }

    public static boolean chatCarbon() {
        return instance.chatCarbon;
    }

    public static boolean chatAdvanced() {
        return instance.chatAdvanced;
    }

    @Override
    public YamlDocument loadConfig(String filePath) {
        return loadConfig(filePath, '.');
    }

    @Override
    public YamlDocument loadConfig(String filePath, char routeSeparator) {
        try (InputStream inputStream = new FileInputStream(resolveConfig(filePath).toFile())) {
            return YamlDocument.create(
                    inputStream,
                    plugin.getResourceStream(filePath),
                    GeneralSettings.builder().setRouteSeparator(routeSeparator).build(),
                    LoaderSettings
                            .builder()
                            .setAutoUpdate(true)
                            .build(),
                    DumperSettings.builder()
                            .setScalarFormatter((tag, value, role, def) -> {
                                if (role == NodeRole.KEY) {
                                    return ScalarStyle.PLAIN;
                                } else {
                                    return tag == Tag.STR ? ScalarStyle.DOUBLE_QUOTED : ScalarStyle.PLAIN;
                                }
                            })
                            .build(),
                    UpdaterSettings
                            .builder()
                            .setVersioning(new BasicVersioning("config-version"))
                            .build()
            );
        } catch (IOException e) {
            plugin.getPluginLogger().severe("Failed to load config " + filePath, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public YamlDocument loadData(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            return YamlDocument.create(inputStream);
        } catch (IOException e) {
            plugin.getPluginLogger().severe("Failed to load config " + file, e);
            throw new RuntimeException(e);
        }
    }

    protected Path resolveConfig(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
        filePath = filePath.replace('\\', '/');
        Path configFile = plugin.getConfigDirectory().resolve(filePath);
        // if the config doesn't exist, create it based on the template in the resources dir
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectories(configFile.getParent());
            } catch (IOException e) {
                // ignore
            }
            try (InputStream is = plugin.getResourceStream(filePath)) {
                if (is == null) {
                    throw new IllegalArgumentException("The embedded resource '" + filePath + "' cannot be found");
                }
                Files.copy(is, configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return configFile;
    }
}