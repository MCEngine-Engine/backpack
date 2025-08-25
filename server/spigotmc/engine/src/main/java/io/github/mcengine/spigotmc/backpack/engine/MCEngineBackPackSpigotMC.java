package io.github.mcengine.spigotmc.backpack.engine;

import io.github.mcengine.api.core.MCEngineCoreApi;
import io.github.mcengine.api.core.Metrics;
import io.github.mcengine.common.backpack.listener.MCEngineBackPackListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;

/**
 * Main SpigotMC plugin class for MCEngineBackPack.
 *
 * <p>This entry point initializes config and loads extensions. It also registers
 * the backpack listener which wires player interactions to the backpack API.</p>
 */
public class MCEngineBackPackSpigotMC extends JavaPlugin {

    /**
     * Called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        new Metrics(this, 26147);
        saveDefaultConfig(); // Save config.yml if it doesn't exist

        boolean enabled = getConfig().getBoolean("enable", false);
        if (!enabled) {
            getLogger().warning("Plugin is disabled in config.yml (enable: false). Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        String licenseKey = getConfig().getString("license", "free");
        if (!licenseKey.equalsIgnoreCase("free")) {
            getLogger().warning("License key is not valid ('free' expected). Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register backpack listener (non-invasive addition)
        getServer().getPluginManager().registerEvents(new MCEngineBackPackListener(this), this);

        // Load extensions
        MCEngineCoreApi.loadExtensions(
            this,
            "io.github.mcengine.api.backpack.extension.library.IMCEngineBackPackLibrary",
            "libraries",
            "Library"
        );
        MCEngineCoreApi.loadExtensions(
            this,
            "io.github.mcengine.api.backpack.extension.api.IMCEngineBackPackAPI",
            "apis",
            "API"
        );
        MCEngineCoreApi.loadExtensions(
            this,
            "io.github.mcengine.api.backpack.extension.addon.IMCEngineBackPackAddOn",
            "addons",
            "AddOn"
        );
        MCEngineCoreApi.loadExtensions(
            this,
            "io.github.mcengine.api.backpack.extension.dlc.IMCEngineBackPackDLC",
            "dlcs",
            "DLC"
        );

        MCEngineCoreApi.checkUpdate(this, getLogger(), "github", "MCEngine-Engine", "backpack", getConfig().getString("github.token", "null"));
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {}
}
