package com.shms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="activities")
public class Activity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="user_id") private User userEntity;
 private String action;
 @Column(name="entity_type") private String entityType;
 @Column(name="entity_id") private Long entityId;
@Column(name = "description", columnDefinition = "TEXT")
private String description;
 @Column(name="ip_address") private String ipAddress;
 @Column(name="created_at") private LocalDateTime timestamp;
 @PrePersist void pre(){if(timestamp==null)timestamp=LocalDateTime.now();}
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public User getUserEntity(){return userEntity;} public void setUserEntity(User v){userEntity=v;}
 public String getAction(){return action;} public void setAction(String v){action=v;}
 public String getEntityType(){return entityType;} public void setEntityType(String v){entityType=v;}
 public Long getEntityId(){return entityId;} public void setEntityId(Long v){entityId=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public String getIpAddress(){return ipAddress;} public void setIpAddress(String v){ipAddress=v;}
 public LocalDateTime getTimestamp(){return timestamp;} public void setTimestamp(LocalDateTime v){timestamp=v;}
 @Transient public String getUser(){return userEntity==null?"System":userEntity.getFullName();}
 public void setUser(String ignored){}
 @Transient public String getType(){return action;} public void setType(String ignored){}
}
