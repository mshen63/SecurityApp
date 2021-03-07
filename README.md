# On My Way
![home screen](https://github.com/mshen63/SecurityApp/appImages/home.png) ![help screen](https://github.com/mshen63/SecurityApp/appImages/help.png) ![accept caller](https://github.com/mshen63/SecurityApp/appImages/accept.png) ![connected](https://github.com/mshen63/SecurityApp/appImages/connected.png)

## Inspiration
As college students navigating a bustling metro cityscape, we frequently encountered dangerous situations where we wished we had a friend come to our aid. If only there was a way to notify nearby students in order to request and receive help within minutes. We designed On My Way to address these concerns and provide a way for victims to contact their peers in a safe and efficient manner.

## Functionality
On My Way allows users to request help with a push of a button. Nearby users will receive a notification, and once they accept the request, they are provided with navigational directions to locate the victim. Additionally, the victim will have the option to communicate with their rescuer via WiFi call in order to provide more information or intimidate suspicious individuals by making the user appear like they're not alone.

## How We Built It
After utilizing Java and Android Studio to build the app, we implemented the Google Maps SDK in order to display location markers on a map. We also stored location and login information in a Cloud Firestore database. WiFi calling was incorporated using Sinch, and notifications were generated using NotificationCompat builder.

