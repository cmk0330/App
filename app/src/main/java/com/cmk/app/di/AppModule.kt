package com.cmk.app.di

import org.koin.dsl.module

/**
 * @Author: romens
 * @Date: 2019-11-27 13:29
 * @Desc:
 */

val viewModelModule = module {
/**
 * ------添加Module对象-------
 *
 * single获取的实例为单例  single { HomeViewModel() }
 * factory实例工厂，每次获取都是新的实例对象  factory { HomeViewModel() }
 * viewModel获取的实例为ViewModel,并且具有ViewModel的功能  viewModel { HomeViewModel() }
 */

/**
 * ------获取factory对象------
 *
 * 使用[inject]获取FactoryModel实例
 * 其他教程中有说也可以使用[get]获取
 * 并且[inject]知识[lazy]版的[get]
 * 可以点开[inject]看到源码确实如此，但我这里是用[get]时提示
 * Missing 'getValue(FactoryActivity, KProperty<*>)' method on delegate of type 'FactoryModel'`
 * 推荐:
 * 获取实例时使用[inject],初始化Koin时使用[get]
 */
//    viewModel { BaseViewModel() }
//    viewModel { NewBaseViewModel() }
//    single { HomeViewModel(get()) }
//    single { LoginViewModel(get()) }
//    viewModel { WebViewModel(get()) }
//    viewModel { PlazaViewModel(get()) }
//    viewModel { WxViewModel(get()) }
//    viewModel { MineViewModel(get()) }
//    viewModel { SearchViewModel(get()) }
//    viewModel { DownUpViewModel(get()) }
}

val repositoryModule = module {
//        single { HomeRepository() }
//        single { PlazaRepository() }
//        single { CollectRepository() }
//        single { WxRepository() }
//        single { MineRepository() }
//        single { SearchRepository() }
//        single { DownUpRepository() }
    }

val appModule = listOf(
    viewModelModule,
    repositoryModule
)

