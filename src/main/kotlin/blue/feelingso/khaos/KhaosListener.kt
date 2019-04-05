package blue.feelingso.khaos

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
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
        val radius = conf.getInt("radius", 2)
        val isConsume = conf.getBoolean("consume", true)
        val forceOnSneaking = conf.getBoolean("forceOnSneaking", false)
        val dontDigFloor = conf.getBoolean("dontDigFloor", true)

        // 手前にいくつ，奥にいくつ掘るかという設定．nearは負数でfarは正数であって欲しい．
        val near = if (conf.getInt("near", 0) < 0) conf.getInt("near", 0) else conf.getInt("near", 0) * -1
        val far = if (conf.getInt("far", 2) < 0) conf.getInt("far", 2) * -1 else conf.getInt("far", 2)

        // 破壊したブロックの数だけアイテムの耐久度を減らす（コード上は増やす）
        val tool = player.inventory.itemInMainHand

        // プレイヤの権限を確認
        if (!player.hasPermission("khaos.dig")) return

        // まずプレイヤ個人が機能を有効にしているか確認
        if (!khaos.getPlayerConf(player.name)) return

        // サバイバルか確認
        if (player.gameMode != GameMode.SURVIVAL) return

        // スニーク中は無効
        if (player.isSneaking && !forceOnSneaking) return

        // 最初に破壊されたブロックがそのツールの対象か確認
        if (!conf.getStringList("allowTools.${tool.type}").contains(block.type.toString())) return

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

        //val blockType = block.type
        val blockTypes = conf.getStringList("allowTools.${tool.type}");

        // 最初に破壊したブロックと同じidのブロックを破壊．
        for (i in 0..radius) {
            for (j in 0..radius) {
                for (k in near..(far - 1))
                {
                    val targetBlock =
                            when (compass) {
                                Compass.EAST -> {
                                    block.getRelative(k,i - radius / 2,j - radius / 2)
                                }
                                Compass.WEST -> {
                                    block.getRelative(-k,i - radius / 2,j - radius / 2)
                                }
                                Compass.NORTH -> {
                                    block.getRelative(j - radius / 2,i - radius / 2, -k)
                                }
                                Compass.SOUTH -> {
                                    block.getRelative(j - radius / 2,i - radius / 2, k)
                                }
                            }
                    // 最初に掘ったブロックと同一でかつ，dontDigFloorが有効の場合は足元より上のみ
                    //if (targetBlock.type == blockType && (targetBlock.y >= player.location.blockY || !dontDigFloor)) {
                    if (blockTypes.contains(targetBlock.type.toString()) && (targetBlock.y >= player.location.blockY || !dontDigFloor)) {
                        targetBlock.breakNaturally(tool)
                        if (isConsume) tool.durability = (tool.durability + 1).toShort()
                    }
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

    @EventHandler
    fun onBlockPunched(ev :BlockDamageEvent) {
        // なにも持ってない状態で殴ったら機能をトグルする

        if (
                ev.player.inventory.itemInMainHand.type === Material.AIR
                && khaos.getConfigure().getBoolean("switchFromPunch", true)
                && ev.player.hasPermission("khaos.switch")
        ) {
            // 設定を変えて，メッセージを出力して終了
            khaos.setPlayerConf(ev.player.name, !khaos.getPlayerConf(ev.player.name))
            ev.player.sendMessage("[Khaos] Switched to ${if (khaos.getPlayerConf(ev.player.name)) "ON" else "OFF"}")
        }
        else if (
                ev.player.inventory.itemInMainHand.type === Material.STICK
                && ev.player.hasPermission("khaos.debug")
        ) {
            ev.player.sendMessage("Knocked " + ev.block.type.toString())
        }
        /*
        // まず設定を見る
        if (!khaos.getConfigure().getBoolean("switchFromPunch", true)) return

        // 権限を見る
        if (!ev.player.hasPermission("khaos.switch")) return

        // なにも持っていないか確認
        if (ev.player.inventory.itemInMainHand.type !== Material.AIR) return

        // 設定を変えて，メッセージを出力して終了
        khaos.setPlayerConf(ev.player.name, !khaos.getPlayerConf(ev.player.name))
        ev.player.sendMessage("[Khaos] Switched to ${if (khaos.getPlayerConf(ev.player.name)) "ON" else "OFF"}")
        */
    }

    @EventHandler()
    fun onPlayerRightClick(ev :PlayerInteractEvent) {

        val player = ev.player
        val clickedBlock = ev.clickedBlock


        when(ev.action) {
            Action.RIGHT_CLICK_BLOCK -> if (isUsefulItem(clickedBlock.type)) return
            Action.RIGHT_CLICK_AIR -> {}
            else -> return
        }

        // 前回の実行などを見る（1度に2回呼ばれるのが謎）
        if (timer.get(player, "lastPlayerRightClick")) return

        // タイマをセット
        timer.set(player, "lastPlayerRightClick")

        // 設定を見る
        if (!khaos.getConfigure().getBoolean("switchRightClick", true)) return

        // 権限を見る
        if (!player.hasPermission("khaos.switch")) return

        val itemInMainHand = player.inventory.itemInMainHand

        // 耐久値のあるものを除外
        if (itemInMainHand.type.maxDurability == 0.toShort()) return

        // 右クリで使用するタイプのアイテムか?
        if (itemInMainHand.type.isTool()) return

        // スコップで土を殴ったときに除外
        if (clickedBlock.type == Material.GRASS_BLOCK && itemInMainHand.type.isHoe()) return

        // クワで耕したときに除外
        if (clickedBlock.type.isPlowable() && itemInMainHand.type.isShovel()) return

        // 設定を変えて，メッセージを出力して終了
        khaos.setPlayerConf(player.name, !khaos.getPlayerConf(player.name))
        player.sendMessage("[Khaos] Switched to ${if (khaos.getPlayerConf(player.name)) "ON" else "OFF"}")
    }

    private fun isUsefulItem(material :Material) :Boolean {
        return mutableListOf(
                // だるい
                // Material.WORKBENCH,
                Material.CHEST,
                Material.ENDER_CHEST,
                Material.TRAPPED_CHEST,
                Material.FURNACE,
                // Material.BURNING_FURNACE,
                Material.DROPPER,
                Material.DISPENSER,
                Material.MINECART,
                Material.BREWING_STAND,
                Material.ENCHANTING_TABLE,
                Material.ANVIL,
                Material.BEACON,
                Material.COMMAND_BLOCK_MINECART,
                Material.TNT_MINECART,
                Material.HOPPER_MINECART,
                Material.ACACIA_DOOR,
                Material.BIRCH_DOOR,
                Material.DARK_OAK_DOOR,
                Material.IRON_DOOR,
                Material.JUNGLE_DOOR,
                Material.SPRUCE_DOOR,
                Material.OAK_TRAPDOOR,
                Material.SPRUCE_TRAPDOOR,
                Material.JUNGLE_TRAPDOOR,
                Material.DARK_OAK_TRAPDOOR,
                Material.ACACIA_TRAPDOOR,
                Material.BIRCH_TRAPDOOR,
                Material.IRON_TRAPDOOR,
                Material.STONE_BUTTON,
                Material.OAK_BUTTON,
                Material.DARK_OAK_BUTTON,
                Material.BIRCH_BUTTON,
                Material.DARK_OAK_BUTTON,
                Material.ACACIA_BUTTON,
                Material.SPRUCE_BUTTON,
                Material.JUNGLE_TRAPDOOR,
                Material.STONE_BUTTON,
                Material.LEVER,
                Material.BLACK_SHULKER_BOX,
                Material.BLUE_SHULKER_BOX,
                Material.BROWN_SHULKER_BOX,
                Material.CYAN_SHULKER_BOX,
                Material.GRAY_SHULKER_BOX,
                Material.GREEN_SHULKER_BOX,
                Material.LIGHT_BLUE_SHULKER_BOX,
                Material.LIME_SHULKER_BOX,
                Material.MAGENTA_SHULKER_BOX,
                Material.ORANGE_SHULKER_BOX,
                Material.PINK_SHULKER_BOX,
                Material.PURPLE_SHULKER_BOX,
                Material.RED_SHULKER_BOX,
                Material.LIGHT_GRAY_SHULKER_BOX,
                Material.WHITE_SHULKER_BOX,
                Material.YELLOW_SHULKER_BOX,
                Material.ACACIA_BOAT,
                Material.OAK_BOAT,
                Material.DARK_OAK_BOAT,
                Material.BIRCH_BOAT,
                Material.JUNGLE_BOAT,
                Material.JUNGLE_BOAT,
                Material.SPRUCE_BOAT,
                Material.ACACIA_FENCE_GATE,
                Material.BIRCH_FENCE_GATE,
                Material.DARK_OAK_FENCE_GATE,
                Material.JUNGLE_FENCE_GATE,
                Material.SPRUCE_FENCE_GATE,
                Material.DAYLIGHT_DETECTOR,
                Material.COMPARATOR,
                Material.SADDLE,
                Material.BEDROCK,
                Material.SIGN,
                Material.WALL_SIGN
        ).contains(material)
    }
}
