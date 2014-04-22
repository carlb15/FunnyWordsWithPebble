FunnyWordsWithPebble General Overview:
======

This is the repo for a android-companion/pebble application that generates funny and unique words with their definitions. The word and it’s definition is randomly chosen when the button is pressed, it is then displayed on this companion android app as well as the pebble watch. It’s a fun, educational and extremely easy way to improve your vocabulary all at the touch of your wrist! 

Get the application for pebble here:
https://dev-portal.getpebble.com/applications/5356b9d14d862b74d1000029

Get the android compainion application here:
https://play.google.com/store/apps/details?id=vt.edu.funnywordsgenerator

Detailed Write up:
======

My partner Carl Barbee and I tried to come up with an idea that would be lightweight knowing that the amount of data that could be sent to the Pebble and the memory available on the Pebble was limited. We wanted to create an application that we would actually use on our Pebbles. Thus, we can up with the idea of a random word generator that displays words and their definitions on the Pebble Watch. While working on idea, we realized that choosing a word from a complete dictionary often resulted in monotonous words, so we decided to query from only funny, unique words. Our “FunnyWordGenerator” application is entertaining, educational, and easy to use.
 
The application works by loading a small lightweight textfile with over one hundred entertaining words into a locally stored list. One word from this list is randomly chosen every time the select button on the Pebble or the button on the Android application is pressed. Once pressed, the Android application sends data to the pebble as a string array, where the first value in the array is the word, and the second value is the word’s definition. Once the definition is received on the Pebble it will vibrate every five times you press the button to let the user know. The word and it’s definition are then displayed on both the Android device and the Pebble watch. The Pebble’s up and down buttons traverse through the list alphabetically allowing the user the ability to find a specific word. The traversal is accomplished by storing a current index value in the Android application and iterating/decrementing when the appropriate key is received from the Pebble.
 
One of the limitations encountered during development was a lack of debugging when we used CloudPebble. There were a few bugs where we had to constantly update the text layers to figure out the issue. Another issue was the Bluetooth connection where it would take a few seconds for the signal to be received, processed, and displayed on the Pebble watch, even after the new definition is displayed on the Android application. The next feature for our application would be to have a more involved understanding on when the passed information is successfully displayed on the watch. Because during one of the aforementioned delays the user can rapidly choose to generate another word multiple times before the original word is displayed, preventing this confusion would be a good additional feature.
 
The constricted information that can be passed and displayed on the Pebble is definitely a hindrance, and prevents a lot of possible application ideas. However, our application is lightweight, and efficient. It also presents the ability to learn a new word at the touch of my wrist is still fantastic, that’s what “FunnyWordGenerator” is all about!
