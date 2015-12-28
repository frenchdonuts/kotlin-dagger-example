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
     */
    class View (context: Context,
                val actionS: PublishSubject<Action>) : RecyclerView(context), AECListView<VH> {

        override fun createViewHolder(): VH = VH(Counter.View(context), actionS)

        lateinit var adapter: AECAdapter<Action, Counter.Model, VH>
        init {
            adapter = AECAdapter(this)
            this.setAdapter(adapter)

            // TODO: Parameterize this?
            layoutManager = LinearLayoutManager(context)
        }

        public fun render(m: Model) {
            // Instead of having AECAdapter defining its own Actions, and us passing them in,
            //   we could just call methods on AECAdapter instead:
            //   adapter.insert(m.action.pos) etc.
            val action = when (m.action) {
                is Action.Id -> AECAdapter.Action.Id
                is Action.Insert -> AECAdapter.Action.Insert(m.action.pos)
                is Action.Remove -> AECAdapter.Action.Remove(m.action.pos)
                is Action.Modify -> AECAdapter.Action.Modify(m.action.pos)
            }
            adapter.render(m.counters, action)
        }
    }

    class VH(view: Counter.View,
             actionS: PublishSubject<Action>) : AECViewHolder<Action, Counter.Model>(view, actionS) {
        override fun bind(m: Counter.Model, pos: Int) {
            Log.i("VH", "pos: $pos")
            (itemView as Counter.View).render(m)
            (itemView).setActionsOutput(actionS.contramap { Action.Modify(pos, it) })

        }
    }
}

public inline fun ViewManager.counterListView(actionS: PublishSubject<CounterList.Action>,
                                              init: CounterList.View.() -> Unit): CounterList.View {
    return ankoView({ CounterList.View(it, actionS) }, init)
}