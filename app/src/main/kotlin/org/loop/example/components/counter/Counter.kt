package org.loop.example.components.counter

import android.content.Context
import android.widget.LinearLayout
import org.jetbrains.anko.*

/**
 * Created by pamelactan on 12/14/15.
 */
class Counter {
    sealed class Action {
        object INIT : Action()

        object UP : Action()

        object DOWN : Action()
    }

    data class Model(val counter: Int = 0)

    companion object {
        fun update(action: Action, model: Model): Model {
            return when (action) {
                is Action.INIT -> Model()
                is Action.UP -> Model(model.counter + 1)
                is Action.DOWN -> Model(model.counter - 1)
            }
        }

        // I want to turn this into just a regular view, that receives new Models as they come in and only
        // renders if necessary
        fun view(context: Context, model: Model, dispatch: (Action) -> Unit): LinearLayout {
            with(context) {
                return verticalLayout {
                    button("up") {
                        onClick { dispatch(Action.UP) }
                    }
                    button("down") {
                        onClick { dispatch(Action.DOWN) }
                    }
                    textView(model.counter.toString())
                }
            }
        }
    }

    /*class CounterView : AnkoComponent {
        override fun createView(ui: AnkoContext) = with(ui) {
            verticalLayout {
                val name = editText()
            }
        }
    }*/
}