package com.shms.service;

import com.shms.entity.Activity;
import com.shms.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void publish(String description, String user, String type) {

        // Save activity to DB
        Activity activity = new Activity();
        activity.setDescription(description);
        activity.setUser(user);
        activity.setType(type);

        activityRepository.save(activity);

        // Send activity through WebSocket
        messagingTemplate.convertAndSend("/topic/activities", activity);
    }

    public List<Activity> getRecentActivities() {
        return activityRepository.findTop10ByOrderByTimestampDesc();
    }
}
