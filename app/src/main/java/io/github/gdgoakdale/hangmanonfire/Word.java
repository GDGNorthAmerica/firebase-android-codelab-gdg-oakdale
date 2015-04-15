package io.github.gdgoakdale.hangmanonfire;

public class Word {
    private String hint;
    private String word;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    public  Word() {
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
