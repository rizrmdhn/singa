# <b>SINGA (Signa Lingua): Translating Silence, Transcending Barriers</b>

<p>
SINGA is a service that bridges communication between ordinary people and people with hearing impairments

We provide a mobile application that translates sign language into text and vice versa. With the primary goal to help people with hearing impairments to communicate with ordinary people. The application is equipped with a machine learning model that can recognize sign language gestures and translate them into text.
</p>

## Demo

Check out the live demo of Singa [CLICK DEMO VIDEO HERE]()

## Installation

### Prerequisites

- Android Studio installed on your machine.
- Android SDK with API Level 27.

## Steps

1. Clone the repository:

   ```
   git clone https://github.com/Signa-Lingua/singa-app.git

   cd singa-app
   ```

2. Open the project in Android Studio:

   - Open Android Studio.
   - Click on File -> Open.
   - Navigate to the singa-app directory and select it.

3. Configure SDK:

   - Ensure you have the Android SDK with API Level 27 installed.
   - You can install it via the SDK Manager in Android Studio (Tools -> SDK Manager).

4. Add api singa properties:

   - In the local.properties file (create it in the root project directory if it doesn't exist), add the following line:

   ```
   APP_MODE=prod
   PRODUCTION_MODE=true
   API_URL=
   API_URL_PROD=
   ARTICLE_URL=
   KEYSTORE_PATH=
   KEY_ALIAS=
   KEY_PASSWORD=
   KEYSTORE_PASSWORD=
   ```

5. Build and run the project:

   - Click the Run button in Android Studio to build and run the app on an emulator or physical device.

## Contribution

Contributions are welcome! Please fork the repository and create a pull request with your changes.

## Reporting Issues

If you encounter any issues or have suggestions, please open an issue on GitHub.
