import com.tgyuu.build.logic.configureHiltAndroid
import com.tgyuu.build.logic.configureKotlinAndroid
import com.tgyuu.build.logic.configureTestAndroid

plugins {
    id("com.android.library")
}

configureKotlinAndroid()
configureTestAndroid()
configureHiltAndroid()
