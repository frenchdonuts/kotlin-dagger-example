package org.loop.example.components

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView
import org.loop.example.contramap
import rx.Observable
import rx.subjects.PublishSubject

/**
 * Created by pamelactan on 12/18/15.
 */
class CounterList {

    // TODO: How would we generalize this to work w/ any Model?
    data class Model(val counters: List<Counter.Model>)

    sealed class Action {
        object Id: Action()
        object Insert: Action()

        class Remove(val pos: Int): Action()
        class Modify(val pos: Int, val action: Counter.Action): Action()
        //class Filter(val pred: (Counter.Model) -> Boolean): Action()
        // Hmmm...should the Actions be: Create, Read, Update, Delete (CRUD) ?
        // Should it have all the animation events: SwipeLeft, SwipeRight, etc. ?
    }

    companion object {
        fun update(a: Action, m: Model): Model {
            return when (a) {
                is Action.Id -> m
                is Action.Insert -> insert(m)
                is Action.Remove -> remove(a, m)
                is Action.Modify -> modify(a, m)
            }
        }

        private fun insert(m: Model): Model =
                m.copy(
                        counters = m.counters + Counter.Model()
                )

        private fun remove(a: Action.Remove, m: Model) =
                m.copy(
                        counters = m.counters - m.counters.get(a.pos)
                )

        private fun modify(a: Action.Modify, m: Model) =
                m.copy(
                        counters = m.counters.mapIndexed { i, childModel ->
                            if (i == a.pos) Counter.update(a.action, childModel)
                            else childModel
                        }
                )
    }
    // TODO: Look at your Giphy Client to see how to implement infinite scroll
    /**
     * Questions:
     * Should we define the RecyclerView.Adapter here? How about the ViewHolder?
     *
     * When would we set the adapter?
     *
     * Should the Model include configuration variables?
     *   What should the configuration variables be?
     *   Is the LayoutManager one?
     *   Should the (Rx)SwipeRefreshLayout be included by default or should it be another configuration variable?
     *   How about ItemTouchHelpers?
     *   etc.
     *   I have a strong feeling that these should just go into the constructor of the View.
     *     Have some defaults for the case when they are not assigned
     *
     * Where would we set the LayoutManager?
     *
     *
     * For now, do the most basic thing and just make a list of Counters; We can generalize later
     */
    class View (context: Context,
                val modelAndActionO: Observable<Pair<CounterList.Model, CounterList.Action>>,
                val actionS: PublishSubject<Action>) : RecyclerView(context) {
        init {
            adapter = CounterList.Adapter(modelAndActionO, actionS)
        }

    }
    class Adapter(val modelAndActionO: Observable<Pair<Model, Action>>,
                  val actionS: PublishSubject<Action>) : RecyclerView.Adapter<VH>() {
        var count: Int = 0
        init {
            modelAndActionO.subscribe {
                val (model, action) = it

                when (action) {
                    is Action.Id -> //
                    is Action.Insert -> //
                    is Action.Modify -> //
                    is Action.Remove -> //
                }
            }
        }

        override fun getItemCount(): Int {
            throw UnsupportedOperationException()
        }

        override fun onBindViewHolder(holder: VH?, position: Int) {
            throw UnsupportedOperationException()
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH? {
            throw UnsupportedOperationException()
        }

    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {}
}

public inline fun ViewManager.counterListView(modelAndActionO: Observable<Pair<CounterList.Model, CounterList.Action>>,
                                              actionS: PublishSubject<CounterList.Action>,
                                              init: CounterList.View.() -> Unit): CounterList.View {
    return ankoView({ CounterList.View(it, modelAndActionO, actionS) }, init)
}