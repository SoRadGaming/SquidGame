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

import com.soradgaming.squidgame.arena.Arena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetupTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}
		ArrayList<String> completions = new ArrayList<>();

		if (args.length == 1) {
			completions = new ArrayList<>(Arena.getArenasNames());
			completions.add("save");
			completions.add("create");
			completions = getApplicableTabCompletes(args[0], completions);
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("placeholder(was_saved)")) {
				completions = new ArrayList<>(Arena.getArenasNames());
				completions = getApplicableTabCompletes(args[1], completions);
			} else if (args[0].equalsIgnoreCase("create")) {
				completions = new ArrayList<>(List.of(""));
				completions = getApplicableTabCompletes(args[1], completions);
			} else {
				completions = new ArrayList<>(Arrays.asList("maxplayers", "minplayers", "games", "startTime", "endTime", "intermissionTime"));
				completions = getApplicableTabCompletes(args[1], completions);
			}
		} else if (args.length == 3) {
			if (args[1].equalsIgnoreCase("games")) {
				completions = new ArrayList<>(Arrays.asList("game1", "game2", "game3", "game4", "game6", "game7"));
				completions = getApplicableTabCompletes(args[2], completions);
			}
		} else if (args.length == 4) {
			switch (args[2]) {
				case ("game1") -> {
					completions = new ArrayList<>(Arrays.asList("spawn", "barrier", "killzone", "goal", "time", "countdown", "lightSwitchMin", "lightSwitchMax"));
					completions = getApplicableTabCompletes(args[3], completions);
				}
				case ("game2") -> {
					completions = new ArrayList<>(Arrays.asList("spawn_red", "spawn_blue", "spawn_green", "spawn_yellow",
							"BuildZone1", "BuildZone2", "BuildZone3", "BuildZone4","DisplayZone1", "DisplayZone2", "DisplayZone3", "DisplayZone4", "time", "countdown"));
					completions = getApplicableTabCompletes(args[3], completions);
				}
				case ("game3") -> {
					completions = new ArrayList<>(List.of("spawn", "time", "countdown", "lightSwitchOn", "lightSwitchOff"));
					completions = getApplicableTabCompletes(args[3], completions);
				}
				case ("game4") -> {
					completions = new ArrayList<>(Arrays.asList("spawn", "time", "countdown"));
					completions = getApplicableTabCompletes(args[3], completions);
				}
				case ("game6") -> {
					completions = new ArrayList<>(Arrays.asList("spawn", "glass", "goal", "barrier"));
					completions = getApplicableTabCompletes(args[3], completions);
				}
				case ("game7") -> {
					completions = new ArrayList<>(List.of("spawn", "time", "countdown", "killBlock"));
					completions = getApplicableTabCompletes(args[3], completions);
				}
			}
		}
		Collections.sort(completions);
		return completions;
	}

	public ArrayList<String> getApplicableTabCompletes(String arg, ArrayList<String> completions) {
		if (arg == null || arg.equalsIgnoreCase("")) {
			return completions;
		}
		ArrayList<String> valid = new ArrayList<>();
		for (String possibly : completions) {
			if (possibly.startsWith(arg)) {
				valid.add(possibly);
			}
		}
		return valid;
	}
}
