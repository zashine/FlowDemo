package com.example.flowdemo.module

import com.example.flowdemo.DefaultDispatchers
import com.example.flowdemo.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AppModule 类使用 @InstallIn(SingletonComponent::class) 注解，表示它提供的依赖项的作用域是整个应用程序。
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     *  provideDispatchers() 方法使用 @Provides 注解，表示它提供一个 DispatcherProvider 类型的实例
     */
    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DefaultDispatchers()
    }
}