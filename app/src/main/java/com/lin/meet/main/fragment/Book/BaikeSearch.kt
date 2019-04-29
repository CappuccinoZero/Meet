package com.lin.meet.main.fragment.Book

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.lin.meet.R
import kotlinx.android.synthetic.main.activity_baike_search.*

class BaikeSearch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baike_search)
        initView()
    }

    private fun initView(){
        search.requestFocus()
        search.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                search_text.visibility = if(search.text.toString().isNotEmpty()) View.VISIBLE else View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        back.setOnClickListener {
            finish()
        }
        search_text.setOnClickListener{
            onSearch()
        }
    }

    private fun hideInputMethod() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = window.peekDecorView()
        if (null != v) {
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    private fun onSearch(){
        val intent = Intent()
        intent.putExtra("Search",search.text.toString())
        setResult(1001,intent)
        finish()
    }

    override fun finish() {
        super.finish()
        hideInputMethod()
    }
}
