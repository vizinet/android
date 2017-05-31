package edu.wsu.lar.airpact_fire.data.realm.model;

import java.util.Date;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Post extends RealmObject {

    @PrimaryKey
    public String postId;           // i.e. secret key
    public Date date;
    public String imageLocation;
    public double visualRange;
    public boolean isSubmitted;     // Flags post as queued or submitted
    public RealmList<Target> targets;
    public RealmList<Coordinate> geoCoordinates;
    public String description;
    public String tags;
}
