package org.loop.example.components

import rx.subjects.PublishSubject

/**
 * Created by pamelactan on 12/24/15.
 */
interface AECView<Action, Model> {
    fun render(m: Model): Unit
    fun setActionStream(actionS: PublishSubject<Action>): Unit
}