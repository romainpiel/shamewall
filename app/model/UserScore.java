package model;

/**
 * Created by romainpiel on 28/09/2014.
 */
public class UserScore {
    
    private User user;
    private int score;

    public UserScore(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public int getScore() {
        return score;
    }

    public int incrementScore() {
        return incrementScore(1);
    }

    public int incrementScore(int by) {
        score += by;
        return score;
    }
}
