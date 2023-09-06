package io.portx.cbs.connector.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import io.portx.cbs.connector.idempotence.IdempotentSession;

// import java.util.Optional;
import java.util.UUID;

@Repository
public  interface IdempotentSessionRepository extends JpaRepository<IdempotentSession, Long>{

  void deleteByClientRequestId(UUID sessionId);

  IdempotentSession findIdempotentSessionByClientRequestId(UUID clientRequestId);
}