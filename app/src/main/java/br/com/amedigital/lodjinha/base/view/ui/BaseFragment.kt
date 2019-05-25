package br.com.amedigital.lodjinha.base.view.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.amedigital.lodjinha.R
import br.com.amedigital.lodjinha.base.gateway.BaseViewModel
import br.com.amedigital.lodjinha.base.gateway.ViewState

abstract class BaseFragment<V:BaseViewModel>: Fragment() {
    protected lateinit var viewModel: V

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupViews(view)
        observeViewModel()
    }

    protected open fun setupViews(view: View) {}

    protected abstract fun observeViewModel()

    protected fun setupToolbar(toolbar: Toolbar, homeAsUpEnabled: Boolean) {
        (activity as? BaseActivity)?.resetToolbar(toolbar, homeAsUpEnabled)
    }

    protected fun setupDrawer(toolbar: Toolbar) {
        (activity as? BaseActivity)?.setupDrawer(toolbar)
    }

    protected fun setupViewModel() {
        val clazz = getViewModelClass()
        viewModel = ViewModelProviders.of(this).get(clazz)
    }

    protected abstract fun getViewModelClass(): Class<V>

    protected fun observeChannel(channelName: String) {
        viewModel.observe(channelName,this, Observer { v-> handleResponse(v) })
    }

    protected open fun handleResponse(state: ViewState) {
        Log.w("BASE", "handleResponse called")

        if(!state.handled)  {
            state.handled = true
            if (state.isError()) {
                handleError(state.output.error)
            } else {
                handleSuccess(state.output.value)
            }
        }
    }

    protected open fun handleError(error: Throwable?) {}

    protected open fun handleSuccess(value: Any?) {}

    protected fun reveal(view: View) {
        if(view.visibility != View.VISIBLE) {
            view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
            view.visibility = View.VISIBLE
        }
    }
}