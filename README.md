# My Simple Music Player

The goal of this project is to demonstrate an implementation of a music player in android using the MVI (Model View Intent) architecture.

The application uses a client/server design as defined in the [official docs](https://developer.android.com/guide/topics/media-apps/audio-app/building-an-audio-app)

<img src="./images/screenshot.png" width="200px">

<br/>

# Architecture overview

The following diagram illustrates the overall client/server architecture of the application.

<img src="./images/music_player_architecture.drawio.png" width="300">

## Server
Server contains the data layer and the `MusicService`.

### Data
Data layer uses the repository pattern.  [Repository](https://github.com/nak411/MyMusicApp/blob/main/app/src/main/java/com/naveed/mymusicapp/core/data/api/MusicRepositoryImpl.kt) is responsible for managing different data sources.  There is currently a single local data source which provides access to the audio files stored on the device.

### Music Service
Music service is implemented using a foreground service.  This service serves as the server and allows the client to connect to it.  This allows the music to play even when the user has backed out of the application.

## Client
Client is responsible for binding with the service and providing the UI and controls.

### Domain
Domain layer serves as an abstraction layer between the data layer.  This provides us the ability to modify, swap or change the data layer without impacting the ui layer.
Domain layer consists of several use cases.  A single use case is reponsible for a single action such as `Load Song`, `Play Song`, `Pause Song` etc.

In the current implementation, the domain layer communicates with the `MusicService` to provide data to a bound client and also acts as a mediator for controlling the service.

The following diagram illustrates the client architecture:

<img src="./images/client_architecture.drawio.png">

A state object is used to mutate and display the UI.  Any user interaction is tracked using an `Event` which is then sent to the `ViewModel`.  The `ViewModel` uses the domain provided use cases to handle each `Event.`  This creates the full event loop and maintains uni-directional data flow.

