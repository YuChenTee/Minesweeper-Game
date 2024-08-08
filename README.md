# TimeBOMB Sweep

Welcome to TimeBOMB Sweep! This strategy game challenges you to clear a rectangular board filled with hidden mines. Your objective is to uncover cells without detonating any mines. Each cell reveals a number indicating how many mines are adjacent to it. If you suspect a cell contains a mine, you can flag it to avoid accidental clicks. The game incorporates elements of logic and strategy, enhanced with superpowers to help you tackle challenging situations.

**Features**

**Gameplay**
-Board Clearing: Begin by clicking on any cell, which will always be safe. The revealed number will show how many adjacent cells contain mines.
-Mines and Flags: Flag cells where you believe mines are located. Revealing a mine will result in a game loss, while flagging all mines correctly will result in a win.
-Superpowers:
  -Superpower 1: Flags 2 random mine cells on the board, offering a strategic advantage when you're unsure.
  -Superpower 2: Freezes the game timer for 10 seconds, giving you additional time to strategize your next moves.
  
**Leaderboard**
-High Scores: A leaderboard tracks and displays high scores, allowing you to compare your performance with other players and see who has mastered the game.

**UI**
-Timer: The timer shows the remaining time you have to complete the current level, adding a time-based challenge.
-Mine Counter: Displays the number of mines left to be flagged, helping you keep track of your progress.
-Level Indicator: Shows the current level of the game, which can increase in difficulty as you progress.
-Message Display: Provides essential notifications and guidance throughout the game, such as win/loss messages or hints.

**BGM**
-Background Music: The game features a carefully selected background music track that complements the gameplay experience, enhancing immersion and engagement.

**OOP Design**
The game is structured using Object-Oriented Programming (OOP) principles to ensure modularity, scalability, and ease of maintenance:
-GameBoard: Manages the grid layout and the placement of mines. Handles game logic related to cell states and win/loss conditions.
-Cell: Represents an individual cell on the board. Each cell tracks whether it is revealed, flagged, or contains a mine. It also holds the number of adjacent mines.
-Superpower: Encapsulates the functionality of each superpower, including activation and effects on the game state.
-Timer: Controls the game timer, including starting, stopping, and freezing operations.
-UIComponents: Handles the interaction between the player and the game. Manages graphical elements like buttons, displays, and notifications.

**MVC Architecture**
The game adheres to the Model-View-Controller (MVC) architecture to maintain a clean separation of concerns:
-Model:
  -Manages the core game data, including the game board, cell states, and superpower effects.
  -Handles player scores and updates the leaderboard.
-View:
  -Displays the game board, cell states, and game status updates (e.g., timer, mine counter).
  -Manages the graphical representation of the user interface elements, including buttons, messages, and backgrounds.
-Controller:
  -Processes user inputs (e.g., cell clicks, flagging) and updates the model accordingly. Manages game state transitions and triggers superpower actions.
  -Coordinates interactions between the user interface and the game model, ensuring that user actions are correctly reflected in the view.
