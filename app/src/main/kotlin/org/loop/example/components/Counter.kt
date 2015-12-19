package org.loop.example.components

import android.content.Context
import android.view.ViewManager
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
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
               val modelO: Observable<Model>,
               val actionsS: PublishSubject<Action>) : LinearLayout(context) {
        val TAG = View::class.java.simpleName;
        // How do I get references to the views I create in CounterViewUI?
        //   Hmmm...getters and setters in CounterViewUI?
        // How do I make it so that I don't double-nest unnecessary ViewGroups? (This view, and CounterViewUI's verticalLayout)

        // Define UI
        private lateinit var tvCounter: TextView
        private fun init() = AnkoContext.createDelegate(this).apply {
            button("Up") {
                onClick {
                    actionsS.onNext(Action.Up)
                }
            }
            button("Down") {
                onClick {
                    actionsS.onNext(Action.Down)
                }
            }
            tvCounter = textView("")
        }

        init {
            init()
            modelO.subscribe {
                tvCounter.text = it.counter.toString()
            }
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

public inline fun ViewManager.counterView(modelO: Observable<Counter.Model>,
                                          actionsS: PublishSubject<Counter.Action>,
                                          init: Counter.View.() -> Unit): Counter.View {
    return ankoView({ Counter.View(it, modelO, actionsS) }, init)
}
