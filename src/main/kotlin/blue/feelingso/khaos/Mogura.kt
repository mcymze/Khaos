package blue.feelingso.khaos

import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.absoluteValue

/**
 * Digging blocks likes Mogura
 *
 */

class Mogura(private val executor: Player, private val block: Block, private val tool: ItemStack, private val conf: KhaosConfig) {
    private val blockTypes = conf.getAllowedItems(tool.type)
    val runnable = blockTypes.contains(block.type.toString())

    fun run() {
        // 向いている向きとradius設定から破壊する範囲を設定し
        val direction = executor.eyeLocation.direction.normalize()

        // 方位を取得する
        // x軸が東西，z軸が南北
        val compass =
                if (direction.z.absoluteValue > direction.x.absoluteValue)
                    if (direction.z < 0)
                        Compass.NORTH
                    else
                        Compass.SOUTH
                else
                    if (direction.x < 0)
                        Compass.WEST
                    else Compass.EAST

        // 最初に破壊したブロックと同じidのブロックを破壊．
        for (i in 1 - conf.radius until conf.radius) {
            for (j in 1 - conf.radius until conf.radius) {
                for (k in conf.near..(conf.far - 1))
                {
                    val targetBlock =
                            when (compass) {
                                Compass.EAST -> {
                                    block.getRelative(k, i, j)
                                }
                                Compass.WEST -> {
                                    block.getRelative(-k, i, j)
                                }
                                Compass.NORTH -> {
                                    block.getRelative(j, i, -k)
                                }
                                Compass.SOUTH -> {
                                    block.getRelative(j, i, k)
                                }
                            }

                    if (blockTypes.contains(targetBlock.type.toString()) && (targetBlock.y >= executor.location.blockY || !conf.dontDigFloor)) {
                        targetBlock.breakNaturally(tool)
                        if (conf.consume) tool.durability = (tool.durability + 1).toShort()
                    }
                }
            }
        }

        // それでも1つ分は減らす
        if (!conf.consume) tool.durability = (tool.durability + 1).toShort()

        // 上限に達したら壊す
        if (tool.type.maxDurability < tool.durability) {
            executor.inventory.remove(tool)
        }
    }
}