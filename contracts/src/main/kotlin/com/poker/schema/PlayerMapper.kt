package com.poker.schema

import com.poker.model.Card
import com.poker.states.PlayerState
import net.corda.core.identity.AbstractParty
import org.mapstruct.*
import org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT


@Mapper(nullValueMappingStrategy = RETURN_DEFAULT, unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface PlayerMapper     {

    @Mappings(Mapping(target = "linearId", source = "linearId"),
            Mapping(target = "cards", source = "cards", qualifiedByName = ["toCardSchemaV1Set"]),
            Mapping(target = "playerA", source = "participants", qualifiedByName = ["toPlayerA"]),
            Mapping(target = "playerB", source = "participants", qualifiedByName = ["toPlayerB"]),
            Mapping(target = "rankingEnum", source = "rankingEnum"),
            Mapping(target = "rankingList", source = "rankingList", qualifiedByName = ["toRankingList"]),
            Mapping(target = "highCard", source = "highCard", qualifiedByName = ["toCardSchemaV1"]))
    fun toPersistentPlayerSchemaV1(playerState: PlayerState): PlayerSchemaV1.PersistentPlayerSchemaV1

    @Named("toCardSchemaV1Set")
    fun toCardSchemaV1Set(cards: Array<Card?>) : Set<CardSchemaV1>

    @Named("toCardSchemaV1")
    @InheritInverseConfiguration
    fun toCardSchemaV1(card: Card) : CardSchemaV1

    @Named("toRankingList")
    fun toRankingList(cards: List<Card>) : List<CardSchemaV1>

    @Named("toPlayerA")
    fun toPlayerA(participants: List<AbstractParty>): AbstractParty = participants.get(0)

    @Named("toPlayerB")
    fun toPlayerB(participants: List<AbstractParty>): AbstractParty = participants.get(1)
}
