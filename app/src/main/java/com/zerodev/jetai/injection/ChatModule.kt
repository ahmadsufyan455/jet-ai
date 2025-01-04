package com.zerodev.jetai.injection

import com.google.ai.client.generativeai.GenerativeModel
import com.zerodev.jetai.BuildConfig
import com.zerodev.jetai.repository.ChatRepository
import com.zerodev.jetai.repository.ChatRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {
    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideChatRepository(generativeModel: GenerativeModel): ChatRepository {
        return ChatRepositoryImpl(generativeModel)
    }
}