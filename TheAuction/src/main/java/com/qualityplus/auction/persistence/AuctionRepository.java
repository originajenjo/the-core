package com.qualityplus.auction.persistence;

import com.qualityplus.auction.persistence.data.AuctionHouse;
import com.qualityplus.auction.persistence.data.User;
import com.qualityplus.assistant.lib.eu.okaeri.persistence.document.DocumentPersistence;
import com.qualityplus.assistant.lib.eu.okaeri.persistence.repository.DocumentRepository;
import com.qualityplus.assistant.lib.eu.okaeri.persistence.repository.annotation.DocumentCollection;
import com.qualityplus.assistant.lib.eu.okaeri.persistence.repository.annotation.DocumentIndex;
import com.qualityplus.assistant.lib.eu.okaeri.persistence.repository.annotation.DocumentPath;
import com.qualityplus.assistant.lib.eu.okaeri.platform.core.annotation.DependsOn;

import java.util.Optional;

@DependsOn(type = DocumentPersistence.class, name = "persistence")
@DocumentCollection(path = "auction", keyLength = 36, indexes = {
        @DocumentIndex(path = "name", maxLength = 24)
})
public interface AuctionRepository extends DocumentRepository<String, AuctionHouse> {

    @DocumentPath("name")
    Optional<User> findByName(String name);

    default AuctionHouse get() {
        AuctionHouse actionHouse = this.findOrCreateByPath("global");

        actionHouse.setName("global");

        return actionHouse;
    }
}
