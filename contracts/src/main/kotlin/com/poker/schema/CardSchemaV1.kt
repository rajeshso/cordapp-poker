package com.poker.schema

import com.poker.model.CardRankEnum
import com.poker.model.CardSuitEnum
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Entity
@Table(name = "cards")
class CardSchemaV1(
        @Id
        @GeneratedValue(generator = "system-uuid")
        @GenericGenerator(name = "system-uuid", strategy = "uuid2")
        var id: String? = null,

        @Enumerated(EnumType.STRING)
        val suit: CardSuitEnum,

        @Enumerated(EnumType.STRING)
        val rank: CardRankEnum)