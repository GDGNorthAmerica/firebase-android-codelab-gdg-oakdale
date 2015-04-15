package io.github.gdgoakdale.hangmanonfire;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FireHangman {
    private static final String FIREBOT_NAME = "** FIREBOT **";

    private Firebase mBaseRef;
    private Firebase mMessagesRef;
    private Firebase mPlayersRef;
    private Firebase mGameRef;
    private Firebase mCurrentGameRef;
    private Firebase mWordsRef;

    private String mUserId;
    private int mPlayerId;
    private boolean mIsGameRunning;
    private Game mCurrentGame;
    //private String mSelectedWord;
    private String mSelectedHint;
    private String mUsedLetters;
    private List<String> playerList;
    private List<Word> mWords;

    public FireHangman (String firebaseURL, String userId) {

    }

    public void StartGame() {

    }

    public void Guess(String letter) {

    }

    private void SetRandomWord() {
        Random random = new Random();
        int randomNum = random.nextInt(this.mWords.size() - 1);
    }

    private void BotSays(String message) {
        Chat chat = new Chat(this.FIREBOT_NAME, message, this.FIREBOT_NAME);
        this.mMessagesRef.push().setValue(chat);
    }

    private void Reset() {
        this.mGameRef.removeValue();
        this.mIsGameRunning = false;
    }

    private String GetBlanksForWord(String selectedWord) {
        String output = "";
        int length = selectedWord.length();

        for (int i = 0; i < length; i++ ) {
            if(selectedWord.charAt(i) == ' ') {
                output += " ";
            }
            else {
                output += "_";
            }
        }

        return  output;
    }

    private String UpdateBlanksForWord(String letter) {
        String wordState = this.mCurrentGame.getWordState();
        String currentWord = this.mCurrentGame.getWord();
        int index = 0;
        index = currentWord.indexOf(letter);
        while (index >= 0) {
            wordState = ReplaceAt(wordState, index, letter);
            index = currentWord.indexOf(letter, index + 1);
        }

        return wordState;
    }

    private  String ReplaceAt (String string, int index, String character) {
        return string.substring(0, index) + character + string.substring(index + character.length());
    }
}
