package de.chaosolymp.anarchy

import java.util.*

data class PlayerStatistic(val uuid: UUID, val name: String, val killCount: Int, val deathCount: Int, val joinCount: Int, val killStreak: Int, val ranking: Int) {
    fun getKD(): Double = this.killCount.toDouble() / this.deathCount.toDouble()
}