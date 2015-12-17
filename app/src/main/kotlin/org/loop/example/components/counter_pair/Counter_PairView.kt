package org.loop.example.components.counter_pair

import android.content.Context
import android.view.View
import org.jetbrains.anko.*
import org.loop.example.components.counter.CounterView

/**
 * Created by pamelactan on 12/15/15.
 */
class Counter_PairView(val model: Counter_Pair.Model, val dispatch: (Counter_Pair.Action) -> Unit) : AnkoComponent<Context> {
    val counterViewTop = CounterView(model.topCounter, { a -> Counter_Pair.Action.Top(a) })
    val counterViewBot = CounterView(model.botCounter, { a -> Counter_Pair.Action.Bot(a) })

    override fun createView(ui: AnkoContext<Context>): View = ui.apply {
        // Why when I wrap a vertical layout around counterViewTop.createView(ui), it doesn't show up, but
        //  when it's just counterViewBot.createView(ui), it works?
        verticalLayout {
            counterViewTop.createView(ui)
            counterViewBot.createView(ui)
        }
    }.view
}