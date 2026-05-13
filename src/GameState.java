/**
 * GameState class is for tracking game scores and winner of the game correctly.
 */
public class GameState {
    int strangerPoint;
    int survivorPoint;
    public GameState() {
        this.strangerPoint = 0;
        this.survivorPoint = 0;
    }

    /**
     * In each priority, the scores are same, so with the priority parameter, we update the scores.
     * @param priority
     */
    public void updateScores(int priority) {
        if (priority == 1){
            this.survivorPoint = this.survivorPoint + 2;
            this.strangerPoint = this.strangerPoint + 1;
        }
        else if (priority == 2){
            this.survivorPoint = this.survivorPoint + 1;
            this.strangerPoint = this.strangerPoint + 1;
        }
        else if (priority == 3){
            this.survivorPoint = this.survivorPoint + 2;
            this.strangerPoint = this.strangerPoint + 2;
        }
        else if (priority == 4){
            this.survivorPoint = this.survivorPoint + 1;
            this.strangerPoint = this.strangerPoint + 2;
        }
        else this.strangerPoint += 2;
    }

    /**
     * @return the current winner string.
     */
    public String findCurrentWinner(){
        if (survivorPoint >= strangerPoint) return "The Survivor, Score: " + survivorPoint + "\n";
        else return "The Stranger, Score: " + strangerPoint + "\n";
    }
}