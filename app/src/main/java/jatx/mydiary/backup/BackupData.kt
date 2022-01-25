package jatx.mydiary.backup

import jatx.mydiary.domain.models.Entry

data class BackupData(
    val list: List<Entry>
)
