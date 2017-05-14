package edu.wsu.lar.airpact_fire.data.manager;

// TODO: Adapt the below JavaDoc

import android.content.Context;

import edu.wsu.lar.airpact_fire.data.model.User;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 33    * This class consists exclusively of static methods that operate on or return
 34    * collections.  It contains polymorphic algorithms that operate on
 35    * collections, "wrappers", which return a new collection backed by a
 36    * specified collection, and a few other odds and ends.
 37    *
 38    * <p>The methods of this class all throw a <tt>NullPointerException</tt>
 39    * if the collections or class objects provided to them are null.
 40    *
 41    * <p>The documentation for the polymorphic algorithms contained in this class
 42    * generally includes a brief description of the <i>implementation</i>.  Such
 43    * descriptions should be regarded as <i>implementation notes</i>, rather than
 44    * parts of the <i>specification</i>.  Implementors should feel free to
 45    * substitute other algorithms, so long as the specification itself is adhered
 46    * to.  (For example, the algorithm used by <tt>sort</tt> does not have to be
 47    * a mergesort, but it does have to be <i>stable</i>.)
 48    *
 49    * <p>The "destructive" algorithms contained in this class, that is, the
 50    * algorithms that modify the collection on which they operate, are specified
 51    * to throw <tt>UnsupportedOperationException</tt> if the collection does not
 52    * support the appropriate mutation primitive(s), such as the <tt>set</tt>
 53    * method.  These algorithms may, but are not required to, throw this
 54    * exception if an invocation would have no effect on the collection.  For
 55    * example, invoking the <tt>sort</tt> method on an unmodifiable list that is
 56    * already sorted may or may not throw <tt>UnsupportedOperationException</tt>.
 57    *
 58    * <p>This class is a member of the
 59    * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 60    * Java Collections Framework</a>.
 61    *
 62    * @author  Josh Bloch
 63    * @author  Neal Gafter
 64    * @see     Collection
 65    * @see     Set
 66    * @see     List
 67    * @see     Map
 68    * @since   1.2
 69    */

// Class for handling
public class RealmDataManager implements DataManager {

    private Realm mRealm;

    public RealmDataManager(Context context) {

        // Initialize Realm
        Realm.init(context);

        // Get a Realm instance for this thread
        mRealm = Realm.getDefaultInstance();
    }

    public void init() { }

    @Override
    public boolean isAuthenticatedUser(String username, String password) {
        final RealmResults<User> matchingUsers = mRealm.where(User.class)
                .equalTo("username", username)
                .equalTo("password", password)
                .findAll();
        return matchingUsers.size() == 0 ? false : true;
    }

    @Override
    public void createAndAddUser(String username, String password) {
        User user = mRealm.createObject(User.class);
        user.username = username;
        user.password = password;
        mRealm.commitTransaction();
    }

    @Override
    public User getRecentUser() {
        return null;
    }

    @Override
    public void startSession() {
        // TODO: Init session with user
    }
}
