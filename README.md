#AudioPlayerService

This library is a service for audio media playback, its control and notification associated with playing audio.
Service wraps MediaPlayer and manages its state through set of Intents. 
Also allows small customization of player notification bar.

I will abandon this project because of introduction of MediaBrowserService in support libraries.
see: http://developer.android.com/samples/MediaBrowserService/project.html and https://www.youtube.com/watch?v=FBC1FgWe5X4



##Integration
Add library to your project dependency by adding repository in main project's 'build.gradle' file:

    allprojects {
        repositories {
            ....
            maven {
                url  "http://dl.bintray.com/magillus/maven"
            }
        }
    }
    
Add dependency:

    compile(group: 'com.matsdevelopsolutions', name: 'audioservicelibrary', version: '0.1', ext: 'aar')
    
and use helper class (name of class) static methods to communicate with service and it media player.
todo show dependency snippet
Register BroadcastReceiver from helper class (name of class) methods to get updates about playback stream, buffering, state of the player and progress.
Todo show the methods usage

##Example usage
           
#### Play a audio stream
    MediaInfo mediaInfo = new MediaInfo();
    mediaInfo.artUri = "<ART URL>";
    mediaInfo.streamUrl = "<Stream url>";
    mediaInfo.description = "<Description>";
    mediaInfo.title = "<Title>";
    Intent playIntent = IntentGenerator.createPlayIntent(context, mediaInfo);
    startService(playIntent);
####Set track art (see artUri above)
    IntentGenerator.createNotificationUpdateIntent
####On/off notification (TODO)
####Play/pause/stop stream:
    startService(IntentGenerator.createStopIntent(context));
    startService(IntentGenerator.createPauseIntent(context));
    startService(IntentGenerator.createPlayToggleIntent(context));
####Control volume and mute
    startService(IntentGenerator.createToggleMuteIntent(context));
    IntentGenerator.createChangeVolumeIntent(context, 0.5f) //50% volume
    
####Set PendingIntent for opening main application from notification (TODO)

##Roadmap
+ 0.1 first release - simple URL playback
+ 0.2 playback resume option and restart per url #1, MediaReqeust #6, notification pending intent #5
+ 0.3 bulletproof playback requests - restarts playback on play even if player state is invalid
+ 0.4 working audio service with example app - media layer from local - #7
+ 0.5 working player example app with remote urls and bug fixes #8
+ 0.6 art URI passing for notification view background, passing notification icon
+ 0.9 notification management and styling
+ 1.0 playing single stream and control of it though intents and notification, pallette colors on notification based art image.
+ 1.1 bug fixes and optimization updates
+ 1.2 playlist support (add,insert,delete,clear) google cast integration

##Issues
Please create issues if you encounter bugs or feature requests. 
However most bugs noticed will be fixed for 1.0 release.

##License
    The MIT License (MIT)
     
     Copyright (c) 2015 Mateusz Perlak
     
     Permission is hereby granted, free of charge, to any person obtaining a copy
     of this software and associated documentation files (the "Software"), to deal
     in the Software without restriction, including without limitation the rights
     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     copies of the Software, and to permit persons to whom the Software is
     furnished to do so, subject to the following conditions:
     
     The above copyright notice and this permission notice shall be included in all
     copies or substantial portions of the Software.
     
     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
     SOFTWARE.
