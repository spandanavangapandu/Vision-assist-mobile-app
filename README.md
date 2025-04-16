# Vision Assist App

![GitHub repo size](https://img.shields.io/github/repo-size/spandanavangapandu/vision-assist-mobile-app)
![GitHub issues](https://img.shields.io/github/issues/spandanavangapandu/vision-assist-mobile-app)

## 🧠 Description

The **Vision Assist App** empowers individuals with visual impairments by providing intelligent tools to help them better understand and navigate their surroundings. Leveraging machine learning, computer vision, and accessibility-first design, the app aims to enhance independence and quality of life.

---

## ✨ Features

- 🎯 **Object Recognition** – Detect and announce objects in real time using the camera.
- 🧭 **Navigation Assistance** – Provide basic directional or environmental awareness.
- 🎙️ **Voice Commands** – Hands-free interaction for seamless control.
- 🔊 **Text-to-Speech** – Speak out detected objects and environment info.

---

## 📲 Installation

To install and run the app locally:

```bash
git clone https://github.com/spandanavangapandu/vision-assist-mobile-app.git
cd vision-assist-mobile-app
Open the project in Android Studio

Connect an Android device or start an emulator

Click Run ▶️ in Android Studio
```

## 🧰 Tech Stack
- **Language**: Kotlin / Java
- **ML Frameworks**: TensorFlow Lite, ML Kit
- **Camera API**: CameraX
- **Other**: Android Jetpack Libraries, Text-to-Speech

## 🧪 Machine Learning Model
The app uses a pre-trained model (model.tflite) for object recognition.

📁 Place it under:
```
app/src/main/assets/
```
Include any label files (e.g. labels.txt) in the same directory.

## 🔍 Accessibility Focus
We prioritize usability for visually impaired users:
- Audio feedback for detected objects
- Minimal-touch UI
- Voice-command integration

## 🧑‍💻 Contributing
We welcome community contributions!

### Step-by-step
1. Fork the repository
2. Create a new branch: `git checkout -b feature-name`
3. Commit your changes: `git commit -m "Added new feature"`
4. Push to your fork: `git push origin feature-name`
5. Submit a Pull Request 🙌

## 📸 Preview
Coming soon: Screenshots and demo GIFs of object recognition in action.

## 📚 Credits
This project is built in reference to the amazing tutorial by Programming Hut on YouTube. We’ve extended the base functionality and will continue evolving the app to support more accessibility features.

## 📄 License
This project is licensed under the MIT License.
