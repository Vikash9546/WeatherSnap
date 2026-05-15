package com.weathersnap.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weathersnap.app.data.local.entity.ReportDraftEntity

@Dao
interface ReportDraftDao {

    @Query("SELECT * FROM report_drafts WHERE id = 1 LIMIT 1")
    suspend fun getDraft(): ReportDraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: ReportDraftEntity)

    @Query("DELETE FROM report_drafts WHERE id = 1")
    suspend fun clearDraft()
}
