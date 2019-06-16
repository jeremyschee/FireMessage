package android.com.firemessage

import android.annotation.SuppressLint
import android.com.firemessage.fragment.MyAccountFragment
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.navigation_people ->{
                    true
                }
                R.id.navigation_my_account ->{
                    replaceFragment(MyAccountFragment())
                    true
                }
                else -> false
            }
        }
    }

    @SuppressLint("CommitTransaction")
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_layout,fragment)
            commit()
        }
    }
}
