package edu.wsu.lar.airpact_fire.data.interface_object;

/**
 * Interface representing database objects which can be "interfaced"
 * within an {@link android.app.Activity} and whose properties can be
 * changed and viewed.
 *
 * <p>Changes made to implementors of this interface will be reflected
 * in the database.</p>
 */
public interface InterfaceObject {
    Object getRaw();
}
