package de.chaosolymp.anarchy.command

import de.chaosolymp.anarchy.AnarchyPlugin
import de.chaosolymp.anarchy.config.Replacement
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class TopCommandExecutor(private val plugin: AnarchyPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val top = this.plugin.databaseManager.getTopKillers(10)
        sender.sendMessage(this.plugin.messageConfiguration.getMessage("command.top.heading", emptyArray()))
        for (statistic in top) {
            sender.sendMessage(
                this.plugin.messageConfiguration.getMessage(
                    "command.top.element", arrayOf(
                        Replacement("uuid", statistic.uuid.toString()),
                        Replacement("player", statistic.name),
                        Replacement("ranking", statistic.ranking),
                        Replacement("kills", statistic.killCount),
                        Replacement("deaths", statistic.deathCount),
                        Replacement("joins", statistic.joinCount)
                    )
                )
            )
        }

        return true
    }

}