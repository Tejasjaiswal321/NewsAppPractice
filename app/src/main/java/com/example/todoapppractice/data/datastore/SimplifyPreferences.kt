package com.example.todoapppractice.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "splitwise_prefs"
)

class SimplifyPreferences(context: Context) {

    private val IS_SIMPLIFY_ON = booleanPreferencesKey("is_simplify_on")

    private val dataStore = context.dataStore

    val isSimplifyOn: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[IS_SIMPLIFY_ON] ?: false
    }

    suspend fun toggleSimplify() {
        dataStore.edit { prefs ->
            val current = prefs[IS_SIMPLIFY_ON] ?: false
            prefs[IS_SIMPLIFY_ON] = !current
        }
    }
}
