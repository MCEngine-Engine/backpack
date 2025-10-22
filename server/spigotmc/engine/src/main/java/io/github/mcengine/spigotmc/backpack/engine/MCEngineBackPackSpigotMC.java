package io.github.mcengine.spigotmc.backpack.engine;

import io.github.mcengine.api.core.MCEngineCoreApi;
import io.github.mcengine.api.core.Metrics;
import io.github.mcengine.common.backpack.MCEngineBackPackCommon;
import io.github.mcengine.common.backpack.command.BackPackCommand;
import io.github.mcengine.common.backpack.listener.MCEngineBackPackListener;
import io.github.mcengine.common.backpack.tabcompleter.BackPackTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
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

        String license = getConfig().getString("licenses.license", "free"); 
        if (!license.equalsIgnoreCase("free")) { 
            getLogger().warning("Plugin is disabled in config.yml.");
            getLogger().warning("Invalid license.");
            getLogger().warning("Check license or use \"free\".");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register backpack listener (non-invasive addition)
        getServer().getPluginManager().registerEvents(new MCEngineBackPackListener(this), this);

        // --- Register /backpack command using dispatcher pattern (main:sub -> default) ---
        MCEngineBackPackCommon api = new MCEngineBackPackCommon(this);
        String namespace = "backpack";
        api.registerNamespace(namespace);
        api.registerSubCommand(namespace, "default", new BackPackCommand(this));
        api.registerSubTabCompleter(namespace, "default", new BackPackTabCompleter());

        CommandExecutor dispatcher = api.getDispatcher(namespace);
        if (getCommand("backpack") != null) {
            getCommand("backpack").setExecutor(dispatcher);
            getCommand("backpack").setTabCompleter((TabCompleter) dispatcher);
        } else {
            getLogger().severe("Command 'backpack' not found in plugin.yml. Please define it to enable command handling.");
        }
        // -------------------------------------------------------------------------------

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
