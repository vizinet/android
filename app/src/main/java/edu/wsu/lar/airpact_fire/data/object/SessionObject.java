// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.object;

import java.util.Date;
import edu.wsu.lar.airpact_fire.app.Reference;

/**
 * Usage session object interface for UI to deal handle.
 *
 * <p>Changes made to implementors of this interface will be reflected
 * in the database.</p>
 *
 * @author  Luke Weber
 * @since   0.9
 */
public interface SessionObject {

    UserObject getUser();

    Date getStartDate();
    Date getEndDate();
    void setEndDate(Date value);

    Object getRaw();
}
