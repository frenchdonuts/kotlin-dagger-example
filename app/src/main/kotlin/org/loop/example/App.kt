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
                     val counterListModelAndAction: Pair<CounterList.Model, CounterList.Action> =
                                                    Pair(CounterList.Model(ArrayList()), CounterList.Action.Id))

    companion object {
        fun update(a: Action, m: Model): Model {
            return when (a) {
                is Action.Id -> m
                is Action.counter -> m.copy( counter = Counter.update(a.action, m.counter) )
                is Action.counterPair -> m.copy( counterPair = CounterPair.update(a.action, m.counterPair) )
                is Action.counterList -> updateCounterList(a, m)
            }
        }

        private fun updateCounterList(a: Action.counterList, m: Model): Model {
            var p = Pair(
                    CounterList.update(a.action, m.counterListModelAndAction.first),
                    a.action
            )
            return m.copy(counterListModelAndAction = p)
        }
    }
}