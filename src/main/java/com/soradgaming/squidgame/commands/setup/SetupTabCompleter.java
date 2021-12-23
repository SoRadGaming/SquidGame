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
			List<Arena> arenas = Arena.getArenas().stream().toList();
			ArrayList<String> arenaArrayList = new ArrayList<>();
			for (Arena arena:arenas) {
				arenaArrayList.add(arena.toString());
			}
			completions = arenaArrayList;
			completions = getApplicableTabCompletes(args[0], completions);
		} else if (args.length == 2) {
			completions = new ArrayList<>(Arrays.asList("game1", "game2", "game3", "game4", "game5", "game6", "game7"));
			completions = getApplicableTabCompletes(args[1], completions);
		} else if (args.length == 3) {
			switch (args[1]) {
				case ("game1") -> {
					completions = new ArrayList<>(Arrays.asList("spawn", "barrier", "killzone", "goal"));
					completions = getApplicableTabCompletes(args[2], completions);
				}
				case ("game2") -> {
					completions = new ArrayList<>(Arrays.asList("spawn_red", "spawn_blue", "spawn_green", "spawn_yellow",
							"BuildZone1", "BuildZone2", "BuildZone3", "BuildZone4","DisplayZone1", "DisplayZone2", "DisplayZone3", "DisplayZone4"));
					completions = getApplicableTabCompletes(args[2], completions);
				}
				case ("game3"), ("game7") -> {
					completions = new ArrayList<>(List.of("spawn"));
					completions = getApplicableTabCompletes(args[2], completions);
				}
				case ("game6") -> {
					completions = new ArrayList<>(Arrays.asList("spawn", "glass", "goal", "barrier"));
					completions = getApplicableTabCompletes(args[2], completions);
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
