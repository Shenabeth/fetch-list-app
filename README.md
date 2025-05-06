# Fetch List App

This is a **native Android app written in Kotlin** that reads a local JSON file (`hiring.json`), filters and groups the data, and displays it in an organized and visually clean list. The app groups items by `listId`, filters out entries with blank or null names, and sorts the data first by `listId`, then by `name`.

> 💡 This project was developed for evaluation purposes and demonstrates data processing, UI design, and best practices using Android Jetpack Compose and ViewModel architecture.

---

## ✅ Features

- 📂 Loads local JSON data from `assets/hiring.json`
- 🧹 Filters out items with blank or null `name` values
- 📊 Groups valid items by `listId`
- 🔤 Sorts items within each group by `name`
- 📱 Displays the data in a clean, scrollable, expandable list view
- 🎨 Custom color palette:
    - White (`#FFFFFF`)
    - Gold/Amber (`#f3a412`)
    - Deep Purple (`#300b3f`)

---

## 🛠 Requirements

- **Android Studio (Meerkat or newer recommended)**
- **Android SDK API 35**
- **Kotlin 1.9.0+**
- **Gradle 8+**

---

## 🚀 Setup & Installation

Follow these steps to run the app on your machine:

### 1. Clone the repository

```
git clone https://github.com/yourusername/fetch-list-app.git
cd fetch-list-app
```

### 2. Open in Android Studio

- Open Android Studio
- Choose **"Open an existing project"**
- Select the cloned `fetch-list-app` directory
- Let Gradle sync and finish indexing

### 3. Add `hiring.json` to the assets folder

> If the `assets` folder does not already exist:

- Right-click `app > src > main`
- Choose **New > Directory**
- Name it: `assets`
- Place your `hiring.json` file in this folder

### 4. Build and run the app

> Please make sure you have a device or emulator that supports API level 35 or higher.

- Connect a physical device or launch an emulator
- Click the green **Run ▶** button in Android Studio

---

## 📄 Expected JSON Format

```
[
  { "id": 755, "listId": 2, "name": "" },
  { "id": 684, "listId": 1, "name": "Item 684" },
  { "id": 808, "listId": 4, "name": "Item 808" }
]
```

- `name` can be null or an empty string — these items will be excluded
- Valid items are grouped by `listId` and sorted by `name`

---

## 🧱 Architecture Overview

- **MVVM (Model-View-ViewModel)** architecture separates concerns between UI and business logic
- UI built with **Jetpack Compose** for a modern, declarative experience
- Data managed via a **ViewModel** and **LiveData**, ensuring lifecycle awareness
- JSON data parsed from `assets/hiring.json` using **Gson**
- Filtered, grouped, and sorted data exposed to the UI as a list of grouped models

---

## 🎨 UI Design

- Uses **Scaffold**, **LazyColumn**, and **Card** components in Compose
- Grouped items are displayed inside expandable cards by `listId`
- Individual items within groups are sorted by `name`
- Responsive design optimized for readability
- Color palette:
    - Background: Deep Purple `#300b3f`
    - Accent: Gold/Amber `#f3a412`
    - Foreground: White `#FFFFFF`

---

## 👩🏽‍💻 Author

**Shenabeth Jenkins**

*Computer Science + Astronomy @ University of Maryland*

