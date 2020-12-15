package org.telegram.bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.telegram.bot.domain.entities.GoogleSearchResult;

public interface GoogleSearchResultRepository extends JpaRepository<GoogleSearchResult, Long> {
}
