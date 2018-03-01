1) Run "gradle wrapper" to create wrapper
2) Run "gradle idea" to create Idea files
3) Import project into Idea
    a) add support of Gradle (use wrapper)
4) Install Idea "MapStruct Support" plugin
# 5) Add "build/classes/java/main" into build dependencies (so we can debug directly from Idea)
#     NOTE: This has to be done after every change of Gradle script!