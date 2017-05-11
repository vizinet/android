package edu.wsu.lar.airpact_fire.data.model;

import java.util.Date;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Post extends RealmObject {

    @PrimaryKey
    public String postId;
    public Date date;
    public String imageLocation;
    public double estimatedVisualRange;
    // TODO: The rest
}
