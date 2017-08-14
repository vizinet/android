// TODO: Background service for requesting more accurate and current GPS coordinates (as per https://developer.android.com/guide/topics/location/strategies.html)
// TODO: Enable dynamic loading and caching system for gallery
// TODO: Make sure posts get destroyed if someone exits without completing it.
// TODO: Button feedback for our custom buttons

// TODO: Allow user to add tags to post like "fog," "rainy," "smokey," etc. This will give us good metadeta.
// TODO: AppManager: Method which adds service to monitor app state (namely, exit, so we can call onAppEnd)
// TODO: Add copyright to every source file
// TODO: Add LICENCE.TXT
// TODO: Look into making custom image-capture activity
// TODO: Adapt to new research-based, clean design scheme
// TODO: Splash screen
// TODO: Add "alpha" print to logo
// TODO: Have auto-fill for login page and post page
// TODO: Better image storage - perhaps Make separate files for the image, linked to by the <image /> tag by the user (e.g. "test_image.jpg")
// TODO: Internet status (color-coded) on home, view gallery option (web browser), as well as last login time and other stats
// TODO: Custom Toast display, to make it more obvious to user
// TODO: More responsive buttons
// TODO: "Last logged in X days ago" on home screen
// TODO: Add notification when we have connection to server, and not just internet access. Although,
//  we still want to know about internet access so we can know when to queue posts? We could just check
//  to see if
// TODO: Add notifications for when server comes up. Have a batch of checks and actions done by the app occasionally,
//  say, every 3 hours, like posting for backlogged posts. Also have frequent checks while app is running
//  that give toast/notifications when server is up. Maybe do something with notifications as well.
// TODO: Show post trends (location, time, etc.)
// TODO: Allow user to view post coordinates in Google Maps
// TODO: When queued post is submitted, don't create a new post entirely for SQL database. Rather, just change the original.
// TODO: Have loading icon for SignInActivity on first-time install (because it takes a little while).
// TODO: Be able to handle null inputs on PictureDetailsActivity
// TODO: Be able to check for valid inputs on same activity.
// TODO: If a post has been queued, allow users to edit a limited amount of fields, like description, VR, and location
// TODO: Know if post is uncompleted -> notify user it has been drafted in toast and on home screen (this means we might
//  want to use SQL for everything and populate each SQL post gradually. Also, it means we'll have the following identifiers