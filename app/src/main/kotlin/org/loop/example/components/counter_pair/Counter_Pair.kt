package org.loop.example.components.counter_pair

import android.content.Context
import android.widget.LinearLayout
import org.jetbrains.anko.UI
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.loop.example.components.counter.Counter

/**
 * Created by pamelactan on 12/14/15.
 */
class Counter_Pair {
    sealed class Action {
        object Reset : Action()
        class Top(val counterAction: Counter.Action) : Action()
        class Bot(val counterAction: Counter.Action) : Action()
    }

    data class Model(val topCounter: Counter.Model = Counter.Model(),
                     val botCounter: Counter.Model = Counter.Model())

    companion object {
        fun update(action: Action, model: Model): Model {
            return when (action) {
                is Action.Reset -> Model()
                is Action.Top -> Model(Counter.update(action.counterAction, model.topCounter), model.botCounter)
                is Action.Bot -> Model(model.topCounter, Counter.update(action.counterAction, model.botCounter))
            }
        }

        // We want to compose (counter.Action -> counter_pair.Action) with (counter_pair.Action -> Unit)
        fun view(context: Context, model: Model, dispatch: (Action) -> Unit): LinearLayout {
            with(context) {
                return verticalLayout {
                    textView("Heyy")
                    UI {
                        Counter.view(context, model.topCounter, { action -> dispatch(Action.Top(action)) })
                    }
                    UI {
                        Counter.view(context, model.botCounter, { action -> dispatch(Action.Bot(action)) })
                    }
                }
            }
        }
    }
}