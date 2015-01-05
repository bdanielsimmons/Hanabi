package com.ordonteam.hanabi.game

import com.ordonteam.hanabi.view.CardView
import com.ordonteam.hanabi.view.CardsRow
import groovy.transform.CompileStatic

@CompileStatic
class HanabiPlayer implements Serializable {
    static Map<Integer, IntRange> maxCardsFor = [(2): (1..5), (3): (1..5), (4): (1..4), (5): (1..4)]

    List<HanabiCard> cardsOnHand = []

    HanabiPlayer(HanabiGame game, int playersNumber) {
        cardsOnHand = maxCardsFor[playersNumber].collect {
            game.drawCard()
        }
    }

    void hintColor(int cardIndex) {
        cardsOnHand.each { HanabiCard card ->
            if (card.color == cardsOnHand.get(cardIndex).color)
                card.isColorKnown = true
        }
    }

    void hintNumber(int cardIndex) {
        cardsOnHand.each { HanabiCard card ->
            if (card.value == cardsOnHand.get(cardIndex).value)
                card.isValueKnown = true
        }
    }

    HanabiCard rejectCard(int index, HanabiCard newCard) {
        HanabiCard rejected = cardsOnHand.remove(index)
        cardsOnHand.add(newCard)
        return rejected
    }

    void updateCards(List<CardView> row, boolean self) {
        for (int i = 0; i < cardsOnHand.size(); i++) {
            if (self)
                row[i].setUserCard(cardsOnHand[i])
            else
                row[i].setCard(cardsOnHand[i])
        }
    }
}
