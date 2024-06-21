package com.sd.laborator.services

import com.sd.laborator.interfaces.ReplicationServiceInterface
import org.springframework.stereotype.Service

@Service
class ReplicationService : ReplicationServiceInterface{
    override fun replicateService(replications: Int) {
        // do magic :O
    }
}