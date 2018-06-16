package blue.feelingso.khaos

import org.bukkit.GameMode
import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import kotlin.math.absoluteValue

class KhaosListener(_conf :FileConfiguration) : Listener, CommandExecutor{
    private val conf = _conf
    private var playerConf : MutableMap<String, Boolean?> = mutableMapOf()
    @EventHandler
    fun onBlockBroken(ev :BlockBreakEvent)
    {
        val player = ev.player
        val block = ev.block
        val radius = conf.getInt("radius", 2)
        val isConsume = conf.getBoolean("isConsume", true)

        // プレイヤの権限を確認

        // まずプレイヤ個人が機能を有効にしているか確認
        if (playerConf[player.name] != true)
        {
            if (playerConf[player.name] == null)
                playerConf[player.name] = conf.getBoolean("players.${player.name}", false)
            return
        }

        // サバイバルか確認
        if (ev.player.gameMode != GameMode.SURVIVAL) return

        // 最初に破壊されたブロックが対象か確認
        if (!conf.getStringList("allowBlocks").contains(block.type.toString())) return
        // 向いている向きとradius設定から破壊する範囲を設定し
        val direction = player.eyeLocation.direction.normalize()
        // 方位を取得する
        // x軸が東西，z軸が南北
        val compass: Compass
        if (direction.z.absoluteValue > direction.x.absoluteValue)
        {
            compass = if (direction.z < 0) Compass.NORTH else Compass.SOUTH
        }
        else
        {
            compass = if (direction.x < 0) Compass.WEST else Compass.EAST
        }

        // 破壊したブロックの数だけアイテムの耐久度を減らす（コード上は増やす）
        val tool = ev.player.inventory.itemInMainHand
        val blockType = block.type

        // 最初に破壊したブロックと同じidのブロックを破壊．
        for (i in 0..radius)
        {
            for (j in 0..radius)
            {
                var targetBlock : Block
                when (compass) {
                    Compass.EAST ->
                    {
                        targetBlock = block.getRelative(0,i - radius / 2,j - radius / 2)
                    }
                    Compass.WEST ->
                    {
                        targetBlock = block.getRelative(0,i - radius / 2,j - radius / 2)
                    }
                    Compass.NORTH ->
                    {
                        targetBlock = block.getRelative(j - radius / 2,i - radius / 2,0)
                    }
                    Compass.SOUTH ->
                    {
                        targetBlock = block.getRelative(j - radius / 2,i - radius / 2,0)
                    }
                }

                if (targetBlock.type == blockType)
                {
                    targetBlock.breakNaturally(tool)
                    if (isConsume) tool.durability = (tool.durability + 1).toShort()
                }
            }
        }

        // 上限に達したら壊す
        if (tool.type.maxDurability < tool.durability)
        {
            player.inventory.remove(tool)
        }
    }

    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean
    {
        if (sender != null) sender.sendMessage("Your now ${playerConf[sender.name]}")
        if (args != null)
        {
            if (args.size == 1)
            {
                if (args[0] == "switch")
                {
                    if (sender == null) return false
                    playerConf[sender.name] = if (playerConf[sender.name] != null) playerConf[sender.name] == true else false
                }
            }
        }

        return true
    }
}