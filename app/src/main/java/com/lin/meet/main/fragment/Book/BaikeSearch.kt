package com.lin.meet.main.fragment.Book

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.hw.ycshareelement.transition.ChangeTextTransition
import com.lin.meet.R
import kotlinx.android.synthetic.main.activity_baike_search.*

class BaikeSearch : AppCompatActivity() {
    var firstInto = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baike_search)
        initView()
        initTransitionAnimation()
        showInputMethod()
    }

    private fun initTransitionAnimation(){
        ViewCompat.setTransitionName(search,"baike_search")
        val set = TransitionSet()
        set.addTransition(ChangeBounds())
        set.addTransition(ChangeImageTransform())
        set.addTransition(ChangeTransform())
        set.addTransition(ChangeTextTransition())
        set.addTarget(search)
        window.sharedElementExitTransition = set
        window.sharedElementEnterTransition = set
    }

    private fun initView(){
        search.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                search_text.visibility = if(search.text.toString().isNotEmpty()) View.VISIBLE else View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        search.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    onSearch()
                    return true
                }
                return false
            }
        })
        back.setOnClickListener {
            hideInputMethod()
            onBackPressed()
        }
        search_text.setOnClickListener{
            onSearch()
        }
    }

    override fun onResume() {
        if(firstInto)
            search.requestFocus()
        super.onResume()
    }

    private fun hideInputMethod() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = window.peekDecorView()
        if (null != v) {
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    private fun showInputMethod(){
        Thread({
            Thread.sleep(600)
            runOnUiThread({
                search.setSelection(0)
                search.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(search, 0)
            })
        }).start()
    }

    private fun onSearch(){
        val intent = Intent()
        intent.putExtra("Search",search.text.toString())
        setResult(1001,intent)
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        hideInputMethod()
    }
}
