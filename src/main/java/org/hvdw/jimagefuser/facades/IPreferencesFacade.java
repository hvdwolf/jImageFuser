package org.hvdw.jimagefuser.facades;

public interface IPreferencesFacade {
    enum PreferenceKey {
        USE_LAST_OPENED_FOLDER("uselastopenedfolder"),
        LAST_OPENED_FOLDER("lastopenedfolder"),
        DEFAULT_START_FOLDER("defaultstartfolder"),
        VERSION_CHECK("versioncheck"),
        LAST_APPLICATION_VERSION("applicationVersion"),
        EXIFTOOL_PATH("exiftool"),
        PREFERRED_APP_LANGUAGE("System default"),
        PREFERRED_FILEDIALOG("jfilechooser"),
        LOG_LEVEL("loglevel"),
        USER_DEFINED_FILE_FILTER("userdefinedfilefilter"),
        PRESERVE_MODIFY_DATE("preservemodifydate"),
        DUAL_COLUMN("dualcolumn"),
        GUI_WIDTH("guiwidth"), //rootpanel width
        GUI_HEIGHT("guiheight"), //rootpanel height
        SPLITPANEL_POSITION("splitpanelpostion"), //percentual position left/right splitpane

        ;

        public final String key;
        PreferenceKey(String key) {
            this.key = key;
        }
    }

    boolean keyIsSet(PreferenceKey key);

    String getByKey(PreferenceKey key, int i);
    String getByKey(PreferenceKey key, String defaultValue);
    boolean getByKey(PreferenceKey key, boolean defaultValue);
    void storeByKey(PreferenceKey key, String value);
    void storeByKey(PreferenceKey key, boolean value);

    IPreferencesFacade defaultInstance = PreferencesFacade.thisFacade;
}
