package edu.wsu.lar.airpact_fire.app;

import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.manager.DataManager;
import edu.wsu.lar.airpact_fire.debug.manager.DebugManager;
import edu.wsu.lar.airpact_fire.image.manager.ImageManager;
import edu.wsu.lar.airpact_fire.server.manager.ServerManager;

// TODO: Something to pass around all activities (and no re-initializing each time)
public class AppInstance {
    public ServerManager serverManager;
    public DebugManager debugManager;
    public AppManager appManager;
    public DataManager dataManager;
    public ImageManager imageManager;
}
