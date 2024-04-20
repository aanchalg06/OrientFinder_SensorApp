## Smartphone Orientation Tracker and Predictor App
This Android application tracks the orientation of a smartphone using its accelerometers and gyroscope sensors in real-time. It also logs this orientation data into a database for historical analysis and implements a predictive model to forecast future orientation values based on past data.

## Features
Real-time Orientation Display: Shows the current orientation of the smartphone in terms of three angles (pitch, roll, yaw).
Data Logging: Stores orientation data over time into a local SQLite database.
Historical Analysis: Visualizes the historical orientation data using graphs within the app.
Prediction Model: Utilizes historical orientation data to predict future orientation values.
Prediction Accuracy Plotting: Compares predicted values against actual values to evaluate the prediction model's accuracy.
Flexible Sensing Intervals: Allows users to change sensing intervals for data collection and prediction.
## Technologies Used
Android SDK: Development platform for creating the mobile application.
Java: Primary programming language for Android app development.
SQLite: Local database management system for storing orientation data.
SensorManager: Android framework component for accessing device sensors (accelerometer, gyroscope).
Graphing Libraries: Used to plot historical and predicted orientation data.
Machine Learning (Optional): For implementing the predictive model.
## Usage
Real-time Display: Launch the app to view the current orientation angles of your smartphone.
Data Logging: The app will automatically start logging orientation data to the database.
View Historical Data: Navigate to the history activity within the app to see graphs of past orientation data.
Predictive Model: Access the predictive model feature to forecast future orientation values based on historical data.
## Screenshots of Application
Home Page: Data not loading yet

<img src ="https://github.com/aanchalg06/OrientFinder_SensorApp/assets/108565060/a3621827-d10b-4705-94f2-e5687a8cd4a2" width="200">

Home Page: Data starts loading

<img src ="https://github.com/aanchalg06/OrientFinder_SensorApp/assets/108565060/3404a5fd-a147-4d1d-92a4-e145e20cf3c9" width="200">

Graphs of history

<img src ="https://github.com/aanchalg06/OrientFinder_SensorApp/assets/108565060/5d4531d2-5747-4af4-98e1-70c85e235673" width="200">

Database downloaded in the device

<img src ="https://github.com/aanchalg06/OrientFinder_SensorApp/assets/108565060/5d711bbb-3fb1-4f7c-8ff2-1c49766a7ee7" width="200">




