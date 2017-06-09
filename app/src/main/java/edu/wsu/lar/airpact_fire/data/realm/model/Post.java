package edu.wsu.lar.airpact_fire.data.realm.model;

import java.util.Date;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Post extends RealmObject {

    @PrimaryKey
    public String postId;           // i.e. secret key
    public User user;
    public Date date;
    public int mode;
    public String imageLocation;
    public RealmList<VisualRange> visualRanges;
    public boolean isSubmitted;     // Flags post as queued or submitted
    public RealmList<Target> targets;
    public Coordinate geoCoordinate;
    public String description;
    public String tags;
}
