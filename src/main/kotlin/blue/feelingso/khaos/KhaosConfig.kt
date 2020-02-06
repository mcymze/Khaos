package blue.feelingso.khaos

import org.bukkit.configuration.file.FileConfiguration
import kotlin.math.abs
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

class KhaosConfig(bukkitConfig : FileConfiguration){
    private val bukkitConfig = bukkitConfig

    val radius: Int // 掘削半径
        get() = bukkitConfig.getInt("radius", 2)

    val consume: Boolean // ツールを消費するか
        get() = bukkitConfig.getBoolean("consume", true)

    val forceOnSneaking: Boolean // スニーキング中になんだっけ
        get() = bukkitConfig.getBoolean("forceOnSneaking", false)

    val dontDigFloor: Boolean // 足元を掘らない
        get() = bukkitConfig.getBoolean("dontDigFloor", true)

    val near: Int // 手前に掘る距離（負数）
        get() = -abs(bukkitConfig.getInt("near", 0))

    val far: Int // 奥に掘る距離
        get() = abs(bukkitConfig.getInt("far", 2))

    val switchRightClick: Boolean // 右クリックでスイッチするか
        get() = bukkitConfig.getBoolean("switchRightClick", true)

    fun getAllowedItems(tool: Material): List<String> {
        return bukkitConfig.getStringList("allowTools.$tool")
    }

    fun isTargetBlockByTool(tool: ItemStack, block: Block) = getAllowedItems(tool.type).contains(block.type.toString())
}
