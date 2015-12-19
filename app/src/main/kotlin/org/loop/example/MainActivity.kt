package org.loop.example

import android.app.Activity
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.activity_main.textView
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import org.loop.example.components.counter.Counter
import org.loop.example.components.counter.CounterView
import org.loop.example.components.counter.counterView
import org.loop.example.components.counter_pair.Counter_Pair
import org.loop.example.components.counter_pair.Counter_PairView
import org.loop.example.components.counter_pair.counterPairView
import rx.subjects.PublishSubject
import rx.subjects.Subject
import javax.inject.Inject
import javax.inject.Named

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
        //   and bundle/unbundle them the normal way
        actionSubject = PublishSubject.create()
        val modelO = actionSubject.startWith(App.Action.Id)
                                  .scan(App.Model(), { model, action -> App.update(action, model) })
                                  .cache(1)  // Do we need this?
                                  .doOnNext { Log.i(TAG, "model: " + it.toString()) }


        setContentView(
                verticalLayout {
                    counterView(
                            modelO.map { appModel -> appModel.counter },
                            actionSubject.contramap({ a -> App.Action.counter(a) }), {})

                    counterPairView(
                            modelO.map { appModel -> appModel.counterPair },
                            actionSubject.contramap({ a -> App.Action.counterPair(a) }), {})
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
