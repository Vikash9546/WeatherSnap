package com.weathersnap.app.data.repository

import com.weathersnap.app.data.local.dao.ReportDraftDao
import com.weathersnap.app.data.local.entity.ReportDraftEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportDraftRepository @Inject constructor(
    private val dao: ReportDraftDao
) {
    suspend fun getDraft(): ReportDraftEntity? = withContext(Dispatchers.IO) {
        dao.getDraft()
    }

    suspend fun saveDraft(draft: ReportDraftEntity) = withContext(Dispatchers.IO) {
        dao.saveDraft(draft)
    }

    suspend fun clearDraft() = withContext(Dispatchers.IO) {
        dao.clearDraft()
    }
}
