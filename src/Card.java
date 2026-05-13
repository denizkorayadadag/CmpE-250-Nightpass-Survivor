/**
 * Card class is an important class that stores card's properties such as attack, health, insertion, discard ID etc.
 */
public class Card {
    String name;
    int healthCurrent;
    int attackCurrent;
    int attackInitial;
    int healthInitial;
    int healthBase;
    int attackBase;
    int missingHealth;
    int revivalProgress;
    int discardID;
    int insertionID;
    public Card(String cardName, int Ainitial, int Hinitial) {
        this.name = cardName;
        this.attackInitial = Ainitial;
        this.healthInitial = Hinitial;
        this.attackCurrent = Ainitial; // Initial current = initial
        this.healthCurrent = Hinitial; // Initial current = initial
        this.attackBase = Ainitial;    // Initial base = initial
        this.healthBase = Hinitial;    // Initial base = initial
        this.insertionID = -1; // Will be set upon insertion
    }

    /**
     * Card takes damage, and its health and attack is updated accordingly.
     * @param attack
     */
    public void cardIsAttacked(int attack){
        this.healthCurrent -= attack;
        this.attackCurrent = Math.max(1, (int) Math.floor((double) this.attackBase * this.healthCurrent / this.healthBase));
    }

    /**
     * Card revival process is handled, its properties are reset.
     */
    public void reviveCard(){
        this.healthCurrent = this.healthBase;
        this.missingHealth = 0;
        this.attackBase = Math.max(1, (int) Math.floor(this.attackBase * 0.90));
        this.revivalProgress = this.healthBase;
        this.attackCurrent = this.attackBase;
    }

    /**
     * Partial revival process is applied.
     * @param heal points that are going to be added to the revival progress of the card.
     */
    public void applyPartialRevival(int heal){
        this.revivalProgress = this.revivalProgress + heal;
        this.missingHealth = this.healthBase - this.revivalProgress;
        this.attackBase = Math.max(1, (int) Math.floor(this.attackBase * 0.95));
        this.attackCurrent = this.attackBase;
    }

    /**
     * The method that kills the card.
     * @param card
     */
    public void cardIsKilled(Card card){
        this.healthCurrent = 0;
        this.revivalProgress = 0;
        this.missingHealth = card.healthBase;
}
}