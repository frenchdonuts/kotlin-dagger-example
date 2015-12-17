package org.loop.example

import org.loop.example.components.counter.Counter

/**
 * Created by pamelactan on 12/16/15.
 */
class App {
    sealed class Action {
        object INIT : Action()

        class COUNTER(val action: Counter.Action) : Action()
    }

    data class Model(val counter: Counter.Model = Counter.Model())

    companion object {
        fun update(action: Action, model: Model): Model {
            return when (action) {
                is Action.INIT -> App.Model()
                is Action.COUNTER -> Model(Counter.update(action.action, model.counter))
            }
        }
    }
}