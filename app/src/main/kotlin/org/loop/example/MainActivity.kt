package org.loop.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.verticalLayout
import org.loop.example.components.CounterList
import org.loop.example.components.counterListView
import org.loop.example.components.counterView
import org.loop.example.components.counterPairView
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
        //   and bundle/unbundle them the normal way
        actionSubject = PublishSubject.create()
        val modelO = actionSubject.startWith(App.Action.Id)
                                  .scan(App.Model(), { model, action -> App.update(action, model) })
                                  .cache(1)  // Do we need this?
                                  .doOnNext { Log.i(TAG, "model: " + it.toString()) }
        // ^ Should our RecyclerView.Adapter have a little engine inside running like this?
        // The problem is that the Adapter needs to know specific Actions sometimes


        setContentView(
                verticalLayout {
                    counterView(
                            modelO.map { appModel -> appModel.counter },
                            actionSubject.contramap({ App.Action.counter(it) }), {})

                    counterPairView(
                            modelO.map { appModel -> appModel.counterPair },
                            actionSubject.contramap({ App.Action.counterPair(it) }), {})

                    counterListView(
                            modelO.map { it.counterListModelAndAction },
                            actionSubject.contramap { App.Action.counterList(it) }, {})
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
