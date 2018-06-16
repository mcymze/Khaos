package blue.feelingso.khaos

import org.bukkit.plugin.java.JavaPlugin

class Khaos : JavaPlugin() {

    override fun onEnable() {
        logger.info("hello!!")
        saveDefaultConfig()
        server.pluginManager.registerEvents(KhaosListener(config), this)
        getCommand("khaos").executor = KhaosListener(config)
    }

    override fun onDisable() {
        logger.info("goodbye!!!")
    }
}