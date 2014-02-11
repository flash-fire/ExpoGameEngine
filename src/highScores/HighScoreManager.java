package highScores;


import java.util.*;
import java.io.*;

public class HighScoreManager {
    public ArrayList<Score> scores;
    private static final String HIGHSCORE_FILE = "resources/scoreData/scores.dat";
    ObjectOutputStream outputStream = null;
    ObjectInputStream inputStream = null;
    final int max = 5;
    
    public HighScoreManager() {
        scores = new ArrayList<Score>();
    }
    public ArrayList<Score> getScores() {
        loadScoreFile();
        sort();
        return scores;
    }
    private void sort() {
        ScoreComparator comparator = new ScoreComparator();
        Collections.sort(scores, comparator);
}
    public void addScore(String name, int score) {
        loadScoreFile();
        scores.add(new Score(name, score));
        updateScoreFile();
}
    
    @SuppressWarnings("unchecked")
	public void loadScoreFile() {
        try {
            inputStream = new ObjectInputStream(new FileInputStream(HIGHSCORE_FILE));
            scores = (ArrayList<Score>) inputStream.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("[Laad] FNF Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[Laad] IO Error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("[Laad] CNF Error: " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                System.out.println("[Laad] IO Error: " + e.getMessage());
            }
        }
}
    public void updateScoreFile() {
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(HIGHSCORE_FILE));
            outputStream.writeObject(scores);
        } catch (FileNotFoundException e) {
            System.out.println("[Update] FNF Error: " + e.getMessage() + ",the program will try and make a new file");
        } catch (IOException e) {
            System.out.println("[Update] IO Error: " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                System.out.println("[Update] Error: " + e.getMessage());
            }
        }
}
    public String getHighscoreString() {
        String highscoreString = "";
        ArrayList<Score> scores;
        scores = getScores();

        int i = 0;
        int x = scores.size();
        if (x > max) {
            x = max;
        }
        while (i < x) {
            highscoreString += (i + 1) + ".\t" + scores.get(i).getName() + "\t\t" + scores.get(i).getScore() + "\n";
            i++;
        }
        return highscoreString;
}
    /** 
     * checks int <score> to see if it is within the top <numScores> of the high scores file.
     * @param score - player score
     * @param numScores -  number of scores to check
     * @return boolean
     */
    public boolean isHighScore(int score, int numScores){
    	boolean top5 = false; // player in top 5
        ArrayList<Score> checker; // scores logged in sorted order
        checker = getScores(); 
    	int i = 0; // counter
        int high = numScores - 1; // max scores to check 0-4 = 5 scores
        if(checker.size() < numScores){
        	high = checker.size();
        }
        while (i < high) { // haven't checked the max number
            if(checker.get(i).getScore()<score || high == 0){ // if the score is lower, player has high score
            	top5 = true;
            }
            i++;
        }
    	return top5;
    }
    
    public static void main(String[] args) {
        HighScoreManager hm = new HighScoreManager();
        hm.addScore("OPEN	",1);
        hm.addScore("OPEN	",2);
        hm.addScore("OPEN	",3);
        hm.addScore("OPEN	",4);
        hm.addScore("OPEN	",5);

        System.out.print(hm.getHighscoreString());
    }


}