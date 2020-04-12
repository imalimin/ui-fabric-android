/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.officeuifabricdemo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import com.microsoft.officeuifabric.listitem.ListItemDivider
import com.microsoft.officeuifabric.listitem.ListItemView
import com.microsoft.officeuifabric.search.Searchbar
import kotlinx.android.synthetic.main.activity_demo_list.*

/**
 * This activity presents a list of [Demo]s, which when touched,
 * lead to a subclass of [DemoActivity] representing demo details.
 */
class DemoListActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var searchbar: Searchbar

    override fun onCreate(savedInstanceState: Bundle?) {
        // Launch Screen: Setting the theme here removes the launch screen, which was added to this activity
        // by setting the theme to App.Launcher in the manifest.
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_demo_list)

        setupAppBar()

        demo_list.adapter = DemoListAdapter()
        demo_list.addItemDecoration(ListItemDivider(this, DividerItemDecoration.VERTICAL))

        Initializer.init(application)
    }

    private fun setupAppBar() {
        app_bar.toolbar.subtitle = BuildConfig.VERSION_NAME

        searchbar = Searchbar(this)
        searchbar.onQueryTextListener = this
        app_bar.accessoryView = searchbar
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        val userInput = query.toLowerCase()
        val filteredDemoList = DEMOS.filter { it.title.toLowerCase().contains(userInput) }

        searchbar.showSearchProgress = true

        Handler().postDelayed({
            (demo_list.adapter as DemoListAdapter).demos = filteredDemoList as ArrayList<Demo>
            searchbar.showSearchProgress = false
        }, 500)

        return true
    }

    private class DemoListAdapter : RecyclerView.Adapter<DemoListAdapter.ViewHolder>() {
        var demos = DEMOS
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        private val onClickListener = View.OnClickListener { view ->
            val demo = view.tag as Demo
            val intent = Intent(view.context, demo.demoClass.java)
            intent.putExtra(DemoActivity.DEMO_ID, demo.id)
            view.context.startActivity(intent)
        }

        override fun getItemCount(): Int = demos.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val listItemView = ListItemView(parent.context)
            listItemView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            return ViewHolder(listItemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val demo = demos[position]
            holder.listItem.title = demo.title
            with(holder.itemView) {
                tag = demo
                setOnClickListener(onClickListener)
            }
        }

        private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val listItem: ListItemView = view as ListItemView
        }
    }
}