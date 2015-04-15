package io.github.gdgoakdale.hangmanonfire;

public class Game {
    private int left;
    private String message;
    private int turn;
    private String usedLetters;
    private String word;
    private String wordState;

    public Game() {
        this.turn = 0;
        this.left = 7;
        this.usedLetters = "";
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setUsedLetters(String usedLetters) {
        this.usedLetters = usedLetters;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getLeft() {
        return left;
    }

    public String getMessage() {
        return message;
    }

    public int getTurn() {
        return turn;
    }

    public String getUsedLetters() {
        return usedLetters;
    }

    public String getWord() {
        return word;
    }

    public String getWordState() {
        return wordState;
    }

    public void setWordState(String wordState) {
        this.wordState = wordState;
    }
}
