package org.loop.example

import rx.subjects.PublishSubject

/**
 * Created by pamelactan on 12/18/15.
 */

/**
 * map PublishSubject<App.Action> (App.Action -> Counter.Action) :: PublishSubject<Counter.Action>
 * contramap PublishSubject<App.Action> (Counter.Action -> App.Action) :: PublishSubject<Counter.Action>
 */
fun <A, B> PublishSubject<A>.contramap(f: (B) -> A): PublishSubject<B> {
    var newPublishSubject: PublishSubject<B> = PublishSubject.create()
    newPublishSubject.subscribe {
        this.onNext(f(it))
    }
    return newPublishSubject
}

fun <T> List<T>.containsElementAtIndex(i: Int): Boolean {
    return 0 <= i && i < this.size
}

fun <T> List<T>.insert(x: T, index: Int): List<T> {
    return this.mapIndexed { i, t ->
        if (i == index) listOf(x, t)
        else listOf(t)
    }.flatten()
}
