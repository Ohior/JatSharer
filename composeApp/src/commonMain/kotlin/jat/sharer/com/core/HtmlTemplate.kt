package jat.sharer.com.core


object HtmlTemplate {
    private const val HTMLSTYLE = """
    body {
      margin: 0;
      font-family: Arial, sans-serif;
      display: flex;
      height: 100vh;
    }
    .card-container {
        display: flex;
        flex-wrap: wrap; /* Ensures responsiveness */
        gap: 15px; /* Adds spacing between cards */
    }
    .clickable-card {
        flex: 1; /* Makes it responsive */
        min-width: 250px; /* Ensures a minimum size */
        max-width: 300px; /* Limits the maximum width */
        padding: 15px;
        border: 1px solid #ccc;
        border-radius: 8px;
        text-decoration: none;
        color: black;
        background-color: #f9f9f9;
        transition: background-color 0.3s;
    }
    .clickable-card:hover {
        background-color: #e0e0e0;
    }
    .title {
        font-size: 18px;
        font-weight: bold;
    }
    .description {
        font-size: 14px;
        color: #666;
    }
    .column button {
      padding: 10px 20px;
      margin: 10px;
      font-size: 16px;
      border: none;
      border-radius: 5px;
      cursor: pointer;
    }
    .column button:hover {
      background-color: #ddd;
    }
    .column {
      flex: 0.3; /* Takes up 30% of the width */
      background-color: #f0f0f0;
      display: flex;
      align-items: bottom;
      justify-content: bottom;
      display: flex;
      flex-direction: column;
      gap: 10px;
    }
    .details {
      flex: 1; /* Takes up 50% of the width */
      background-color: #e0e0e0;
      display: flex;
      align-items: center;
      justify-content: center;
    }   
    """

    fun tagButton(url: String = "/", text: String = "Button"): String {
        return """<button><a href="$url">$text</a></button>"""
    }

    fun fileItem(title:String, description:String, urlPath:String):String{
        return """<a href="$urlPath" class="clickable-card">
        <div class="title">$title</div>
        <div class="description">$description</div>
        </a>"""
    }


    fun selectFileForm(url: String = "/upload"): String {
        return """
    <h1>Upload Multiple Files</h1>
    <form id="fileUploadForm">
        <input type="file" id="fileInput" name="files" multiple>
        <button type="button" id="uploadButton">Upload Files</button>
    </form>

    <script>
        document.getElementById('uploadButton').addEventListener('click', async () => {
            const fileInput = document.getElementById('fileInput');
            const files = fileInput.files;

            if (files.length === 0) {
                alert("Please select at least one file.");
                return;
            }

            const formData = new FormData();

            for (let i = 0; i < files.length; i++) {
                formData.append('files', files[i], files[i].name);
            }

            try {
                const response = await fetch('/upload', {
                    method: 'POST',
                    body: formData
                });
                if (response.ok) {
                    const result = await response.text();
                    alert(`Upload successful: result`);
                } else {
                    alert(`Upload failed: response.statusText`);
                }
            } catch (error) {
                console.error('Error uploading files:', error);
                alert('An error occurred while uploading the files.');
            }
        });
    </script>
    """
    }

    fun myHtmlPage(
        contents: List<String>? = null,
        details: List<String>? = null
    ): String {
        var conts = ""
        var dets = ""
        contents?.forEach { content -> conts += content } ?: ""
        details?.forEach { detail -> dets += detail } ?: ""
        return """
    <!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Document</title>
    <style>$HTMLSTYLE</style>
</head>
<body>
    <div class="column">
        $conts
    </div>
    <div class="details">
        <div class="card-container">
            $dets
        </div>
    </div>
</body>
</html>
""".trimIndent()
    }
}
