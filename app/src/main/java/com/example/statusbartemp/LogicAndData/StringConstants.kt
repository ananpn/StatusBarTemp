package com.example.statusbartemp.LogicAndData

class StringConstants {
    companion object {
        
        //Strings *********************************************************************
        
        val INTENT_ACTION = "SBT_Update_Temperature"
        val INTENT_NAME = "Status_Bar_Temp"
        val INTENT_MESSAGE = "SBT_Work"
        //val NOTIFICATION = "notification"
        //val NOTIFICATION_LIST = "notification list"
        val CHANNEL_ID = "Status Bar Temp Channel"
        val CHANNEL_NAME = "Status Bar Temperature"

        val WINDOW_ALARM_ACTION = "SBT Window Alarm Work"
        
        //Placeholders
        const val EMPTY_STRING = ""
        
        //Input
        const val GIVE_LOCATION_NAME = "Name of location."
        const val GIVE_LATITUDE = "Latitude (e.g 62.601)"
        const val GIVE_LONGITUDE = "Longitude (e.g 29.745)"
        const val SAVE_LOCATION_TITLE = "Give a location."
        
        //Buttons
        const val SAVE_LOCATION_BUTTON = "Save"
        const val LOAD_SAVED_LOCATION = "Load saved"
        const val CANCEL_BUTTON = "Cancel"
        const val LOAD_CURRENT_LOCATION = "Load current"
        
        const val DONE_BUTTON = "Done"
        const val DELETE_BUTTON = "Delete"
        const val UPDATE_BACKGROUND_PROCESS = "Start"
        const val STOP_BACKGROUND_PROCESS = "Stop"
        const val STOP_UPDATING = "Stop updating"
        
        
        //Toasts
        const val INVALID_VALUES_ERROR = "Inputted values invalid"
        const val PERMISSIONS_OK = "App has all needed permissions 👍"
        const val PERMISSIONS_NOT_OK = "App doesn't have all necessary permissions: check Android Settings"
        const val CANNOT_LOAD_SAVED = "Loading of saved values failed"
        const val CANNOT_LOAD_CURRENT = "Loading of current values failed"
        const val ENABLE_LOCATION_UPDATE = "Please enable location updating"
        const val NO_OPTIMIZATIONS = "App not restricted by Android 👍"
        const val OPTIMIZATIONS_ENFORCED = "Android OS restricts app"
        
        //Dialog strings
        const val PERMISSIONS_DIALOG_TITLE = "Manage permissions"
        const val RESTRICTIONS_DIALOG_TITLE = "Manage app settings"
        const val TEMPERATURE_HISTORY_DIALOG_TITLE = "Temperature history"
        
        //Restrictions dialog
        
        const val SELECT_PROCESS_TYPE = "Background process priority: "
        
        
        //Info texts
        const val PERMISSIONS_INFO = "For the background process, Location Access must be allowed all the time for updating location."
        const val RESTRICTIONS_INFO = "Android OS can use battery optimization to restrict apps from running in the background."
        const val UPDATE_INTERVAL_INFO = "The temperature is updated after this interval has passed in a 10 minute window: " +
                "if the interval is 15 minutes, the actual time between updates is 15-25 minutes."
    }
}