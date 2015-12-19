package org.loop.example.components

import android.content.Context
import android.view.ViewManager
import android.widget.LinearLayout
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.UI
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.loop.example.components.Counter
import org.loop.example.components.counterView
import org.loop.example.contramap
import rx.Observable
import rx.subjects.PublishSubject

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
public inline fun ViewManager.counterPairView(modelO: Observable<Counter_Pair.Model>,
                                              actionsS: PublishSubject<Counter_Pair.Action>,
                                              init: Counter_Pair.View.() -> Unit): Counter_Pair.View {
    return ankoView({ Counter_Pair.View(it, modelO, actionsS) }, init)
}
