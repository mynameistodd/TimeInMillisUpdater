# TimeInMillisUpdater
An application which updates the TimeInMillis application

### Functionality ###
There are two apps involved in this solution.
1. TimeInMillis - This is a trivial application which just outputs the current time in milliseconds. This application is the target of what is updated with the second app.
2. TimeInMillisUpdater - The purpose of this application is to launch the TimeInMillis application, and then check for any updates that are available. If they are available, they are downloaded to local disk, and used in the next launch.

### Assumptions ###
1. Application needed to be 'cross platform', so I went with a simple Java app, since Java runs everywhere.
2. Needed to write own solution end-to-end.

### Notes
1. Traditionally, I would steer away from reinventing the wheel. Using something like [Getdown](https://github.com/threerings/getdown), [Sparkle](https://sparkle-project.org/), or [ClickOnce](https://en.wikipedia.org/wiki/ClickOnce#Updates) for desktop applications.
2. There's the possibility that a project like [Electron](https://electronjs.org/) might be a good fit. I've never used it, though, but it would be fun to learn!
3. Used Gitlab CI to build the project and host the files.
