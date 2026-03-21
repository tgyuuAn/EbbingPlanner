package com.tgyuu.home.di

import com.tgyuu.home.graph.addtodo.AddTodoViewModel
import com.tgyuu.home.graph.editdate.EditDateViewModel
import com.tgyuu.home.graph.edittodo.EditTodoViewModel
import com.tgyuu.home.graph.main.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddTodoViewModel)
    viewModelOf(::EditTodoViewModel)
    viewModelOf(::EditDateViewModel)
}
