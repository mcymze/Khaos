package blue.feelingso.khaos

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent

class KhaosListener(khaos :Khaos) : Listener {
    private val khaos = khaos
    private val timer = PlayerTimer(khaos)

    @EventHandler
    fun onBlockBroken(ev :BlockBreakEvent) {
        val conf = khaos.khaosConfig
        val player = ev.player

        // 破壊したブロックの数だけアイテムの耐久度を減らす（コード上は増やす）
        val tool = player.inventory.itemInMainHand

        // プレイヤの権限を確認
        if (!player.hasPermissionDig()) return

        // まずプレイヤ個人が機能を有効にしているか確認
        if (!khaos.playerConfig.isActive(player)) return

        // サバイバルか確認
        if (player.gameMode != GameMode.SURVIVAL) return

        // スニーク中は無効
        if (player.isSneaking && conf.forceOnSneaking) return

        val mogura = Mogura(player, ev.block, tool, conf)

        if (!mogura.isRunnable) return

        mogura.run()
    }

    @EventHandler
    fun onPlayerRightClick(ev :PlayerInteractEvent) {

        val conf = khaos.khaosConfig
        val player = ev.player
        val tool = player.inventory.itemInMainHand
        val clickedBlock = ev.clickedBlock ?: return

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
        if (!player.hasPermissionSwitch()) return

        // スコップで草ブロックを殴ったときに除外
        if (clickedBlock.type == Material.GRASS_BLOCK && tool.type.isShovel()) return

        // 設定を変えて，メッセージを出力して終了
        khaos.playerConfig.flip(player)
        player.sendMessage("[Khaos] Switched to ${if (khaos.playerConfig.isActive(player)) "ON" else "OFF"}")
    }
}
