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
        this.mBaseRef = new Firebase(firebaseURL);
        this.mMessagesRef = this.mBaseRef.child("messages");
        this.mPlayersRef = this.mBaseRef.child("players");
        this.mGameRef = this.mBaseRef.child("game");
        this.mWordsRef = this.mBaseRef.child("words");
        this.mUserId = userId;

        mPlayersRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                GenericTypeIndicator<List<String>> t= new GenericTypeIndicator<List<String>>() {};
                playerList = mutableData.getValue(t);

                if(playerList == null) {
                    playerList = new ArrayList<String>();
                }

                if (!playerList.contains(mUserId)) {
                    playerList.add(mUserId);
                }

                mPlayerId = playerList.indexOf(mUserId);

                mutableData.setValue(playerList);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

        mGameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() == null) {
                    mIsGameRunning = false;
                }
                else {
                    mIsGameRunning = true;
                    mCurrentGameRef = mGameRef.child(dataSnapshot.getKey());
                    mCurrentGame = dataSnapshot.getValue(Game.class);

                    if(mCurrentGame.getWord().equals(mCurrentGame.getWordState()) || mCurrentGame.getLeft() == 0) {
                        Reset();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() == null) {
                    mIsGameRunning = false;
                }
                else {
                    mIsGameRunning = true;
                    mCurrentGameRef = mGameRef.child(dataSnapshot.getKey());
                    mCurrentGame = dataSnapshot.getValue(Game.class);

                    if(mCurrentGame.getWord().equals(mCurrentGame.getWordState()) || mCurrentGame.getLeft() == 0) {
                        Reset();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // No-op
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // No-op
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });

        mWordsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Word>> t = new GenericTypeIndicator<List<Word>>() {};
                mWords = dataSnapshot.getValue(t);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    public void StartGame() {
        if(this.mIsGameRunning) {
            BotSays("Hey, pay attention. We've already started a game.");
            BotSays("The current hint is: " + this.mCurrentGame.getMessage());
        }
        else {
            SetRandomWord();
        }
    }

    public void Guess(String letter) {
        if(this.mCurrentGame.getTurn() != this.mPlayerId){
            BotSays("Whooaaa there " + this.mUserId + ", it's not your turn. Slow your roll.");
        }
        else {
            this.mCurrentGame.setWordState(UpdateBlanksForWord(letter));
            this.mCurrentGame.setUsedLetters(this.mCurrentGame.getUsedLetters() + letter);

            if(this.mCurrentGame.getWord().indexOf(letter) >= 0) {
                BotSays("We have a hit captain!");

                if(this.mCurrentGame.getWord().equals(this.mCurrentGame.getWordState())) {
                    BotSays("Winner winner firebase dinner! You did it " + this.mUserId + "! Answer:" + this.mCurrentGame.getWord());
                }
                else {
                    BotSays("Turns left: " + this.mCurrentGame.getLeft());
                    BotSays("Word state: " + this.mCurrentGame.getWordState());
                }
            }
            else {
                BotSays("We have missed, the meter grows towards death.");
                this.mCurrentGame.setLeft(this.mCurrentGame.getLeft() - 1);

                if(this.mCurrentGame.getLeft() == 0) {
                    BotSays("It's game over mannnnn! Answer was: " + this.mCurrentGame.getWord());
                }
                else {
                    BotSays("Turns left: " + this.mCurrentGame.getLeft());
                    BotSays("Word state: " + this.mCurrentGame.getWordState());

                    mPlayersRef.child(Integer.toString(this.mPlayerId + 1)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int nextTurn = 0;

                            if (dataSnapshot.getValue() != null) {
                                nextTurn = mPlayerId + 1;
                            }

                            mCurrentGame.setTurn(nextTurn);
                            mCurrentGameRef.setValue(mCurrentGame);

                            mPlayersRef.child(Integer.toString(nextTurn)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() != null) {
                                        BotSays("It's your turn " + dataSnapshot.getValue() + "! Guess with /guess {{letter}}");
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    // No-op
                                }
                            });
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            // No-op
                        }
                    });
                }
            }

            this.mCurrentGameRef.setValue(this.mCurrentGame);
        }
    }

    private void SetRandomWord() {
        Random random = new Random();
        int randomNum = random.nextInt(this.mWords.size() - 1);

        Word selectedWord = this.mWords.get(randomNum);
        Game game = new Game();
        game.setWord(selectedWord.getWord());
        game.setMessage(selectedWord.getHint());
        game.setWordState(GetBlanksForWord(selectedWord.getWord()));

        this.mGameRef.push().setValue(game);

        BotSays("New Game started! Word/Phrase: " + GetBlanksForWord(selectedWord.getWord()));
        BotSays("The current hint is: " + selectedWord.getHint());

        mPlayersRef.child("0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String player = dataSnapshot.getValue(String.class);
                BotSays("It's your turn " + player + "! Guess with /guess {{letter}}");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
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
