package org.loop.example.components

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.loop.example.contramap
import org.loop.example.insert
import rx.subjects.PublishSubject

/**
 * Created by pamelactan on 12/26/15.
 *
 * Maybe these AEC modules should be more like FACTORIES. So I just instantiate an instance of
 *   AECList say (w/ proper type parameters) and just inject that instance wherever I need it.
 *   Same w/ all the other modules.
 *
 * Should this be a Fragment?
 */
class AECList {

    // Leave action in Model for now
    data class Model<CA, CM>(val items: List<CM> = arrayListOf(),
                             val action: Action<CA, CM> = Action.Id())

    sealed class Action<CA, CM>(val pos: Int) {
        class Id<CA, CM> : Action<CA, CM>(pos = -1)
        class Insert<CA, CM>(val model: CM, pos: Int) : Action<CA, CM>(pos)
        class Remove<CA, CM>(val model: CM, pos: Int) : Action<CA, CM>(pos)
        class Modify<CA, CM>(val childAction: CA, pos: Int) : Action<CA, CM>(pos)
    }

    companion object {
        fun <CA, CM> update(childUpdate: (CA, CM) -> CM, a: Action<CA, CM>, m: Model<CA, CM>): Model<CA, CM> {
            return when (a) {
                is Action.Id<CA, CM> -> m
                is Action.Insert<CA, CM> -> m.copy(
                        items = m.items.insert(a.model, a.pos),
                        action = Action.Insert(a.model, a.pos)
                )
                is Action.Remove<CA, CM> -> m.copy(
                        items = m.items - a.model,
                        action = Action.Remove(a.model, a.pos)
                )
                is Action.Modify<CA, CM> -> m.copy(
                        items = m.items.mapIndexed { i, childModel ->
                            if (i == a.pos) childUpdate(a.childAction, childModel)
                            else childModel
                        },
                        action = Action.Modify(a.childAction, a.pos)
                )
            }
        }
    }
    // TODO: Look at your Giphy Client to see how to implement infinite scroll
    /**
     * Questions:
     */
    class View<CA, CM, V>(context: Context,
                          val childView: (Context) -> V,
                          val actionS: PublishSubject<Action<CA, CM>>) : RecyclerView(context),
                                                                         AECListView<AECViewHolder<CA, CM, V>>
                                                                         where V : android.view.View,
                                                                               V : AECView<CA, CM> {
        override fun createViewHolder(): AECViewHolder<CA, CM, V> {
            return AECViewHolder(childView(context), actionS)
        }


        lateinit var adapter: AECAdapter<CA, CM, V, AECViewHolder<CA, CM, V>>

        init {
            adapter = AECAdapter(this)
            this.setAdapter(adapter)

            // TODO: Put this into a context object
            layoutManager = LinearLayoutManager(context)
        }

        public fun render(m: Model<CA, CM>) {
            when (m.action) {
                is Action.Id<CA, CM> -> adapter.renderId()
                is Action.Insert<CA, CM> -> adapter.renderInsert(m.items, m.action.pos)
                is Action.Remove<CA, CM> -> adapter.renderRemove(m.items, m.action.pos)
                is Action.Modify<CA, CM> -> adapter.renderModify(m.items, m.action.pos)
            }
        }

        class AECAdapter<CA, CM, V, VH : AECViewHolder<CA, CM, V>>(
                val aecListView: AECListView<VH>) : RecyclerView.Adapter<VH>()
                where V : android.view.View, V : AECView<CA, CM> {
            var items: List<CM> = arrayListOf()

            // Render fns should correspond to all possible notify___Changed functions ?
            public fun renderId() {
            }

            public fun renderInit(items: List<CM>) {
                this.items = items
                notifyDataSetChanged()
            }

            public fun renderInsert(items: List<CM>, pos: Int) {
                this.items = items
                notifyItemInserted(pos)
                notifyItemRangeChanged(pos + 1, items.size - (pos + 1))
            }

            public fun renderRemove(items: List<CM>, pos: Int) {
                this.items = items
                notifyItemRemoved(pos)
                notifyItemRangeChanged(pos + 1, items.size - (pos + 1))
            }

            public fun renderModify(items: List<CM>, pos: Int) {
                this.items = items
                notifyItemChanged(pos)
            }

            override fun getItemCount(): Int {
                return items.size;
            }

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH? {
                return aecListView.createViewHolder()
            }

            override fun onBindViewHolder(holder: VH?, position: Int) {
                // This is not called on any structural changes such as notifyItemInserted or notifyItemRemoved
                // How can call setActionOutput on the child views on structural changes to update the position?
                holder?.bind(items.get(position), position)
            }

            override fun onBindViewHolder(holder: VH?, position: Int, payloads: MutableList<Any>?) {
                // Wow...I just realized how crazy this method is. Different positions can actually have different TYPES of payloads
                //  and it's up to the programmer to keep track of this!!
                super.onBindViewHolder(holder, position, payloads)
            }
        }
    }
    class AECViewHolder<CA, CM, V>(var view: V,
                                   val actionS: PublishSubject<Action<CA, CM>>) : RecyclerView.ViewHolder(view)
                                   where V : android.view.View, V : AECView<CA, CM> {
        fun bind(model: CM, pos: Int): Unit {
            view.render(model)
            view.setActionStream(actionS.contramap { AECList.Action.Modify<CA, CM>(it, pos) })
        }
    }
}