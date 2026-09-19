package ie.gaeilge.learning

import androidx.compose.ui.window.ComposeUIViewController
import ie.gaeilge.learning.app.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
