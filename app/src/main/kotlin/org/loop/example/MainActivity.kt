package org.loop.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout
import org.loop.example.components.*
import rx.subjects.PublishSubject

public class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.name

/*    @Inject
    lateinit var locationManager: LocationManager

    @field:[Inject Named("something")]
    lateinit var something: String

    @field:[Inject Named("somethingElse")]
    lateinit var somethingElse: String*/

    lateinit var actionSubject: PublishSubject<App.Action>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Restore state on rotation
        // Possible sln:
        //   Have App.Model(and all other *.Model's) implement Parcelable (AutoParcel)
        //   and bundle/unbundle them the normal way; then use it as the initial value in scan
        actionSubject = PublishSubject.create()
        val modelO = actionSubject.startWith(App.Action.Id)
                                  .scan(App.Model(), { model, action -> App.update(action, model) })
                                  .cache(1)  // Do we need this?
                                  .doOnNext { Log.i(TAG, "model: " + it.toString()) }

        setContentView(
                verticalLayout {
                    val counterView = counterView(actionSubject.contramap { App.Action.counter(it) }, {})

                    // We could do something...awkward? here. We could make new Actions,
                    // CounterPairTopUp, CounterPairTopDown
                    // CounterPairBotUp, CounterPairBotDown
                    // And in this contramap, do some pattern matching to tag the incoming Actions appropriately
                    // Epiphany: You can move any Action as "high up" as you want, as long as the modules below you use
                    //   all the Actions of their child modules e.g. you cannot dispatch CounterPairTopUp if the CounterPair
                    //   module does not use Counter.Action.Up in any of it's Actions
                    // Wow...even within this Elm Arch. there is alot of flexibility in the soln space.
                    //   One dimension one can play with is the granularity of the Actions one exposes at each level
/*             e.g. counterPairView(actionSubject.contramap {
                        when (it) {
                            is CounterPair.Action.Top -> { another pattern match... App.Action.CounterPairTopUp(it) ... }
                            is CounterPair.Action.Bot -> { ... App.Action.CounterPairBotDown(it) ... }
                        }
                    }, {})*/
                    val counterPairView = counterPairView(actionSubject.contramap { App.Action.counterPair(it) }, {})

                    val counterListView = counterListView(actionSubject.contramap { App.Action.counterList(it) }, {})//.lparams(width = matchParent, height = dip(50))

                    modelO.subscribe {
                        // Should I put equality checks in the render methods?
                        counterView.render(it.counter)
                        counterPairView.render(it.counterPair)
                        counterListView.render(it.counterList)
                    }
                }
        )


/*
        MyApplication.graph.inject(this)
        Log.d(TAG, "$something and $somethingElse")*/
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
