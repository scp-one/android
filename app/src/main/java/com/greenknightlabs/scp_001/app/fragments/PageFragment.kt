//package com.greenknightlabs.scp_001.app.fragments
//
//import android.os.Bundle
//import android.view.View
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
//import com.greenknightlabs.scp_001.R
//import com.greenknightlabs.scp_001.app.adapters.PageAdapter
//import com.greenknightlabs.scp_001.app.enums.PageState
//import com.greenknightlabs.scp_001.app.fragments.PageViewModel
//import com.greenknightlabs.scp_001.databinding.FragmentPageBinding
//import java.util.*
//
//abstract class PageFragment<T, U: PageViewModel<T>> : BaseFragment<FragmentPageBinding>(R.layout.fragment_page) {
//    // dependencies
//    abstract val vm: U
//
//    // properties
////    protected lateinit var adapter: PageAdapter<T>
////    protected lateinit var layoutManager: LinearLayoutManager
////    protected lateinit var refreshLayout: SwipeRefreshLayout
//
//    // functions
//    override fun configureView(view: View, savedInstanceState: Bundle?) {
//        super.configureView(view, savedInstanceState)
////        adapter = adapterForView()
////        layoutManager = layoutManagerForView()
////        layoutManager.isItemPrefetchEnabled = true
////        configureRecyclerView()
////        refreshLayout = binding.fragmentPageRefreshLayout
////        refreshLayout.setOnRefreshListener {
////            tryRefreshing()
////        }
////        if (savedInstanceState == null) {
////            tryRefreshing()
////        }
//    }
//
////    protected abstract fun adapterForView(): PageAdapter<T>
//
////    protected open fun layoutManagerForView(): LinearLayoutManager {
////        return LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
////    }
////
////    protected open fun configureRecyclerView() {
////        binding.fragmentPageRecyclerView.adapter = adapter
////        binding.fragmentPageRecyclerView.layoutManager = layoutManager
////        binding.fragmentPageRecyclerView.itemAnimator = null
////        binding.fragmentPageRecyclerView.setHasFixedSize(true)
////        binding.fragmentPageRecyclerView.addOnScrollListener(onScrollListenerForView())
////    }
//
////    protected open fun onScrollListenerForView(): RecyclerView.OnScrollListener {
////        return object : RecyclerView.OnScrollListener() {
////            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
////                super.onScrollStateChanged(recyclerView, newState)
////                if (vm.state.value != PageState.Idle) {
////                    return
////                }
////                if (layoutManager.findLastVisibleItemPosition() + 1 == layoutManager.itemCount) {
////                    paginate(false)
////                }
////            }
////        }
////    }
////
////    private fun tryRefreshing() {
////        if (canRefresh) {
////            refreshedAt = Date().time
////            paginate(true)
////        } else {
////            stopRefreshing()
////        }
////    }
//
////    protected abstract fun paginate(refresh: Boolean)
////
////    protected fun stopRefreshing() {
////        refreshLayout.isRefreshing = false
////    }
//}