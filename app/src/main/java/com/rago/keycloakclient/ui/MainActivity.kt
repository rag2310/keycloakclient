package com.rago.keycloakclient.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.rago.keycloakclient.R
import com.rago.keycloakclient.databinding.ActivityMainBinding
import com.rago.keycloakclient.utils.AuthStateManager
import com.rago.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mStateManager: AuthStateManager

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mBinding: ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        /*val intent = Intent(baseContext, LoginActivity::class.java)
        startActivity(intent)
        finish()*/
    }
}