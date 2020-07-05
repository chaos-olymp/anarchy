package de.chaosolymp.anarchy

import de.chaosolymp.anarchy.command.HelpCommandExecutor
import de.chaosolymp.anarchy.command.StatsCommandExecutor
import de.chaosolymp.anarchy.command.SuicideCommandExecutor
import de.chaosolymp.anarchy.command.TopCommandExecutor
import de.chaosolymp.anarchy.config.DatabaseConfiguration
import de.chaosolymp.anarchy.config.MessageConfiguration
import de.chaosolymp.anarchy.config.PluginConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import sun.plugin2.main.server.Plugin
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AnarchyPlugin : JavaPlugin() {

    lateinit var playerListener: PlayerListener
    lateinit var pluginConfiguration: PluginConfiguration
    lateinit var messageConfiguration: MessageConfiguration
    lateinit var databaseManager: DatabaseManager
    lateinit var databaseConfig: DatabaseConfiguration

    override fun onEnable() {
        val startTime = System.currentTimeMillis()
        this.initPluginConfig()
        this.initDatabaseConfig()
        this.initMessageConfig()
        this.databaseManager = DatabaseManager(this)
        this.databaseManager.createTable()
        this.getCommand("suicide")?.setExecutor(SuicideCommandExecutor(this))
        this.getCommand("stats")?.setExecutor(StatsCommandExecutor(this))
        this.getCommand("top")?.setExecutor(TopCommandExecutor(this))
        this.getCommand("help")?.setExecutor(HelpCommandExecutor(this))
        this.playerListener = PlayerListener(this)
        this.server.pluginManager.registerEvents(this.playerListener, this)
        this.logger.info(String.format("Plugin warmup finished (Took %dms)", System.currentTimeMillis() - startTime))
    }

    private fun initMessageConfig() {
        val config = File(this.dataFolder, "messages.yml");
        if(!this.dataFolder.exists()) {
            this.dataFolder.mkdir()
            this.logger.info("Created plugin data folder ${dataFolder.name}")
        }
        if(!config.exists()) {
            if(config.createNewFile()) {
                val defaultConfig = MessageConfiguration.getDefaultConfiguration();
                defaultConfig.save(config)
                this.messageConfiguration = MessageConfiguration(defaultConfig)
                this.logger.info("Created default configuration file ${config.name}")
            }
        } else {
            this.messageConfiguration = MessageConfiguration(YamlConfiguration.loadConfiguration(config))
            this.logger.info("Loaded configuration file ${config.name}")
        }
    }

    private fun initDatabaseConfig() {
        val config = File(this.dataFolder, "database.yml");
        if(!this.dataFolder.exists()) {
            this.dataFolder.mkdir()
            this.logger.info("Created plugin data folder ${dataFolder.name}")
        }
        if(!config.exists()) {
            if(config.createNewFile()) {
                val defaultConfig = YamlConfiguration()
                val defaultJdbc = "jdbc:mysql://localhost:3306/anarchy"
                val defaultUsername = "root"
                val defaultPassword = "password"
                defaultConfig.set("jdbc", defaultJdbc)
                defaultConfig.set("username", defaultUsername)
                defaultConfig.set("password", defaultPassword)
                defaultConfig.save(config)
                this.databaseConfig = DatabaseConfiguration(defaultJdbc, defaultUsername, defaultPassword)
                this.logger.info("Created default configuration file ${config.name}")
                this.logger.warning("Please edit your database settings - Password \"password\" is not secure enough.")
            }
        } else {
            val yamlConfig = YamlConfiguration.loadConfiguration(config)
            if(yamlConfig.contains("jdbc") && yamlConfig.contains("username") && yamlConfig.contains("password")) {
                this.databaseConfig = yamlConfig.getString("jdbc")?.let {
                    DatabaseConfiguration(
                        it,
                        yamlConfig.getString("username")!!,
                        yamlConfig.getString("password")!!
                    )
                }!!
            } else {
                this.logger.severe("Error whilst loading configuration file")
            }
            this.logger.info("Loaded configuration file ${config.name}")
        }
    }

    private fun initPluginConfig() {
        val config = File(this.dataFolder, "config.yml");
        if(!this.dataFolder.exists()) {
            this.dataFolder.mkdir()
            this.logger.info("Created plugin data folder ${dataFolder.name}")
        }
        if(!config.exists()) {
            if(config.createNewFile()) {
                val defaultConfig = YamlConfiguration()
                val defaultKillStreakJump = 5
                defaultConfig.set("killstreak.jump", defaultKillStreakJump)
                defaultConfig.save(config)
                this.pluginConfiguration = PluginConfiguration(defaultKillStreakJump)
                this.logger.info("Created default configuration file ${config.name}")
            }
        } else {
            val yamlConfig = YamlConfiguration.loadConfiguration(config)
            if(yamlConfig.contains("killstreak.jump")) {
                this.pluginConfiguration = PluginConfiguration(yamlConfig.getInt("killstreak.jump"))
            } else {
                this.logger.severe("Error whilst loading configuration file")
            }
            this.logger.info("Loaded configuration file ${config.name}")
        }
    }

}