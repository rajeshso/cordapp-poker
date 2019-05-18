package com.poker.schema

import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.*

object PlayerSchema

object PlayerSchemaV1 : MappedSchema(
        schemaFamily = PlayerSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentPlayerSchemaV1::class.java, CardSchemaV1::class.java
        )) {
    @Entity
    @Table(name = "player_state")
    class PersistentPlayerSchemaV1(

            @Column(name = "linear_id")
            val linearId: UUID,

            @Column(name = "player")
            val player: AbstractParty,

            @OneToMany(cascade = arrayOf(CascadeType.ALL), orphanRemoval = true)
            val cards: Set<CardSchemaV1>,

            //Note: A temproary workaround could be that we support only two players
            //TODO: net.corda.nodeapi.internal.persistence.HibernateConfigException: Could not create Hibernate configuration: Use of @OneToMany or @ManyToMany targeting an unmapped class: com.poker.schema.GameSchemaV1$PersistentGameSchemaV1.players[java.lang.String]
//            @OneToMany(cascade = arrayOf(CascadeType.ALL), orphanRemoval = true)
//            val participants: Set<String>,
            @Column(name = "playerA")
            val playerA: AbstractParty,

            @Column(name = "playerB")
            val playerB: AbstractParty,

            @OneToMany(cascade = arrayOf(CascadeType.ALL), orphanRemoval = true)
            val rankingList: List<CardSchemaV1>,

            @OneToOne(cascade = arrayOf(CascadeType.ALL))
            @JoinColumn(name = "highCard", referencedColumnName = "id")
            val highCard: CardSchemaV1

    ) : PersistentState()
}



