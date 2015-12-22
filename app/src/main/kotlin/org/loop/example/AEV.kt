package org.loop.example

import rx.subjects.PublishSubject

/**
 * Created by pamelactan on 12/21/15.
 */
interface AEV<Model, Action> {
    fun render(model: Model)
    fun setActionsOutput(actions: PublishSubject<Action>)
}