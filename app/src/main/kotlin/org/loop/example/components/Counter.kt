package org.loop.example.components

import android.content.Context
import android.view.ViewManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.loop.example.AEV
import rx.Observable
import rx.subjects.PublishSubject

/**
 * Created by pamelactan on 12/14/15.
 */
class Counter {
    sealed class Action {
        object Up : Action()

        object Down : Action()
    }

    data class Model(val counter: Int = 0)

    companion object {
        fun update(action: Action, model: Model): Model {
            return when (action) {
                is Action.Up -> Model(model.counter + 1)
                is Action.Down -> Model(model.counter - 1)
            }
        }
    }


    class View(context: Context,
               var actionS: PublishSubject<Action>) : LinearLayout(context), AEV<Model, Action> {
        val TAG = View::class.java.simpleName;

        // Define UI
        private lateinit var tvCounter: TextView
        private lateinit var btnUp: Button
        private lateinit var btnDown: Button
        private fun init() = AnkoContext.createDelegate(this).apply {
            button("Up") {
                onClick { actionS.onNext(Action.Up) }
            }

            btnDown = button("Down") {
                onClick { actionS.onNext(Action.Down) }
            }

            tvCounter = textView("")
        }

        init {
            init()
        }

        override public fun render(m: Model) {
            tvCounter.text = m.counter.toString()
        }

        override public fun setActionsOutput(actionS: PublishSubject<Action>) {
            this.actionS = actionS
        }
     }

/*    class ViewUI(val model: Counter.Model, val dispatch: (Counter.Action) -> Unit): AnkoComponent<Counter.View> {
        override fun createView(ui: AnkoContext<Counter.View>): View = ui.apply {
            verticalLayout {
                    button("Up") {
                        onClick { dispatch(Counter.Action.UP )}
                    }
                    button("Down") {
                        onClick { dispatch(Counter.Action.DOWN )}
                    }
                    textView(model.counter.toString())
                }
            }.view
    }*/



}
public inline fun ViewManager.counterView() = counterView(PublishSubject.create(), {})
public inline fun ViewManager.counterView(actionS: PublishSubject<Counter.Action>,
                                          init: Counter.View.() -> Unit): Counter.View {
    return ankoView({ Counter.View(it, actionS) }, init)
}
