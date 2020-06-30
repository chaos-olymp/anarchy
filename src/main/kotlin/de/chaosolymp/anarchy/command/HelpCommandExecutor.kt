package de.chaosolymp.anarchy.command

import de.chaosolymp.anarchy.AnarchyPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class HelpCommandExecutor(private val plugin: AnarchyPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage(this.plugin.messageConfiguration.getMessage("command.help", emptyArray()))
        return true
    }

}