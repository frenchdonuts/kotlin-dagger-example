package org.loop.example.start_app

import android.app.Application
import rx.Observable

/**
 * Created by pamelactan on 12/18/15.
 */
class App : Application() {
    data class Config<Model, Action>(val init: Pair<Model, Action>,
                                 val update: (Model, Action) -> Model,
                                 val inputs: List<Observable<Action>>)
    //

    data class App<Model>(val model: Observable<Model>)

/*    public fun <Model, Action> start(config: Config<Model, Action>): App<Model> = {
        val model
        return App(model)
    }*/
}