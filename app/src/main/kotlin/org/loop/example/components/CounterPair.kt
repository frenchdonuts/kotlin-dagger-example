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
               val modelO: Observable<Model>,
               val actionsS: PublishSubject<Action>) : LinearLayout(context) {
        val TAG = View::class.java.simpleName

        private fun init() = AnkoContext.createDelegate(this).apply {
            counterView(
                    modelO.map { it.topCounter },
                    actionsS.contramap({ a -> Action.Top(a) }),
                    {})
            counterView(
                    modelO.map { it.botCounter },
                    actionsS.contramap({ a -> Action.Bot(a) }),
                    {})
        }

        init {
            init()
        }
    }
}
public inline fun ViewManager.counterPairView(modelO: Observable<CounterPair.Model>,
                                              actionsS: PublishSubject<CounterPair.Action>,
                                              init: CounterPair.View.() -> Unit): CounterPair.View {
    return ankoView({ CounterPair.View(it, modelO, actionsS) }, init)
}
