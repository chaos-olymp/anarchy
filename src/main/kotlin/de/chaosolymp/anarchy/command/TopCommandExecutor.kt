package de.chaosolymp.anarchy.command

import de.chaosolymp.anarchy.AnarchyPlugin
import de.chaosolymp.anarchy.config.Replacement
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class TopCommandExecutor(private val plugin: AnarchyPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val collection = this.plugin.databaseManager.getTopKillers(10).get()
        sender.sendMessage(this.plugin.messageConfiguration.getMessage("command.top.heading", emptyArray()))
        for (statistic in collection) {
            sender.sendMessage(
                this.plugin.messageConfiguration.getMessage(
                    "command.top.element", arrayOf(
                        Replacement("player", statistic.name),
                        Replacement("ranking", statistic.ranking),
                        Replacement("kills", statistic.killCount)
                    )
                )
            )
        }
        return true
    }

}