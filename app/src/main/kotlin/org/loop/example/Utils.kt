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
