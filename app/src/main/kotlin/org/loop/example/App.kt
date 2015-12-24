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

        class IncCounterList(val action: CounterPair.Action) : Action()
        class DecCounterList(val action: CounterPair.Action) : Action()
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
                is Action.counter -> m.copy(
                        counter = Counter.update(a.action, m.counter)
                )
                is Action.counterPair -> m.copy(
                        counterPair = CounterPair.update(a.action, m.counterPair),
                        // TODO: Find a better way to do this...How can Actions triggered in a different branch of the tree affect a view in another branch?
                        // You know, the required depth of this pattern matching also just depends on the type of Actions CounterPair exposes.
                        // For example, if CounterPair had actions like TOP_UP, TOP_DOWN, BOT_UP, and BOT_DOWN, then we would only need 1 level of pattern
                        // matching. Keep this in mind when you start making compound views like CounterPair
                        counterList = when(a.action) {
                            is CounterPair.Action.Top -> when(a.action.counterAction) {
                                is Counter.Action.Up -> CounterList.update(CounterList.Action.Insert(0), m.counterList)
                                is Counter.Action.Down -> m.counterList
                            }
                            is CounterPair.Action.Bot -> when(a.action.counterAction) {
                                is Counter.Action.Up -> m.counterList
                                is Counter.Action.Down -> CounterList.update(CounterList.Action.Remove(m.counterList.counters.size - 1), m.counterList)
                            }
                            else -> m.counterList
                        }
                )
                is Action.counterList -> m.copy(
                        counterList = CounterList.update(a.action, m.counterList)
                )
                is Action.IncCounterList -> m.copy(
                        counterPair = CounterPair.update(a.action, m.counterPair),
                        counterList = CounterList.update(CounterList.Action.Insert(0),
                                                         m.counterList)
                )
                is Action.DecCounterList -> m.copy(
                        counterPair = CounterPair.update(a.action, m.counterPair),
                        counterList = CounterList.update(CounterList.Action.Remove(m.counterList.counters.size - 1),
                                                         m.counterList)
                )
            }
        }
    }
}