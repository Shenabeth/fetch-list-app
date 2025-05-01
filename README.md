# fetch-list-app

This Android application retrieves and displays hiring data from a JSON file, grouped by listId and sorted by name.

## Features
- Loads data from a local JSON file
- Groups items by listId
- Sorts items first by listId, then by name within each group
- Filters out items with blank or null names
- Displays results in an easy-to-read list with a clean UI
- Uses a color palette of white, orange (#f3a412), and purple (#300b3f)


## Requirements
- Android Studio (Meerkat)
- Android SDK 35
- Kotlin 1.9.0 or higher


## Setup & Installation

1. Clone this repository:
git clone https://github.com/yourusername/fetch-list-app.git

2. Open the project in Android Studio:
- Launch Android Studio
- Select "Open an existing Android Studio project"
- Navigate to the cloned repository folder
- Click "Open"

3. Add your hiring.json file to the assets folder:
If you haven't created the assets folder yet:
- Right-click on app > src > main
- Select New > Directory
- Type "assets" and click OK
- Copy your hiring.json file to this folder

4. Build and run the application:
- Connect an Android device or use an emulator
- Click the "Run" button in Android Studio


## JSON Format
The application expects the hiring.json file to contain an array of objects with the following structure:
```
json[
{
"id": 1,
"listId": 1,
"name": "Item name"
},
...
]
```


## Implementation Details

### Architecture:
The app uses a simple architecture with model classes, UI components, and utility classes

### Data Processing:
Loading JSON data from the assets folder using Gson
Filtering out invalid items (blank or null names)
Grouping by listId
Sorting by name within each group

### UI:
Material Design components
Nested RecyclerViews for displaying grouped items
CardView for list groups
Custom color scheme with white, amber (#f3a412), and purple (#300b3f)


## Author
Shenabeth Jenkins
