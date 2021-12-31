/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package com.soradgaming.squidgame.commands.setup;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Messages;
import com.soradgaming.squidgame.commands.setup.Games.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SetupCommandsHandler implements CommandExecutor {
	private HashMap<String, CommandHandlerInterface> commandHandlers = new HashMap<>();
	private SquidGame plugin;

	public SetupCommandsHandler(SquidGame plugin) {
		this.plugin = plugin;
		commandHandlers.put("game1", new Game1Setup(plugin));
		commandHandlers.put("game2", new Game2Setup(plugin));
		commandHandlers.put("game3", new Game3Setup(plugin));
		commandHandlers.put("game6", new Game6Setup(plugin));
		commandHandlers.put("game7", new Game7Setup(plugin));
		commandHandlers.put("create", new Create(plugin));
		commandHandlers.put("save", new Save(plugin));
		commandHandlers.put("maxplayers", new maxPlayers(plugin));
		commandHandlers.put("minplayers", new minPlayers(plugin));
		commandHandlers.put("startTime", new startTime(plugin));
		commandHandlers.put("endTime", new endTime(plugin));
		commandHandlers.put("intermissionTime", new intermissionTime(plugin));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("Player is expected");
			return true;
		}
		if (args.length == 0) {
			//help
		}
		if (args.length == 2) {
			if (commandHandlers.containsKey(args[0])) {
				return false;
			}
			CommandHandlerInterface commandHandlerInterface = commandHandlers.get(args[0]);
			//check args length
			if (args.length - 1 < commandHandlerInterface.getMinArgsLength()) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c ERROR: Please use &6/sqsetup &c to view all valid game commands"));
				return false;
			}
			return commandHandlerInterface.handleCommand(player, Arrays.copyOfRange(args, 1, args.length));
		} else if (args.length == 3) {
			if (commandHandlers.containsKey(args[1])) {
				return false;
			}
			CommandHandlerInterface commandHandlerInterface = commandHandlers.get(args[1]);
			//check args length
			if (args.length < commandHandlerInterface.getMinArgsLength()) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c ERROR: Please use &6/sqsetup &c to view all valid game commands"));
				return false;
			}
			return commandHandlerInterface.handleCommand(player, Arrays.copyOfRange(args, 0, args.length));
		} else if (args.length == 4) {
			if (commandHandlers.containsKey(args[2])) {
				return false;
			}
			CommandHandlerInterface commandHandlerInterface = commandHandlers.get(args[2]);
			//check args length
			if (args.length < commandHandlerInterface.getMinArgsLength()) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c ERROR: Please use &6/sqsetup &c to view all valid game commands"));
				return false;
			}
			return commandHandlerInterface.handleCommand(player, Arrays.copyOfRange(args, 0, args.length));
		} else if (args.length == 5) {
			if (commandHandlers.containsKey(args[2])) {
				return false;
			}
			CommandHandlerInterface commandHandlerInterface = commandHandlers.get(args[2]);
			//check args length
			if (args.length < commandHandlerInterface.getMinArgsLength()) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c ERROR: Please use &6/sqsetup &c to view all valid game commands"));
				return false;
			}
			return commandHandlerInterface.handleCommand(player, Arrays.copyOfRange(args, 0, args.length));
		}
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c ERROR: Please use &6/sqsetup &c to view all valid game commands"));
		return false;
	}
}
