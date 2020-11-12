package com.cube.notifier.commands

import com.cube.notifier.Configuration
import com.cube.notifier.Update
import com.cube.notifier.Update.Companion.timer
import com.cube.notifier.Update.Companion.update
import com.cube.notifier.Utils.drawText
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.cube.api.commands.CommandData
import org.cube.api.commands.MinecraftCommand
import org.cube.api.timer.Countdown
import org.cube.api.timer.convertTime

class UpdateCommand {

    @MinecraftCommand(
        name ="update",
        description = "Stops a current Update",
        usage = "/update 1m20s {message}",
        aliases = ["us"],
        permission = "update.*",
        types = [String::class,String::class]
    )

    fun update(data : CommandData) {
        val player = data.getPlayer()
        val args = data.args
        if (check(args) && args[0].any { it.isDigit() }) {
            if (update) {
                player.sendMessage("Server is Already updating")
                return
            } else {
                update = true
                val time = args[0].replace("s","s ").replace("m","m ").replace("h","h ")
                val message = args[1].replace(","," ")
                update(time, message, player)
            }
        } else {
            if(data.isPlayer() ) {
                player.sendMessage(Configuration.data.error)
            } else {
                Bukkit.getConsoleSender().sendMessage(Configuration.data.error)
            }
           return
        }
    }


    private fun check(args: Array<String>): Boolean = args.isEmpty()
            || args.size > 2
            || args[0].contains("s")
            || args[0].contains("m")
            || args[0].contains("h")
            || args[1].isNotEmpty()

    private fun update(time: String, message: String, sender: CommandSender) {
        try {
            timer = object : Countdown(Update.plugin, convertTime(time)) {
                override fun onFinish() {
                    Bukkit.dispatchCommand(sender, "restart")
                }
                override fun onTick() {
                    Update.message = message
                    drawText()
                }
            }
            timer.start()
            update = true
        }catch (event : Exception) {
            update = false
        }

    }


}
