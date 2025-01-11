package com.zerodev.jetai.injection

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.zerodev.jetai.BuildConfig
import com.zerodev.jetai.db.ChatDao
import com.zerodev.jetai.db.ChatDatabase
import com.zerodev.jetai.db.ChatSessionDao
import com.zerodev.jetai.repository.ChatRepository
import com.zerodev.jetai.repository.ChatRepositoryImpl
import com.zerodev.jetai.repository.GenerativeModelRepository
import com.zerodev.jetai.repository.GenerativeModelRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {
    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideGenerativeModelRepository(generativeModel: GenerativeModel): GenerativeModelRepository {
        return GenerativeModelRepositoryImpl(generativeModel)
    }

    @Provides
    @Singleton
    fun provideChatRepository(chatSessionDao: ChatSessionDao, chatDao: ChatDao): ChatRepository {
        return ChatRepositoryImpl(chatSessionDao, chatDao)
    }

    @Provides
    @Singleton
    fun provideChatDatabase(@ApplicationContext context: Context): ChatDatabase {
        return ChatDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideChatSessionDao(chatDatabase: ChatDatabase) = chatDatabase.chatSessionDao()

    @Provides
    @Singleton
    fun provideChatDao(chatDatabase: ChatDatabase) = chatDatabase.chatDao()
}