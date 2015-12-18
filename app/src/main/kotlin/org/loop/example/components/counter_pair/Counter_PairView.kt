package org.loop.example.components.counter_pair

import android.content.Context
import android.view.View
import android.view.ViewManager
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import org.loop.example.components.counter.CounterView
import org.loop.example.components.counter.counterView
import org.loop.example.contramap
import rx.Observable
import rx.subjects.PublishSubject

/**
 * Created by pamelactan on 12/15/15.
 */
class Counter_PairView(context: Context,
                       val modelO: Observable<Counter_Pair.Model>,
                       val actionsS: PublishSubject<Counter_Pair.Action>) : LinearLayout(context) {
    val TAG = Counter_PairView::class.java.simpleName

    private fun init() = AnkoContext.createDelegate(this).apply {
        counterView(
                modelO.map { it.topCounter },
                actionsS.contramap({ a -> Counter_Pair.Action.Top(a) }),
                {})
        counterView(
                modelO.map { it.botCounter },
                actionsS.contramap({ a -> Counter_Pair.Action.Bot(a) }),
                {})
    }
}

public inline fun ViewManager.counterPairView(modelO: Observable<Counter_Pair.Model>,
                                              actionsS: PublishSubject<Counter_Pair.Action>,
                                              init: Counter_PairView.() -> Unit): Counter_PairView {
    return ankoView({ Counter_PairView(it, modelO, actionsS) }, init)
}