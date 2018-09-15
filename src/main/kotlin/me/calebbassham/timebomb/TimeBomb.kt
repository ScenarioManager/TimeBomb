package me.calebbassham.timebomb

import me.calebbassham.scenariomanager.api.Scenario
import me.calebbassham.scenariomanager.api.ScenarioEvent
import me.calebbassham.scenariomanager.plugin.ScenarioManagerPlugin
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin

class TimeBomb(plugin: JavaPlugin): Scenario("TimeBomb", plugin), Listener {

    override val description = "When a player dies, their items will be added to a chest that explodes in 30 seconds."

    @EventHandler(priority = EventPriority.HIGHEST) // Any drops added in other death events will be added to chest
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val drops = e.drops

        val chest1 = e.entity.location.add(0.0, 1.0, 0.0)
        chest1.block.type = Material.CHEST

        if(drops.size > 27) {
            val chest2 = chest1.add(1.0, 0.0, 0.0)
            chest2.block.type = Material.CHEST
        }

        val chestInv = chest1.block as Inventory

        chestInv.contents = drops.toTypedArray()

        ScenarioManagerPlugin.scenarioManager?.eventScheduler?.scheduleEvent(object : ScenarioEvent("${e.entity.displayName} explodes", true) {
            override fun run() {
                chest1.world.createExplosion(chest1, 4f)
            }
        }, 20 * 30)
    }

}