package blue.feelingso.khaos

import org.bukkit.command.CommandSender

// Khaosの権限についてチェックさせる

fun CommandSender.hasPermissionGetStatus(): Boolean {
    return hasPermission("khaos.status")
}

fun CommandSender.hasPermissionSwitch(): Boolean {
    return hasPermission("khaos.switch")
}

fun CommandSender.hasPermissionDig(): Boolean {
    return hasPermission("khaos.dig")
}

fun CommandSender.hasPermissionReload(): Boolean {
    return hasPermission("khaos.reload")
}

fun CommandSender.hasPermissionMaster(): Boolean {
    return hasPermission("khaos.*")
}

