package me.calebbassham.timebomb

import me.calebbassham.scenariomanager.ScenarioManagerUtils
import me.calebbassham.scenariomanager.api.Scenario
import me.calebbassham.scenariomanager.api.ScenarioEvent
import me.calebbassham.scenariomanager.api.ScenarioManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin

class TimeBomb(plugin: JavaPlugin, private val sm: ScenarioManager) : Scenario("TimeBomb", plugin), Listener {

    override val description = "When a player dies, their items will be added to a chest that explodes in 30 seconds."

    @EventHandler(priority = EventPriority.HIGHEST) // Any drops added in other death events will be added to chest
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val player = e.entity
        val world = player.world

        if (!sm.isGamePlayer(player))
        if (!sm.isGameWorld(world)) return

        val drops = e.drops.filter { it != null }.filter { it.type != Material.AIR }.toTypedArray()
        e.drops.clear()

        if (drops.isEmpty()) return

        val chest1 = player.location.block
        chest1.type = Material.CHEST

        val chest1data = chest1.blockData as org.bukkit.block.data.type.Chest
        chest1data.facing = BlockFace.SOUTH

        chest1.blockData = chest1data

        if (drops.size > 27) {
            chest1data.type = org.bukkit.block.data.type.Chest.Type.RIGHT

            chest1.getRelative(BlockFace.EAST).apply {
                val chest2 = this
                chest2.type = Material.CHEST

                val chest2data = chest2.blockData as org.bukkit.block.data.type.Chest
                chest2data.facing = BlockFace.SOUTH

                chest2data.type = org.bukkit.block.data.type.Chest.Type.LEFT

                chest2.blockData = chest2data
            }
        }

        val chest = chest1.state as Chest

        chest.inventory.contents = drops

        val standLocation = if (drops.size > 27) {
            // middle of double chest
            chest1.location.add(1.0, 0.75, 0.5)
        } else {
            // above single chest
            chest1.location.add(0.5, 0.75, 0.5)
        }

        val stand = chest1.world.spawn(standLocation, ArmorStand::class.java).apply {
            isCustomNameVisible = true
            isSmall = true
            setGravity(false)
            isVisible = false
            isMarker = true
        }

        sm.eventScheduler.scheduleEvent(object : ScenarioEvent("${player.displayName} explodes", true) {
            override fun run() {
                chest1.world.createExplosion(chest1.location, 4f)
                chest1.world.strikeLightning(standLocation)
                stand.remove()
            }

            override fun onTick(ticksRemaining: Long) {
                if (ticksRemaining % 20 != 0L) return
                stand.customName = ChatColor.DARK_RED.toString() + ScenarioManagerUtils.formatTicks(ticksRemaining)
            }
        }, 20 * 30)
    }

}