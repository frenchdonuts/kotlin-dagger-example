package org.loop.example.components.counter

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewManager
import android.widget.FrameLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView

/**
 * Created by pamelactan on 12/15/15.
 *
 * If we let the View do queries against the global app state, then this local view has to know
 *   what the global app state looks like, breaking modularity
 *
 * At the same time, I don't want the parent view handing down state to their children, because that
 *   would make it so that every view has some work to do on every dispatch, even though only a small
 *   child view is the only thing affected by it.
 *
 * But exactly how much work does every view need to do for the latter architecture^? It's really not
 *   THAT expensive to just pass down data. It's the rendering part that's expensive, and we could just
 *   let each view decided if it needs to re-render based on the state/model it got from its parent.
 */

// I need to create a class that extends a ViewGroup, THEN set the corresponding AnkoComponent as its UI
// Which view lifecycle callback do I use?
class CounterView(context: Context, val model: Counter.Model, val dispatch: (Counter.Action) -> Unit) : FrameLayout(context) {
    // How do I get references to the views I create in CounterViewUI?
    //   Hmmm...getters and setters in CounterViewUI?
    // How do I make it so that I don't double-nest unnecessary ViewGroups? (This view, and CounterViewUI's verticalLayout)

    private fun init() = AnkoContext.createDelegate(this).apply {
        CounterViewUI(model, dispatch).createView(this)
    }
    init {
        init()
    }
 }

class CounterViewUI(val model: Counter.Model, val dispatch: (Counter.Action) -> Unit): AnkoComponent<CounterView> {
    override fun createView(ui: AnkoContext<CounterView>): View = ui.apply {
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
}

public inline fun ViewManager.counterView(model: Counter.Model, noinline dispatch: (Counter.Action) -> Unit, init: CounterView.() -> Unit): CounterView {
    return ankoView({ CounterView(it, model, dispatch) }, init)
}

