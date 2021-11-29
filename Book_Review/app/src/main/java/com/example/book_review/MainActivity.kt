package com.example.book_review

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.book_review.adapter.BookAdapter
import com.example.book_review.adapter.HistoryAdapter
import com.example.book_review.api.BookService
import com.example.book_review.databinding.ActivityMainBinding
import com.example.book_review.model.BestSellerDto
import com.example.book_review.model.History
import com.example.book_review.model.SearchBookDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binidng: ActivityMainBinding
    private lateinit var bookAdapter: BookAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var bookService: BookService

    private lateinit var db: AppDatabase

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binidng = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binidng.root)

        bookAdapter = BookAdapter(itemClickedListener = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })
        binidng.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binidng.bookRecyclerView.adapter = bookAdapter

        historyAdapter = HistoryAdapter(historyDeleteClickedListener = {
            deleteSearchKeyword(it)
        }, historyKeywordClickedListener = {
            autoFillKeyword(it)
        })

        binidng.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binidng.historyRecyclerView.adapter = historyAdapter

        binidng.searchEditText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binidng.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        binidng.searchEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
            }
            return@setOnTouchListener false
        }

        db = getAppDatabase(this)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(getString(R.string.api_key))
            .enqueue(object : Callback<BestSellerDto> {
                override fun onResponse(
                    call: Call<BestSellerDto>,
                    response: Response<BestSellerDto>
                ) {
                    // todo 성공처리
                    if (response.isSuccessful.not()) {
                        Log.e(TAG, "NOT SUCCESS!!!")
                        return
                    }

                    response.body()?.let {
                        Log.d(TAG, it.toString())

                        it.books.forEach { book ->
                            Log.d(TAG, book.toString())
                        }

                        bookAdapter.submitList(it.books)
                    }
                }

                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                    // todo 실패처리
                    Log.e(TAG, t.toString())
                }

            })

        binidng.searchEditText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binidng.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun search(keyword: String) {
        bookService.getBooksByName(getString(R.string.api_key), keyword)
            .enqueue(object : Callback<SearchBookDto> {
                override fun onResponse(
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {
                    // todo 성공처리
                    binidng.searchEditText.clearFocus()
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(
                        currentFocus?.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )

                    hideHistoryView()
                    saveSearchKeyword(keyword)

                    if (response.isSuccessful.not()) {
                        Log.e(TAG, "NOT SUCCESS!!!")
                        return
                    }

                    bookAdapter.submitList(response.body()?.books.orEmpty())
                }

                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    // todo 실패처리
                    hideHistoryView()
                    Log.e(TAG, t.toString())
                }

            })
    }

    private fun showHistoryView() {
        Thread {
            val keywords = db.historyDao().getAll().reversed()
            runOnUiThread {
                binidng.historyRecyclerView.visibility = View.VISIBLE
                historyAdapter.submitList(keywords)
            }
        }.start()
        binidng.historyRecyclerView.visibility = View.VISIBLE
    }

    private fun hideHistoryView() {
        binidng.historyRecyclerView.visibility = View.GONE
    }

    private fun saveSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().insertHistory(History(null, keyword))
        }.start()
    }

    private fun deleteSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().delete(keyword)
            showHistoryView()
        }.start()
    }

    private fun autoFillKeyword(keyword: String) {
        binidng.searchEditText.setText(keyword)
        search(keyword)
    }

    override fun onBackPressed() {
        if (binidng.historyRecyclerView.isVisible) {
            hideHistoryView()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val BASE_URL = "https://book.interpark.com"
    }
}