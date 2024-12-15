# 2048_Java
Java version of 2048, with custom AI.

# Project Description
2048 is a sliding tile game that was created by Gabriele Cirulli in March 2014. The aim of the game is to combine tiles, starting initially with 2’s and 4’s, until the 2048 tile is reached. Initially it was created as a web application. You can play the original version of the game here:

http://gabrielecirulli.github.io/2048/

To move the tiles around, press the arrow keys (up, down, left and right). When you hit a key, all tiles that can move will move in that direction as far as they can go. When two 2’s collide, they make a 4 (and add 4 points to your score), when two 4’s collide they make an 8 (and add 8 points to the score) and so on. If you reach 2048, you can keep playing, to see how high you can get your score. It is possible to create the 4096 tile, the 8192 tile, and so on. The game ends when the board is full and you have no legal moves that you can play.

A new AI agent has been implemented to play the game of 2048. The goal is to create an AI player for this game that can get as high a score as possible.

# How to run the code

1. Clone the repo
2. Run Controller main, found in src/controller.
3. You should now see a game of 2048. Hit "Play" to see the RandomAI agent play a game. The score is shown in the title bar.
4. To play as my AI, select PaulBurnsAI from the dropdown list in the 2048 window.

Our version of 2048 enforces a time limit of 1 second per move. The way this works is by running a timer while waiting for your agent to post its move: if the time taken is longer than 1 second, the game will ignore the move made, drop a new tile onto the board and then start the timer again.