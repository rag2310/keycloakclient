package com.rago.keycloakclient.ui.login

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rago.keycloakclient.databinding.FragmentLoginBinding
import com.rago.keycloakclient.utils.AuthStateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.openid.appauth.*
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        private const val TAG = "LoginFragment"
    }

    @Inject
    lateinit var mStateManager: AuthStateManager

    private lateinit var mBinding: FragmentLoginBinding
    private val mViewModel: LoginViewModel by viewModels()

    private val authentication =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intentResult = result.data
            mViewModel.resultLogin(intentResult)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentLoginBinding.inflate(inflater)
        setUpFlow()
        setUpClicks()
        return mBinding.root
    }

    private fun setUpClicks() {
        mBinding.apply {
            login.setOnClickListener {
                mViewModel.login {
                    authentication.launch(it)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.isAuthorized()
    }

    private fun setUpFlow() {
        lifecycleScope.launchWhenCreated {
            launch {
                mViewModel.loginUIState.collect(::loginUiState)
            }
        }
    }

    private fun loginUiState(state: LoginUIState) {
        when (state) {
            LoginUIState.Authorized -> {
                mBinding.loading.visibility = View.GONE
                Toast.makeText(requireContext(), "Authorized", Toast.LENGTH_LONG).show()
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToTokenFragment())
            }
            is LoginUIState.Error -> {
                mBinding.loading.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${state.msg}", Toast.LENGTH_LONG).show()
            }
            LoginUIState.Loading -> {
                mBinding.loading.visibility = View.VISIBLE
            }
            LoginUIState.NotAuthorized -> {
                mBinding.loading.visibility = View.GONE
                Toast.makeText(requireContext(), "Not Authorized", Toast.LENGTH_LONG).show()
            }
            LoginUIState.NotState -> {}
        }
    }
}