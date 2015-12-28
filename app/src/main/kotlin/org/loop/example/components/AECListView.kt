package org.loop.example.components

import android.support.v7.widget.RecyclerView

/**
 * Created by pamelactan on 12/26/15.
 */
interface AECListView<VH> {
    // Should this be <VH : AECViewHolder> ??
    fun createViewHolder(): VH
}