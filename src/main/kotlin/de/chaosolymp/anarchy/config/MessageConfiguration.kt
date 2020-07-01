package de.chaosolymp.anarchy.config

import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration

class MessageConfiguration(private val config: YamlConfiguration) {

    companion object {
        fun getDefaultConfiguration(): YamlConfiguration {
            val config = YamlConfiguration()
            config.set("variables.prefix", "&8[&e&l!&8] &6&lAnarchy &8»")
            config.set("messages.greeting", "\n%prefix% &7Willkommen auf dem &eAnarchy Server&7. &7Benutze &6/help\n%prefix% &cHier gelten &4&n&lKEINE REGELN&c,  alles ist &4&n&lerlaubt &cund PvP ist &4&l&naktiv&c!\n\n")
            config.set("messages.join", "%prefix% &e{player} &7hat den Anarchy &abetreten")
            config.set("messages.quit", "%prefix% &e{player} &7hat den Anarchy &cverlassen")

            config.set("messages.death.kill", "%prefix% &c{player} &7wurde von &4{killer} &7getötet")
            config.set("messages.death.fall-damage", "%prefix% &c{player} &7ist an &eFallschaden &7gestorben")
            config.set("messages.death.suicide", "%prefix% &e{player} &7hat sich das Leben genommen")
            config.set("messages.death.suicide.personal", "%prefix% &7Du hast dir das Leben genommen")
            config.set("messages.death.default", "%prefix% &c{player} &7ist an gestorben")

            config.set("messages.killstreak.global", "%prefix% &c{player} &7hat eine &e{streak}er Killstreak &7erreicht")

            config.set("messages.command.help", "%prefix% &eAnarchy Help Liste\n%prefix% &7/stats <Spieler> &8┃ &eGlobale Statistiken\n%prefix% &7/top Spieler] &8┃ &eTop 10 Kills\n%prefix% &7/suicide &8┃ &eStarte neu")
            config.set("messages.command.stats", "%prefix% &eStatistiken von {player}\n%prefix% &7Platz im Ranking: &e{ranking}\n%prefix% &7Kills: &e{kills}\n%prefix% &7Tode: &e{deaths}\n%prefix% &7K/D: &e{kd}")
            config.set("messages.command.top.heading", "%prefix% &eGlobale Top 10 der Kills")
            config.set("messages.command.top.element", "%prefix% &6#{ranking} &7{player} &8┃ &e{kills} Kills")
            config.set("messages.command.suicide", "%prefix% &7Du hast dir das Leben genommen")

            config.set("messages.error.no-permission", "%prefix% &cKeine Rechte")
            config.set("messages.error.no-stats", "%prefix% &cDieser Spieler ist nicht in unseren Statistiken verzeichnet.")
            config.set("messages.error.player-not-found", "%prefix% &cDieser Spieler konnte nicht gefunden werden.")
            config.set("messages.error.not-a-player", "%prefix% &cDu bist kein Spieler.")
            config.set("messages.error.database-error", "%prefix% &cDatenbankfehler")
            config.set("messages.error.syntax", "%prefix% &cMeintest du &o{syntax}&r&c?")

            return config;
        }
    }

    fun getMessage(key: String, replacements: Array<Replacement>) = this.getLanguageElement("messages.$key", replacements)
    private fun getVariable(key: String, replacements: Array<Replacement>) = this.getLanguageElement("variables.$key", replacements)
    private fun getAllVariableKeys(): MutableSet<String>? {
        val variableSection = config.getConfigurationSection("variables")
        return variableSection?.getKeys(true);
    }

    private fun getLanguageElement(key: String, replacements: Array<Replacement>): String {
        var string = this.config.getString(key)!!
        string = ChatColor.translateAlternateColorCodes('&', string)
        for(replacement in replacements) {
            string = string.replace("{${replacement.key}}", replacement.value.toString())
        }
        for(variable in this.getAllVariableKeys()!!) {
            if (string.contains("%$variable%")) {
                string = string.replace("%$variable%", this.getVariable(variable, emptyArray()))
            }
        }
        return string
    }
}

