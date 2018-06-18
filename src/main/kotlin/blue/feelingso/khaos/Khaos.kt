package blue.feelingso.khaos

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import org.bukkit.configuration.file.YamlConfiguration

class Khaos : JavaPlugin() {
    private var _playerConf = YamlConfiguration.loadConfiguration(File(dataFolder, "player.yml"))

    override fun onEnable() {
        saveDefaultConfig()
        server.pluginManager.registerEvents(KhaosListener(this), this)
        getCommand("khaos").executor = KhaosCommandExecutor(this)
    }

    override fun onDisable() {
    }

    public fun getConfigure() : FileConfiguration {
        return config
    }

    public fun loadConfigure() {
        reloadConfig()
    }

    public fun loadPlayerConf() {
        _playerConf = YamlConfiguration.loadConfiguration(File(dataFolder, "player.yml"))
    }

    public fun getPlayerConf(name :String) : Boolean {
        return _playerConf.getBoolean(name, _playerConf.getBoolean("default", false))
    }

    public fun setPlayerConf(name :String, flag :Boolean) {
        _playerConf.set(name, flag)
        _playerConf.save(File(dataFolder, "player.yml"))
    }
}
