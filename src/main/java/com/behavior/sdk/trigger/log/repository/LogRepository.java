package com.behavior.sdk.trigger.log.repository;

import com.behavior.sdk.trigger.log.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogRepository extends JpaRepository<Log, UUID> {



}
