// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.server.contract;

import org.json.simple.JSONObject;
import edu.wsu.lar.airpact_fire.data.object.PostObject;

public interface ServerContract {

    String DATE_FORMAT = "yyyy.MM.dd.HH.mm.ss";

    JSONObject toJSON(PostObject postObject);
}
