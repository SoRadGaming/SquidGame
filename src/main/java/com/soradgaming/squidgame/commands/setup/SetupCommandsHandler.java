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
			CommandHandlerInterface commandHandlerInterface = commandHandlers.get(args[0]);
			if (args[0].equalsIgnoreCase("save")) {
				return commandHandlerInterface.handleCommand(player, Arrays.copyOfRange(args, 1, args.length));
			} else if (args[0].equalsIgnoreCase("create")) {
				return commandHandlerInterface.handleCommand(player, Arrays.copyOfRange(args, 1, args.length));
			}
		} else if (args.length == 3) {
			CommandHandlerInterface commandHandlerInterface = commandHandlers.get(args[1]);
			if (args[1].equalsIgnoreCase("game1")) {
				return commandHandlerInterface.handleCommand(player, Arrays.copyOfRange(args, 0, args.length));
			} else if (args[1].equalsIgnoreCase("game2")) {
				return commandHandlerInterface.handleCommand(player, Arrays.copyOfRange(args, 0, args.length));
			} else if (args[1].equalsIgnoreCase("game3")) {
				return commandHandlerInterface.handleCommand(player, Arrays.copyOfRange(args, 0, args.length));
			} else if (args[1].equalsIgnoreCase("game6")) {
				return commandHandlerInterface.handleCommand(player, Arrays.copyOfRange(args, 0, args.length));
			} else if (args[1].equalsIgnoreCase("game7")) {
				return commandHandlerInterface.handleCommand(player, Arrays.copyOfRange(args, 0, args.length));
			}
		}
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c ERROR: Please use &6/sqsetup &c to view all valid game commands"));
		return false;
	}
}
