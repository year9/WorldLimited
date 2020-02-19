package cn.year9;


import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class Config extends YamlConfiguration {
   private final Plugin plugin;

    public Config(Plugin plugin) {
        this.plugin = plugin;
    }

    public File getConfigFile()
    {
        File pluginsFolder = this.plugin.getDataFolder();
        return new File(pluginsFolder,"config.yml");
    }
}
