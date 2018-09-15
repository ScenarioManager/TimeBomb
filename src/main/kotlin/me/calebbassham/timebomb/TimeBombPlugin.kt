package me.calebbassham.timebomb

import me.calebbassham.scenariomanager.plugin.ScenarioManagerPlugin
import org.bukkit.plugin.java.JavaPlugin

class TimeBombPlugin : JavaPlugin() {

    override fun onEnable() {
        ScenarioManagerPlugin.scenarioManager?.registerScenario(TimeBomb(this))
    }

}