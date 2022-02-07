package uz.javokhirdev.svocabulary.data.db.sets

import androidx.room.Embedded
import androidx.room.Relation
import uz.javokhirdev.svocabulary.data.db.cards.CardEntity

data class SetWithCards(
    @Embedded val set: SetEntity,
    @Relation(
        parentColumn = "set_id",
        entityColumn = "card_id"
    )
    val cards: List<CardEntity>
)
