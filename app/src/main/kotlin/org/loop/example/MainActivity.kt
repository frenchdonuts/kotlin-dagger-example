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
import org.jetbrains.anko.verticalLayout
import org.loop.example.components.counter.Counter
import org.loop.example.components.counter.CounterView
import org.loop.example.components.counter_pair.Counter_Pair
import org.loop.example.components.counter_pair.Counter_PairView
import javax.inject.Inject
import javax.inject.Named

public class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.name

    @Inject
    lateinit var locationManager: LocationManager

    @field:[Inject Named("something")]
    lateinit var something: String

    @field:[Inject Named("somethingElse")]
    lateinit var somethingElse: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dispatch(this)
        val counterView = CounterView(Counter.Model(), dispatch)

        verticalLayout {
            counterView.
        }
/*        setContentView(R.layout.activity_main)
        MyApplication.graph.inject(this)
        assert(textView != null)
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

    fun dispatch(activity: Activity, action: Counter.Action = Counter.Action.INIT, model: Counter.Model = Counter.Model()) {
        val updatedModel = Counter.update(action, model)
        val dispatch = dispatch(activity, updatedModel)

        activity.setContentView(CounterView(updatedModel, dispatch).createView(AnkoContext.createReusable(activity)))
        //activity.setContentView(Counter_Pair.view(activity, updatedModel, dispatch))
    }

    fun dispatch(activity: Activity, model: Counter.Model) = { newAction: Counter.Action ->
        dispatch(activity, newAction, model)
    }
}
