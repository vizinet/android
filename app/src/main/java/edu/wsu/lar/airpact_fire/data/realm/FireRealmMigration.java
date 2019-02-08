// Copyright © 2019,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.data.realm;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Perform our Realm database migrations.
 *
 * Necessary for when we change our Realm models in any way. Reference the current schema version:
 * {@link edu.wsu.lar.airpact_fire.data.realm.manager.RealmDataManager.SCHEMA_VERSION}.
 *
 * Check out the <see href="https://realm.io/docs/java/latest#migrations">docs</see> for more
 * information.
 */
public class FireRealmMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        // DynamicRealm exposes an editable schema.
        RealmSchema schema = realm.getSchema();

        // Migrate to version 1: Add `User` fields for GPS locations.
        if (oldVersion == 0) {
            RealmObjectSchema userSchema = schema.get("User");
            if (!userSchema.hasField("lastLatitude"))
                userSchema.addField("lastLatitude", double.class);
            if (!userSchema.hasField("lastLongitude"))
                userSchema.addField("lastLongitude", double.class);
            oldVersion++;
        }

        if (oldVersion == 1) {
            // ...
        }
    }
}
