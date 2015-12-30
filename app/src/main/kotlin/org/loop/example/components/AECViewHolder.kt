package org.loop.example.components

import android.support.v7.widget.RecyclerView
import android.view.View
import org.loop.example.contramap
import rx.subjects.PublishSubject

/**
 * Created by pamelactan on 12/26/15.
 */
class AECViewHolder<A, CA, CM, V>(val view: V,
                                  val actionS: PublishSubject<A>,
                                  val modifyAction: (Int, CA) -> A) : RecyclerView.ViewHolder(view)
                                  where V : View, V : AECView<CA, CM> {
    fun bind(model: CM, pos: Int): Unit {
        view.render(model)
        view.setActionStream(actionS.contramap<A, CA> { modifyAction(pos, it) })
    }
}

