# 🏠 Home Activity Reminder

A simple and user-friendly **Android Mobile Application** developed using **Kotlin** and **Android Studio**. The application allows users to create daily activity reminders and receive an alarm notification with a sound when the scheduled time arrives.

## 📱 About the Project

**Home Activity Reminder** is designed to help users remember their important daily tasks and activities.

The user can:

- Add a new reminder
- Enter the activity/task name
- Select a reminder time
- View saved reminders on the home screen
- Schedule an alarm for the selected time
- Receive a reminder when the alarm time arrives
- Play a reminder sound automatically
- Remove the reminder after it is triggered

The project is designed as a simple **Mobile Application Development** project and demonstrates Android concepts such as Activities, XML layouts, Kotlin, AlarmManager, BroadcastReceiver, Services, SharedPreferences, and Material/Card-based UI.

---

## ✨ Features

### 📝 Add Reminder
Users can enter an activity or task and create a reminder.

### ⏰ Time-Based Alarm
The application uses Android's `AlarmManager` to schedule reminders for a selected time.

### 🔔 Reminder Notification
When the scheduled time arrives, the application receives the alarm event and triggers the reminder service.

### 🔊 Reminder Sound
A built-in audio file is played when the reminder is triggered.

### 💾 Reminder Persistence
Reminder information is stored locally using `SharedPreferences`, allowing saved reminders to remain available after reopening the application.

### 🎨 User-Friendly Interface
The application uses CardViews and a scrollable layout to present reminders in a clean and simple interface.

### 🗑️ Automatic Removal
After a reminder is triggered, it is removed from the active reminder list.

---

## 🔄 Application Flow

```text
Open Application
       ↓
Home Screen
       ↓
Add Reminder
       ↓
Enter Activity / Task
       ↓
Select Time
       ↓
Create Reminder
       ↓
Reminder Saved Locally
       ↓
AlarmManager Schedules Alarm
       ↓
Selected Time Arrives
       ↓
BroadcastReceiver Receives Alarm
       ↓
AlarmService Starts
       ↓
🔊 Reminder Sound Plays
       ↓
Reminder Message Displayed
       ↓
Reminder Removed
```

---

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Kotlin | Application programming |
| Android Studio | Development environment |
| XML | User interface design |
| Android SDK | Android application development |
| AlarmManager | Scheduling alarms |
| BroadcastReceiver | Receiving alarm events |
| Service | Running reminder sound/background task |
| SharedPreferences | Local reminder storage |
| CardView | Reminder card UI |
| ConstraintLayout | UI layout structure |
| NestedScrollView | Scrollable home screen |
| Gradle | Project build system |

---

## 📂 Project Structure

```text
Assignment_MAD/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com.example.assignment/
│           │       ├── MainActivity.kt
│           │       ├── AlarmBroadcastReceiver.kt
│           │       └── AlarmService.kt
│           │
│           ├── res/
│           │   ├── drawable/
│           │   ├── layout/
│           │   │   ├── activity_main.xml
│           │   │   └── item_reminder_card.xml
│           │   ├── mipmap/
│           │   ├── raw/
│           │   │   └── song.mp3
│           │   └── values/
│           │
│           └── AndroidManifest.xml
│
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🧩 Main Components

### `MainActivity.kt`

The main activity controls the application's home screen.

It is responsible for:

- Displaying the reminder interface
- Accepting reminder input
- Opening the time picker
- Creating reminder cards
- Scheduling alarms
- Saving reminders locally
- Loading previously saved reminders

### `AlarmBroadcastReceiver.kt`

This component receives the alarm broadcast from Android when the scheduled reminder time is reached.

It then starts the reminder service.

### `AlarmService.kt`

The service handles the reminder action.

It:

- Receives the reminder information
- Plays the reminder sound
- Displays the reminder message
- Stops itself after completing the reminder task

### `activity_main.xml`

Contains the main application interface, including the reminder creation section and the container used to display reminders.

### `item_reminder_card.xml`

Defines the design of an individual reminder card displayed on the home screen.

---

## 🔐 Android Permission

The application uses the exact alarm permission required for scheduling precise alarms on supported Android versions.

```xml
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

The application also declares the required Activity, BroadcastReceiver, and Service components in `AndroidManifest.xml`.

---

## 💾 Local Data Storage

The application uses **SharedPreferences** for lightweight local storage.

This allows reminder information to be retained locally on the device without requiring an external database or internet connection.

```text
User creates reminder
        ↓
Reminder stored in SharedPreferences
        ↓
Application restarted
        ↓
Saved reminder loaded again
```

---

## 🎯 Learning Objectives

This project demonstrates important concepts of Android Mobile Application Development:

- Kotlin programming
- Android Activities
- XML-based UI design
- Event handling
- TimePickerDialog
- AlarmManager
- PendingIntent
- BroadcastReceiver
- Android Service
- SharedPreferences
- Resource management
- Audio playback
- Android Manifest configuration
- Card-based UI design

---

## 🚀 How to Run the Project

### 1. Clone the repository

```bash
git clone <YOUR-GITHUB-REPOSITORY-URL>
```

### 2. Open in Android Studio

Open the cloned project using **Android Studio**.

### 3. Sync Gradle

Allow Android Studio to download and synchronize the required Gradle dependencies.

### 4. Connect an Android Device

Enable **Developer Options** and **USB Debugging** on your Android phone, or use an Android Emulator.

### 5. Run the Application

Click the **Run ▶** button in Android Studio.

---

## 📋 How to Use

1. Open the application.
2. Enter the activity or task you want to remember.
3. Select the desired reminder time.
4. Create the reminder.
5. The reminder appears on the home screen.
6. At the scheduled time, the alarm is triggered.
7. The reminder sound plays and the reminder message is displayed.
8. The completed reminder is removed from the active list.

---

## 📸 Screenshots

Add screenshots of your application here after uploading them to the repository.

Example:

```markdown
![Home Screen](screenshots/home.png)

![Add Reminder](screenshots/add-reminder.png)

![Reminder Card](screenshots/reminder-card.png)
```

---

## 🔮 Future Enhancements

Possible improvements for future versions include:

- 🔔 Android notification support
- 🔁 Recurring daily/weekly reminders
- ✏️ Edit existing reminders
- 🗑️ Manual reminder deletion
- 🌙 Dark mode
- 📅 Calendar-based reminders
- ☁️ Cloud database synchronization
- 👤 User accounts and authentication
- 🔄 Backup and restore reminders
- 🎵 Custom reminder sounds

---

## 👨‍💻 Project Information

**Project Name:** Home Activity Reminder  
**Application Type:** Android Mobile Application  
**Platform:** Android  
**Language:** Kotlin  
**IDE:** Android Studio  
**Package Name:** `com.example.assignment`

---

## 📚 Academic Purpose

This project was developed as part of **Mobile Application Development** coursework to demonstrate practical implementation of Android application development concepts using Kotlin and XML.

---

## ⭐ Support

If you find this project useful, consider giving the repository a ⭐ on GitHub.

---

### Made with ❤️ using Kotlin & Android Studio
