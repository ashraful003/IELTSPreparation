package com.example.ieltspreparation.presentation.login

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.ieltspreparation.R
import com.example.ieltspreparation.databinding.FragmentLoginInputBinding
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

class LoginInputFragment : Fragment() {
    val actionSignUp = Navigation.createNavigateOnClickListener(R.id.action_loginInputFragment_to_loginCreateFragment)
    val actionForgotPassword = Navigation.createNavigateOnClickListener(R.id.action_loginInputFragment_to_loginForgotPasswordFragment)
    private lateinit var binding: FragmentLoginInputBinding
    private lateinit var viewModel: LoginViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_input, container, false)
        binding.model = this
        isEnableSignInButton(false)
        val emailStream = RxTextView.textChanges(binding.emailEt)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe {
            binding.emailEt.error = if (it) getString(R.string.error_email) else null
        }

        val passwordStream = RxTextView.textChanges(binding.passwordEt)
            .skipInitialValue()
            .map { password ->
                password.isEmpty()
            }

        val invalidFiledStream = Observable.combineLatest(
            emailStream,
            passwordStream
        ) { emailInvalid: Boolean, passwordInvalid: Boolean ->
            !emailInvalid && !passwordInvalid
        }
        invalidFiledStream.subscribe { isValid ->
            isEnableSignInButton(isValid)
        }
        binding.loginErrorTv.visibility = View.GONE

        return binding.root
    }
    private fun isEnableSignInButton(isEnable: Boolean) {
        if (isEnable) {
            binding.btnSignIn.isEnabled = true
            binding.btnSignIn.backgroundTintList =
                ContextCompat.getColorStateList(requireActivity(), R.color.blue_300)
        } else {
            binding.btnSignIn.isEnabled = true
            binding.btnSignIn.backgroundTintList =
                ContextCompat.getColorStateList(requireActivity(), R.color.blue_500)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }
}