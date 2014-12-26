package com.ordonteam.tictactoe

import org.robolectric.Robolectric
import pl.polidea.robospock.RoboSpecification

class MainActivitySpec extends RoboSpecification {

    def "Robo Guice Test"() {
        given:
        def builder = Robolectric.buildActivity(MainActivity)

        when:
        MainActivity mainActivity = builder.create().get()

        then:
        mainActivity.helloText != null
        mainActivity.helloText.text == 'Hello Robo Guice'
    }
}