package org.loop.example

import org.loop.example.components.Counter
import org.loop.example.components.CounterList
import org.loop.example.components.CounterPair
import java.util.*

/**
 * Created by pamelactan on 12/16/15.
 */
class App {
    sealed class Action {
        object Id : Action()

        class counter(val action: Counter.Action) : Action()
        class counterPair(val action: CounterPair.Action) : Action()
        class counterList(val action: CounterList.Action): Action()
    }

    data class Model(val counter: Counter.Model = Counter.Model(),
                     val counterPair: CounterPair.Model = CounterPair.Model(),
                     val counterList: CounterList.Model = CounterList.Model())

    companion object {
        fun update(a: Action, m: Model): Model {
            return when (a) {
                is Action.Id -> m
                is Action.counter -> m.copy( counter = Counter.update(a.action, m.counter) )
                is Action.counterPair -> m.copy( counterPair = CounterPair.update(a.action, m.counterPair) )
                is Action.counterList -> m.copy( counterList = CounterList.update(a.action, m.counterList) )
            }
        }
    }
}