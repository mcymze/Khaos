package blue.feelingso.khaos

import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable

class PlayerTimer(_khaos :Khaos) {
    private val khaos = _khaos

    public fun set(player :Player, key :String, delay :Long = 10) {
        player.setMetadata(key, FixedMetadataValue(khaos, true))
        object : BukkitRunnable() {
            override fun run() {
                player.setMetadata(key, FixedMetadataValue(khaos, false))
            }
        }.runTaskLater(khaos, delay)
    }

    public fun get(player :Player, key :String) :Boolean {
        try {
            return player.getMetadata(key)[0].value() as Boolean
        } catch (e: Exception) {
            return false
        }
    }
}