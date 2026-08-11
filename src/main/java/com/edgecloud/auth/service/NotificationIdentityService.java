package com.edgecloud.auth.service;

import java.util.List;

import com.edgecloud.auth.dto.NotificationIdentityRequest;
import com.edgecloud.auth.dto.NotificationIdentityResponse;

public interface NotificationIdentityService {

    /** Unknown user IDs are omitted; results contain only authoritative stored identities. */
    List<NotificationIdentityResponse> resolve(NotificationIdentityRequest request);
}
