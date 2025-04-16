package com.programminghut.realtime_object;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LaunchActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private static final String TAG = "LaunchActivity";
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    private boolean isNameCaptured = false;
    private boolean isCapturingMobile = false;
    private boolean isCapturingAddress = false;
    private boolean isCapturingDestination = false;
    private boolean isChoosingMode = false;

    private String userName = "";
    private String userMobile = "";
    private String userAddress = "";

    private FirebaseFirestore firestore;
    private TextView tvVoiceOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_launch);

        firestore = FirebaseFirestore.getInstance();
        tvVoiceOutput = findViewById(R.id.tvVoiceOutput);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                speakAndDisplay("Welcome to Vision Assist App. What is your name?");
                startVoiceInput();
            } else {
                Log.e(TAG, "TTS Initialization failed.");
            }
        });
    }

    private void startVoiceInput() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (Exception e) {
                speakAndDisplay("Error: " + e.getMessage());
            }
        }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String userSpeech = Objects.requireNonNull(result).get(0).toLowerCase();

            if (!isNameCaptured) {
                userName = userSpeech;
                isNameCaptured = true;
                speakAndDisplay("Hello " + userName + ". Let me check if you are a user.");
                checkUserInDatabase(userName);
            } else if (!isCapturingMobile) {
                isCapturingMobile = true;
                userMobile = userSpeech.replaceAll("\\s+", "");

                if (!isValidMobileNumber(userMobile)) {
                    isCapturingMobile = false;
                    speakAndDisplay("Invalid mobile number. Please provide a 10-digit number.");
                    startVoiceInput();
                    return;
                }

                speakAndDisplay("Thank you. Now, please tell me your address.");
                startVoiceInput();
            } else if (!isCapturingAddress) {
                isCapturingAddress = true;
                userAddress = userSpeech;
                saveUserDetailsToFirestore();
            } else if (isChoosingMode) {
                isChoosingMode = false;

                if (userSpeech.contains("obstacle")) {
                    speakAndDisplay("Opening obstacle detection.");
                    startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                } else if (userSpeech.contains("navigation") || userSpeech.contains("navigate")) {
                    isCapturingDestination = true;
                    speakAndDisplay("Where do you want to go?");
                    startVoiceInput();
                } else {
                    speakAndDisplay("Please say detect obstacles or start navigation.");
                    isChoosingMode = true;
                    startVoiceInput();
                }
            } else if (isCapturingDestination) {
                isCapturingDestination = false;
                startNavigation(userSpeech);
            }
        }
    }

    private void checkUserInDatabase(String name) {
        firestore.collection("user").document(name).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    speakAndDisplay("User found. Do you want to detect obstacles or start navigation?");
                    isCapturingMobile = true;
                    isCapturingAddress = true;
                    isChoosingMode = true;
                    new Handler().postDelayed(this::startVoiceInput, 3000);
                } else {
                    speakAndDisplay("User not found. Please tell me your mobile number.");
                    isCapturingMobile = false;
                    startVoiceInput();
                }
            } else {
                Log.e(TAG, "Error fetching document: ", task.getException());
            }
        });
    }

    private void saveUserDetailsToFirestore() {
        Long mobileNumber = Long.parseLong(userMobile);
        User user = new User(userName, mobileNumber, userAddress);

        firestore.collection("user").document(userName).set(user)
                .addOnSuccessListener(aVoid -> {
                    speakAndDisplay("Your details are saved. Do you want to detect obstacles or start navigation?");
                    isChoosingMode = true;
                    new Handler().postDelayed(this::startVoiceInput, 3000);
                })
                .addOnFailureListener(e -> speakAndDisplay("Error saving details. Please try again."));
    }

    private void startNavigation(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                speakAndDisplay("Starting navigation to " + address);
                startNavigating(latitude, longitude);
            } else {
                speakAndDisplay("Address not found! Please say the destination again.");
                isCapturingDestination = true;
                new Handler().postDelayed(this::startVoiceInput, 3000);
            }
        } catch (IOException e) {
            speakAndDisplay("Error retrieving coordinates: " + e.getMessage());
        }
    }

    private void startNavigating(double latitude, double longitude) {
        String navigationUri = "google.navigation:q=" + latitude + "," + longitude;
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationUri));
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        startActivity(new Intent(LaunchActivity.this, MainActivity.class));
    }

    private boolean isValidMobileNumber(String mobileNumber) {
        return mobileNumber.matches("\\d{10}");
    }

    private void speakAndDisplay(String message) {
        tvVoiceOutput.setText(message);
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
