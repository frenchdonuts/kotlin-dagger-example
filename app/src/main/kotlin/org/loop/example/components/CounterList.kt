package org.loop.example.components

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView
import org.loop.example.containsElementAtIndex
import org.loop.example.contramap
import rx.Observable
import rx.subjects.PublishSubject
import java.util.*

/**
 * Created by pamelactan on 12/18/15.
 */
class CounterList {

    // TODO: How would we generalize this to work w/ any Model?
    data class Model(val counters: List<Counter.Model> = ArrayList(),
                     val action: Action = Action.Id)

    sealed class Action {
        object Id: Action()

        class Insert(val pos: Int): Action()
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
                is Action.Insert -> insert(a, m)
                is Action.Remove -> if (m.counters.containsElementAtIndex(a.pos)) remove(a, m) else m
                is Action.Modify -> if (m.counters.containsElementAtIndex(a.pos)) modify(a, m) else m
            }
        }

        private fun insert(a: Action.Insert, m: Model): Model =
                m.copy( // TODO: Insert at specific position
                        counters = m.counters + Counter.Model(),
                        action = Action.Insert(a.pos)
                )

        private fun remove(a: Action.Remove, m: Model) =
                m.copy(
                        counters = m.counters - m.counters.get(a.pos),
                        action = Action.Remove(a.pos)
                )

        private fun modify(a: Action.Modify, m: Model) =
                m.copy(
                        counters = m.counters.mapIndexed { i, childModel ->
                            if (i == a.pos) Counter.update(a.action, childModel)
                            else childModel
                        },
                        action = Action.Modify(a.pos, a.action)
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
     *
     * Can I implement all the standard List functions (map, filter, etc.) on this?
     * Can I implement transducers on this?
     *   This would amount to applying the functions on the internal data set and call the appropriate
     *   notifyRangeChanged methods
     */
    class View (context: Context,
                val actionS: PublishSubject<Action>) : RecyclerView(context) {
        lateinit var adapter: CounterList.Adapter
        init {
            adapter = CounterList.Adapter(context, actionS)
            this.setAdapter(adapter)
            // TODO: Parameterize this
            layoutManager = LinearLayoutManager(context)
        }

        public fun render(m: Model) {
            adapter.render(m)
        }

    }
    class Adapter(val context: Context,
                  val actionS: PublishSubject<Action>) : RecyclerView.Adapter<VH>() {
        val TAG = Adapter::class.java.simpleName
        var items: List<Counter.Model> = ArrayList()
        init {
            // ???
        }

        // TODO: Have render take a context object so we can move the Counter.Action out of the Model
        public fun render(m: Model) {
            val (items, action) = m

            this.items = items

            // Instead of having the action hold the position, we could have it hold an id, which we
            //   can find the position of here
            //val pos = this.items.indexOfFirst { (id, counterModel) -> id == action.id }
            Log.i(TAG, "CounterList rendering...$items. Action: $action")
             when (action) {
                is Action.Id -> {}
                // is Action.Init -> notifyDataSetChanged()
                is Action.Insert -> {
                    notifyItemInserted(action.pos)
                    // We actually have to do 2 different ranges: all items before action.pos, and all items after
                    notifyItemRangeChanged(1, this.items.size - 1, false)
                }
                is Action.Modify -> notifyItemChanged(action.pos, null)
                is Action.Remove -> notifyItemRemoved(action.pos)
            }
            //notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return items.size;
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH? {
            return VH(Counter.View(context, PublishSubject.create()), actionS)
        }

        override fun onBindViewHolder(holder: VH?, position: Int) {
            // This is not called on any structural changes such as notifyItemInserted or notifyItemRemoved
            // How can call setActionOutput on the child views on structural changes to update the position?
            holder?.bind(items.get(position), position)
        }

        override fun onBindViewHolder(holder: VH?, position: Int, payloads: MutableList<Any>?) {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    class VH(var view: Counter.View,
             val actionS: PublishSubject<Action>) : RecyclerView.ViewHolder(view) {
        public fun bind(m: Counter.Model, pos: Int) {
            Log.i("VH", "pos: $pos")
            view.render(m)
            view.setActionsOutput(actionS.contramap { Action.Modify(pos, it) })

        }
    }
}

public inline fun ViewManager.counterListView(actionS: PublishSubject<CounterList.Action>,
                                              init: CounterList.View.() -> Unit): CounterList.View {
    return ankoView({ CounterList.View(it, actionS) }, init)
}