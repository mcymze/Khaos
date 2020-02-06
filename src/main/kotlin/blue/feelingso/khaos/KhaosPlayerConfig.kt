package blue.feelingso.khaos

import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

// Khaosの主機能を有効状態を管理する
class KhaosPlayerConfig(plugin: Khaos) {
    private val namespacedKey = plugin.makeNamespacedKey(KEY)

    fun isActive(player: Player): Boolean {
        return player.persistentDataContainer.getOrDefault(namespacedKey, DATA_TYPE, 0) != 0
    }

    fun flip(player: Player) {
        player.persistentDataContainer.set(namespacedKey, DATA_TYPE, if (isActive(player)) 0 else 1)
    }

    companion object {
        private val DATA_TYPE = PersistentDataType.INTEGER
        private const val KEY = "ACTIVE"
    }
}
