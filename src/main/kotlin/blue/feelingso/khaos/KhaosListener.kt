package blue.feelingso.khaos

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.math.absoluteValue

class KhaosListener(_khaos :Khaos) : Listener {
    private val khaos = _khaos
    private val timer = PlayerTimer(_khaos)

    @EventHandler
    fun onBlockBroken(ev :BlockBreakEvent) {
        val conf = khaos.getConfigure()
        val player = ev.player
        val block = ev.block

        // 破壊したブロックの数だけアイテムの耐久度を減らす（コード上は増やす）
        val tool = player.inventory.itemInMainHand

        // プレイヤの権限を確認
        if (!player.hasPermission("khaos.dig")) return

        // まずプレイヤ個人が機能を有効にしているか確認
        if (!khaos.getPlayerConf(player.name)) return

        // サバイバルか確認
        if (player.gameMode != GameMode.SURVIVAL) return

        // スニーク中は無効
        if (player.isSneaking && conf.forceOnSneaking) return

        // 最初に破壊されたブロックがそのツールの対象か確認
        val blockTypes = conf.getAllowedItems(tool.type)
        if (!blockTypes.contains(block.type.toString())) return

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

        // 最初に破壊したブロックと同じidのブロックを破壊．
        for (i in 0..conf.radius) {
            for (j in 0..conf.radius) {
                for (k in conf.near..(conf.far - 1))
                {
                    val targetBlock =
                            when (compass) {
                                Compass.EAST -> {
                                    block.getRelative(k,i - conf.radius / 2,j - conf.radius / 2)
                                }
                                Compass.WEST -> {
                                    block.getRelative(-k,i - conf.radius / 2,j - conf.radius / 2)
                                }
                                Compass.NORTH -> {
                                    block.getRelative(j - conf.radius / 2,i - conf.radius / 2, -k)
                                }
                                Compass.SOUTH -> {
                                    block.getRelative(j - conf.radius / 2,i - conf.radius / 2, k)
                                }
                            }

                    if (blockTypes.contains(targetBlock.type.toString()) && (targetBlock.y >= player.location.blockY || !conf.dontDigFloor)) {
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
            player.inventory.remove(tool)
        }
    }

    @EventHandler
    fun onPlayerRightClick(ev :PlayerInteractEvent) {

        val conf = khaos.getConfigure()
        val player = ev.player
        val tool = player.inventory.itemInMainHand
        val clickedBlock = ev.clickedBlock

        when(ev.action) {
            // allowToolsに記載されていないツールとブロックのセットなら無視する
            Action.RIGHT_CLICK_BLOCK -> if (!conf.getAllowedItems(tool.type).contains(clickedBlock.type.toString())) return
            Action.RIGHT_CLICK_AIR -> {}
            else -> return
        }

        // 前回の実行などを見る（1度に2回呼ばれるのが謎）
        if (timer.get(player, "lastPlayerRightClick")) return

        // タイマをセット
        timer.set(player, "lastPlayerRightClick")

        // 設定を見る
        if (!conf.switchRightClick) return

        // 権限を見る
        if (!player.hasPermission("khaos.switch")) return

        // スコップで草ブロックを殴ったときに除外
        if (clickedBlock.type == Material.GRASS_BLOCK && tool.type.isShovel()) return

        // 設定を変えて，メッセージを出力して終了
        khaos.setPlayerConf(player.name, !khaos.getPlayerConf(player.name))
        player.sendMessage("[Khaos] Switched to ${if (khaos.getPlayerConf(player.name)) "ON" else "OFF"}")
    }
}
