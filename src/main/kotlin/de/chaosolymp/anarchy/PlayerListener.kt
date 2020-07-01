package de.chaosolymp.anarchy

import de.chaosolymp.anarchy.config.Replacement
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class PlayerListener(private val plugin: AnarchyPlugin) : Listener {

    val suicideList = mutableListOf<UUID>()

    @EventHandler
    fun handleJoin(event: PlayerJoinEvent) {
        val player = event.player
        this.plugin.databaseManager.insertIntoTableIfNotExists(player.uniqueId, player.name).thenAccept {
            this.plugin.databaseManager.incrementJoins(player.uniqueId)
        }

        event.joinMessage = this.plugin.messageConfiguration.getMessage("join", arrayOf(
            Replacement("player", event.player.name)
        ))
        player.sendMessage(this.plugin.messageConfiguration.getMessage("greeting", emptyArray()))
    }

    @EventHandler
    fun handleQuit(event: PlayerQuitEvent) {
        event.quitMessage = this.plugin.messageConfiguration.getMessage("quit", arrayOf(
            Replacement("player", event.player.name)
        ))
    }

    @EventHandler
    fun handleDeath(event: PlayerDeathEvent) {
        if(this.suicideList.contains(event.entity.uniqueId)) {
            for(player in this.plugin.server.onlinePlayers)  {
                if(player.uniqueId == event.entity.uniqueId) {
                    player.sendMessage(this.plugin.messageConfiguration.getMessage("death.suicide.personal", arrayOf(
                        Replacement("player", player.name)
                    )))
                } else {
                    player.sendMessage(this.plugin.messageConfiguration.getMessage("death.suicide", arrayOf(
                        Replacement("player", player.name)
                    )))
                }
            }
            this.suicideList.remove(event.entity.uniqueId)
            event.deathMessage = null
            return
        }

        when (event.entity.lastDamageCause?.cause) {
            EntityDamageEvent.DamageCause.SUICIDE -> event.deathMessage = this.plugin.messageConfiguration.getMessage("death.suicide", arrayOf(Replacement("player", event.entity.name)))
            EntityDamageEvent.DamageCause.FALL -> event.deathMessage = this.plugin.messageConfiguration.getMessage("death.fall-damage", arrayOf(Replacement("player", event.entity.name)))
            EntityDamageEvent.DamageCause.PROJECTILE, EntityDamageEvent.DamageCause.ENTITY_ATTACK, EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK -> {
                if(event.entity.killer != null) {
                    event.deathMessage = this.plugin.messageConfiguration.getMessage(
                        "death.kill",
                        arrayOf(
                            Replacement("player", event.entity.name),
                            Replacement("killer", event.entity.killer!!.name)
                        )
                    )
                } else {
                    event.deathMessage = this.plugin.messageConfiguration.getMessage(
                        "death.default",
                        arrayOf(
                            Replacement("player", event.entity.name)
                        )
                    )
                }
            }
            else -> event.deathMessage = this.plugin.messageConfiguration.getMessage(
                "death.default",
                arrayOf(
                    Replacement("player", event.entity.name)
                )
            )
        }

        event.entity.killer?.uniqueId?.let { it ->
            this.plugin.databaseManager.incrementKills(it).thenAccept {_ ->
                this.plugin.databaseManager.getPlayerStatistic(it).thenAccept {it2 ->
                    if(it2.isPresent) {
                        val statistic = it2.get()
                        if(statistic.killStreak > 0 && statistic.killStreak % this.plugin.pluginConfiguration.killStreakJump == 0) {
                            Bukkit.broadcastMessage(this.plugin.messageConfiguration.getMessage("killstreak.global", arrayOf(
                                Replacement("player", statistic.name),
                                Replacement("streak", statistic.killStreak)
                            )))
                        }
                    }
                }

            }

        }
        this.plugin.databaseManager.incrementDeaths(event.entity.uniqueId).thenAccept {
            this.plugin.databaseManager.resetKillStreak(event.entity.uniqueId)
        }

    }

}
