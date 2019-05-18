package com.poker.schema

import com.poker.model.CardRankEnum
import com.poker.model.CardSuitEnum
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

object PlayerSchema

object PlayerSchemaV1 : MappedSchema(
        schemaFamily = PlayerSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentPlayerSchemaV1::class.java, CardSchemaV1::class.java
                )) {

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

    @Entity
    @Table(name = "player_state")
    class PersistentPlayerSchemaV1(

            @Column(name = "linear_id")
            val linearId: UUID,

            @Column(name = "player")
            val player: AbstractParty,

            @OneToMany(cascade = arrayOf(CascadeType.ALL), orphanRemoval = true)
            val cards: Set<CardSchemaV1>,

            //TODO To find a workaround for one to many of parties
/*            @OneToMany(cascade = arrayOf(CascadeType.ALL), mappedBy = "owningKey")
            val participants: Set<AbstractParty>,*/

            @OneToMany(cascade = arrayOf(CascadeType.ALL), orphanRemoval = true)
            val rankingList: List<CardSchemaV1>,

            @OneToOne(cascade = arrayOf(CascadeType.ALL))
            @JoinColumn(name = "highCard", referencedColumnName = "id")
            val highCard: CardSchemaV1

    ) : PersistentState()


}



