package org.loop.example.components

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup

/**
 * Created by pamelactan on 12/24/15.
 */
class AECAdapter<A, CM, VH : AECViewHolder<A, CM>>(val aecListView: AECListView<VH>) : RecyclerView.Adapter<VH>() {
    sealed class Action {
        object Id : Action()
        object Init: Action()

        class Insert(val pos: Int) : Action()
        class Remove(val pos: Int) : Action()
        class Modify(val pos: Int) : Action()
        // Should probably create Actions corresponding to all the different methods that notify
        //   how the data set changed
        //class Filter(val pred: (Counter.Model) -> Boolean): Action()
        // Hmmm...should the Actions be: Create, Read, Update, Delete (CRUD) ?
        // Should it have all the animation events: SwipeLeft, SwipeRight, etc. ?
    }

    val TAG = AECAdapter::class.java.simpleName
    var items: List<CM> = arrayListOf()

    public fun render(items: List<CM>, action: Action) {
        this.items = items

        Log.i(TAG, "CounterList rendering...$items. Action: $action")
        when (action) {
            is Action.Id -> {}
            is Action.Init -> notifyDataSetChanged()

            is Action.Insert -> {
                notifyItemInserted(action.pos)
                // ViewHolders responsible for all items after the newly inserted item
                // need to update their PublishSubject w/ new positions
                // Range that changed [action.pos + 1, items.size - 1]
                // or, (action.pos, items.size)
                notifyItemRangeChanged(action.pos + 1, items.size - (action.pos + 1))
            }

            is Action.Modify -> notifyItemChanged(action.pos)

            is Action.Remove -> {
                notifyItemRemoved(action.pos)
                notifyItemRangeChanged(action.pos + 1, items.size - (action.pos + 1))
            }
        }
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