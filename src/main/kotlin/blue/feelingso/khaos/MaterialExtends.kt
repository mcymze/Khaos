package blue.feelingso.khaos

import org.bukkit.Material

// このアイテムはスコップか?
fun Material.isShovel() :Boolean {
    return (when(this) {
        Material.WOODEN_SHOVEL -> true
        Material.STONE_SHOVEL -> true
        Material.IRON_SHOVEL -> true
        Material.GOLDEN_SHOVEL -> true
        Material.DIAMOND_SHOVEL -> true
        else -> false
    })
}

// このアイテムはクワか?
fun Material.isHoe(): Boolean {
    return (when(this) {
        Material.WOODEN_HOE -> true
        Material.STONE_HOE -> true
        Material.IRON_HOE -> true
        Material.GOLDEN_HOE -> true
        Material.DIAMOND_HOE -> true
        else -> false
    })
}

// このブロックは耕せるか?
fun Material.isPlowable(): Boolean {
    return (when(this) {
        Material.GRASS_BLOCK -> true
        Material.DIRT -> true
        else -> false
    })
}

// 右クリで使うようなツールか?
fun Material.isTool() :Boolean {
    return (when(this) {
        Material.FLINT_AND_STEEL -> true
        Material.SHEARS -> true
        else -> false
    })
}
