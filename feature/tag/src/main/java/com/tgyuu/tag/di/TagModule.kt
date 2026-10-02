package com.tgyuu.tag.di

import com.tgyuu.tag.graph.addtag.AddTagViewModel
import com.tgyuu.tag.graph.edittag.EditTagViewModel
import com.tgyuu.tag.graph.main.TagViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val tagModule = module {
    viewModelOf(::AddTagViewModel)
    viewModelOf(::TagViewModel)
    viewModelOf(::EditTagViewModel)
}
