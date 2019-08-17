package com.example.pleasegod.view

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.pleasegod.R
import com.example.pleasegod.model.entity.Restroom
import com.example.pleasegod.observer.DefaultObserver
import com.example.pleasegod.observer.DefaultSingleObserver
import com.example.pleasegod.view.adapter.RestroomListAdapter
import com.example.pleasegod.viewmodel.RestroomViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_restroom_list.*
import kotlinx.android.synthetic.main.bottom_sheet_search_view.view.*
import java.util.concurrent.TimeUnit

class RestroomListActivity : AppCompatActivity() /* , LocationAdapter.OnItemClickListener */ {
    companion object {
        val TAG: String = RestroomListActivity::class.java.simpleName
        const val PREFERENCES_KEY: String = "selected_location"
        private val LOCATION_LIST: MutableList<String> = mutableListOf(
            "가평군", "고양시",
            "과천시", "광명시", "광주시", "구리시", "군포시", "김포시",
            "남양주시", "동두천시", "부천시", "성남시", "수원시", "시흥시", "안산시", "안성시",
            "안양시", "양주시", "양평군", "여주시", "연천군", "오산시", "용인시", "의왕시", "의정부시",
            "이천시", "파주시", "평택시", "포천시", "하남시", "화성시"
        )
        private val LOCATION_ICON_LIST: Array<Int> = arrayOf(
            R.drawable.gapyeong,
            R.drawable.goyang,
            R.drawable.gwacheon,
            R.drawable.gwangmyeong,
            R.drawable.gwangju,
            R.drawable.guri,
            R.drawable.gunpo,
            R.drawable.gimpo,
            R.drawable.namyangju,
            R.drawable.dongducheon,
            R.drawable.bucheon,
            R.drawable.seongnam,
            R.drawable.suwon,
            R.drawable.siheung,
            R.drawable.yeoncheon,
            R.drawable.anseong,
            R.drawable.anyang,
            R.drawable.yangju,
            R.drawable.yangpyeong,
            R.drawable.yeoju,
            R.drawable.yeoncheon,
            R.drawable.osan,
            R.drawable.yongin,
            R.drawable.uiwang,
            R.drawable.uijeongbu,
            R.drawable.icheon,
            R.drawable.paju,
            R.drawable.pyeongtaek,
            R.drawable.pocheon,
            R.drawable.hanam,
            R.drawable.hwaseong)
    }

    private val mRestroomViewModel: RestroomViewModel by lazy {
        ViewModelProviders.of(this@RestroomListActivity).get(RestroomViewModel::class.java).apply {
            mRestroomLiveData.observe(this@RestroomListActivity, Observer {
                Log.d(TAG, "mRestroomLiveData changed()")

                mRestroomList.clear()
                mRestroomList.addAll(it)
                mRestroomListAdapter.copyTotalRestroom()
                mRestroomListAdapter.notifyDataSetChanged()
                loading_progress_bar.visibility = View.GONE
            })
        }
    }
    private val mRestroomListAdapter: RestroomListAdapter by lazy {
        RestroomListAdapter(this, mGlideRequestManager, mRestroomList)
    }
    private val mRestroomList: MutableList<Restroom> = mutableListOf()
    private lateinit var mBottomSheetDialog: BottomSheetDialog
    private val mDrawer: Drawer by lazy {
        initDrawer()
    }
    private val mBackPressSubject: BehaviorSubject<Long> = BehaviorSubject.createDefault(System.currentTimeMillis())
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()
    private val mTextChangeSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val mGlideRequestManager: RequestManager by lazy {
        Glide.with(this@RestroomListActivity)
    }
    private val mDividerItemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(applicationContext, LinearLayoutManager(this@RestroomListActivity).orientation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restroom_list)
        setSupportActionBar(toolbar_location)

        init()
        showRestroomList(0)
    }

    private fun init() {
        mCompositeDisposable.add(
            mTextChangeSubject
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DefaultObserver<String>() {
                    override fun onNext(text: String) {
                        mRestroomListAdapter.filter.filter(text)
                    }
                })
        )
        mCompositeDisposable.add(
            mBackPressSubject
                .observeOn(AndroidSchedulers.mainThread())
                .buffer(2, 1)
                .map {
                    Pair(it[0], it[1])
                }
                .subscribeWith(object : DefaultObserver<Pair<Long, Long>>() {
                    override fun onNext(t: Pair<Long, Long>) {
                        if (t.second - t.first < 1500) {
                            Log.d(TAG, "${t.first}, ${t.second}")

                            finish()
                        } else {
                            Toast.makeText(
                                this@RestroomListActivity,
                                getString(R.string.back_press_message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }))
        rv_restroom_list.apply {
            layoutManager = LinearLayoutManager(this@RestroomListActivity)
            adapter = mRestroomListAdapter
            addItemDecoration(mDividerItemDecoration)
        }
        fab_restroom_list.apply {
            setOnClickListener {
                Log.d(TAG, "onClick()")

                var isQuerySubmitted: Boolean = false
                val bottomSheetView: View =
                    LayoutInflater.from(this@RestroomListActivity).inflate(R.layout.bottom_sheet_search_view, null)
                        .apply {
                            search_view_restroom.apply {
                                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                                    override fun onQueryTextSubmit(query: String?): Boolean {
                                        Log.d(TAG, query)

                                        isQuerySubmitted = true
                                        mBottomSheetDialog.dismiss()

                                        return true
                                    }

                                    override fun onQueryTextChange(newText: String?): Boolean {
                                        newText?.let {
                                            mTextChangeSubject.onNext(newText)
                                        }

                                        return false
                                    }
                                })
                            }
                        }
                mBottomSheetDialog = BottomSheetDialog(this@RestroomListActivity).apply {
                    setContentView(bottomSheetView)
                    setOnShowListener {
                        Log.d(TAG, "onShow()")
                    }
                    setOnDismissListener {
                        Log.d(TAG, "onDismiss()")

                        if (!isQuerySubmitted) {
                            mRestroomListAdapter.restoreTotalRestroomData()
                        }
                    }
                }
                val bottomSheetBehavior: BottomSheetBehavior<View> =
                    BottomSheetBehavior.from(bottomSheetView.parent as View).apply {
                        setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                            }

                            override fun onStateChanged(bottomSheet: View, newState: Int) {

                            }
                        })
                    }

                mBottomSheetDialog.show()
            }
        }
    }

    private fun initDrawer(): Drawer =
        DrawerBuilder(this@RestroomListActivity)
            .withRootView(R.id.drawer_container)
            .withToolbar(toolbar_location)
            .withDisplayBelowStatusBar(false)
            .withActionBarDrawerToggle(true)
            .withActionBarDrawerToggleAnimated(true)
            .withOnDrawerItemClickListener(
                object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                        Log.d(TAG, "onItemClick() $position")

                        showRestroomList(position)

                        return true
                    }
                })
            .apply {
                for (i in 0 until LOCATION_LIST.size) {
                    addDrawerItems(
                        PrimaryDrawerItem().withName(LOCATION_LIST[i]).withIcon(
                            getDrawable(
                                LOCATION_ICON_LIST[i]
                            )
                        )
                    )
                }
            }
            .build()

    private fun showRestroomList(position: Int) {
        getRestroomList(LOCATION_LIST[position])
        mDrawer.closeDrawer()

        val sharedPreferences = getSharedPreferences("selected_location_preferences", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit().apply {
            putString(PREFERENCES_KEY, LOCATION_LIST[position])
            apply() // apply() commits changes asynchronously and we don't have to store selected location synchronously
        }
    }

    private fun getRestroomList(sigunName: String) {
        getRestroomList(1, 1000, sigunName)
    }

    private fun getRestroomList(pageIndex: Int = 1, pageSize: Int = 1000, sigunName: String = "고양시") {
        loading_progress_bar.visibility = View.VISIBLE
        mRestroomViewModel.getRestroomList(getString(R.string.api_key), pageIndex, pageSize, sigunName)
    }

    override fun onBackPressed() {
        if (mDrawer.isDrawerOpen) {
            mDrawer.closeDrawer()
        } else {
            mBackPressSubject.onNext(System.currentTimeMillis())
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")

        super.onDestroy()

        mRestroomListAdapter.clearDisposable()
        mCompositeDisposable.clear()
    }
}
