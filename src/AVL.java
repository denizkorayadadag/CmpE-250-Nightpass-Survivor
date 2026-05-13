public class AVL {
    Node root;
    int numOfCards= 0;
    int numOfCardsDiscardPile = 0;
    int numOfCardsDiscardPile2 = 0; // Counter for descending discard pile

    /**
     * Since our AVL tree is sorted in ascending Attack, Health, and Insertion id Respectively, With inOrder search, the first card found is the best candidate.
     * @param attack we want card.healthCurrent > attack.
     * @param health we want card.AttackCurrent >= heatlh.
     * @param node the node we start searching
     * @return the card we found.
     */
    public Card searchForPriority1(int attack, int health, Node node){
        if (node == null) return null;
        if (node.maxAttackInSubTree < health) return null;
        if (node.maxHealthInSubTree <= attack) return null;
        Card card = searchForPriority1(attack, health, node.left);
        if (card != null) return card;
        if (node.card.attackCurrent >= health && node.card.healthCurrent > attack) return node.card;
        return searchForPriority1(attack, health, node.right);
    }
    /**
     * Since our AVL tree is sorted in ascending Attack, Health, and Insertion id Respectively, With inOrder search, the first card found is the best candidate.
     * @param strangerAttack we want card.healthCurrent <= attack.
     * @param node the node we start searching
     * @return the card we found.
     */
    public Card searchForPriority3(int strangerAttack, int strangerHealth, Node node) {
        if (node == null) return null;
        if (node.maxAttackInSubTree < strangerHealth) return null;
        Card option1 = searchForPriority3(strangerAttack, strangerHealth, node.left);
        if (option1 != null) return option1;
        if (node.card.healthCurrent <= strangerAttack && node.card.attackCurrent >= strangerHealth) return node.card;
        return searchForPriority3(strangerAttack, strangerHealth, node.right);
    }

    /**
     * Compare logic for Priority2 and Priority4 is identical hence we use 1 compare method in order to make search methods clearer.
     * @param option1
     * @param option2
     * @return better option
     */
    private Card compareForPriorityTwoAndFour(Card option1, Card option2) {
        if (option1 == null) return option2;
        if (option2 == null) return option1;
        if (option1.attackCurrent > option2.attackCurrent) {
            return option1;
        } else if (option2.attackCurrent > option1.attackCurrent) {
            return option2;
        }
        if (option1.healthCurrent < option2.healthCurrent) {
            return option1;
        } else if (option2.healthCurrent < option1.healthCurrent) {
            return option2;
        }
        if (option1.insertionID < option2.insertionID) {
            return option1;
        } else {
            return option2;
        }
    }

    /**
     * We search for Survivor but not killer.
     * @param attack
     * @param health
     * @param node
     * @return
     */
    public Card searchForPriority2(int attack, int health, Node node) {
        if (node == null) return null;
        if (node.maxHealthInSubTree <= attack) return null;
        Card current = null;
        if (node.card.healthCurrent > attack && node.card.attackCurrent < health) {
            current = node.card;
        }
        Card better = compareForPriorityTwoAndFour(searchForPriority2(attack, health, node.right), current);
        if (better != null) {
            if (node.left != null && node.left.maxAttackInSubTree >= better.attackCurrent) {
                better = compareForPriorityTwoAndFour(better, searchForPriority2(attack, health, node.left));
            }
        } else {
            if (node.left != null) {
                better = searchForPriority2(attack, health, node.left);
            }
        }
        return better;
    }

    /**
     * Search for the destruction since there are no cards that can survive or kill.
     * @param strangerAttack
     * @param strangerHealth
     * @param node
     * @return best possible card.
     */
    public Card searchForPriority4(int strangerAttack, int strangerHealth, Node node) {
        if (node == null) return null;
        Card better;
        better = compareForPriorityTwoAndFour(searchForPriority4(strangerAttack, strangerHealth, node.right), node.card);
        if (node.left != null && node.left.maxAttackInSubTree >= better.attackCurrent) {
            better = compareForPriorityTwoAndFour(better , searchForPriority4(strangerAttack, strangerHealth, node.left));
        }
        return better;
    }
    /**
     * For Type-2 we search for the one that has largest healthMissing among the DiscardPile
     * @param node the node that we start searching recursively.
     * @param healPool The card should be revived fully so healPool amount is considered in comparisons.
     * @return the card that is revivable and also with the largest health missing.
     */
    public Card findGreatestHmissing(Node node, int healPool) {
        if (node == null) return null;
        if (node.minHmissingSubTree > healPool) return null;
        Card leftResult = findGreatestHmissing(node.left, healPool);
        if (leftResult != null) return leftResult;
        if (node.card.missingHealth <= healPool) return node.card;
        return findGreatestHmissing(node.right, healPool);
    }
    /**
     * For type-2 after full revival, we search for partial revive.
     * @param node the node we start searching recursively
     * @return the card which is the most suitable for the case.
     */
    public Card findSmallestHmissing(Node node) {
        if (node == null) return null;
        if (node.left == null) return node.card;
        return findSmallestHmissing(node.left);
    }

    /**
     * @param card The card object that is going to be inserted to our discardPile (ASCENDING).
     * @param root The root of the discardPile, we start searching the place to insert from the root.
     * @return After insertion, it returns the balanced version of The discardPile in case of any unbalanced situation.
     */
    public Node insertToDiscardPile(Card card, Node root){
        if (root == null) {
            numOfCardsDiscardPile++;
            return new Node(card);
        }
        if (card.missingHealth < root.card.missingHealth) {
            root.left = insertToDiscardPile(card, root.left);
        } else if (card.missingHealth > root.card.missingHealth) {
            root.right = insertToDiscardPile(card, root.right);
        } else {
            root.right = insertToDiscardPile(card, root.right);
        }
        updateNodeProperties(root);
        return balanceNode(root);
    }

    /**
     * Insert to DESCENDING Hmissing discard pile (larger Hmissing goes LEFT)
     * @param card The card object that is going to be inserted to our discardPile2.
     * @param root The root of the discardPile2, we start searching the place to insert from the root.
     * @return After insertion, it returns the balanced version of The discardPile2 in case of any unbalanced situation.
     */
    public Node insertToDiscardPile2(Card card, Node root){
        if (root == null) {
            numOfCardsDiscardPile2++;
            return new Node(card);
        }
        if (card.missingHealth > root.card.missingHealth) {
            root.left = insertToDiscardPile2(card, root.left);
        } else if (card.missingHealth < root.card.missingHealth) {
            root.right = insertToDiscardPile2(card, root.right);
        } else {
            if (card.discardID < root.card.discardID) {
                root.left = insertToDiscardPile2(card, root.left);
            } else {
                root.right = insertToDiscardPile2(card, root.right);
            }
        }
        updateNodeProperties(root);
        return balanceNode(root);
    }

    /**
     * Deletes from the ASCENDING discardPile.
     * @param card the card is wanted to be deleted.
     * @param root the root which we start searching.
     * @return after deletion, it returns the balanced version of the discard pile tree.
     */
    public Node deleteFromDiscardPile(Card card, Node root) {
        if (root == null) return root;
        if (card.missingHealth < root.card.missingHealth) {
            root.left = deleteFromDiscardPile(card, root.left);
        } else if (card.missingHealth > root.card.missingHealth) {
            root.right = deleteFromDiscardPile(card, root.right);
        } else {
            if (card.discardID < root.card.discardID) {
                root.left = deleteFromDiscardPile(card, root.left);
            } else if (card.discardID > root.card.discardID) {
                root.right = deleteFromDiscardPile(card, root.right);
            }
            else {
                if (root.left == null) {
                    numOfCardsDiscardPile--;
                    return root.right;
                }
                if (root.right == null) {
                    numOfCardsDiscardPile--;
                    return root.left;
                }
                Node successor = findMin(root.right);
                root.card = successor.card;
                root.right = deleteFromDiscardPile(successor.card, root.right);
            }
        }
        if (root == null) return root;
        updateNodeProperties(root);
        return balanceNode(root);
    }

    /**
     * Delete from DESCENDING Hmissing discard pile (discardPile2)
     * @param card the card is wanted to be deleted.
     * @param root the root which we start searching.
     * @return after deletion, it returns the balanced version of the discard pile tree.
     */
    public Node deleteFromDiscardPile2(Card card, Node root) {
        if (root == null) return root;
        if (card.missingHealth > root.card.missingHealth) {
            root.left = deleteFromDiscardPile2(card, root.left);
        } else if (card.missingHealth < root.card.missingHealth) {
            root.right = deleteFromDiscardPile2(card, root.right);
        } else {
            if (card.discardID < root.card.discardID) {
                root.left = deleteFromDiscardPile2(card, root.left);
            } else if (card.discardID > root.card.discardID) {
                root.right = deleteFromDiscardPile2(card, root.right);
            } else {
                if (root.left == null) {
                    numOfCardsDiscardPile2--;
                    return root.right;
                }
                if (root.right == null) {
                    numOfCardsDiscardPile2--;
                    return root.left;
                }
                Node successor = findMin(root.right);
                root.card = successor.card;
                root.right = deleteFromDiscardPile2(successor.card, root.right);
            }
        }
        if (root == null) return root;
        updateNodeProperties(root);
        return balanceNode(root);
    }
    /**
     * Search algorithm for steal_card command.
     * @param attack
     * @param health
     * @param node
     * @return
     */
    public Card stealTheCard(int attack, int health, Node node) {
        if (node == null) return null;
        if (node.maxAttackInSubTree <= attack) return null;
        if (node.maxHealthInSubTree <= health) return null;
        Card stole = stealTheCard(attack, health, node.left);
        if (stole != null) return stole;
        if (node.card.attackCurrent > attack && node.card.healthCurrent > health) return node.card;
        return stealTheCard(attack, health, node.right);
    }
    /**
     * The code below is for getting Attack, Health and Height properties for our AVL tree in Nodes.
     * Hence, the comparison helper methods for health and attack is written.
     */
    private int getMaxHealth(Node node){
        if(node==null) return Integer.MIN_VALUE;
        return node.maxHealthInSubTree;
    }
    private int getMinHealth(Node node){
        if(node==null) return Integer.MAX_VALUE;
        return node.minHealthInSubTree;
    }
    private int getMaxAttack(Node node){
        if(node==null) return Integer.MIN_VALUE;
        return node.maxAttackInSubTree;
    }
    private int getMinAttack(Node node){
        if(node==null) return Integer.MAX_VALUE;
        return node.minAttackInSubTree;
    }
    private int getMaxHmissing(Node node){
        if(node==null) return Integer.MIN_VALUE;
        return node.maxHmissingSubTree;
    }
    private int getMinHmissing(Node node){
        if(node==null) return Integer.MAX_VALUE;
        return node.minHmissingSubTree;
    }
    /**
     * After any deletion or insertion operation, we call this method to update node's dependent properties.
     * @param node the node that is needed to be updated.
     */
    private void updateNodeProperties(Node node) {
        if (node == null) return;
        node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;
        node.maxHealthInSubTree = Math.max(node.card.healthCurrent, Math.max(getMaxHealth(node.left), getMaxHealth(node.right)));
        node.minHealthInSubTree = Math.max(node.card.healthCurrent, Math.max(getMinHealth(node.left), getMinHealth(node.right)));
        node.maxAttackInSubTree = Math.max(node.card.attackCurrent, Math.max(getMaxAttack(node.left), getMaxAttack(node.right)));
        node.minAttackInSubTree = Math.min(node.card.attackCurrent, Math.min(getMinAttack(node.left), getMinAttack(node.right)));
        node.maxHmissingSubTree = Math.max(node.card.missingHealth, Math.max(getMaxHmissing(node.left), getMaxHmissing(node.right)));
        node.minHmissingSubTree = Math.min(node.card.missingHealth, Math.min(getMinHmissing(node.left), getMinHmissing(node.right)));
    }

    /**
     * A standard method for an AVL implementation whiich calculate the balance of the AVL from that node.
     * @param node The node
     * @return the balance.
     */
    private int getBalance(Node node) {
        if (node == null) return 0;
        return getHeight(node.left) - getHeight(node.right);
    }

    /**
     * After draw_card command from the input file, we insert the card with ascending Attack, if equal Health, if equal to the right.
     * @param card The card object that is going to be inserted to our AVL.
     * @param root The root of the AVL, we start searching the place to insert from the root.
     * @return After insertion, it returns the balanced version of The AVL in case of any unbalanced situation.
     */
    public Node insert(Card card, Node root){
        if (root == null) {
            numOfCards++;
            return new Node(card);
        }
        if (card.attackCurrent < root.card.attackCurrent) root.left = insert(card, root.left);
        else if (card.attackCurrent > root.card.attackCurrent) root.right = insert(card, root.right);
        else {
            if (card.healthCurrent < root.card.healthCurrent) root.left = insert(card, root.left);
            else if (card.healthCurrent > root.card.healthCurrent) root.right = insert(card, root.right);
            else root.right = insert(card, root.right);
        }
        updateNodeProperties(root);
        return balanceNode(root);
    }

    /**
     * Used in delete methods, it finds the minimum of the Nodes in terms of Attack.
     * @param node
     * @return the minimum node.
     */
    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /**
     * Deletes the card from the AVL TREE where each node has its card. And by comparisons, we want to find the card to delete.
     * @param card The card object that is wanted to discarded from the tree.
     * @param root The root of the AVL TREE, we start searching from there.
     * @return the balanced version of the found root. After deletion, we might have an unbalanced tree, we check and balance it if necessary.
     */
    public Node delete(Card card, Node root){
        if (root == null) return root;
        if (card.attackCurrent<root.card.attackCurrent) root.left = delete(card, root.left);
        else if (card.attackCurrent>root.card.attackCurrent) root.right = delete(card, root.right);
        else {
            if (card.healthCurrent < root.card.healthCurrent) root.left = delete(card,root.left);
            else if (card.healthCurrent > root.card.healthCurrent) root.right = delete(card,root.right);
            else{
                if (card.insertionID < root.card.insertionID) root.left = delete(card,root.left);
                else if (card.insertionID > root.card.insertionID) root.right = delete(card,root.right);
                else {
                    if (root.left == null) {
                        numOfCards--;
                        return root.right;}
                    if (root.right == null){
                        numOfCards--;
                        return root.left;}
                    Node successor = findMin(root.right);
                    root.card = successor.card;
                    root.right = delete(successor.card, root.right);
                }
            }
        }
        updateNodeProperties(root);
        return balanceNode(root);
    }
    /**
     *
     * @param node
     * @return the height of the node.
     */
    private int getHeight(Node node){
        if(node==null) return 0;
        return node.height;
    }
    /**
     * Applying right rotation to balance the tree.
     * @param node
     * @return
     */
    private Node rightRotation(Node node){
        Node rootNew = node.left;
        node.left = rootNew.right;
        rootNew.right = node;
        updateNodeProperties(node);
        updateNodeProperties(rootNew);
        return rootNew;
    }
    /**
     * Applying left rotation to balance the tree.
     * @param node unbalanced node is the parameter.
     * @return balanced node is returned.
     */
    private Node leftRotation(Node node){
        Node rootNew = node.right;
        node.right = rootNew.left;
        rootNew.left = node;
        updateNodeProperties(node);
        updateNodeProperties(rootNew);
        return rootNew;
    }
    /**
     * Applying Left-Right rotation to balance the tree.
     * @param node unbalanced node is the parameter.
     * @return balanced node is returne
     */
    private Node leftRightRotation(Node node) {
        Node child = node.left;
        Node rootNewFromFirstRotation = child.right;
        child.right = rootNewFromFirstRotation.left;
        rootNewFromFirstRotation.left = child;
        updateNodeProperties(child);
        updateNodeProperties(rootNewFromFirstRotation);
        node.left = rootNewFromFirstRotation;
        Node rootNew = node.left;
        node.left = rootNew.right;
        rootNew.right = node;
        updateNodeProperties(node);
        updateNodeProperties(rootNew);
        return rootNew;
    }
    /**
     * Applying Right-Left rotation to balance the tree.
     * @param node unbalanced node is the parameter.
     * @return balanced node is returned
     */
    private Node rightLeftRotation(Node node) {
        Node child = node.right;
        Node rootNewFromFirstRotation = child.left;
        child.left = rootNewFromFirstRotation.right;
        rootNewFromFirstRotation.right = child;
        updateNodeProperties(child);
        updateNodeProperties(rootNewFromFirstRotation);
        node.right = rootNewFromFirstRotation;
        Node rootNew = node.right;
        node.right = rootNew.left;
        rootNew.left = node;
        updateNodeProperties(node);
        updateNodeProperties(rootNew);
        return rootNew;
    }
    /**
     * Written for balancing nodes after any insertion or deletion to the AVL tree.
     * @param node unbalanced node is the parameter.
     * @return balanced node is returne
     */
    private Node balanceNode(Node node) {
        int balance = getBalance(node);
        if (balance > 1) {
            if (getBalance(node.left) >= 0) {
                return rightRotation(node);
            } else {
                return leftRightRotation(node);
            }
        }
        if (balance < -1) {
            if (getBalance(node.right) <= 0) {
                return leftRotation(node);
            } else {
                return rightLeftRotation(node);
            }
        }
        return node;
    }
}