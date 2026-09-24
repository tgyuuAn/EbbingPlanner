package com.tgyuu.memo.di

import com.tgyuu.memo.graph.addmemo.AddMemoViewModel
import com.tgyuu.memo.graph.editmemo.EditMemoViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val memoModule = module {
    viewModelOf(::AddMemoViewModel)
    viewModelOf(::EditMemoViewModel)
}
