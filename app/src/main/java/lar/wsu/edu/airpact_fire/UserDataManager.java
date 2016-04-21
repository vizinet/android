package lar.wsu.edu.airpact_fire;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
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

// Manages storing and retrieving of data for users with XML.
//
// Stores user info when:
//  - User logs in (username, last login time, password)
//  - User selects indicator points (highXY[], lowXY[])
//  - User fills out post form and submits or queues (tags, estimated visual range, description)
//
// Retrieves user info when:
//  - LoginActivity (getLastUser --> username, password)
//  - ViewImageActivity ()
//  - AddPictureDetailsActivity ()
//
// Stores like this;
//  <app>
//      <mLastUser>[username]</mLastUser>
//      <[username]>
//           <imageName>[image name]</imageName>
//          ...
//      </[username]>
//      ...
//  </app>
public class UserDataManager {
    public static final String FILENAME = "sample_data_29.xml";//"user_data.xml";
    public static final String[] APP_ELEMENTS = {
            "lastUser",
            "user*"
    };
    public static final String[] USER_ELEMENTS = {
            "password",
            "imageName",
            "loginTime",
            "tags",
            "description",
            "estimatedVR",
            "highIndicatorX",
            "highIndicatorY",
            "lowIndicatorX",
            "lowIndicatorY"
    };
    public static final String[] TEST_USER_CRED = {"testUser", "testPassword"};

    private static Context mContext;
    private static String mLastUser;

    // 1. Set context
    // 2. Create skeleton XML data file if needed (with test user)
    public static void init(Context context) {
        try {
            // Set context
            mContext = context;

            // Get data from disk
            FileInputStream fis = mContext.getApplicationContext().openFileInput(FILENAME);
            Toast.makeText(mContext, "Data file '" + FILENAME + "' already exists.", Toast.LENGTH_LONG).show();
            fis.close();

        } catch (FileNotFoundException e) {
            // Data file doesn't exist. So, create skeleton file.
            e.printStackTrace();
            Toast.makeText(mContext, "Data file '" + FILENAME + "' does not exist. Creating now...", Toast.LENGTH_LONG).show();

            try {
                FileOutputStream fos = mContext.getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);

                // Truncate file to prepare for updated XML
                fos.getChannel().truncate(0);
                fos.getChannel().force(true);
                fos.getChannel().lock();

                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // Root element
                Document doc = docBuilder.newDocument();
                Element root = doc.createElement("app");
                doc.appendChild(root);

                // NOTE: We will have issue if a user is named "lastUser"
                // NOTE: Creating test user Luke, Luke

                // Last user elements starting with a testUser
                Element lastUser = doc.createElement("lastUser");
                lastUser.appendChild(doc.createTextNode(TEST_USER_CRED[0]));
                root.appendChild(lastUser);

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

                // TODO: The Rest?

                root.appendChild(testUser);

                // Write skeleton (as string) to file
                fos.write(domToString(doc).getBytes());

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

    // Source: http://stackoverflow.com/questions/5456680/xml-document-to-string
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

    // Source: http://stackoverflow.com/questions/562160/in-java-how-do-i-parse-xml-as-a-string-instead-of-a-file
    private static Document stringToDom(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    // Returns last user logged in
    public static String getLastUser() {
        if (mContext == null) return null;
        if (mLastUser != null) return mLastUser;

        try {
            // Convert xml file from disk into string
            FileInputStream fis = mContext.getApplicationContext().openFileInput(FILENAME);
            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = fis.read()) != -1){
                builder.append((char)ch);
            }

            // Close after we get content
            fis.close();

            //Toast.makeText(mContext, builder.toString(), Toast.LENGTH_LONG).show();

            // Build XML from said string and get last user element
            Document doc = stringToDom(builder.toString());
            Node lastUser = doc.getElementsByTagName("lastUser").item(0);

            // Return user
            return lastUser.getTextContent();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Set last user for future reference
    public static void setLastUser(String user) {
        if (mContext == null) return;
        //if (mLastUser != null) return;

        try {
            // Convert xml file from disk into string
            FileInputStream fis = mContext.getApplicationContext().openFileInput(FILENAME);
            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = fis.read()) != -1){
                builder.append((char)ch);
            }
            fis.close();

            // Build XML from said string and get last user element
            Document doc = stringToDom(builder.toString());
            Node lastUser = doc.getElementsByTagName("lastUser").item(0);
            lastUser.setTextContent(user);

            mLastUser = user;

            return;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;

    }

    // 1. Read XML file into Document object
    // 2. Check if user exists under root Element
    //      a. If so, return false;
    //      b. Else, append user to root Element
    private static boolean createUser(String user) {
        try {
            // 1.
            FileInputStream fis = mContext.getApplicationContext().openFileInput(FILENAME);
            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = fis.read()) != -1){
                builder.append((char)ch);
            }
            fis.close();

            Document doc = stringToDom(builder.toString());
            Element root = doc.getDocumentElement();

            // 2. a. If user doesn't exist yet
            if (doc.getElementsByTagName(user).getLength() == 0) {

                FileOutputStream fos = mContext.getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);

                // Truncate file to prepare for updated XML
                fos.getChannel().truncate(0);
                fos.getChannel().force(true);
                fos.getChannel().lock();

                Toast.makeText(mContext, "Creating new user '" + user + "'...", Toast.LENGTH_LONG).show();

                // 2. b.
                Element newUser = doc.createElement(user);
                for (int i = 0; i < USER_ELEMENTS.length; i++) {
                    Element newElement = doc.createElement(USER_ELEMENTS[i]);
                    newUser.appendChild(newElement);
                }
                root.appendChild(newUser);
                fos.write(domToString(doc).getBytes());

                //Toast.makeText(mContext, "Created user: " + domToString(doc), Toast.LENGTH_LONG).show();

                fos.close();
                return true;
            }

            Toast.makeText(mContext, "User '" + user + "' already exists", Toast.LENGTH_LONG).show();

            return false;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(mContext, "createUser(...) threw an exception", Toast.LENGTH_LONG).show();

        return false;
    }

    // TODO: Check if element is inside USER_ELEMENTS

    // TODO: Fix problem with setting user data

    public static boolean setUserData(String user, String element, String content) {
        // Add content into right element of user element
        if (mContext == null) return false;
        if (mLastUser != null) return false;

        // Create user if nonexistent
        boolean didUserExist = !createUser(user);

        try {
            // Convert xml file from disk into string
            FileInputStream fis = mContext.getApplicationContext().openFileInput(FILENAME);
            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = fis.read()) != -1){
                builder.append((char) ch);
            }
            fis.close();

            // Build XML from said string and get last user element
            Document doc = stringToDom(builder.toString());
            Element root = doc.getDocumentElement();
            Element currentUser = (Element) root.getElementsByTagName(user).item(0);
            Element currentUserSubElement = (Element) currentUser.getElementsByTagName(element).item(0);

            // Add content to element
            currentUserSubElement.appendChild(doc.createTextNode(content));

            Toast.makeText(mContext, "setUserData(user = " + user + ", element = " + element + ", content = " + content
                    + ")", Toast.LENGTH_LONG).show();

            Toast.makeText(mContext, "setUserData: " + domToString(doc), Toast.LENGTH_LONG).show();

            return didUserExist;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(mContext, "setUserData(...) threw an exception", Toast.LENGTH_LONG).show();

        return didUserExist;
    }

    // Get particular data field from user
    public static Object getUserData(String user, String element) {
        if (mContext == null) return null;
        if (mLastUser != null) return mLastUser;

        try {
            // Convert xml file from disk into string
            FileInputStream fis = mContext.getApplicationContext().openFileInput(FILENAME);
            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = fis.read()) != -1){
                builder.append((char)ch);
            }
            fis.close();

            //Toast.makeText(mContext, builder.toString(), Toast.LENGTH_LONG).show();

            // NOTE: Node ==> Element, AttributeNode, TextNode, etc.

            // Build XML from said string and get last user element
            Document doc = stringToDom(builder.toString());
            Element root = doc.getDocumentElement();
            Element currentUser = (Element) root.getElementsByTagName(user).item(0);
            NodeList currentUserSubElementChildren = currentUser.getElementsByTagName(element).item(0).getChildNodes();

            //if (currentUserSubElementChildren.getLength() == 0) return null;

            Object currentUserData = currentUser.getElementsByTagName(element).item(0).getTextContent();

            Log.i("UserDataManager", "getUserData(user = " + user + ", element = " + element
                    + ") => " + currentUserData);

            // Return user
            return currentUserData;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
