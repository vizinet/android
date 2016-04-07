package lar.wsu.edu.airpact_fire;

import java.util.Date;

// Object for handling our picture posts, along with all the metadata
public class Post {

    // Publicly accessible variables
    public String
            ImageLocation,
            Description;
    public String[] Tags;
    public float EstimatedVisualRange;
    public float[] LatitudeLongitude;
    // TODO public final Bitmap Image;
    public Date Time;

    // Empty constructor
    Post()
    {

    }

    // Constructor storing final values
    Post(String imageLocation, String description, String[] tags, float estimatedVisualRange)
    {
        ImageLocation = imageLocation;
        Description = description;
        EstimatedVisualRange = estimatedVisualRange;
        Tags = tags;
        // TODO get geolcation, time captured, etc. from ExifInterface
        Time = new Date();
        LatitudeLongitude = new float[]{};
    }
}
