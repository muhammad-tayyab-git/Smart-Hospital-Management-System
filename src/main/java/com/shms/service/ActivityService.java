package com.shms.service;
import com.shms.entity.Activity;
import com.shms.entity.User;
import com.shms.repository.ActivityRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ActivityService {
 private final ActivityRepository repo; private final SimpMessagingTemplate messaging;
 public ActivityService(ActivityRepository repo,SimpMessagingTemplate messaging){this.repo=repo;this.messaging=messaging;}
 public void publish(String description,String userName,String type){
   Activity a=new Activity(); a.setAction(type==null?"ACTIVITY":type.toUpperCase()); a.setDescription(description); repo.save(a); messaging.convertAndSend("/topic/activities",a);
 }
 public void publish(String description, User user, String type){
   Activity a=new Activity(); a.setUserEntity(user); a.setAction(type==null?"ACTIVITY":type.toUpperCase()); a.setDescription(description); repo.save(a); messaging.convertAndSend("/topic/activities",a);
 }
 public List<Activity> getRecentActivities(){return repo.findTop10ByOrderByTimestampDesc();}
}
