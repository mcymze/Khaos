package blue.feelingso.khaos

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import kotlin.math.absoluteValue

class KhaosListener(_khaos :Khaos) : Listener {
    private val khaos = _khaos
    @EventHandler
    fun onBlockBroken(ev :BlockBreakEvent) {
        val conf = khaos.getConfigure()
        val player = ev.player
        val block = ev.block
        val radius = conf.getInt("radius", 2)
        val isConsume = conf.getBoolean("consume", true)
        val forceOnSneaking = conf.getBoolean("forceOnSneaking", false)
        val dontDigFloor = conf.getBoolean("dontDigFloor", true)

        // 破壊したブロックの数だけアイテムの耐久度を減らす（コード上は増やす）
        val tool = player.inventory.itemInMainHand

        // プレイヤの権限を確認 (今はナシ）

        // まずプレイヤ個人が機能を有効にしているか確認
        if (!khaos.getPlayerConf(player.name)) return
        // サバイバルか確認
        if (player.gameMode != GameMode.SURVIVAL) return

        // スニーク中は無効
        if (player.isSneaking && !forceOnSneaking) return

        // 最初に破壊されたブロックがそのツールの対象か確認
        if (!conf.getStringList("allowTools.${tool.type.toString()}").contains(block.type.toString())) return

        // 向いている向きとradius設定から破壊する範囲を設定し
        val direction = player.eyeLocation.direction.normalize()
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

        val blockType = block.type

        // 最初に破壊したブロックと同じidのブロックを破壊．
        for (i in 0..radius) {
            for (j in 0..radius) {
                val targetBlock =
                when (compass) {
                    Compass.EAST -> {
                        block.getRelative(0,i - radius / 2,j - radius / 2)
                    }
                    Compass.WEST -> {
                        block.getRelative(0,i - radius / 2,j - radius / 2)
                    }
                    Compass.NORTH -> {
                        block.getRelative(j - radius / 2,i - radius / 2,0)
                    }
                    Compass.SOUTH -> {
                        block.getRelative(j - radius / 2,i - radius / 2,0)
                    }
                }

                // 最初に掘ったブロックと同一でかつ，dontDigFloorが有効の場合は足元より上のみ
                if (targetBlock.type == blockType && (targetBlock.y >= player.location.blockY || !dontDigFloor)) {
                    targetBlock.breakNaturally(tool)
                    if (isConsume) tool.durability = (tool.durability + 1).toShort()
                }
            }
        }

        // それでも1つ分は減らす
        if (!isConsume) tool.durability = (tool.durability + 1).toShort()

        // 上限に達したら壊す
        if (tool.type.maxDurability < tool.durability) {
            player.inventory.remove(tool)
        }
    }
}