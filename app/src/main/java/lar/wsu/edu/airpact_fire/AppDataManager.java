package lar.wsu.edu.airpact_fire;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

// Manages storing and retrieving of data for user preferences and such with XML
//
// Stores algorithm names
//
// Stores user info when:
//  - User logs in (username, last login time, password)
//  - User selects indicator points (highXY[], lowXY[])
//  - User fills out post form and submits or queues (tags, estimated visual range, description)
//
// Retrieves user info when:
//  - LoginActivity (getRecentUser --> username, password)
//  - ViewImageActivity ()
//  - AddPictureDetailsActivity ()
//
// Stores like this:
//  <app>
//      <meta>
//          <mLastUser>[username]</mLastUser>
//          ...
//      </meta>
//      <users>
//          <[username]>
//              <image>[image contents]</image>
//              ...
//          </[username]>
//      </users>
//      <scripts>
//          <[algorithm name] />
//          ...
//      </scripts>
//  </app>

public class AppDataManager {
    // NOTE: Version is changed as fields are amended
    public static final String FILENAME = "app_data_v3.xml";
    public static final String[] APP_ELEMENTS = {
            "meta",
            "users",
            "scripts"
    };
    // TODO: Turn this into an enum and make each get/set function take an enum rather than string
    // NOTE: This list is an extension of Post.USER_FIELDS
    public static final String[] USER_ELEMENTS = {
            "isAuth",
            "password",
            "secretKey",
            "image",
            "tags",
            "description",
            "visualRangeOne",
            "visualRangeTwo",
            "highX",
            "highY",
            "lowX",
            "lowY",
            "highColor",
            "lowColor",
            "geoX",
            "geoY",
            "distanceUnits",
            "algorithmType",
            "firstLoginTime",
            "lastLoginTime"
    };
    public static final String[] TEST_USER_CRED = {"testuser", "1234567890"};

    // Activity context which allows us to do things on UI, mainly for
    // debugging purposes
    private static Context mContext;

    // Cached string of last user so we don't have to go back to disk!
    private static String mLastUser;

    // Cached copy of XML data, so we don't have to keep reading it from disk;
    // We need to keep this copy fresh and write back changes immediately after
    private static Document mLocalXML;

    // Returns local XML and repopulates local copy as needed
    private static Document getLocalXML() {

        Document doc = null;

        try {
            // Repopulate XML doc
            if (mLocalXML == null) {
                // Convert xml file from disk into string
                FileInputStream fis = mContext.getApplicationContext().openFileInput(FILENAME);
                StringBuilder builder = new StringBuilder();
                int ch;
                while ((ch = fis.read()) != -1) {
                    builder.append((char) ch);
                }
                fis.close();

                // Convert string into XML
                doc = stringToDom(builder.toString());

                // Set global
                mLocalXML = doc;
            } else {
                // User fresh, local copy
                doc = mLocalXML;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return doc;
    }
    // Uses local XML copy and writes those changes back to disk
    private static void writeLocalXML() {
        Document doc = mLocalXML;
        FileOutputStream fos = null;
        try {
            fos = mContext.getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.getChannel().truncate(0);
            fos.write(domToString(doc).getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Log the XML
        //DebugManager.printLog(getXML());
    }

    // 1. Set context
    // 2. Create skeleton XML data file if needed (with test user)
    public static void init(Context context) {
        try {
            // Set context
            mContext = context;

            // Get data from disk
            FileInputStream fis = mContext.getApplicationContext().openFileInput(FILENAME);
            fis.close();

        } catch (FileNotFoundException e) {
            // Data file doesn't exist. So, create skeleton file.
            e.printStackTrace();
            Toast.makeText(mContext, "Initializing app database...", Toast.LENGTH_LONG).show();

            try {
                FileOutputStream fos = mContext.getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);

                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // Root element
                Document doc = docBuilder.newDocument();
                Element root = doc.createElement("app");
                doc.appendChild(root);

                // Three main sub-elements of app
                Element metaElement = doc.createElement("meta");
                root.appendChild(metaElement);
                Element usersElement = doc.createElement("users");
                root.appendChild(usersElement);
                Element scriptsElement = doc.createElement("scripts");
                root.appendChild(scriptsElement);

                // ADDING DUMMY SCRIPT NAME (to be wiped by ScriptManager)
                Element dummyAlgElement = doc.createElement("dummy_alg_0");
                scriptsElement.appendChild(dummyAlgElement);

                // CREATING TEST USER

                // Last user elements starting with a testUser
                Element lastUser = doc.createElement("lastUser");
                lastUser.appendChild(doc.createTextNode(TEST_USER_CRED[0]));
                metaElement.appendChild(lastUser);

                // Create testUser data section
                Element testUser = doc.createElement(TEST_USER_CRED[0]);

                // Password
                Element testPassword = doc.createElement("password");
                testPassword.appendChild(doc.createTextNode(TEST_USER_CRED[1]));
                testUser.appendChild(testPassword);

                // Login time
                Element testLoginTime = doc.createElement("loginTime");
                testLoginTime.appendChild(doc.createTextNode((new Date()).toString()));
                testUser.appendChild(testLoginTime);

                // Tags
                Element testTags = doc.createElement("tags");
                testTags.appendChild(doc.createTextNode("testTag,testwow,testohboy"));
                testUser.appendChild(testTags);

                // Description
                Element testDescription = doc.createElement("description");
                testDescription.appendChild(doc.createTextNode("This is a mundane and superfluous post description."));
                testUser.appendChild(testDescription);

                // Low indicators
                Element testLowIndicatorX = doc.createElement("lowIndicatorX");
                testLowIndicatorX.appendChild(doc.createTextNode("200.0"));
                testUser.appendChild(testLowIndicatorX);
                Element testLowIndicatorY = doc.createElement("lowIndicatorY");
                testLowIndicatorY.appendChild(doc.createTextNode("200.0"));
                testUser.appendChild(testLowIndicatorY);

                // High indicators
                Element testHighIndicatorX = doc.createElement("highIndicatorX");
                testHighIndicatorX.appendChild(doc.createTextNode("100.0"));
                testUser.appendChild(testHighIndicatorX);
                Element testHighIndicatorY = doc.createElement("highIndicatorY");
                testHighIndicatorY.appendChild(doc.createTextNode("100.0"));
                testUser.appendChild(testHighIndicatorY);

                usersElement.appendChild(testUser);

                // Write skeleton (as string) to file
                fos.write(domToString(doc).getBytes());

                Toast.makeText(mContext, domToString(doc), Toast.LENGTH_LONG).show();

                fos.flush();
                fos.close();

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ParserConfigurationException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: Move to Util
    // SOURCE: http://stackoverflow.com/questions/5456680/xml-document-to-string
    private static String domToString(Node doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString().replaceAll("\n|\r", "");

            return output;
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }
    // SOURCE: http://stackoverflow.com/questions/562160/in-java-how-do-i-parse-xml-as-a-string-instead-of-a-file
    private static Document stringToDom(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    /**
     * SCRIPT METHODS (Deprecated)
     **/

    // Remove all algorithm names from app storage
    public static void clearScriptNames() {
        if (mContext == null) return;

        Document doc = getLocalXML();
        Element scriptsElement = (Element) doc.getElementsByTagName("scripts").item(0);
        Util.removeNodeChildren(scriptsElement);

        Log.println(Log.DEBUG, "SCRIPTNAMES", "Clear script names. Now we have "
                + scriptsElement.getChildNodes().getLength()
                + " script names");

        writeLocalXML();
    }

    // Returns true or false depending on if script doesn't exist or not, respectively
    public static boolean addScriptName(String name) {
        Log.println(Log.DEBUG, "SCRIPTNAMES", "Attempting to add script name = " + name);

        Document doc = getLocalXML();
        Element scriptsElement = (Element) doc.getElementsByTagName("scripts").item(0);
        // Stop if we find that script name is already there
        if (scriptsElement.getElementsByTagName(name).getLength() > 0) return false;
        scriptsElement.appendChild(doc.createElement(name));

        Log.println(Log.DEBUG, "SCRIPTNAMES", "Script name added!");

        writeLocalXML();
        return true;
    }

    public static ArrayList<String> getScriptNames() {
        ArrayList<String> scriptNames = new ArrayList<>();

        Document doc = getLocalXML();
        Element scriptsElement = (Element) doc.getElementsByTagName("scripts").item(0);

        NodeList nameNodes = scriptsElement.getChildNodes();
        for (int i = 0; i < nameNodes.getLength(); i++) {
            scriptNames.add(((Element) (nameNodes.item(i))).getTagName());
        }

        return scriptNames;
    }

    /**
     * USER METHODS
     **/

    // Returns last user logged
    public static String getRecentUser() {
        if (mContext == null) return null;
        if (mLastUser != null) return mLastUser; // Return cached info

        // XML data
        Document doc = getLocalXML();

        // Get last user
        Node metaNode = doc.getElementsByTagName("meta").item(0);
        Node lastUser = ((Element) metaNode).getElementsByTagName("lastUser").item(0);

        // Return user
        return lastUser.getTextContent();
    }
    // Set last user for future reference
    public static void setRecentUser(String user) {
        if (mContext == null) return;

        // Get XML data
        Document doc = getLocalXML();

        // Set last user in XML and local var
        Node metaNode = doc.getElementsByTagName("meta").item(0);
        Node lastUser = ((Element) metaNode).getElementsByTagName("lastUser").item(0);
        lastUser.setTextContent(user);
        mLastUser = user;

        // Write changes back
        writeLocalXML();
    }

    // 1. Read XML file into Document object
    // 2. Check if user exists under root Element
    //      a. If so, return false;
    //      b. Else, append user to root Element
    private static boolean createUser(String user) {
        // 1.
        Document doc = getLocalXML();
        Element root = doc.getDocumentElement(); // "app"
        Element usersElement = (Element) root.getElementsByTagName("users").item(0);

        // 2. a. If user doesn't exist yet
        if (usersElement.getElementsByTagName(user).getLength() == 0) {
            // 2. b.
            Element newUser = doc.createElement(user);
            for (int i = 0; i < USER_ELEMENTS.length; i++) {
                Element newElement = doc.createElement(USER_ELEMENTS[i]);
                newUser.appendChild(newElement);
            }
            usersElement.appendChild(newUser);

            // Write back
            writeLocalXML();
            return true;
        }

        return false;

    }

    // Overload - skip unnecessary shit
    public static boolean setUserData(String element, String content) {
        return setUserData(getRecentUser(), element, content);
    }
    public static boolean setUserData(String user, String element, String content) {
        // Add content into right element of user element
        if (mContext == null) return false;

        //DebugManager.printLog("SetUserData: User = " + user + ", Element = " + element + ", Content = " + content);

        // Create user if nonexistent
        boolean didUserExist = !createUser(user);

        // Get XML
        Document doc = getLocalXML();

        // Build XML from said string and get last user element
        Element root = doc.getDocumentElement();
        Element usersElement = (Element) root.getElementsByTagName("users").item(0);
        Element currentUser = (Element) usersElement.getElementsByTagName(user).item(0);
        Element currentUserSubElement = (Element) currentUser.getElementsByTagName(element).item(0);

        // Add content to element
        currentUserSubElement.setTextContent(content);

        // Write XML back
        writeLocalXML();

        return didUserExist;
    }
    // Get particular data field from user
    public static String getUserData(String element) {
        return getUserData(getRecentUser(), element);
    }
    public static String getUserData(String user, String element) {
        if (mContext == null) return null;

        //DebugManager.printLog("GetUserData: User = " + user + ", Element = " + element);

        // Get XML
        Document doc = getLocalXML();

        // Get user data
        Element root = doc.getDocumentElement();
        Element usersElement = (Element) root.getElementsByTagName("users").item(0);
        Element currentUser = (Element) usersElement.getElementsByTagName(user).item(0);
        Element userDataElement = (Element) currentUser.getElementsByTagName(element).item(0);

        // If no element or no data in element, kill
        if (userDataElement == null ||
                !userDataElement.hasChildNodes()) return null;

        // Return data
        return userDataElement.getTextContent();
    }

    public static String getXML() {
        // Convert xml file from disk into string
        try {
            FileInputStream fis = mContext.getApplicationContext().openFileInput(FILENAME);

            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = fis.read()) != -1){
                builder.append((char)ch);
            }
            fis.close();

            return builder.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String();
    }
}
