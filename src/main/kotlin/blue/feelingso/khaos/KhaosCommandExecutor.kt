package blue.feelingso.khaos

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class KhaosCommandExecutor(_khaos: Khaos) : CommandExecutor {
    private val khaos = _khaos
    private var playerConf : MutableMap<String, Boolean?> = mutableMapOf()
    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean
    {
        if (args != null && sender != null) {
            if (args.size > 0) {
                when (args[0]) {
                    "reload" -> {
                        khaos.reloadConfig()
                        khaos.loadPlayerConf()
                        sender.sendMessage("[Khaos] reloaded!")
                    }
                    "switch" -> {
                        khaos.setPlayerConf(sender.name, !khaos.getPlayerConf(sender.name))
                        sender.sendMessage("[Khaos] Switched to ${if (khaos.getPlayerConf(sender.name)) "ON" else "OFF"}")
                    }
                    "status" -> {
                        sender.sendMessage("[Khaos] Status: ${if (khaos.getPlayerConf(sender.name)) "ON" else "OFF"}")
                    }
                }
            }
            else
            {
                sender.sendMessage("[Khaos] Usage: khaos <reload/switch/status>")
            }
        }

        return true
    }
}