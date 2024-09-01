package me.sankalpchauhan.synclearning.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.sankalpchauhan.synclearning.data.DefaultNotesRepository
import me.sankalpchauhan.synclearning.data.NotesRepository
import me.sankalpchauhan.synclearning.data.network.NotesService
import retrofit2.Retrofit

@InstallIn(SingletonComponent::class)
@Module
abstract class NotesModule {
    @Binds
    abstract fun bindNotesRepository(impl: DefaultNotesRepository): NotesRepository

    companion object{
        @Provides
        fun provideNotesService(
            retrofit: Retrofit
        ): NotesService {
            return retrofit.create(NotesService::class.java)
        }
    }
}