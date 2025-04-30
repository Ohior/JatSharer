package jat.sharer.com

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTType
import platform.UniformTypeIdentifiers.UTTypeContent
import platform.darwin.NSObject

@Composable
actual fun rememberFilePickerLauncher(
    onResult: (List<PlatformFile>) -> Unit,
): FilePickerLauncher {
    // Remember the launcher instance tied to the onResult callback
    return remember(onResult) {
        IOSFilePickerLauncher(onResult)
    }
}

// Actual class implementing the launcher logic for iOS
 class IOSFilePickerLauncher(
    private val onResult: (List<PlatformFile>) -> Unit
) :FilePickerLauncher(onResult){
    // Keep a reference to the delegate
    private val delegate = DocumentPickerDelegate(onResult)

    @OptIn(ExperimentalForeignApi::class)
    override fun launch(allowMultiple: Boolean, allowedMimeTypes: List<String>?) {
        // Convert MIME types to UTTypes if possible. This is more complex.
        // For simplicity, we might use broad categories or pass common UTTypes directly.
        // Example: UTType.Image(), UTType.PDF()
        // Using UTTypeContent for broader compatibility if specific types aren't provided.
        val utTypes: List<UTType> = allowedMimeTypes?.mapNotNull { mimeToUTType(it) }
            ?: listOf(UTTypeContent) // Default to generic content

        // Create the document picker view controller
        // Note: documentTypes requires UTType objects on newer iOS versions.
        // For older versions, you might need documentTypes based on strings (UTIs).
        val pickerController = UIDocumentPickerViewController(forOpeningContentTypes = utTypes)

        pickerController.delegate = delegate
        pickerController.allowsMultipleSelection = allowMultiple

        // Get the root view controller to present the picker
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(pickerController, animated = true, completion = null)
    }

    // Basic MIME type to UTType mapping (needs expansion for real-world use)
    @OptIn(ExperimentalForeignApi::class)
    private fun mimeToUTType(mimeType: String): UTType? {
//        return null
        throw NotImplementedError("MIME type to UTType mapping not implemented")
//        return when (mimeType.lowercase()) {
//            "image/png" -> UTType.PNG
//            "image/jpeg", "image/jpg" -> UTType.JPEG
//            "application/pdf" -> UTType.PDF
//            "text/plain" -> UTType.PlainText
//            // Add more mappings as needed
//            else -> UTType.typeWithMIMEType(mimeType) // Try dynamic lookup
//                ?: UTType.Data // Fallback to generic data
//        }
    }
}

// Delegate class to handle callbacks from UIDocumentPickerViewController
@OptIn(ExperimentalForeignApi::class)
private class DocumentPickerDelegate(
    private val onResult: (List<PlatformFile>) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*> // List<NSURL>
    ) {
        val files = didPickDocumentsAtURLs.mapNotNull { it as? NSURL }.map { url ->
            // Try to get a coordinated read access for security-scoped URLs
            val secured = url.startAccessingSecurityScopedResource()
            val name = url.lastPathComponent ?: "Untitled"
            val path = url.path // Path might be relevant on iOS/macOS
            // Content reading requires accessing the URL, potentially async
            // val data = NSData.dataWithContentsOfURL(url)
            if (secured) {
                url.stopAccessingSecurityScopedResource()
            }
            PlatformFile(
                name = name, path = path,
                size = TODO()
            )
        }
        onResult(files)
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        onResult(emptyList()) // User cancelled
    }
}

// Type alias for clarity if needed
// private typealias IOSFilePickerLauncher = FilePickerLauncher
