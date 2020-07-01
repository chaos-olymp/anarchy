package de.chaosolymp.anarchy.command

import de.chaosolymp.anarchy.AnarchyPlugin
import de.chaosolymp.anarchy.config.Replacement
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*

class StatsCommandExecutor(private val plugin: AnarchyPlugin) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val target: UUID
        target = if (args.size == 1) {
            val player = this.plugin.server.getPlayerExact(args[0])
            if (player != null) {
                player.uniqueId
            } else {
                sender.sendMessage(this.plugin.messageConfiguration.getMessage("error.player-not-found", emptyArray()))
                return true
            }
        } else {
            if (sender is Player) {
                sender.uniqueId;
            } else {
                sender.sendMessage(this.plugin.messageConfiguration.getMessage("error.not-a-player", emptyArray()))
                return true
            }
        }

        val opt = this.plugin.databaseManager.getPlayerStatistic(target).thenAccept {
            if (it.isPresent) {
                val statistic = it.get()
                sender.sendMessage(
                    this.plugin.messageConfiguration.getMessage(
                        "command.stats", arrayOf(
                            Replacement("player", statistic.name),
                            Replacement("uuid", target.toString()),
                            Replacement("ranking", statistic.ranking),
                            Replacement("killStreaks", statistic.killStreak),
                            Replacement("kills", statistic.killCount),
                            Replacement("deaths", statistic.deathCount),
                            Replacement("joins", statistic.joinCount),
                            Replacement("kd", statistic.getKD())
                        )
                    )
                )
            } else {
                sender.sendMessage(this.plugin.messageConfiguration.getMessage("error.no-stats", emptyArray()))
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val list = mutableListOf<String>()
        if (args.size <= 1) {
            val playerList: Collection<OfflinePlayer> = if (sender.hasPermission("anarchy.complete-offline-players")) {
                this.plugin.server.offlinePlayers.toList()
            } else {
                this.plugin.server.onlinePlayers
            }
            for (player in playerList) {
                player.name?.let { list.add(it) }
            }

        }
        return list
    }

}
