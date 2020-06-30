package de.chaosolymp.anarchy

import de.chaosolymp.anarchy.command.HelpCommandExecutor
import de.chaosolymp.anarchy.command.StatsCommandExecutor
import de.chaosolymp.anarchy.command.SuicideCommandExecutor
import de.chaosolymp.anarchy.command.TopCommandExecutor
import de.chaosolymp.anarchy.config.DatabaseConfiguration
import de.chaosolymp.anarchy.config.MessageConfiguration
import org.bukkit.plugin.java.JavaPlugin

class AnarchyPlugin : JavaPlugin() {

    lateinit var messageConfiguration: MessageConfiguration
    lateinit var databaseManager: DatabaseManager
    lateinit var databaseConfig: DatabaseConfiguration

    override fun onEnable() {
        val startTime = System.currentTimeMillis()
        this.initConfig()
        this.getCommand("suicide")?.setExecutor(
            SuicideCommandExecutor(
                this
            )
        )
        this.getCommand("stats")?.setExecutor(StatsCommandExecutor(this))
        this.getCommand("top")?.setExecutor(TopCommandExecutor(this))
        this.getCommand("help")?.setExecutor(HelpCommandExecutor(this))
        this.server.pluginManager.registerEvents(PlayerListener(this), this)
        this.logger.info(String.format("Plugin warmup finished (Took %dms)", System.currentTimeMillis() - startTime))
    }

    private fun initConfig() {

    }

}