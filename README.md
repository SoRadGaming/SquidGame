# Squid Game
SquidGame plugin for Minecraft

**Commands**:
- /sq
- /sq help
- /sq reload
- /sq join
- /sq leave
- /sq list
- /sq start
- /sq wand
- /sq end
- /sq set lobby
- /sq set game1-7 Variable

**Build Selection for Game 2:**
- BuildPlate needs to be 10x5x10 XYZ (same as display plate)
- first point of schematic must be same as build plate relative to build (not cords but direction)
- schematic should have no offset
- schematic should first and second position should be same relative to build plate
- red = 1 // blue = 2 // green = 3 // yellow = 4

**Glass Selection for Game 6:**
- Tiles need to be 2x2
- gaped 2x2 in both directions
- min platforms 3
- select bottom left of first platform (left platforms)
- select top right of last platform (right platforms)

**Placeholders**:
- `%squidgame_wins%` Return Player Win Count
- `%squidgame_first%`  Get First Position Player
- `%squidgame_second%`  Get Second Position Player
- `%squidgame_third%`  Get Third Position Player
- `%squidgame_fourth%`  Get Fourth Position Player
- `%squidgame_fifth%`  Get Fifth Position Player
- `%squidgame_arena_joined%`  Get Player who most recently Join Game
- `%squidgame_arena_players%`  Get amount of players in Game
- `%squidgame_arena_maxplayers%`  Get Max amount of players
- `%squidgame_arena_leaved%`  Get Player who most recently leave the Game
- `%squidgame_arena_time%`  Get Countdown Time
- `%squidgame_arena_death%`  Get Player who most recently died in Game
- `%squidgame_arena_required%`  Get Min amount of players
- `%squidgame_arena_winner%`  Get Player who most recently won
- `%squidgame_points%`  // Return Player Point Count
- `%squidgame_points1%` // Get First Position Player
- `%squidgame_points2%` // Get Second Position Player
- `%squidgame_points3%` // Get Third Position Player
- `%squidgame_points4%` // Get Fourth Position Player
- `%squidgame_points5%` // Get Fifth Position Player
