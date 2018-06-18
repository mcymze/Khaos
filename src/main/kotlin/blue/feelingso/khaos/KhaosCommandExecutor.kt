package blue.feelingso.khaos

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class KhaosCommandExecutor(_khaos: Khaos) : CommandExecutor {
    private val khaos = _khaos

    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean
    {
        if (args != null && sender != null) {
            if (args.isNotEmpty()) {
                when (args[0]) {
                    "reload", "r" -> reload(sender)
                    "switch", "sw" -> switch(sender)
                    "status", "sts" -> status(sender)
                    "help", "-h" -> help(sender)
                }
            }
            else {
                help(sender)
            }
        }

        return true
    }

    private fun reload(sender: CommandSender)
    {
        if (sender.hasPermission("khaos.reload")) {
            khaos.reloadConfig()
            khaos.loadPlayerConf()
            sender.sendMessage("[Khaos] reloaded!")
        }
        else {
            sender.sendMessage("[Khaos] You don't have a permission to execute this command")
        }
    }

    private fun switch(sender: CommandSender) {
        if (sender.hasPermission("khaos.switch")) {
            khaos.setPlayerConf(sender.name, !khaos.getPlayerConf(sender.name))
            sender.sendMessage("[Khaos] Switched to ${if (khaos.getPlayerConf(sender.name)) "ON" else "OFF"}")
        }
        else {
            sender.sendMessage("[Khaos] You don't have a permission to execute this command")
        }
    }

    private fun status(sender: CommandSender) {
        sender.sendMessage(
                if (sender.hasPermission("khaos.status"))
                    "[Khaos] Status: ${if (khaos.getPlayerConf(sender.name)) "ON" else "OFF"}"
                else
                    "[Khaos] You don't have a permission to execute this command"
        )
    }

    private  fun help(sender: CommandSender) {
        sender.sendMessage(
                if (sender.hasPermission("khaos.status"))
                    "[Khaos] Usage: khaos <reload/switch/status>"
                else
                    "[Khaos] You don't have a permission to execute this command"
        )
    }
}
