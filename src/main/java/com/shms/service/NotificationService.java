package com.shms.service;

import com.shms.entity.Notification;
import com.shms.entity.User;
import com.shms.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notifications;
    public NotificationService(NotificationRepository notifications){this.notifications=notifications;}
    public List<Notification> unread(Long userId){return notifications.findByUserId(userId).stream().filter(n->!Boolean.TRUE.equals(n.getRead())).toList();}
    @Transactional public Notification send(User user,String title,String message,Notification.Type type){Notification n=new Notification();n.setUser(user);n.setTitle(title);n.setMessage(message);n.setType(type==null?Notification.Type.INFO:type);return notifications.save(n);}
    @Transactional public void markRead(Notification n){n.setRead(true);notifications.save(n);}
}
