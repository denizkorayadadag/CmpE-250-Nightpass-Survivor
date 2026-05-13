# Nightpass Survivor Card Game

## Overview
A strategic card battle game where two players **The Survivor** and **The Stranger** compete using custom card decks. Cards have attack and health values that change during battles, and players can revive fallen cards using healing mechanics.

## Game Mechanics

### Players
- **The Survivor**: Plays cards strategically from the deck
- **The Stranger**: The opposing force, gains points from card plays

### Card System
Each card has:
- **Name**: Unique identifier
- **Attack**: Damage output (changes based on card health)
- **Health**: Durability (decreases when attacked)
- **Insertion ID**: Order added to deck
- **Discard ID**: Order moved to discard pile

### Battle Process
When a battle occurs, the game searches for cards in 4 priority levels:
1. **Priority 1**: Cards matching exact attack and health (Survivor gets 2 pts, Stranger gets 1 pt)
2. **Priority 2**: Cards meeting minimum thresholds (Both get 1 pt)
3. **Priority 3**: Cards with lower thresholds (Both get 2 pts)
4. **Priority 4**: Any available card (Survivor gets 1 pt, Stranger gets 2 pts)

If no card is found, The Stranger gets 2 points.

### Card Revival
- **Full Revival**: Heals discarded cards to full health (attack reduces by 10%)
- **Partial Revival**: If healing pool remaining after full revivals

### Game Commands
- `draw_card <name> <attack> <health>`: Add card to deck
- `battle <attack> <health> [heal]`: Initiate battle (heal for full/partial revival)
- `steal_card <attack> <health>`: The Stranger steals a matching card
- `deck_count`: Check cards remaining in deck
- `discard_pile_count`: Check discarded cards
- `find_winning`: See current winner and score

## Technical Details

### Data Structures
- **AVL Trees**: Self-balancing binary search trees used for:
  - Deck management (efficient card searches by priority)
  - Discard pile (ascending order for partial revival)
  - Discard pile 2 (descending order for full revival)

### Key Classes
- **Card**: Stores card properties and handles damage/revival logic
- **AVL**: Custom AVL tree implementation with priority-based search methods
- **Node**: Tree node containing card data
- **GameState**: Tracks player scores and determines winner
- **Main**: Command processor and game loop

## Project Structure
```
project_root/
├── src/                     # Java source files
├── testcase_inputs/         # Sample test cases
├── testcase_outputs/        # Expected outputs
├── output/                  # Generated results
└── test_runner.py           # Automated testing
```

## Compilation & Testing
```bash
# Compile
cd src && javac *.java

# Run manually
java Main ../testcase_inputs/test.txt ../output/test.txt

# Run automated tests
python test_runner.py              # All tests
python test_runner.py --type type1 # Specific type
python test_runner.py --verbose    # Detailed output
```

## Learning Objectives
- AVL tree implementation and balancing
- Priority-based search algorithms
- Game state management
- File I/O in Java
- Complex data structure operations
