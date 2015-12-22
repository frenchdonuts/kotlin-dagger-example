package org.loop.example.components

import android.content.Context
import android.view.ViewManager
import android.widget.LinearLayout
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView
import org.loop.example.contramap
import rx.Observable
import rx.subjects.PublishSubject

/**
 * Created by pamelactan on 12/14/15.
 */
class CounterPair {
    data class Model(val topCounter: Counter.Model = Counter.Model(),
                     val botCounter: Counter.Model = Counter.Model())

    sealed class Action {
        object Reset : Action()
        class Top(val counterAction: Counter.Action) : Action()
        class Bot(val counterAction: Counter.Action) : Action()
    }

    companion object {
        fun update(action: Action, model: Model): Model {
            return when (action) {
                is Action.Reset -> Model()
                is Action.Top -> Model(Counter.update(action.counterAction, model.topCounter), model.botCounter)
                is Action.Bot -> Model(model.topCounter, Counter.Companion.update(action.counterAction, model.botCounter))
            }
        }
    }


    class View(context: Context,
               var actionS: PublishSubject<Action>) : LinearLayout(context) {
        val TAG = View::class.java.simpleName

        lateinit var actionSTop: PublishSubject<Counter.Action>
        lateinit var actionSBot: PublishSubject<Counter.Action>

        lateinit var counterViewTop: Counter.View
        lateinit var counterViewBot: Counter.View
        private fun init() = AnkoContext.createDelegate(this).apply {
            actionSTop = actionS.contramap { Action.Top(it) }
            actionSBot = actionS.contramap { Action.Bot(it) }

            counterViewTop = counterView(actionSTop, {})
            counterViewBot = counterView(actionSBot, {})
        }

        init {
            init()
        }

        public fun render(m: Model) {
            counterViewTop.render(m.topCounter)
            counterViewBot.render(m.botCounter)
        }

        public fun setActionsOutput(actionS: PublishSubject<Action>) {
            actionSTop = actionS.contramap { Action.Top(it) }
            actionSTop = actionS.contramap { Action.Bot(it) }
        }
    }
}
public inline fun ViewManager.counterPairView() = counterPairView(PublishSubject.create(), {})
public inline fun ViewManager.counterPairView(actionS: PublishSubject<CounterPair.Action>,
                                              init: CounterPair.View.() -> Unit): CounterPair.View {
    return ankoView({ CounterPair.View(it, actionS) }, init)
}
