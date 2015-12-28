package org.loop.example.components

import android.support.v7.widget.RecyclerView
import android.view.View
import rx.subjects.PublishSubject

/**
 * Created by pamelactan on 12/26/15.
 */
abstract class AECViewHolder<A, CM>(view: View, val actionS: PublishSubject<A>) : RecyclerView.ViewHolder(view) {
    abstract fun bind(model: CM, pos: Int): Unit
}

