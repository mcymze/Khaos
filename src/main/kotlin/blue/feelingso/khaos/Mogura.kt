package blue.feelingso.khaos

import org.bukkit.block.Block
import org.bukkit.block.BlockFace.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.Damageable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.ceil

/**
 * Digging blocks likes Mogura
 *
 */

class Mogura(private val executor: Player, private val block: Block, private val tool: ItemStack, private val conf: KhaosConfig): BukkitRunnable() {
    private val blockTypes = conf.getAllowedItems(tool.type)
    val isRunnable = blockTypes.contains(block.type.toString())

    override fun run() {
        // 向いている向きから、getRelative関数を定める -> nullの場合returnする
        val getRelative = makeGetRelativeFunc() ?: return

        // 対象になるブロックをここに格納する
        val targetBlocks = mutableListOf<Block>()

        for (i in 1 - conf.radius until conf.radius) {
            for (j in 1 - conf.radius until conf.radius) {
                for (k in conf.near until conf.far) {
                    val targetBlock = getRelative(block, i, j, k)

                    if (canDigBlock(targetBlock)) {
                        targetBlocks.add(targetBlock)
                    }
                }
            }
        }

        targetBlocks.forEach { it.breakNaturally(tool) }

        // 耐久を減らす
        val damage = calculateDamage(tool, if (conf.consume) targetBlocks.size else 1)

        val damageable = tool.itemMeta as Damageable

        damageable.damage = damageable.damage + damage

        tool.itemMeta = damageable as ItemMeta

        // 耐久上限超えたら壊します
        if (damageable.damage > tool.type.maxDurability) {
            executor.inventory.remove(tool)
        }
    }

    // プレイヤの向きから、getRelative関数を定める
    private fun makeGetRelativeFunc(): ((Block, Int, Int, Int) -> Block)? = when(executor.facing) {
        EAST, EAST_NORTH_EAST, EAST_SOUTH_EAST
        -> { block: Block, x: Int, y: Int, z: Int -> block.getRelative(x, y, z)}
        WEST, WEST_NORTH_WEST, WEST_SOUTH_WEST
        -> { block: Block, x: Int, y: Int, z: Int -> block.getRelative(-x, y, z)}
        NORTH, NORTH_EAST, NORTH_WEST, NORTH_NORTH_EAST, NORTH_NORTH_WEST
        -> { block: Block, x: Int, y: Int, z: Int -> block.getRelative(z, y, -x)}
        SOUTH, SOUTH_EAST, SOUTH_WEST, SOUTH_SOUTH_EAST, SOUTH_SOUTH_WEST
        -> { block: Block, x: Int, y: Int, z: Int -> block.getRelative(z, y, x)}
        else -> null
    }

    // 対象のブロックが自分の足元より高い位置にあるか
    //　草の道やソウルサンドのような少し低いブロックの上で掘った際のことを考慮し、プレイヤの高さを切り上げている
    private fun isHigherThanFloor(block: Block) = block.y >= ceil(executor.location.y)

    private fun canDigBlock(block: Block): Boolean {
        // そのブロックはツールの対象に含まれているか
        if (!conf.isTargetBlockByTool(tool, block)) return false

        if (!conf.dontDigFloor) return true

        // 床下を掘らないという設定の場合、対象のブロックが自分の足元より高いか
        return isHigherThanFloor(block)
    }

    // ItemStackと破壊個数からダメージを計算する
    private fun calculateDamage(item: ItemStack, count: Int): Int {
        // 耐久エンチャントのレベル 無しの場合は0が返る
        val enchantmentLevel = item.getEnchantmentLevel(Enchantment.DURABILITY)
        // エンチャントが無い場合は確率の計算を回す必要がないのでそのまま個数を返却する
        if (enchantmentLevel == 0) return count
        val ratio = 1.0f / (enchantmentLevel + 1)
        // count回計算して個数を返す
        return (0 until count).filter { Math.random().toFloat() <= ratio } .size
    }
}