package uz.javokhirdev.svocabulary.database.sets

import androidx.room.Embedded
import androidx.room.Relation
import uz.javokhirdev.svocabulary.database.cards.CardEntity

data class SetWithCards(
    @Embedded val set: SetEntity,
    @Relation(
        parentColumn = "set_id",
        entityColumn = "card_set_id"
    )
    val cards: List<CardEntity>
)
