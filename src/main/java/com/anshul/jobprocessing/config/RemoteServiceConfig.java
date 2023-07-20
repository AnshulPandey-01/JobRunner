package com.anshul.jobprocessing.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RemoteServiceConfig {
    private String triggerActionUrl;
    private String actionStatusUrl;
    private String actionResponseUrl;

    public RemoteServiceConfig(@Value("${job.triggerAction.url}") String triggerActionUrl,
                               @Value("${job.actionStatus.url}") String actionStatusUrl,
                               @Value("${job.actionResponse.url}") String actionResponseUrl) {
        this.triggerActionUrl = triggerActionUrl;
        this.actionStatusUrl = actionStatusUrl;
        this.actionResponseUrl = actionResponseUrl;
    }
}
