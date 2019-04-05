package blue.feelingso.khaos

import org.bukkit.Material

// このアイテムはスコップか?
fun Material.isShovel() :Boolean {
    if (this == Material.WOODEN_SHOVEL) return true
    if (this == Material.STONE_SHOVEL) return true
    if (this == Material.IRON_SHOVEL) return true
    if (this == Material.GOLDEN_SHOVEL) return true
    if (this == Material.DIAMOND_SHOVEL) return true
    return false
}

// このアイテムはクワか?
fun Material.isHoe(): Boolean {
    if (this == Material.WOODEN_HOE) return true
    if (this == Material.STONE_HOE) return true
    if (this == Material.IRON_HOE) return true
    if (this == Material.GOLDEN_HOE) return true
    if (this == Material.DIAMOND_HOE) return true
    return false
}

// このブロックは耕せるか?
fun Material.isPlowable(): Boolean {
    if (this == Material.GRASS_BLOCK) return true
    if (this == Material.DIRT) return true
    return false
}
