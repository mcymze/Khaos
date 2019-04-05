package blue.feelingso.khaos

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

// Khaosの主機能を有効状態を管理する
class KhaosPlayerConfig(configFile: File) {
    private val configFile = configFile
    private val config = YamlConfiguration.loadConfiguration(configFile)

    // デフォルト値
    val default: Boolean
        get() = config.getBoolean("default", false)

    // 機能が有効か取得する
    fun isActive(name: String): Boolean {
        return config.getBoolean(name, default)
    }

    // 切り替えを行う
    fun setActive(name: String, value: Boolean) {
        config.set(name, value)
        config.save(configFile)
    }

    // 状態を反転させる
    fun flip(name: String) {
        setActive(name, !isActive(name))
    }

    // ファイルの再読込
    fun reload() {
        config.load(configFile)
    }
}
