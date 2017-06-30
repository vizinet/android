// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm.model;

import java.util.Date;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Post extends RealmObject {

    @PrimaryKey
    public int postId;
    public String secretKey;
    public int mode;
    public User user;
    public Date date;
    public RealmList<Image> images;
    public float visualRange;
    public String description;
    public String location;
}
