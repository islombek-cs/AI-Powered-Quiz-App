package com.example.ai_poweredquizapp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ai_poweredquizapp.R
import com.example.ai_poweredquizapp.databinding.ActivityProfessorFileUploadBinding

class ProfessorFileUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfessorFileUploadBinding
    private val FILE_PICK_CODE = 1001
    private val uploadedFileNames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfessorFileUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.uploadFileBtn.setOnClickListener { pickFile() }
        binding.viewResultsBtn.isEnabled = false
        binding.viewResultsBtn.alpha = 0.5f

        binding.toolbarBackBtn.setOnClickListener { finish() }

        binding.viewResultsBtn.setOnClickListener {
            val dialog = showLoadingDialog()
            Handler(Looper.getMainLooper()).postDelayed({
                dialog.dismiss()
                val intent = Intent(this, ProfessorQuizGenerationActivity::class.java)
                intent.putStringArrayListExtra("uploadedFiles", ArrayList(uploadedFileNames))
                startActivity(intent)
            }, 6000)
        }
    }

    private fun showLoadingDialog(): AlertDialog {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)
        val loadingText = dialogView.findViewById<TextView>(R.id.loadingMessageTv)
        val messages = listOf("Analyzing files...", "Generating quizzes...", "Finalizing content...")
        val handler = Handler(Looper.getMainLooper())
        var index = 0

        val messageUpdater = object : Runnable {
            override fun run() {
                loadingText.text = messages[index % messages.size]
                index++
                handler.postDelayed(this, 2000)
            }
        }
        handler.post(messageUpdater)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.setOnDismissListener { handler.removeCallbacks(messageUpdater) }
        dialog.show()
        return dialog
    }

    private fun updateSelectedFileText() {
        val fileCount = uploadedFileNames.size
        binding.selectedFileTv.text = when (fileCount) {
            0 -> "No file selected"
            1 -> "1 file selected"
            else -> "$fileCount files selected"
        }
        binding.viewResultsBtn.isEnabled = fileCount > 0
        binding.viewResultsBtn.alpha = if (fileCount > 0) 1f else 0.5f
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
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
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
    @Deprecated("...")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val clipData = data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    addFileIfNotExists(uri)
                }
            } else {
                data?.data?.let { uri ->
                    addFileIfNotExists(uri)
                }
            }
        }
    }

    private fun addFileIfNotExists(uri: Uri) {
        val fileName = getFileNameFromUri(uri)
        if (!uploadedFileNames.contains(fileName)) {
            uploadedFileNames.add(fileName)
            addFileToView(fileName)
            updateSelectedFileText()
        }
    }

    private fun addFileToView(fileName: String) {
        val trimmedName = if (fileName.length > 35) fileName.substring(0, 35) + "..." else fileName

        val horizontalLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setPadding(90, 10, 30, 10)
        }

        val fileTextView = TextView(this).apply {
            text = trimmedName
            textSize = 15f
            setTextColor(resources.getColor(android.R.color.darker_gray))
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }

        val removeBtn = ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_delete)
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            setPadding(8, 8, 48, 28)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setOnClickListener {
                binding.fileListLayout.removeView(horizontalLayout)
                uploadedFileNames.remove(fileName)
                updateSelectedFileText()
            }
        }

        horizontalLayout.addView(fileTextView)
        horizontalLayout.addView(removeBtn)

        binding.fileListLayout.addView(horizontalLayout)
    }
}
