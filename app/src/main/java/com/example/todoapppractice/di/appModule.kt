package com.example.todoapppractice.di

import androidx.room.Room
import com.example.todoapppractice.data.datastore.SimplifyPreferences
import com.example.todoapppractice.data.db.SplitwiseDatabase
import com.example.todoapppractice.data.repository.BalanceRepository
import com.example.todoapppractice.data.repository.ExpenseRepository
import com.example.todoapppractice.data.repository.UserRepository
import com.example.todoapppractice.domain.usecase.AddExpenseUseCase
import com.example.todoapppractice.domain.usecase.DeleteExpenseUseCase
import com.example.todoapppractice.domain.usecase.DeleteSettlementUseCase
import com.example.todoapppractice.domain.usecase.GetBalancesUseCase
import com.example.todoapppractice.domain.usecase.GetHistoryUseCase
import com.example.todoapppractice.domain.usecase.GetPersonSummaryUseCase
import com.example.todoapppractice.domain.usecase.SettleBalanceUseCase
import com.example.todoapppractice.domain.usecase.SimplifyBalancesUseCase
import com.example.todoapppractice.ui.screen.add.AddExpenseViewModel
import com.example.todoapppractice.ui.screen.balances.BalancesViewModel
import com.example.todoapppractice.ui.screen.history.HistoryViewModel
import com.example.todoapppractice.ui.screen.person.PersonDetailViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // ── Database ──
    single {
        Room.databaseBuilder(
            androidContext(),
            SplitwiseDatabase::class.java,
            "splitwise_db"
        ).build()
    }

    // ── DAOs ──
    single { get<SplitwiseDatabase>().userDao() }
    single { get<SplitwiseDatabase>().expenseDao() }
    single { get<SplitwiseDatabase>().settlementDao() }

    // ── Repositories ──
    single { UserRepository(get()) }
    single { ExpenseRepository(get()) }
    single { BalanceRepository(get(), get(), get()) }

    // ── DataStore ──
    single { SimplifyPreferences(androidContext()) }

    // ── UseCases ──
    factory { AddExpenseUseCase(get(), get()) }
    factory { GetBalancesUseCase(get()) }
    factory { SimplifyBalancesUseCase() }
    factory { DeleteExpenseUseCase(get()) }
    factory { DeleteSettlementUseCase(get()) }
    factory { GetPersonSummaryUseCase(get(), get(), get()) }
    factory { SettleBalanceUseCase(get()) }
    factory { GetHistoryUseCase(get(), get(), get()) }

    // ── ViewModels ──
    viewModel { AddExpenseViewModel(get()) }
    viewModel { BalancesViewModel(get(), get()) }
    viewModel { HistoryViewModel(get(), get(), get()) }
    viewModel { params -> PersonDetailViewModel(params.get(), get(), get(), get()) }
}
