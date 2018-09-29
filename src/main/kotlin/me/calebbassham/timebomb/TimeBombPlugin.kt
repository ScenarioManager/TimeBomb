package me.calebbassham.timebomb

import me.calebbassham.scenariomanager.api.scenarioManager
import org.bukkit.plugin.java.JavaPlugin

class TimeBombPlugin : JavaPlugin() {

    override fun onEnable() {
        scenarioManager.register(TimeBomb(), this)

    }

}