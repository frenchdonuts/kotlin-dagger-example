package org.loop.example

import android.app.Activity

/**
 * Created by pamelactan on 12/14/15.
 *
 * Question: How do I generalize this disptach function?
 * Right now, I would have to "re-write" this function each time I want to use it in a new project.
 *   Namely, the imported Action class and Model class would have to be the top-level Action and
 *   Model classes of my new project.
 *   Hmmm...isn't this solved w/ Generics?
 */
/*fun dispatch(activity: Activity, action: Action = Action.Reset, model: Model = Model()) {
    val updatedModel = update(action, model)
    val dispatch = dispatch(activity, updatedModel)

    activity.setContentView(view(activity, updatedModel, dispatch))
}

fun dispatch(activity: Activity, model: Model) = { newAction: Action ->
    dispatch(activity, newAction, model)
}*/
