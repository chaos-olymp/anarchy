package de.chaosolymp.anarchy

import de.chaosolymp.anarchy.config.Replacement
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener(private val plugin: AnarchyPlugin) : Listener {

    @EventHandler
    fun handleJoin(event: PlayerJoinEvent) {
        event.joinMessage = this.plugin.messageConfiguration.getMessage("join", arrayOf(
            Replacement("player", event.player.name)
        ))
        event.player.sendMessage(this.plugin.messageConfiguration.getMessage("greeting", emptyArray()))
    }

    @EventHandler
    fun handleQuit(event: PlayerQuitEvent) {
        event.quitMessage = this.plugin.messageConfiguration.getMessage("quit", arrayOf(
            Replacement("player", event.player.name)
        ))
    }

    @EventHandler
    fun handleDeath(event: PlayerDeathEvent) {
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

        event.entity.killer?.uniqueId?.let { this.plugin.databaseManager.incrementDeaths(it) }
        this.plugin.databaseManager.incrementDeaths(event.entity.uniqueId)
    }

}
