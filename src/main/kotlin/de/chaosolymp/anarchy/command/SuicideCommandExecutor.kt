package de.chaosolymp.anarchy.command

import de.chaosolymp.anarchy.AnarchyPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SuicideCommandExecutor(private val plugin: AnarchyPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender is Player) {
            this.plugin.playerListener.suicideList.add(sender.uniqueId)
            sender.health = 0.0
        } else {
            sender.sendMessage(this.plugin.messageConfiguration.getMessage("error.not-a-player", emptyArray()))
        }
        return true
    }

}
