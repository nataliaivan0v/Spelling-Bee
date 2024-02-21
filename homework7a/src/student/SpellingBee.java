package student;

import edu.willamette.cs1.spellingbee.SpellingBeeGraphics;
import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SpellingBee {
    private static final String ENGLISH_DICTIONARY = "EnglishWords.txt";
    private static final int NUM_HIVES = 7;
    private static final int MIN_WORD_LENGTH = 4;

    private SpellingBeeGraphics sbg;
    private int numWords = 0;
    private int points = 0;
    private List<String> wordList;
    private ArrayList<String> foundWords = new ArrayList<>();

    public void run() {
        sbg = new SpellingBeeGraphics();
        initializeWordList();
        sbg.addField("Puzzle", (s) -> puzzleAction(s));
        sbg.addField("Word", (s) -> wordIsValid(s));
        sbg.addButton("Solve", (s) -> solveAction());
    }

    private void puzzleAction(String s) {
        if (s.length() == NUM_HIVES && onlyEnglishLetters(s) && allUniqueChars(s)) {
            sbg.setBeehiveLetters(s);
        } else if (s.length() != NUM_HIVES) {
            sbg.showMessage("That puzzle word is not seven letters wrong.", Color.RED);
        } else if (!onlyEnglishLetters(s)) {
            sbg.showMessage("That puzzle word contains nonletters.", Color.RED);
        } else if (!allUniqueChars(s)) {
            sbg.showMessage("That puzzle word contains duplicated letters.", Color.RED);
        }
    }

    private void wordIsValid(String s) {
        if (isInDictionary(s) && containsOnlyPuzzleLetters(s) && s.length() >= MIN_WORD_LENGTH
                && containsCenterLetter(s) && wordWasNotFound(s)) {
            numWords++;
            if (s.length() == MIN_WORD_LENGTH) {
                sbg.addWord(s + " (1)", Color.BLACK);
                points += 1;
                foundWords.add(s);
            } else if (isPangram(s)) {
                int pointVal = s.length() + NUM_HIVES;
                sbg.addWord(s + " (" + pointVal + ")", Color.BLUE);
                points += pointVal;
                foundWords.add(s);
            } else {
                sbg.addWord(s + " (" + s.length() + ")", Color.BLACK);
                points += s.length();
                foundWords.add(s);
            }
            sbg.showMessage(numWords + " words; " + points + " points");
        } else if (!isInDictionary(s)) {
            sbg.showMessage("That word is not in the English dictionary", Color.RED);
        } else if (!containsOnlyPuzzleLetters(s)) {
            sbg.showMessage("That word contains letters not in the beehive.", Color.RED);
        } else if (s.length() < MIN_WORD_LENGTH) {
            sbg.showMessage("That word is not four or more letters wrong.", Color.RED);
        } else if (!containsCenterLetter(s)) {
            sbg.showMessage("That word does not include the center letter.", Color.RED);
        } else if (!wordWasNotFound(s)) {
            sbg.showMessage("That word has already been found.", Color.RED);
        }
    }

    private void solveAction() {
        int numWords = 0;
        int points = 0;
        for (String line : wordList) {
            if (line.length() >= MIN_WORD_LENGTH && containsOnlyPuzzleLetters(line) && containsCenterLetter(line)
                    && wordWasNotFound(line)) {
                numWords++;
                if (line.length() == MIN_WORD_LENGTH) {
                    sbg.addWord(line + " (1)", Color.BLACK);
                    points += 1;
                } else if (isPangram(line)) {
                    int pointVal = line.length() + NUM_HIVES;
                    sbg.addWord(line + " (" + pointVal + ")", Color.BLUE);
                    points += pointVal;
                } else {
                    sbg.addWord(line + " (" + line.length() + ")", Color.BLACK);
                    points += line.length();
                }
            }
        }
        numWords += this.numWords;
        points += this.points;
        sbg.showMessage(numWords + " words; " + points + " points", Color.BLACK);
    }

    private void initializeWordList() {
        try {
            wordList = Files.readAllLines(Paths.get("res/" + ENGLISH_DICTIONARY));
        } catch (IOException e) {
            System.err.println("Unable to load EnglishWords.txt");
        }

    }

    private boolean wordWasNotFound(String s) {
        for (String word : foundWords) {
            if (word != null) {
                if (word.equalsIgnoreCase(s)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isInDictionary(String s) {
        for (String line : wordList) {
            if (s.equalsIgnoreCase(line)) {
                return true;
            }
        }
        return false;
    }

    private boolean onlyEnglishLetters(String s) {
        for (int i = 0; i < s.length(); i++) {
            Character c = s.charAt(i);
            if (!isEnglishLetter(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean isEnglishLetter(Character c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    private boolean allUniqueChars(String s) {
        for (int i = 0; i < (s.length() - 1); i++) {
            for (int j = i + 1; j < s.length(); j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isPangram(String s) {
        String puzzle = sbg.getBeehiveLetters().toLowerCase();
        int count = 0;
        for (int i = 0; i < puzzle.length(); i++) {
            if (s.contains(String.valueOf(puzzle.charAt(i)))) {
                count++;
            }
        }
        return count == NUM_HIVES;
    }

    private boolean containsCenterLetter(String s) {
        char c = Character.toLowerCase(sbg.getBeehiveLetters().charAt(0));
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (c == Character.toLowerCase(s.charAt(i))) {
                count++;
            }
        }
        return count > 0;
    }

    private boolean containsOnlyPuzzleLetters(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (isPuzzleLetter(s.charAt(i))) {
                count++;
            }
        }
        return count == s.length();
    }

    private boolean isPuzzleLetter(Character c) {
        String puzzle = sbg.getBeehiveLetters().toLowerCase();
        return puzzle.contains(String.valueOf(c).toLowerCase());
    }

    public static void main(String[] args) {
        new SpellingBee().run();
    }
}
