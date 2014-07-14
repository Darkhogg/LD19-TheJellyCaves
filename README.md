The Jelly Caves
===============

The Jelly Caves is a game originally created for [Ludum Dare 19][ld19]. The game
was created from scratch and entirely by me in less than 48 hours.

  [ld19]: http://www.ludumdare.com/compo/ludum-dare-19/

Your objective is to find treasures in the cave while avoiding and killing an
immense amount of living jelly. Caves are randomly generated and are harder
the deeper they are. They have various levels (potentially infinite) and contain
a number of chests.

The main obstacle of the game are jelly monsters. By digging around the map, you
get rocks that can later be thrown at these monsters to kill them. There's not
much more to the game, though.


### Controls

| Key | Action |
|----:|:-------|
| `←` | Moves the player left
| `→` | Moves the player right
| `↑` | Moves the player up
| `→` | Moves the player down
| `V` | Digs dirt diractly in fron of you
| `C` | Opens a chest directly in fron of you
| `X` | Shoots a rock previously dig


Building/Running the Game
-------------------------

In the [releases](../../releases) page you can find pre-compiled versions of the game
in JAR format. In most systems you should be able to double-click the file to
run it if you have Java installed. If you have problems, the Internet is full of
advice on how to execute a runnable JAR.

If you want to build the game from source, the easiest way is to install Ant and
run the `ant` command inside of the game directory. Running `ant run` will
have the same effect, in addition to immediately run the game.