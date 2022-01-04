package com.example.ott

import android.app.Activity
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ott.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var isGateringMotionAnimating: Boolean = false
    private var isCurationMotionAnimating: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        makeStatusBarTransparent()

        initAppBar()

        initInsetMargin()

        initScrollViewListeners()

        initMotionLayoutListener()
    }

    private fun initScrollViewListeners() {
        binding.scrollView.smoothScrollTo(0, 0)
        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrolledValue = binding.scrollView.scrollY
            if (scrolledValue > 150f.dpToPx(this).toInt()) {
                if (isGateringMotionAnimating.not()) {
                    binding.gatheringDigitalThingsBackgroundMotionLayout.transitionToEnd()
                    binding.gatheringDigitalThingsLayout.transitionToEnd()
                    binding.buttonShownMotionLayout.transitionToEnd()
                }
            } else {
                if (isGateringMotionAnimating.not()) {
                    binding.gatheringDigitalThingsBackgroundMotionLayout.transitionToStart()
                    binding.gatheringDigitalThingsLayout.transitionToStart()
                    binding.buttonShownMotionLayout.transitionToStart()
                }
            }

            if (scrolledValue > binding.scrollView.height) {
                if (isCurationMotionAnimating.not()) {
                    binding.curationAnimationMotionLayout.setTransition(R.id.curation_animation_start1, R.id.curation_animation_end1)
                    binding.curationAnimationMotionLayout.transitionToEnd()
                    isCurationMotionAnimating = true
                }
            }
        }
    }

    private fun initMotionLayoutListener() {
        binding.gatheringDigitalThingsLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
                isGateringMotionAnimating = true
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                isGateringMotionAnimating = false
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {}
        })

        binding.curationAnimationMotionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {}

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                when (currentId) {
                    R.id.curation_animation_end1 -> {
                        binding.curationAnimationMotionLayout.setTransition(R.id.curation_animation_start2, R.id.curation_animation_end2)
                        binding.curationAnimationMotionLayout.transitionToEnd()
                    }
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {}
        })
    }

    private fun initInsetMargin() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(coordinator) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = insets.systemWindowInsetBottom
            toolbarContainer.layoutParams = (toolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0, insets.systemWindowInsetTop, 0, 0)
            }
            collapsingToolbarContainer.layoutParams = (collapsingToolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0, 0, 0, 0)
            }

            insets.consumeSystemWindowInsets()
        }
    }

    private fun initAppBar() {
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val topPadding = 300f.dpToPx(this)
            val realAlphaScrollHeight = appBarLayout.measuredHeight - appBarLayout.totalScrollRange
            val abstractOffset = abs(verticalOffset)

            val realAlphaVerticalOffset = if (abstractOffset - topPadding < 0) 0f else abstractOffset - topPadding

            if (abstractOffset < topPadding) {
                binding.toolbarBackgroundView.alpha = 0f
                return@OnOffsetChangedListener
            }

            val percentage = realAlphaVerticalOffset / realAlphaScrollHeight
            binding.toolbarBackgroundView.alpha = 1 - (if (1 - percentage * 2 < 0) 0f else 1 - percentage * 2)
        })
        initActionBar()
    }

    private fun initActionBar() = with(binding) {
        toolbar.navigationIcon = null
        toolbar.setContentInsetsAbsolute(0, 0)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(false)
            it.setDisplayShowHomeEnabled(false)
            it.setDisplayShowHomeEnabled(false)
        }
    }

    private fun Float.dpToPx(context: Context): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)

    private fun Activity.makeStatusBarTransparent() {
        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }
}