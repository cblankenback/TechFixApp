# TechFixApp

TechFixApp is an Android application designed for technicians to manage their daily tasks efficiently. It allows technicians to view assigned tasks, mark them as complete, and visualize client locations on a map.

## Features

- **User Authentication**: Secure login for technicians.
- **Task List**: View a list of assigned tasks with client details.
- **Task Details**: Access detailed information about each task.
- **Task Completion**: Mark tasks as completed or revert them to pending.
- **Map View**: Visualize client locations and the technician's current location on a Google Map.
- **Persistent Storage**: Task completion statuses are saved locally.
- **Persistent Storage**: Task are created in `task.json`

## Setup Instructions

### Prerequisites

- Android Studio Bumblebee or later.
- An Android device or emulator running Android API level 35 or higher.
- Internet access for Google Maps API.

### Steps

1. **Clone the Repository**

   File > import from verison control > paste the link > import
   ```bash
   https://github.com/yourusername/techfixapp.git
   ```


2. **Obtain a Google Maps API Key**

   - Follow the instructions at [Google Maps Platform](https://developers.google.com/maps/documentation/android-sdk/get-api-key) to get an API key.
   - Ensure that the Maps SDK for Android is enabled in your Google Cloud Console.

3. **Add API Key to `local.properties`**

   - In the project root directory, locate the `local.properties` file.
   - Add the following line at the end of the file:

     ```properties
     MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
     ```

     Replace `YOUR_GOOGLE_MAPS_API_KEY` with the API key you obtained.

4. **Sync the Project**

   - In Android Studio, click on **Sync Project with Gradle Files** to ensure all dependencies are set up correctly.

5. **Build and Run the App**

   - Connect your Android device or start an emulator.
   - Click **Run** > **Run 'app'**.

6. **Login Credentials**

   - Use the following credentials to log in:

     - **Username**: `cst3115`
     - **Password**: `3115`

## App Structure

The app follows the **Model-View-ViewModel (MVVM)** architecture pattern and is built using **Jetpack Compose** for the UI.

### Packages and Modules

- **`data.model`**

  - `Task.kt`: Data class representing a task assigned to a technician.

- **`data.repository`**

  - `TaskRepository.kt`: Handles data operations like fetching tasks and updating their statuses.

- **`ui.components`**

  - `LocationPermissionRequester.kt`: Composable functions to request location permissions and display rationale dialogs.

- **`ui.login`**

  - `LoginScreen.kt`: UI for user authentication.

- **`ui.map`**

  - `MapScreen.kt`: Displays client locations and the technician's current location on a Google Map.

- **`ui.taskdetail`**

  - `TaskDetailScreen.kt`: Shows detailed information about a task and allows status updates.

- **`ui.tasklist`**

  - `TaskListScreen.kt`: Displays a list of tasks with options to view details or mark as complete.

- **`viewmodel`**

  - `LoginViewModel.kt`: Handles login logic and session management.
  - `TaskListViewModel.kt`: Manages the list of tasks and their states.
  - `TaskDetailViewModel.kt`: Manages individual task details and status updates.

### Key Components

- **Authentication**

  - **`LoginViewModel`**: Checks credentials and maintains login state using `PreferencesManager`.
  - **`LoginScreen`**: UI for entering username and password.
 
- **Task Management**
   - **`task.json`**: Stores all the tasks which will be used by TaskRepository as a mock API by leveraging a local JSON file
  - **`TaskRepository`**: Fetches tasks from a local JSON file and updates task statuses using `SharedPreferences`.
  - **`TaskListViewModel`**: Retrieves tasks and handles task completion logic.
  - **`TaskListScreen`**: Displays tasks in a list with options to mark them as complete.

- **Task Details**

  - **`TaskDetailViewModel`**: Fetches and updates individual task details.
  - **`TaskDetailScreen`**: Shows detailed task information and current location if permissions are granted.

- **Mapping**

  - **`MapScreen`**: Integrates Google Maps to display client locations and the technician's current location.
  - Uses **Google Maps SDK** and follows [this tutorial](https://developers.google.com/codelabs/maps-platform/maps-platform-101-compose#0) for implementation guidance.

### Utilities

- **`TaskCompletionManager`**: Utility class for managing task completion statuses in `SharedPreferences`.
- **Permissions Handling**: Uses **Accompanist Permissions** library to manage runtime permissions for location access.

## Additional Notes

- **API Key Security**: The Google Maps API Key is stored in `local.properties` to prevent it from being checked into version control.
- **Data Storage**: Task data is initially loaded from a JSON file bundled with the app and task completion status is saved using `SharedPreferences`.

## Dependencies

- **Jetpack Compose**: For building native UI.
- **Accompanist Permissions**: To handle Android runtime permissions in Compose.
- **Google Maps SDK for Android**: For map functionalities.
- **Kotlin Coroutines**: For asynchronous programming.
