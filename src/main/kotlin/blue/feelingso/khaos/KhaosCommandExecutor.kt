package blue.feelingso.khaos

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class KhaosCommandExecutor(khaos: Khaos) : CommandExecutor {
    private val khaos = khaos

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
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
        if (sender.hasPermissionReload()) {
            khaos.reloadConfig()
            sender.sendMessage("[Khaos] reloaded!")
        }
        else {
            sender.sendMessage("[Khaos] You don't have a permission to execute this command")
        }
    }

    private fun switch(sender: CommandSender) {
        if (sender.hasPermissionSwitch() && sender is Player) {
            khaos.playerConfig.flip(sender)
            sender.sendMessage("[Khaos] Switched to ${if (khaos.playerConfig.isActive(sender)) "ON" else "OFF"}")
        }
        else {
            sender.sendMessage("[Khaos] You don't have a permission to execute this command")
        }
    }

    private fun status(sender: CommandSender) {
        sender.sendMessage(
                if (sender.hasPermissionGetStatus() && sender is Player)
                    "[Khaos] Status: ${if (khaos.playerConfig.isActive(sender)) "ON" else "OFF"}"
                else
                    "[Khaos] You don't have a permission to execute this command"
        )
    }

    private  fun help(sender: CommandSender) {
        sender.sendMessage(
                if (sender.hasPermissionGetStatus())
                    "[Khaos] Usage: khaos <reload/switch/status>"
                else
                    "[Khaos] You don't have a permission to execute this command"
        )
    }
}
