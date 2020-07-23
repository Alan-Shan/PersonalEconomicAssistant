package top.ilum.pea

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import top.ilum.pea.data.Database
import top.ilum.pea.ui.stock.RetrofitBuilder
import top.ilum.pea.ui.stock.StockApiHelper
import top.ilum.pea.ui.stock.StockViewModel
import top.ilum.pea.ui.stock.StockViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: StockViewModel
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Database.getDatabase(application)
        if (savedInstanceState == null) {
            viewModel = ViewModelProvider(
                this,
                StockViewModelFactory(StockApiHelper(RetrofitBuilder.apiService))
            )
                .get(StockViewModel::class.java)
            setupBottomNavigationBar()
            viewModel.getSymbols()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        val navGraphIds = listOf(R.navigation.home_navigation, R.navigation.stock_navigation, R.navigation.calculators_navigation, R.navigation.exchange_navigation)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(
            this,
            Observer { navController ->
                setupActionBarWithNavController(navController)
            }
        )
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean =
        currentNavController?.value?.navigateUp() ?: false
}
