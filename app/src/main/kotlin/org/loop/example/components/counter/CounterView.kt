package org.loop.example.components.counter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView
import rx.Observable
import rx.subjects.PublishSubject

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

// Is there any advantage to using Observable<Counter.Model> vs just having the parent view call this view's render function?
//   Yes. There is. It delegates the rendering WHOLLY to this view. What if the parent view "forgets" to call this view's render function?
//   This way, the parent view cannot fuck up. And this way, this view's constructor defines a CONTRACT that requires that it gets a MODEL
//   THAT CHANGES OVER TIME.
//   In the end, this just puts less cognitive load on the user of this module. The user only has to know how the constructor works.
//   Nothing else
//   Also, I think it is just more semantically honest
class CounterView(context: Context,
                  val modelO: Observable<Counter.Model>,
                  val actionsS: PublishSubject<Counter.Action>) : LinearLayout(context) {
    val TAG = CounterView::class.java.simpleName;
    // How do I get references to the views I create in CounterViewUI?
    //   Hmmm...getters and setters in CounterViewUI?
    // How do I make it so that I don't double-nest unnecessary ViewGroups? (This view, and CounterViewUI's verticalLayout)

    // Define UI
    private lateinit var tvCounter: TextView
    private fun init() = AnkoContext.createDelegate(this).apply {
        button("Up") {
            onClick {
                actionsS.onNext(Counter.Action.UP)
            }
        }
        button("Down") {
            onClick {
                actionsS.onNext(Counter.Action.DOWN)
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

public inline fun ViewManager.counterView(modelO: Observable<Counter.Model>,
                                          actionsS: PublishSubject<Counter.Action>,
                                          init: CounterView.() -> Unit): CounterView {
    return ankoView({ CounterView(it, modelO, actionsS) }, init)
}

