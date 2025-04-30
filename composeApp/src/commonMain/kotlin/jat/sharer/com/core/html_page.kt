package jat.sharer.com.core

import jat.sharer.com.models.DeviceFile
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.lang
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe


fun generateFileSelectorPage(): String {
    return createHTML().html {
        lang = "en"

        head {
            meta { charset = "UTF-8" }
            meta { name = "viewport"; content = "width=device-width, initial-scale=1.0" }
            title("File Selector")
            script { src = "https://cdn.tailwindcss.com" }
            style {
                unsafe {
                    raw(
                        """
                        body {
                            font-family: 'Inter', sans-serif;
                            padding-bottom: 100px;
                        }
                        .file-input-label {
                            cursor: pointer;
                            display: inline-block;
                        }
                        #fileInput {
                            display: none;
                        }
                    """
                    )
                }
            }
        }

        body(classes = "bg-gray-100") {
            div(classes = "container mx-auto p-4") {
                h1(classes = "text-2xl font-semibold mb-4 text-center text-gray-700") {
                    +"Selected Files"
                }
                div {
                    id = "fileDisplayArea"
                    classes =
                        setOf("flex flex-col gap-4 p-4 border border-gray-300 rounded-lg min-h-[100px] bg-white shadow-sm")
                    span(classes = "text-gray-500 italic w-full text-center") {
                        +"No files selected yet."
                    }
                }
            }

            div(classes = "fixed bottom-0 left-0 right-0 bg-gray-200 p-4 border-t border-gray-300 shadow-md") {
                div(classes = "container mx-auto flex flex-wrap justify-center items-center gap-4") {
                    label(classes = "file-input-label bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg shadow cursor-pointer transition duration-150 ease-in-out") {
                        htmlFor = "fileInput"
                        +"Select Files"
                    }
                    input(type = InputType.file, name = "fileInput") {
                        id = "fileInput"
                        multiple = true
                    }
                    button(classes = "bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded-lg shadow transition duration-150 ease-in-out") {
                        id = "clearButton"
                        +"Clear Selection"
                    }
                    button(classes = "bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded-lg shadow transition duration-150 ease-in-out") {
                        id = "uploadButton"
                        +"Upload"
                    }
                    button(classes = "bg-yellow-500 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded-lg shadow transition duration-150 ease-in-out") {
                        id = "cancelButton"
                        +"Cancel Upload"
                    }
                    button(classes = "bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded-lg shadow transition duration-150 ease-in-out") {
                        id = "downloadPageButton"
                        +"Download Files"
                    }
                }
            }

            script {
                unsafe {
                    raw(
                        """
                        const fileInput = document.getElementById('fileInput');
                        const fileDisplayArea = document.getElementById('fileDisplayArea');
                        const clearButton = document.getElementById('clearButton');
                        const uploadButton = document.getElementById('uploadButton');
                        const cancelButton = document.getElementById('cancelButton');
                        const downloadPageButton = document.getElementById('downloadPageButton');
                        const defaultMessage = '<span class="text-gray-500 italic w-full text-center">No files selected yet.</span>';

                        let currentXHR = null;

                        function displayFiles() {
                            fileDisplayArea.innerHTML = '';
                            if (fileInput.files.length === 0) {
                                fileDisplayArea.innerHTML = defaultMessage;
                                uploadButton.disabled = true;
                                uploadButton.classList.add('opacity-50', 'cursor-not-allowed');
                                return;
                            }
                            uploadButton.disabled = false;
                            uploadButton.classList.remove('opacity-50', 'cursor-not-allowed');
                            for (let i = 0; i < fileInput.files.length; i++) {
                                const file = fileInput.files[i];
                                const container = document.createElement('div');
                                container.classList.add('bg-white', 'border', 'border-gray-300', 'rounded-lg', 'p-2', 'shadow-sm');

                                const name = document.createElement('div');
                                name.textContent = file.name;
                                name.classList.add('text-sm', 'font-medium', 'mb-1');

                                const progressWrapper = document.createElement('div');
                                progressWrapper.classList.add('w-full', 'bg-gray-300', 'rounded-full', 'h-2');

                                const progressBar = document.createElement('div');
                                progressBar.classList.add('bg-green-500', 'h-2', 'rounded-full');
                                progressBar.style.width = '0%';
                                progressBar.id = 'progress-'+i;

                                progressWrapper.appendChild(progressBar);
                                container.appendChild(name);
                                container.appendChild(progressWrapper);
                                fileDisplayArea.appendChild(container);
                            }
                        }

                        function clearFiles() {
                            if (currentXHR) {
                                currentXHR.abort();
                                currentXHR = null;
                            }
                            fileInput.value = '';
                            fileDisplayArea.innerHTML = defaultMessage;
                            uploadButton.disabled = true;
                            uploadButton.classList.add('opacity-50', 'cursor-not-allowed');
                        }

//                        async function handleUpload() {
//                            if (fileInput.files.length === 0) {
//                                console.log("No files selected to upload.");
//                                return;
//                            }
//
//                            for (let i = 0; i < fileInput.files.length; i++) {
//                                const file = fileInput.files[i];
//                                const formData = new FormData();
//                                formData.append('file', file);
//
//                                const xhr = new XMLHttpRequest();
//                                currentXHR = xhr;
//                                const progressBar = document.getElementById('progress-'+i);
//
//                                xhr.upload.addEventListener("progress", function (e) {
//                                    if (e.lengthComputable) {
//                                        const percent = (e.loaded / e.total) * 100;
//                                        progressBar.style.width = percent.toFixed(1) + "%";
//                                    }
//                                });
//
//                                xhr.addEventListener("load", function () {
//                                    if (xhr.status >= 200 && xhr.status < 300) {
//                                        progressBar.classList.add('bg-green-600');
//                                    } else {
//                                        progressBar.classList.remove('bg-green-500');
//                                        progressBar.classList.add('bg-red-500');
//                                    }
//                                });
//
//                                xhr.addEventListener("error", function () {
//                                    progressBar.classList.remove('bg-green-500');
//                                    progressBar.classList.add('bg-red-500');
//                                });
//
//                                xhr.open("POST", "/upload");
//                                xhr.send(formData);
//
//                                await new Promise(resolve => xhr.addEventListener("loadend", resolve));
//                            }
//                            currentXHR = null;
//                        }
                        async function handleUpload() {
                            if (fileInput.files.length === 0) {
                                console.log("No files selected to upload.");
                                return;
                            }
                        
                            for (let i = 0; i < fileInput.files.length; i++) {
                                const file = fileInput.files[i];
                                const formData = new FormData();
                                formData.append('file', file);
                        
                                const xhr = new XMLHttpRequest();
                                currentXHR = xhr;
                                const progressBar = document.getElementById('progress-' + i);
                                const container = progressBar.parentElement.parentElement; // Get the outer div container
                        
                                xhr.upload.addEventListener("progress", function (e) {
                                    if (e.lengthComputable) {
                                        const percent = (e.loaded / e.total) * 100;
                                        progressBar.style.width = percent.toFixed(1) + "%";
                                    }
                                });
                        
                                xhr.addEventListener("load", function () {
                                    if (xhr.status >= 200 && xhr.status < 300) {
                                        console.log(file.name + " uploaded successfully");
                                        container.remove(); // ✅ Remove the uploaded file block
                                    } else {
                                        console.error(file.name + " failed to upload.");
                                        progressBar.classList.remove('bg-green-500');
                                        progressBar.classList.add('bg-red-500');
                                    }
                                });
                        
                                xhr.addEventListener("error", function () {
                                    console.error(file.name + " error during upload.");
                                    progressBar.classList.remove('bg-green-500');
                                    progressBar.classList.add('bg-red-500');
                                });
                        
                                xhr.open("POST", "/upload");
                                xhr.send(formData);
                        
                                await new Promise(resolve => xhr.addEventListener("loadend", resolve));
                            }
                            currentXHR = null;
                        }


                        function cancelUpload() {
                            if (currentXHR) {
                                currentXHR.abort();
                                currentXHR = null;
                                console.log("Upload canceled.");
                            }
                        }

                        function openDownloadPage() {
                            window.location.href = "show-files";
                        }

                        fileInput.addEventListener('change', displayFiles);
                        clearButton.addEventListener('click', clearFiles);
                        uploadButton.addEventListener('click', handleUpload);
                        cancelButton.addEventListener('click', cancelUpload);
                        downloadPageButton.addEventListener('click', openDownloadPage);

                        fileDisplayArea.innerHTML = defaultMessage;
                        uploadButton.disabled = true;
                        uploadButton.classList.add('opacity-50', 'cursor-not-allowed');
                    """
                    )
                }
            }
        }
    }
}

fun generateShowFilesPage(fileList: List<DeviceFile>): String {
    return createHTML().html {
        lang = "en"
        head {
            meta { charset = "UTF-8" }
            meta { name = "viewport"; content = "width=device-width, initial-scale=1.0" }
            title("Download Files")
            script { src = "https://cdn.tailwindcss.com" }
        }
        body(classes = "bg-gray-100 min-h-screen p-4") {
            div(classes = "fixed bottom-0 left-0 right-0 bg-gray-200 p-4 border-t border-gray-300 shadow-md") {
                div(classes = "container mx-auto flex flex-wrap justify-center items-center gap-4") {
                    label(classes = " hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg shadow transition duration-150 ease-in-out") {
                        +"Download Files"
                    }
                    a(href = "/") {
                        label(classes = "file-input-label bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-lg shadow cursor-pointer transition duration-150 ease-in-out") {
                            +"Home Page"
                        }
                    }
                }
            }

            div(classes = "container mx-auto flex flex-row gap-4") {

                fileList.forEach { fileName ->

                    a(
                        href = "/download/${fileName.hashId}",
                        classes = "block bg-white hover:bg-gray-100 p-4 rounded-lg shadow transition duration-150 ease-in-out text-blue-600 font-medium"
                    ) {
                        +fileName.name
                    }
                }
            }
        }
    }
}