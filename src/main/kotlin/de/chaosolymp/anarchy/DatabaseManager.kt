package de.chaosolymp.anarchy

import java.util.*
import java.util.concurrent.*


class DatabaseManager(plugin: AnarchyPlugin) {

    private val dataSource = plugin.databaseConfig.dataSource;
    private val executor: ExecutorService = Executors.newCachedThreadPool()

    fun createTable(): Future<*> {
        val task = Runnable {
            val statement = this.dataSource.connection.prepareStatement("CREATE TABLE IF NOT EXISTS `statistics` (`uuid` BINARY(16) NOT NULL, `name` VARCHAR(16), `join_count` INT DEFAULT 0, `death_count` INT DEFAULT 0, `kill_count` INT DEFAULT 0, `kill_streak` INT DEFAULT 0, PRIMARY KEY (`uuid`))")
            statement.execute()
        }
        return executor.submit(task)
    }

    fun insertIntoTableIfNotExists(uuid: UUID, name: String): Future<*> {
        val task = FutureTask<Void?>(Runnable {
            val statement = this.dataSource.connection.prepareStatement("REPLACE INTO `statistics` (`uuid`, `name`) VALUES (?, ?)")
            statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))
            statement.setString(2, name)
            statement.execute()
        }, null)
        return executor.submit(task)
    }

    fun getPlayerStatistic(uuid: UUID): Future<Optional<PlayerStatistic>> {
        val task = Callable {
            val statement = this.dataSource.connection.prepareStatement("SELECT name, join_count, death_count, kill_count, kill_streak, RANK() OVER (ORDER BY `kill_count`) ranking FROM statistics WHERE uuid = ?")
            statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))

            val rs = statement.executeQuery();
            val opt: Optional<PlayerStatistic>
            opt = if(rs.next()) {
                val name = rs.getString("name")
                val joinCount = rs.getInt("join_count")
                val deathCount = rs.getInt("death_count")
                val killCount = rs.getInt("kill_count")
                val killStreak = rs.getInt("kill_streak")
                val ranking = rs.getInt("ranking")
                Optional.of(PlayerStatistic(uuid, name, killCount, deathCount, joinCount, killStreak, ranking))
            } else {
                Optional.empty()
            }
            return@Callable opt

        }

        return executor.submit(task)
    }
    fun getTopKillers(count: Int): Future<Collection<PlayerStatistic>> {
        val task = Callable<Collection<PlayerStatistic>> {
            val statement = this.dataSource.connection.prepareStatement("SELECT uuid, name, join_count, death_count, kill_count, kill_streak FROM statistics ORDER BY `kill_count` DESC LIMIT ?")
            statement.setInt(1, count)
            val rs = statement.executeQuery()
            val list = mutableListOf<PlayerStatistic>()
            var rank = 1;
            while(rs.next()) {
                val uuid = UUIDUtils.getUUIDFromBytes(rs.getBytes("uuid"));
                val name = rs.getString("name")
                val joinCount = rs.getInt("join_count")
                val deathCount = rs.getInt("death_count")
                val killCount = rs.getInt("kill_count")
                val killStreak = rs.getInt("kill_streak")
                list.add(PlayerStatistic(uuid, name, killCount, deathCount, joinCount, killStreak, rank))
                rank++
            }
            return@Callable list
        }
        return this.executor.submit(task)
    }

    fun incrementDeaths(uuid: UUID): Future<*> {
        val task = Runnable {
            val statement = this.dataSource.connection.prepareStatement("UPDATE statistics SET death_count = death_count + 1 WHERE uuid = ?")
            statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))
            statement.execute()
        }
        return executor.submit(task)
    }

    fun incrementKills(uuid: UUID): Future<*> {
        val task = Runnable {
            val statement = this.dataSource.connection.prepareStatement("UPDATE statistics SET kill_count = kill_count + 1, kill_streak = kill_streak + 1 WHERE uuid = ?")
            statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))
            statement.execute()
        }
        return executor.submit(task)
    }

    fun incrementJoins(uuid: UUID): Future<*> {
        val task = Runnable {
            val statement = this.dataSource.connection.prepareStatement("UPDATE statistics SET join_count = join_count + 1 WHERE uuid = ?")
            statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))
            statement.execute()
        }
        return executor.submit(task)
    }

    fun resetKillStreak(uuid: UUID): Future<*> {
        val task = Runnable {
            val statement = this.dataSource.connection.prepareStatement("UPDATE statistics SET kill_streak = 0 WHERE uuid = ?")
            statement.setBytes(1, UUIDUtils.getBytesFromUUID(uuid))
            statement.execute()
        }
        return executor.submit(task)
    }
}