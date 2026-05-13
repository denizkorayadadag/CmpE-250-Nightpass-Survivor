
/**
 * CMPE 250 Project 1 - Nightpass Survivor Card Game
 *
 * This skeleton provides file I/O infrastructure. Implement your game logic
 * as you wish. There are some import that is suggested to use written below.
 * You can use them freely and create as manys classes as you want. However,
 * you cannot import any other java.util packages with data structures, you
 * need to implement them yourself. For other imports, ask through Moodle before
 * using.
 *
 * TESTING YOUR SOLUTION:
 * ======================
 *
 * Use the Python test runner for automated testing:
 *
 * python test_runner.py              # Test all cases
 * python test_runner.py --type type1 # Test only type1
 * python test_runner.py --type type2 # Test only type2
 * python test_runner.py --verbose    # Show detailed diffs
 * python test_runner.py --benchmark  # Performance testing (no comparison)
 *
 * Flags can be combined, e.g.:
 * python test_runner.py -bv              # benchmark + verbose
 * python test_runner.py -bv --type type1 # benchmark + verbose + type1
 * python test_runner.py -b --type type2  # benchmark + type2
 *
 * MANUAL TESTING (For Individual Runs):
 * ======================================
 *
 * 1. Compile: cd src/ && javac *.java
 * 2. Run: java Main ../testcase_inputs/test.txt ../output/test.txt
 * 3. Compare output with expected results
 *
 * PROJECT STRUCTURE:
 * ==================
 *
 * project_root/
 * ├── src/                     # Your Java files (Main.java, etc.)
 * ├── testcase_inputs/         # Input test files
 * ├── testcase_outputs/        # Expected output files
 * ├── output/                  # Generated outputs (auto-created)
 * └── test_runner.py           # Automated test runner
 *
 * REQUIREMENTS:
 * =============
 * - Java SDK 8+ (javac, java commands)
 * - Python 3.6+ (for test runner)
 *
 * @author Deniz Koray Adadag
 */

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.math.*;

public class Main  {
    // We create static object that all can be referenced from every class.
    static AVL deck = new AVL();
    static AVL discardPile = new AVL();  // Ascending Hmissing (for smallest Hmissing - partial revival)
    static AVL discardPile2 = new AVL(); // Descending Hmissing (for greatest Hmissing - full revival)
    static GameState game = new GameState();
    static int numOfInsert = 0;
    static int numOfDiscard = 0;
    /**
     * This method calls the stealTheCard method in the AVL class.
     * @param attack min attack
     * @param health min health.
     * @return The string that is either the card that is stolen or No card to steal.
     */
    public static String steal_card(int attack, int health){
        Card cardToBeStolen = deck.stealTheCard(attack,health,deck.root);
        if (cardToBeStolen == null)
            return "No card to steal\n";
        else {
            deck.root = deck.delete(cardToBeStolen, deck.root);
            return "The Stranger stole the card: " + cardToBeStolen.name + "\n";
        }
    }

    /**
     * the main function that handles the battle.
     * @param attack
     * @param health
     * @param heal
     * @return
     */
    public static String battle(int attack, int health, int heal) {
        Card card;
        int priority = 0;
        int numOfCardsRevived = 0;
        boolean typeTwo = false;
        if (heal != 0) typeTwo = true; //Heal = 0 for all type-1 inputs, otherwise it is type-2.
        card = deck.searchForPriority1(attack, health, deck.root); //We first search for priority 1.
        /*
        If we find the priority 1 card, we do several operations:
        1-We delete it from the AVL tree(deck) to treat the card as a new card.
        2-Update Survivor's, Stranger's scores; card's properties.
        3-We check whether we are in type-2 or not. And then apply revival process.
        4-Return the string.
         */
        if (card != null) { // When found, we set the priority.
            priority = 1;
            deck.root = deck.delete(card, deck.root);
            card.cardIsAttacked(attack);
            card.insertionID = numOfInsert++;
            game.updateScores(priority);
            deck.root = deck.insert(card, deck.root);
            if (typeTwo) numOfCardsRevived =reviveCards(heal);
            return "Found with priority " +priority + ", Survivor plays " + card.name + ", the played card returned to deck, " + numOfCardsRevived + " cards revived\n";
        }
        card = deck.searchForPriority2(attack, health, deck.root);
        //Similar operations that are explained in Priority 1.
        if (card != null) {
            priority = 2;
            deck.root = deck.delete(card, deck.root);
            game.updateScores(priority);
            card.cardIsAttacked(attack);
            card.insertionID = numOfInsert++;
            deck.root = deck.insert(card, deck.root);
            if (typeTwo) numOfCardsRevived =reviveCards(heal);
            return "Found with priority "+ priority +", Survivor plays " + card.name + ", the played card returned to deck, " + numOfCardsRevived + " cards revived\n";
        }
        // Again similar operations to priority 1, except the card's death and death operations.
        card = deck.searchForPriority3(attack, health, deck.root);
        if (card != null) {
            priority = 3;
            deck.root = deck.delete(card, deck.root);
            card.cardIsKilled(card);
            game.updateScores(priority);
            card.discardID = numOfDiscard++;
            discardPile.root = discardPile.insertToDiscardPile(card, discardPile.root);
            discardPile2.root = discardPile2.insertToDiscardPile2(card, discardPile2.root);
            if (typeTwo) numOfCardsRevived =reviveCards(heal);
            return "Found with priority " + priority + ", Survivor plays " +card.name + ", the played card is discarded, " + numOfCardsRevived + " cards revived\n";
        }
        card = deck.searchForPriority4(attack, health, deck.root);
        // Very similar operations to priority 3.
        if (card != null) {
            priority = 4;
            deck.root = deck.delete(card, deck.root);
            game.updateScores(priority);
            card.cardIsKilled(card);
            card.discardID = numOfDiscard++;
            discardPile.root = discardPile.insertToDiscardPile(card, discardPile.root);
            discardPile2.root = discardPile2.insertToDiscardPile2(card, discardPile2.root);
            if (typeTwo) numOfCardsRevived =reviveCards(heal);
            return "Found with priority " + priority + ", Survivor plays " + card.name + ", the played card is discarded, " + numOfCardsRevived + " cards revived\n";
        }
        // If we have not returned any value up to now. We could not find any card in first 4 priority. Hence we return no card to play.
        game.updateScores(priority);
        if (typeTwo) numOfCardsRevived =reviveCards(heal);
        return "No card to play, " + numOfCardsRevived + " cards revived\n";
    }

    /**
     * We search for largest Revivable card, until we cannot find. Then we look for partial revival.
     * @param healPool the amount we can use for revival.
     * @return the number of cards revived totally.
     */
    public static int reviveCards(int healPool) {
        int revivedNumOfCards = 0;
        while (true) {
            Card card = discardPile2.findGreatestHmissing(discardPile2.root, healPool);
            if (card == null) break;
            else {
                healPool -= card.missingHealth;
                reviveTheCard(card);
                revivedNumOfCards++;
            }
        }
        if (healPool > 0) partiallyRevive(healPool);
        return revivedNumOfCards;
    }

    /**
     * Apply revival process the found card.
     * @param card object revived.
     */
    private static void reviveTheCard(Card card) {
        discardPile.root = discardPile.deleteFromDiscardPile(card, discardPile.root);
        discardPile2.root = discardPile2.deleteFromDiscardPile2(card, discardPile2.root);
        card.reviveCard();
        card.insertionID = numOfInsert++;
        deck.root = deck.insert(card, deck.root);
    }

    /**
     * Apply partial revival process.
     * @param heal points that are used in partial revival.
     */
    private static void partiallyRevive(int heal) {
        Card cardPartialRevive = discardPile.findSmallestHmissing(discardPile.root);
        if (cardPartialRevive == null) return;
        discardPile.root = discardPile.deleteFromDiscardPile(cardPartialRevive, discardPile.root);
        discardPile2.root = discardPile2.deleteFromDiscardPile2(cardPartialRevive, discardPile2.root);
        cardPartialRevive.applyPartialRevival(heal);
        cardPartialRevive.discardID = numOfDiscard++;
        discardPile.root = discardPile.insertToDiscardPile(cardPartialRevive, discardPile.root);
        discardPile2.root = discardPile2.insertToDiscardPile2(cardPartialRevive, discardPile2.root);
    }


    /**
     * Draw_card command calls this.
     * @param name
     * @param attack
     * @param health
     * @return string of drawn card.
     */
    public static String draw_card(String name,int attack, int health){
        Card card = new Card(name,attack,health);
        card.insertionID = numOfInsert++;
        deck.root = deck.insert(card,deck.root);
        return "Added " + card.name + " to the deck\n";
    }
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Main <input_file> <output_file>");
            System.out.println("Example: java Main ../testcase_inputs/test.txt ../output/test.txt");
            return;
        }

        String inFile = args[0];
        String outFile = args[1];

        Scanner reader = null;
        try {
            reader = new Scanner(new File(inFile));
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found: " + inFile);
            e.printStackTrace();
            return;
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(outFile);
        } catch (IOException e) {
            System.out.println("Writing error: " + outFile);
            e.printStackTrace();
            if (reader != null)
                reader.close();
            return;
        }

        try {
            while (reader.hasNext()) {
                String line = reader.nextLine();
                Scanner scanner = new Scanner(line);
                String command = scanner.next();
                String out = "";

                switch (command) {
                    case "draw_card": {
                        String name = "";
                        int att = 0;
                        int hp = 0;
                        if (scanner.hasNext())
                            name = scanner.next();
                        if (scanner.hasNext())
                            att = scanner.nextInt();
                        if (scanner.hasNext())
                            hp = scanner.nextInt();
                        out = draw_card(name, att, hp);
                        break;
                    }
                    case "battle": {
                        int att = 0;
                        int hp = 0;
                        int heal = 0;
                        if (scanner.hasNext())
                            att = scanner.nextInt();
                        if (scanner.hasNext())
                            hp = scanner.nextInt();
                        if (scanner.hasNext())
                            heal = scanner.nextInt();
                        out = battle(att, hp, heal);
                        break;
                    }
                    case "find_winning": {
                        out = game.findCurrentWinner();
                        break;
                    }
                    case "deck_count": {
                        out = "Number of cards in the deck: " + deck.numOfCards + "\n";
                        break;
                    }
                    case "discard_pile_count": {
                        out = "Number of cards in the discard pile: " + discardPile.numOfCardsDiscardPile + "\n";
                        break;
                    }
                    case "steal_card": {
                        int att = 0;
                        int hp = 0;
                        if (scanner.hasNext())
                            att = scanner.nextInt();
                        if (scanner.hasNext())
                            hp = scanner.nextInt();
                        out = steal_card(att, hp);
                        break;
                    }
                    default: {
                        System.out.println("Invalid command: " + command);
                        scanner.close();
                        writer.close();
                        reader.close();
                        return;
                    }
                }

                scanner.close();

                try {
                    writer.write(out);
                } catch (IOException e2) {
                    System.out.println("Writing error");
                    e2.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.out.println("Error processing commands: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            writer.close();
        } catch (IOException e2) {
            System.out.println("Writing error");
            e2.printStackTrace();
        }

        if (reader != null) {
            reader.close();
        }

        System.out.println("end");
        return;
    }
}