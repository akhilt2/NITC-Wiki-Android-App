package org.akhil.nitcwiki.talk.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TalkPageSeen constructor(
    @PrimaryKey val sha: String
)
