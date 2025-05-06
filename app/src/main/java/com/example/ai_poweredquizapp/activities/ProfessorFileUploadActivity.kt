package com.example.ai_poweredquizapp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_poweredquizapp.databinding.ActivityProfessorFileUploadBinding

class ProfessorFileUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfessorFileUploadBinding
    private val FILE_PICK_CODE = 1001
    private var selectedFileName: String = "No file selected"
    private val uploadedFileNames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfessorFileUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.uploadFileBtn.setOnClickListener {
            pickFile()
        }

        binding.toolbarBackBtn.setOnClickListener {
            finish()
        }

    }

    fun Cursor.getColumnIndexOpenableColumnsDisplayName(): Int {
        return getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        val mimeTypes = arrayOf(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        )
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // ✅ Allow multiple files
        startActivityForResult(Intent.createChooser(intent, "Select files"), FILE_PICK_CODE)
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var result = "Unknown file"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    result = it.getString(nameIndex)
                }
            }
        }
        return result
    }

    @SuppressLint("SetTextI18n")
    @Deprecated("Deprecated - consider ActivityResultContract for future updates.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val clipData = data?.clipData

            if (clipData != null) {
                val selectedNames = mutableListOf<String>()
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    val name = getFileNameFromUri(uri)
                    selectedNames.add(name)
                }
                val result = selectedNames.joinToString("\n")
                binding.selectedFileTv.text = result
                Toast.makeText(this, "Files Selected:\n$result", Toast.LENGTH_SHORT).show()

            } else {
                data?.data?.let { uri ->
                    val fileName = getFileNameFromUri(uri)
                    binding.selectedFileTv.text = fileName
                    Toast.makeText(this, "File Selected: $fileName", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
