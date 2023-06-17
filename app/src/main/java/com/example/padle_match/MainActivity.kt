    package com.example.padle_match

    import android.content.Intent
    import android.graphics.Color
    import android.os.Bundle
    import com.google.android.material.snackbar.Snackbar
    import androidx.appcompat.app.AppCompatActivity
    import androidx.navigation.findNavController
    import androidx.navigation.ui.AppBarConfiguration
    import androidx.navigation.ui.navigateUp
    import androidx.navigation.ui.setupActionBarWithNavController
    import android.view.Menu
    import android.view.MenuItem
    import androidx.cardview.widget.CardView
    import androidx.lifecycle.Observer
    import androidx.lifecycle.ViewModelProvider
    import androidx.navigation.ui.NavigationUI
    import com.example.padle_match.databinding.ActivityMainBinding
    import com.example.padle_match.fragments.AddClubViewModel
    import com.example.padle_match.fragments.MyProfileViewModel
    import com.google.android.material.bottomnavigation.BottomNavigationView
    import androidx.lifecycle.findViewTreeLifecycleOwner
    import com.example.padle_match.fragments.MyTournamentsViewModel


    class MainActivity : AppCompatActivity() {

        private lateinit var appBarConfiguration: AppBarConfiguration
        private lateinit var binding: ActivityMainBinding
        private lateinit var bottonNavView: BottomNavigationView
        private lateinit var viewModel: MyProfileViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            bottonNavView = findViewById(R.id.bottomBar)


            viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)


            /*viewModel.kill.observe(viewLifecycleOwner, Observer { result ->
                txtCartel.text = result.toString()
            })*/
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            NavigationUI.setupWithNavController(bottonNavView, navController)

           //borrado de torneos anteriores al dia de la fecha
            val myTournamentsViewModel = ViewModelProvider(this).get(MyTournamentsViewModel::class.java)
            myTournamentsViewModel.deleteOldTournaments()
        }

         fun matar() {
            this.finish()
        }



        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            // Inflate the menu; this adds items to the action bar if it is present.
            menuInflater.inflate(R.menu.menu_main, menu)
            return true
        }

       /* override fun onOptionsItemSelected(item: MenuItem): Boolean {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            return when (item.itemId) {
                R.id.action_settings -> true
                else -> super.onOptionsItemSelected(item)
            }
        } */

        override fun onSupportNavigateUp(): Boolean {
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            return navController.navigateUp(appBarConfiguration)
                    || super.onSupportNavigateUp()
        }
    }