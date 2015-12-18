package org.loop.example

import org.loop.example.components.counter.Counter
import org.loop.example.components.counter_pair.Counter_Pair

/**
 * Created by pamelactan on 12/16/15.
 */
class App {
    sealed class Action {
        object Id : Action()
        object Init : Action() // TODO: Do I need this?

        class Counter(val action: Counter.Action) : Action()
        class Counter_Pair(val action: Counter_Pair.Action) : Action()
    }

    data class Model(val counter: Counter.Model = Counter.Model(),
                     val counterPair: Counter_Pair.Model = Counter_Pair.Model())

    companion object {
        fun update(action: Action, model: Model): Model {
            return when (action) {
                is Action.Id -> model
                is Action.Init -> App.Model() // TODO: Do I need this?
                is Action.Counter -> Model(Counter.update(action.action, model.counter),
                                           model.counterPair)
                is Action.Counter_Pair -> Model(model.counter,
                                                Counter_Pair.update(action.action, model.counterPair))
            }
        }
    }
}