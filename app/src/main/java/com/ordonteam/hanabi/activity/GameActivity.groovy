package com.ordonteam.hanabi.activity

import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.games.multiplayer.Participant
import com.ordonteam.hanabi.R
import com.ordonteam.hanabi.game.CardsRowListener
import com.ordonteam.hanabi.game.HanabiGame
import com.ordonteam.hanabi.game.PlayerCardsRowListener
import com.ordonteam.hanabi.game.TurnSubmitter
import com.ordonteam.hanabi.view.GameInfoView
import com.ordonteam.hanabi.view.OtherPlayersView
import com.ordonteam.hanabi.view.dialog.RejectedCardsDialog
import com.ordonteam.hanabi.view.row.CardsRow
import com.ordonteam.hanabi.view.row.FullRow
import com.ordonteam.inject.InjectContentView
import com.ordonteam.inject.InjectView
import groovy.transform.CompileStatic

@CompileStatic
@InjectContentView(R.layout.game_layout)
class GameActivity extends AdditionalAbstractActivity implements TurnSubmitter<HanabiGame> {

    @InjectView(R.id.otherPlayersView)
    OtherPlayersView otherPlayersView
    @InjectView(R.id.playerRow1)
    FullRow playerRow1
    @InjectView(R.id.playerRow2)
    FullRow playerRow2
    @InjectView(R.id.playerRow3)
    FullRow playerRow3
    @InjectView(R.id.playerRow4)
    FullRow playerRow4
    @InjectView(R.id.playedCardsView)
    CardsRow playedCardsView
    @InjectView(R.id.gameInfo)
    GameInfoView gameInfoView
    @InjectView(R.id.logs)
    TextView logs
    @InjectView(R.id.playerRow)
    FullRow playerRow
    @InjectView(R.id.spinner)
    RelativeLayout spinner

    @Override
    byte[] newGameFor(int numberOfPlayers) {
        return new HanabiGame(numberOfPlayers).persist()
    }

    @Override
    void initGameField(int numberOfPlayers, int selfIndex) {
        otherPlayersView.show(numberOfPlayers - 1)
    }

    @Override
    void onMatchMyNextTurn(byte[] matchData, int numberOfPlayers, int selfIndex) {
        HanabiGame game = HanabiGame.unpersist(matchData)
        updateGameView(game, numberOfPlayers, selfIndex, match.participants)
        otherPlayersView.removeActive()
        otherPlayersView.setOnCardClickListener(new CardsRowListener(game, this, selfIndex), selfIndex, numberOfPlayers)
        playerRow.cardsRow.setOnCardClickListener(new PlayerCardsRowListener(game, this))
        dismissSpinner()
    }

    @Override
    void onMatchOtherNextTurn(byte[] matchData, int numberOfPlayers, int selfIndex, int current) {
        updateGameView(HanabiGame.unpersist(matchData), numberOfPlayers, selfIndex, match.participants)
        otherPlayersView.removeActive()
        otherPlayersView.setActive((numberOfPlayers + current - selfIndex - 1) % numberOfPlayers)
        showSpinner()
    }

    @Override
    void onMatchStatusComplete(byte[] matchData) {
        HanabiGame hanabi = HanabiGame.unpersist(matchData)
        dismissSpinner()
        new AchievementsAndLeaderboards(this).update(hanabi)
        Toast.makeText(this, "Match is finished", Toast.LENGTH_LONG).show()
    }

    @Override
    void onMatchStatusCanceled(byte[] data) {
        dismissSpinner()
        super.onBackPressed()
    }

    private void updateGameView(HanabiGame hanabi, int numberOfPlayers, int selfIndex, ArrayList<Participant> participants) {
        otherPlayersView.setPlayersInfo(numberOfPlayers,selfIndex,participants)
        playerRow.playerView.setPlayerInfo(participants[selfIndex])
        hanabi.updateCards(allCardsRows(), selfIndex)
        hanabi.updatePlayedCards(playedCardsView)
        hanabi.updateGameInfo(gameInfoView)
        hanabi.updateLogs(logs, participants, selfIndex)
        gameInfoView.setRejectedCardsOnClickListener({
            new RejectedCardsDialog(this,hanabi.rejectedCards).show();
        })
    }

    @Override
    void onBackPressed() {
        leaveMatch()
        dismissSpinner()
        super.onBackPressed()
    }

    @Override
    void submitTurn(HanabiGame hanabi) { //Extract Interface
        otherPlayersView.removeOnCardClickListeners()
        playerRow.cardsRow.removeOnCardClickListener()
        if (hanabi.isGameFinished()) {
            finishMatch(hanabi.persist())
        } else {
            takeTurn(hanabi.persist())
            Log.e('taking a turn', 'taking a turn')
            showSpinner()
        }
    }

    public void showSpinner() {
        spinner.setVisibility(RelativeLayout.VISIBLE)
    }

    public void dismissSpinner() {
        spinner.setVisibility(RelativeLayout.GONE)
    }

    private List<CardsRow> allCardsRows() {
        return [playerRow.cardsRow] + [playerRow1, playerRow2, playerRow3, playerRow4]*.cardsRow
    }

}
