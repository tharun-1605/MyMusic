# Proguard rules for MyMusic
-keepclassmembers class * {
    @androidx.room.Dao *;
    @androidx.room.Database *;
    @androidx.room.Entity *;
}
