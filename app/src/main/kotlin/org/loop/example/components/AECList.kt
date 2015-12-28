package org.loop.example.components

import org.loop.example.containsElementAtIndex

/**
 * Created by pamelactan on 12/26/15.
 */
class AECList {

    data class Model<CM>(val items: List<CM> = arrayListOf(),
                         val action: Action = Action.Id)

    sealed class Action {
        object Id: Action()

        class Insert<CM>(val model: CM, val pos: Int): Action()
        class Remove<CM>(val model: CM, val pos: Int): Action()
        class Modify<CA>(val pos: Int, val action: CA): Action()

    }

    companion object {
        fun <CA, CM> update(a: Action, m: Model<CM>): Model<CM> {
            return when (a) {
                is Action.Id -> m
                is Action.Insert<*> -> insert(a, m)
                is Action.Remove<*> -> if (m.items.containsElementAtIndex(a.pos)) remove(a, m) else m
                is Action.Modify<*> -> if (m.items.containsElementAtIndex(a.pos)) modify(a, m) else m
            }
        }

        private fun <CM> insert(a: Action.Insert<CM>, m: Model<CM>): Model<CM> =
                m.copy( // TODO: Insert at specific position
                        items = m.items + Counter.Model(),
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