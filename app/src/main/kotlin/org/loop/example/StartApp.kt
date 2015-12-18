package org.loop.example

import rx.Observable

/**
 * Created by pamelactan on 12/18/15.
 */
class StartApp {
    data class Config<Model, Action>(val init: Pair<Model, Action>,
                                     val update: (Model, Action) -> Model,
                                     val inputs: List<Observable<Action>>)


}