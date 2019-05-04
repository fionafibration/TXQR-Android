package dev.novalogic.txqrfincrypt

import android.os.Bundle

import io.flutter.app.FlutterActivity
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity: FlutterActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    GeneratedPluginRegistrant.registerWith(this)
  }
}
// package com.example.flutterqrscan;

// import android.os.Bundle;

// import io.flutter.app.FlutterActivity;
// import io.flutter.plugins.GeneratedPluginRegistrant;

// public class MainActivity extends FlutterActivity {
//   @Override
//   protected void onCreate(Bundle savedInstanceState) {
//     super.onCreate(savedInstanceState);
//     GeneratedPluginRegistrant.registerWith(this);
//   }
// }
