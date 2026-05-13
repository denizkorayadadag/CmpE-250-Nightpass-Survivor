public class Node {
    Node left;
    Node right;
    Card card;
    int height;
    //Each node object stores max-min values for its subtree in order to search faster.
    int maxAttackInSubTree;
    int maxHealthInSubTree;
    int minAttackInSubTree;
    int maxHmissingSubTree;
    int minHealthInSubTree;
    int minHmissingSubTree;

    /**
     * Constructor of Node objects.
     * @param card
     */
    public Node(Card card) {
        left = null;
        right = null;
        this.card = card;
        this.height = 1;
        this.maxHealthInSubTree = card.healthCurrent;
        this.maxAttackInSubTree = card.attackCurrent;
        this.minAttackInSubTree = card.attackCurrent;
        this.minHealthInSubTree = card.healthCurrent;
        this.maxHmissingSubTree = card.missingHealth;
        this.minHmissingSubTree = card.missingHealth;
    }
    }
