/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.car.app.sample.navigation.common.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.Row
import androidx.car.app.model.SearchTemplate
import androidx.car.app.model.SearchTemplate.SearchCallback
import androidx.car.app.model.Template
import androidx.car.app.sample.navigation.common.model.DemoScripts.getNavigateHome

/** Screen for showing entering a search and showing initial results.  */
class SearchScreen(
    carContext: CarContext,
    private val mSettingsAction: Action,
    private val mSurfaceRenderer: SurfaceRenderer
) : Screen(carContext) {
    private var mItemList = withNoResults(ItemList.Builder()).build()
    val mTitles: MutableList<String> = ArrayList()

    private var mSearchText: String? = null
    private val mFakeTitles: List<String> =
        ArrayList(mutableListOf("Starbucks", "Shell", "Costco", "Aldi", "Safeway"))

    override fun onGetTemplate(): Template {
        return SearchTemplate.Builder(
            object : SearchCallback {
                override fun onSearchTextChanged(searchText: String) {
                    doSearch(searchText)
                }

                override fun onSearchSubmitted(searchTerm: String) {
                    // When the user presses the search key use the top item in the list
                    // as the
                    // result and simulate as if the user had pressed that.
                    if (mTitles.size > 0) {
                        onClickSearch(mTitles[0])
                    }
                }
            })
            .setHeaderAction(Action.BACK)
            .setShowKeyboardByDefault(false)
            .setItemList(mItemList)
            .setInitialSearchText((if (mSearchText == null) "" else mSearchText)!!)
            .build()
    }

    fun doSearch(searchText: String) {
        mSearchText = searchText
        mTitles.clear()
        val builder = ItemList.Builder()
        if (searchText.isEmpty()) {
            withNoResults(builder)
        } else {
            // Create some fake data entries.
            for (title in mFakeTitles) {
                mTitles.add(title)
                builder.addItem(
                    Row.Builder()
                        .setTitle(title)
                        .setOnClickListener { onClickSearch(title) }
                        .build())
            }
        }
        mItemList = builder.build()
        invalidate()
        return
    }

    fun onClickSearch(searchText: String) {
        screenManager
            .pushForResult(
                RoutePreviewScreen(carContext, mSettingsAction, mSurfaceRenderer)
            ) { previewResult: Any? -> this.onRouteSelected(previewResult) }
    }

    private fun onRouteSelected(previewResult: Any?) {
        val previewIndex = if (previewResult == null) -1 else previewResult as Int
        if (previewIndex < 0) {
            return
        }
        // Start the same demo instructions. More will be added in the future.
        setResult(getNavigateHome(carContext))
        finish()
    }

    companion object {
        private fun withNoResults(builder: ItemList.Builder): ItemList.Builder {
            return builder.setNoItemsMessage("No Results")
        }
    }
}
